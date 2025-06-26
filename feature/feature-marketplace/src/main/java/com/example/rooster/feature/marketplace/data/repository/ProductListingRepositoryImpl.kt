package com.example.rooster.feature.marketplace.data.repository

import com.example.rooster.core.common.Result
import com.example.rooster.core.common.asResult
import com.example.rooster.feature.marketplace.data.local.dao.ProductListingDao
import com.example.rooster.feature.marketplace.data.local.model.ProductListingEntity
import com.example.rooster.feature.marketplace.data.remote.MarketplaceRemoteDataSource
import com.example.rooster.feature.marketplace.domain.model.ProductCategory
import com.example.rooster.feature.marketplace.domain.model.ProductListing
import com.example.rooster.feature.marketplace.domain.model.ListingStatus
import com.example.rooster.feature.marketplace.domain.repository.ProductListingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductListingRepositoryImpl @Inject constructor(
    private val localDataSource: ProductListingDao,
    private val remoteDataSource: MarketplaceRemoteDataSource
) : ProductListingRepository {

    override fun getProductListings(
        category: ProductCategory?,
        sellerId: String?,
        searchTerm: String?, // Basic client-side filtering for now
        forceRefresh: Boolean
    ): Flow<Result<List<ProductListing>>> = flow {
        // For simplicity, this initial implementation fetches all and then filters client-side if needed.
        // A more robust solution would pass filters to remoteDataSource and have server-side filtering.
        // Also, it always fetches from remote first if forceRefresh is true or local is empty, then updates cache.

        // Emit loading
        emit(Result.Loading)

        // Attempt to fetch from remote
        // TODO: Implement more sophisticated caching:
        // 1. Emit local data first.
        // 2. Then fetch remote.
        // 3. If remote fetch successful, update local and emit updated local data.
        // 4. If remote fetch fails, emit error but user still has stale local data.
        // This current version is a simpler fetch-and-cache.

        try {
            val remoteListingsFlow = remoteDataSource.getProductListingsStream(
                category = category?.name, // Pass category name
                sellerId = sellerId,
                searchTerm = searchTerm // searchTerm might not be used by remote yet
            )

            remoteListingsFlow.collect { remoteResult ->
                when (remoteResult) {
                    is Result.Success -> {
                        val domainListings = remoteResult.data
                        // Cache the results
                        val entities = domainListings.map { mapDomainToEntity(it, needsSync = false) }
                        localDataSource.insertListings(entities)
                        // Emit the fresh data
                        emit(Result.Success(filterListings(domainListings, searchTerm)))
                    }
                    is Result.Error -> {
                        // On remote error, try to serve from local cache
                        val cachedListings = localDataSource.getAllListings().map { entities ->
                            entities.map { mapEntityToDomain(it) }
                        }
                        // This needs to be collected from the Flow
                        // For now, simplified error propagation
                        emit(Result.Error(remoteResult.exception))
                        // TODO: Actually emit cachedListings if remote fails
                    }
                    Result.Loading -> { /* Handled by initial emit */ }
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)


    private fun filterListings(listings: List<ProductListing>, searchTerm: String?): List<ProductListing> {
        if (searchTerm.isNullOrBlank()) {
            return listings
        }
        val lowerSearchTerm = searchTerm.lowercase()
        return listings.filter {
            it.title.lowercase().contains(lowerSearchTerm) ||
            it.description.lowercase().contains(lowerSearchTerm) ||
            it.breed?.lowercase()?.contains(lowerSearchTerm) == true
        }
    }


    override fun getProductListingDetails(listingId: String): Flow<Result<ProductListing?>> = flow {
        emit(Result.Loading)
        // Try local first
        val localListing = localDataSource.getListingById(listingId).map { entity ->
            entity?.let { mapEntityToDomain(it) }
        }
        // Simplified: fetch remote and update cache, then re-emit.
        // A more complex strategy would emit local then update from remote.
        try {
            val remoteResult = remoteDataSource.getProductListingDetails(listingId)
            if (remoteResult is Result.Success && remoteResult.data != null) {
                val domainListing = remoteResult.data
                localDataSource.insertListing(mapDomainToEntity(domainListing, needsSync = false))
                emit(Result.Success(domainListing))
            } else if (remoteResult is Result.Error) {
                // If remote fails, rely on whatever localListing flow emits.
                // This part needs to be structured better to combine flows.
                // For now, just propagating remote error if data is null.
                 localListing.collect { emit(Result.Success(it)) } // emit local if remote fails to get data
            } else {
                 localListing.collect { emit(Result.Success(it)) }
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }

    }.flowOn(Dispatchers.IO)


    override suspend fun createProductListing(listing: ProductListing): Result<String> = withContext(Dispatchers.IO) {
        try {
            val listingWithId = if (listing.id.isBlank()) listing.copy(id = UUID.randomUUID().toString()) else listing
            val entity = mapDomainToEntity(listingWithId, needsSync = true)
            localDataSource.insertListing(entity)

            val remoteResult = remoteDataSource.createProductListing(listingWithId)
            if (remoteResult is Result.Success) {
                // Mark as synced if remote save is successful
                localDataSource.insertListing(entity.copy(needsSync = false))
                Result.Success(remoteResult.data) // Return ID from remote
            } else {
                // Remote save failed, needsSync remains true for worker
                Result.Error((remoteResult as Result.Error).exception)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateProductListing(listing: ProductListing): Result<Unit> = withContext(Dispatchers.IO) {
         try {
            val entity = mapDomainToEntity(listing, needsSync = true) // Mark for sync
            localDataSource.updateListing(entity) // or insert, as it's REPLACE

            val remoteResult = remoteDataSource.updateProductListing(listing)
            if (remoteResult is Result.Success) {
                localDataSource.updateListing(entity.copy(needsSync = false))
            }
            // If remote fails, needsSync=true ensures worker picks it up.
            remoteResult // Return the result of the remote operation
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteProductListing(listingId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            localDataSource.deleteListingById(listingId) // Delete locally first

            // Attempt to delete from remote.
            // If this fails, there's no local record to mark as needsSync for deletion.
            // This scenario requires a "pending deletes" table or soft deletes if robust offline deletion sync is needed.
            // For now, assume remote deletion is attempted.
            remoteDataSource.deleteProductListing(listingId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // --- Mappers ---
    // TODO: Extract mappers to a separate utility if they become complex or are shared.

    private fun mapEntityToDomain(entity: ProductListingEntity): ProductListing {
        return ProductListing(
            id = entity.id,
            sellerId = entity.sellerId,
            title = entity.title,
            description = entity.description,
            category = entity.category,
            breed = entity.breed,
            ageInWeeks = entity.ageInWeeks,
            weightInKg = entity.weightInKg,
            price = entity.price,
            currency = entity.currency,
            quantityAvailable = entity.quantityAvailable,
            imageUrls = entity.imageUrls,
            locationCity = entity.locationCity,
            locationDistrict = entity.locationDistrict,
            locationState = entity.locationState,
            isOrganic = entity.isOrganic,
            isVaccinated = entity.isVaccinated,
            postedDateTimestamp = entity.postedDateTimestamp,
            updatedDateTimestamp = entity.updatedDateTimestamp,
            status = entity.status,
            additionalProperties = entity.additionalProperties
            // needsSync is a local concern, not part of domain model usually
        )
    }

    private fun mapDomainToEntity(domain: ProductListing, needsSync: Boolean): ProductListingEntity {
        return ProductListingEntity(
            id = domain.id.ifBlank { UUID.randomUUID().toString() },
            sellerId = domain.sellerId,
            title = domain.title,
            description = domain.description,
            category = domain.category,
            breed = domain.breed,
            ageInWeeks = domain.ageInWeeks,
            weightInKg = domain.weightInKg,
            price = domain.price,
            currency = domain.currency,
            quantityAvailable = domain.quantityAvailable,
            imageUrls = domain.imageUrls,
            locationCity = domain.locationCity,
            locationDistrict = domain.locationDistrict,
            locationState = domain.locationState,
            isOrganic = domain.isOrganic,
            isVaccinated = domain.isVaccinated,
            postedDateTimestamp = domain.postedDateTimestamp,
            updatedDateTimestamp = domain.updatedDateTimestamp,
            status = domain.status,
            additionalProperties = domain.additionalProperties,
            needsSync = needsSync
        )
    }
}
