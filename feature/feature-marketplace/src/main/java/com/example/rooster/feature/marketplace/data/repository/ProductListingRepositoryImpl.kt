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
import kotlinx.coroutines.flow.collect
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
        searchTerm: String?,
        forceRefresh: Boolean
    ): Flow<Result<List<ProductListing>>> = flow {
        emit(Result.Loading)

        try {
            val remoteListingsFlow = remoteDataSource.getProductListingsStream(
                category = category?.name,
                sellerId = sellerId,
                searchTerm = searchTerm
            )

            remoteListingsFlow.collect { remoteResult ->
                when (remoteResult) {
                    is Result.Success -> {
                        val domainListings = remoteResult.data
                        val entities =
                            domainListings.map { mapDomainToEntity(it, needsSync = false) }
                        localDataSource.insertListings(entities)
                        emit(
                            Result.Success(
                                filterListings(
                                    domainListings,
                                    searchTerm,
                                    category,
                                    sellerId
                                )
                            )
                        )
                    }
                    is Result.Error -> {
                        emit(Result.Error(remoteResult.exception))
                    }

                    Result.Loading -> { /* Handled by initial emit */
                    }
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

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

    override fun getProductListingDetails(listingId: String): Flow<Result<ProductListing?>> = flow {
        emit(Result.Loading)
        try {
            val remoteResult = remoteDataSource.getProductListingDetails(listingId)
            when (remoteResult) {
                is Result.Success -> {
                    val domainListing = remoteResult.data
                    if (domainListing != null) {
                        localDataSource.insertListing(
                            mapDomainToEntity(
                                domainListing,
                                needsSync = false
                            )
                        )
                        emit(Result.Success(domainListing))
                    } else {
                        emit(Result.Success(null))
                    }
                }
                is Result.Error -> {
                    val localListing = localDataSource.getListingById(listingId).map { entity ->
                        entity?.let { mapEntityToDomain(it) }
                    }
                    localListing.collect { emit(Result.Success(it)) }
                }

                Result.Loading -> {
                    // Already handled by initial emit
                }
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
                localDataSource.insertListing(entity.copy(needsSync = false))
                Result.Success(remoteResult.data)
            } else {
                Result.Error((remoteResult as Result.Error).exception)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateProductListing(listing: ProductListing): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = mapDomainToEntity(listing, needsSync = true)
            localDataSource.insertListing(entity)

            val remoteResult = remoteDataSource.updateProductListing(listing)
            if (remoteResult is Result.Success) {
                localDataSource.insertListing(entity.copy(needsSync = false))
            }
            remoteResult
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteProductListing(listingId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            localDataSource.deleteListingById(listingId)
            remoteDataSource.deleteProductListing(listingId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUnsyncedProductListings(): List<ProductListing> = withContext(Dispatchers.IO) {
        localDataSource.getUnsyncedListingsSuspend().map { mapEntityToDomain(it) }
    }

    override suspend fun syncListing(productListing: ProductListing): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteDataSource.createProductListing(productListing)

            if (remoteResult is Result.Success) {
                val entity = mapDomainToEntity(productListing, needsSync = false)
                localDataSource.insertListing(entity)
                Result.Success(Unit)
            } else if (remoteResult is Result.Error) {
                Result.Error(remoteResult.exception)
            } else {
                Result.Error(Exception("Unknown error during listing sync"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

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
