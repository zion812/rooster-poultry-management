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
    ): Flow<Result<List<ProductListing>>> {
        // Improved strategy: Network-Bound Resource approach
        // 1. Emit Loading.
        // 2. Query local DAO. Emit local data.
        // 3. If forceRefresh or local data is empty/stale, fetch from remote.
        // 4. If remote fetch successful, save to local DAO (which triggers re-emission of local flow).
        // 5. If remote fetch fails, emit error (UI can show local data + error).

        return localBackedRemoteResource(
            localCall = {
                // Apply local filtering if possible, or fetch all and filter after mapping
                // For now, local filtering is not implemented, relying on remote or post-fetch.
                localDataSource.getAllListings().map { entities ->
                    filterListings(entities.map { mapEntityToDomain(it) }, searchTerm, category, sellerId)
                }
            },
            remoteCall = {
                remoteDataSource.getProductListingsStream(
                    category = category?.name,
                    sellerId = sellerId,
                    searchTerm = searchTerm // Assuming remote can handle this or it's for consistency
                )
            },
            saveRemoteResult = { listings ->
                val entities = listings.map { mapDomainToEntity(it, needsSync = false) }
                localDataSource.insertListings(entities)
            },
            shouldFetch = { localData ->
                forceRefresh || localData.isNullOrEmpty() // Basic condition, could be time-based staleness
            }
        ).flowOn(Dispatchers.IO)
    }


    // Enhanced filterListings to also handle category and sellerId for client-side filtering if needed
    private fun filterListings(
        listings: List<ProductListing>,
        searchTerm: String?,
        category: ProductCategory?,
        sellerId: String?
    ): List<ProductListing> {
        var filteredListings = listings
        if (category != null) {
            filteredListings = filteredListings.filter { it.category == category }
        }
        if (sellerId != null) {
            filteredListings = filteredListings.filter { it.sellerId == sellerId }
        }
        if (!searchTerm.isNullOrBlank()) {
            val lowerSearchTerm = searchTerm.lowercase()
            filteredListings = filteredListings.filter {
                it.title.lowercase().contains(lowerSearchTerm) ||
                it.description.lowercase().contains(lowerSearchTerm) ||
                it.breed?.lowercase()?.contains(lowerSearchTerm) == true
            }
        }
        return filteredListings
    }

    override fun getProductListingDetails(listingId: String): Flow<Result<ProductListing?>> {
        return localBackedRemoteResource(
            localCall = { localDataSource.getListingById(listingId).map { it?.let { entity -> mapEntityToDomain(entity) } } },
            remoteCall = { remoteDataSource.getProductListingDetails(listingId) },
            saveRemoteResult = { listing ->
                if (listing != null) {
                    localDataSource.insertListing(mapDomainToEntity(listing, needsSync = false))
                }
            },
            shouldFetch = { localData -> localData == null } // Fetch if not in cache
        ).flowOn(Dispatchers.IO)
    }


    override suspend fun createProductListing(listing: ProductListing): Result<String> = withContext(Dispatchers.IO) {
        try {
            val listingWithId = if (listing.id.isBlank()) listing.copy(id = UUID.randomUUID().toString()) else listing
            val entity = mapDomainToEntity(listingWithId, needsSync = true)
            localDataSource.insertListing(entity)

            val remoteResult = remoteDataSource.createProductListing(listingWithId)
            if (remoteResult is Result.Success && remoteResult.data != null) {
                // Mark as synced if remote save is successful
                localDataSource.insertListing(entity.copy(needsSync = false))
                Result.Success(remoteResult.data) // Return ID from remote
            } else if (remoteResult is Result.Error) {
                // Remote save failed, needsSync remains true for worker
                Result.Error(remoteResult.exception)
            } else {
                // Remote save "succeeded" but returned null ID, treat as error or handle as per API contract
                Result.Error(Exception("Remote data source returned null ID for created listing"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateProductListing(listing: ProductListing): Result<Unit> = withContext(Dispatchers.IO) {
         try {
            val entity = mapDomainToEntity(listing, needsSync = true) // Mark for sync
            localDataSource.insertListing(entity) // Use insert with OnConflictStrategy.REPLACE for update

            val remoteResult = remoteDataSource.updateProductListing(listing)
            if (remoteResult is Result.Success) {
                localDataSource.insertListing(entity.copy(needsSync = false)) // Update to synced
            }
            // If remote fails, needsSync=true ensures worker picks it up.
            remoteResult // Return the result of the remote operation
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteProductListing(listingId: String): Result<Unit> = withContext(Dispatchers.IO) {
        // For robust offline deletion, this would mark as 'deleted' locally and sync that state.
        // Current implementation: local delete, then attempt remote delete.
        // If remote fails, the item is gone locally but might still exist remotely.
        // A sync worker for deletions would require a different strategy (e.g., soft deletes).
        try {
            val remoteDeleteResult = remoteDataSource.deleteProductListing(listingId)
            if (remoteDeleteResult is Result.Success) {
                localDataSource.deleteListingById(listingId) // Delete locally only after successful remote delete
                Result.Success(Unit)
            } else {
                // Option: If allowed, delete locally anyway and flag for later sync if possible (harder for deletes)
                // Or, don't delete locally and return the error.
                Result.Error((remoteDeleteResult as Result.Error).exception)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

// Generic helper for network-bound resource pattern
// S: Source type from remote (e.g., ProductListing, List<ProductListing>)
// L: Local entity type (e.g., ProductListingEntity, List<ProductListingEntity>)
// D: Domain model type (e.g., ProductListing, List<ProductListing>)
private inline fun <D, S> localBackedRemoteResource(
    crossinline localCall: () -> Flow<D?>, // Flow of domain model from local source
    crossinline remoteCall: suspend () -> Result<S?>, // Suspend fun for remote source, S is remote type
    crossinline saveRemoteResult: suspend (S) -> Unit, // Save remote S type to local
    crossinline shouldFetch: (D?) -> Boolean = { true } // When to fetch remote
): Flow<Result<D?>> = flow {
    emit(Result.Loading)
    val localData = localCall().firstOrNull() // Get initial local data once

    if (localData != null) {
        emit(Result.Success(localData)) // Emit local data first
    }

    if (shouldFetch(localData)) {
        when (val remoteResult = remoteCall()) {
            is Result.Success -> {
                if (remoteResult.data != null) {
                    saveRemoteResult(remoteResult.data)
                    // After saving, localCall() flow should emit the new data if it's an observable query
                    // If localCall is not continuously emitting, or to ensure latest data:
                    localCall().collect { updatedLocalData -> emit(Result.Success(updatedLocalData)) }
                } else {
                    // Remote call succeeded but no data (e.g. 404 not found for details)
                    // If localData was null, this means not found anywhere.
                    if (localData == null) emit(Result.Success(null))
                    // else, localData was already emitted, and remote confirms it's not there or is empty.
                }
            }
            is Result.Error -> {
                // Emit error, but UI can still show localData if it was previously emitted
                emit(Result.Error(remoteResult.exception, localData)) // Pass localData as stale data
            }
            Result.Loading -> { /* Should not be emitted by remoteCall directly if it's a suspend fun */ }
        }
    } else if (localData == null) {
        // Not fetching and no local data means not found or empty
        emit(Result.Success(null))
    }
    // If shouldFetch is false and localData was emitted, we're done.
}.catch { e ->
    // Catch exceptions from localCall or the flow construction itself
    emit(Result.Error(e))
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
