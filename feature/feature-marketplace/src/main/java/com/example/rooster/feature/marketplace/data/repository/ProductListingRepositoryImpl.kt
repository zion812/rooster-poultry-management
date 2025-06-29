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
 jules/arch-assessment-1
        forceRefresh: Boolean,
        pageSize: Int,
        lastVisibleTimestamp: Long?,
        lastVisibleDocId: String?
=======
        forceRefresh: Boolean
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
 main
 main
 main
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
 jules/arch-assessment-1
                // The remoteDataSource.getProductListingsStream is a Flow,
                // but localBackedRemoteResource expects suspend () -> Result<S?>.
                // We need to collect the first emission for the paginated fetch.
                // This also means the 'Stream' naming in DataSource might be misleading if used this way.
                // Alternatively, the repository itself directly collects the stream from data source
                // if continuous updates for a page are needed, which complicates pagination logic.
                // For simple pagination, a suspend fun in DataSource returning Result<List<ProductListing>> is cleaner.
                // Assuming for now getProductListingsStream will be adapted or a new suspend fun added.
                // For this change, I'll assume remoteDataSource.getProductListingsStream is modified to a suspend fun
                // or we adapt. Let's assume a new suspend function for clarity:
                // remoteDataSource.fetchProductListingsPage(...)
                // For now, I will adapt the existing stream call by taking the first element.
                // This is NOT ideal for a stream meant for real-time updates for that page.
                // A proper fix would be a separate suspend fun in DataSource for paginated fetches.
                val result = remoteDataSource.getProductListingsStream(
                    category = category?.name,
                    sellerId = sellerId,
                    searchTerm = searchTerm,
                    pageSize = pageSize,
                    lastVisibleTimestamp = lastVisibleTimestamp,
                    lastVisibleDocId = lastVisibleDocId
                ).firstOrNull() // Taking first emission for pagination
                result ?: Result.Success(emptyList()) // If flow completes without emission
            },
            saveRemoteResult = { remoteListings -> // remoteListings is List<ProductListing>
                val entitiesToSave = mutableListOf<ProductListingEntity>()
                for (remoteListing in remoteListings) {
                    val localUnsynced = localDataSource.getUnsyncedListingByIdSuspend(remoteListing.id)
                    if (localUnsynced?.needsSync == true) {
                        Timber.w("Marketplace: Local listing ID ${remoteListing.id} has unsynced changes during batch update. Skipping remote overwrite for this item.")
                        // Optionally, could add the local unsynced entity to a list to ensure it's part of the emitted flow
                        // but the localCall() in localBackedRemoteResourceList should already handle emitting it.
                    } else {
                        entitiesToSave.add(mapDomainToEntity(remoteListing, needsSync = false))
                    }
                }
                if (entitiesToSave.isNotEmpty()) {
                    // Regarding pagination and clearing:
                    // If it's the first page (lastVisibleTimestamp == null), and we are force-refreshing,
                    // then clearing *synced* items before inserting new ones might be an option.
                    // However, simply using insertListings (with REPLACE) will update existing synced items
                    // and add new ones, which is generally fine and avoids deleting items not in the current page.
                    // The TODO about nuanced cache handling remains valid for more advanced strategies.
                    localDataSource.insertListings(entitiesToSave)
                    Timber.d("Marketplace: Saved/Updated ${entitiesToSave.size} listings in cache from remote.")
                }
            },
            shouldFetch = { localData ->
                forceRefresh || localData.isNullOrEmpty() || (lastVisibleTimestamp != null) // Always fetch if paginating, or if first page is empty/forced
=======
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
 main
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

import timber.log.Timber // Ensure Timber is imported

    override fun getProductListingDetails(listingId: String): Flow<Result<ProductListing?>> {
        return localBackedRemoteResource(
            localCall = { localDataSource.getListingById(listingId).map { it?.let { entity -> mapEntityToDomain(entity) } } },
            remoteCall = { remoteDataSource.getProductListingDetails(listingId) },
            saveRemoteResult = { remoteListingDomain -> // This is 'S', the remote type (ProductListing)
                if (remoteListingDomain != null) {
                    val localEntity = localDataSource.getUnsyncedListingByIdSuspend(listingId) // Check if an unsynced version exists
                    if (localEntity?.needsSync == true) {
                        Timber.w("Marketplace: Local listing ID $listingId has unsynced changes. Remote update from listener will be ignored for now.")
                        // Potentially emit the local data again if the flow structure requires it,
                        // but localBackedRemoteResource already emits localData first.
                        // The key is to NOT overwrite the local unsynced data.
                    } else {
                        localDataSource.insertListing(mapDomainToEntity(remoteListingDomain, needsSync = false))
                        Timber.d("Marketplace: Cache updated from remote for listing ID $listingId.")
                    }
                }
            },
            shouldFetch = { localData -> localData == null } // Fetch if not in cache
        ).flowOn(Dispatchers.IO)
    }
 jules/arch-assessment-1

=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
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
 main

 main
 main
 main

    override suspend fun createProductListing(listing: ProductListing): Result<String> = withContext(Dispatchers.IO) {
        try {
            val listingWithId = if (listing.id.isBlank()) listing.copy(id = UUID.randomUUID().toString()) else listing
            val entity = mapDomainToEntity(listingWithId, needsSync = true)
            localDataSource.insertListing(entity)

            val remoteResult = remoteDataSource.createProductListing(listingWithId)
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
 main
 main
 main
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
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
=======
            if (remoteResult is Result.Success) {
                // Mark as synced if remote save is successful
                localDataSource.insertListing(entity.copy(needsSync = false))
                Result.Success(remoteResult.data) // Return ID from remote
            } else {
                // Remote save failed, needsSync remains true for worker
                Result.Error((remoteResult as Result.Error).exception)
 main
 main
 main
 main
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateProductListing(listing: ProductListing): Result<Unit> = withContext(Dispatchers.IO) {
         try {
            val entity = mapDomainToEntity(listing, needsSync = true) // Mark for sync
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
 main
 main
 main
            localDataSource.insertListing(entity) // Use insert with OnConflictStrategy.REPLACE for update

            val remoteResult = remoteDataSource.updateProductListing(listing)
            if (remoteResult is Result.Success) {
                localDataSource.insertListing(entity.copy(needsSync = false)) // Update to synced
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
=======
            localDataSource.updateListing(entity) // or insert, as it's REPLACE

            val remoteResult = remoteDataSource.updateProductListing(listing)
            if (remoteResult is Result.Success) {
                localDataSource.updateListing(entity.copy(needsSync = false))
 main
 main
 main
 main
            }
            // If remote fails, needsSync=true ensures worker picks it up.
            remoteResult // Return the result of the remote operation
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteProductListing(listingId: String): Result<Unit> = withContext(Dispatchers.IO) {
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
 main
 main
 main
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
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
=======
        try {
            localDataSource.deleteListingById(listingId) // Delete locally first

            // Attempt to delete from remote.
            // If this fails, there's no local record to mark as needsSync for deletion.
            // This scenario requires a "pending deletes" table or soft deletes if robust offline deletion sync is needed.
            // For now, assume remote deletion is attempted.
            remoteDataSource.deleteProductListing(listingId)
 main
 main
 main
 main
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

 jules/arch-assessment-1
// Generic helper for network-bound resource pattern
// Generic helper for network-bound resource pattern
// S: Source type from remote (e.g., ProductListing, List<ProductListing>)
// D: Domain model type (e.g., ProductListing, List<ProductListing>)
// Note: This helper is for single item. For lists, a different one or adaptation is needed if localCall returns List.
private inline fun <D, S> localBackedRemoteResource( // This is for SINGLE item detail
    crossinline localCall: () -> Flow<D?>,
    crossinline remoteCall: suspend () -> Result<S?>,
    crossinline saveRemoteResult: suspend (S) -> Unit,
    crossinline shouldFetch: (D?) -> Boolean = { true }
): Flow<Result<D?>> = flow {
    emit(Result.Loading)
    val localData = localCall().firstOrNull()
=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
 main
 main
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
 main

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
 jules/arch-assessment-1
    localCall().collect { updatedLocalData -> emit(Result.Success(updatedLocalData)) } // This re-emits from local source
=======
                    localCall().collect { updatedLocalData -> emit(Result.Success(updatedLocalData)) }
 main
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

 jules/arch-assessment-1
// A specific helper for lists, or adapt the generic one.
// For getProductListings which returns Flow<Result<List<ProductListing>>>
private inline fun <D, S> localBackedRemoteResourceList(
    crossinline localCall: () -> Flow<List<D>>,
    crossinline remoteCall: suspend () -> Result<List<S>>, // Remote call fetches a list
    crossinline saveRemoteResult: suspend (List<S>) -> Unit,
    crossinline shouldFetch: (List<D>?) -> Boolean = { true }
): Flow<Result<List<D>>> = flow {
    emit(Result.Loading)
    val localData = localCall().firstOrNull()

    if (localData != null && localData.isNotEmpty()) { // Emit local data if not empty
        emit(Result.Success(localData))
    } else if (localData != null && localData.isEmpty() && !shouldFetch(localData)) {
        // If local data is empty and we are not fetching, emit empty success
        emit(Result.Success(emptyList()))
    }


    if (shouldFetch(localData)) {
        when (val remoteResult = remoteCall()) {
            is Result.Success -> {
                saveRemoteResult(remoteResult.data)
                localCall().collect { updatedLocalData -> emit(Result.Success(updatedLocalData)) }
            }
            is Result.Error -> {
                // Emit error, but UI can still show localData if it was previously emitted
                emit(Result.Error(remoteResult.exception, localData ?: emptyList()))
            }
            Result.Loading -> { /* Should not be emitted by remoteCall directly if it's a suspend fun */ }
        }
    } else if (localData == null) { // Only if local data was null and we didn't fetch
        emit(Result.Success(emptyList()))
    }
}.catch { e -> emit(Result.Error(e)) }


=======

 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
=======
 main
 main
 main
 main
 feature/phase1-foundations-community-likes
=======
 feature/phase1-foundations-community-likes
 main
    override suspend fun getUnsyncedProductListingEntities(): List<ProductListingEntity> = withContext(Dispatchers.IO) {
        localDataSource.getUnsyncedListingsSuspend()
    }

    override suspend fun syncListingRemote(productListing: ProductListing): Result<Unit> = withContext(Dispatchers.IO) {
        // This method now ONLY attempts the remote synchronization.
        // It does not interact with the local DAO for needsSync or syncAttempts flags.
        // It assumes productListing.id is correctly populated for remote identification if it's an update.
        // Firestore's set with document ID is an upsert.
        try {
            val remoteResult = remoteDataSource.createProductListing(productListing) // This effectively acts as an upsert if ID is consistent
            if (remoteResult is Result.Success && remoteResult.data != null && remoteResult.data.isNotBlank()) {
 feature/phase1-foundations-community-likes
=======
=======
    override suspend fun getUnsyncedProductListings(): List<ProductListing> = withContext(Dispatchers.IO) {
        localDataSource.getUnsyncedListingsSuspend().map { mapEntityToDomain(it) }
    }

    override suspend fun syncListing(productListing: ProductListing): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Attempt to save to remote
            val remoteResult = remoteDataSource.createProductListing(productListing) // Or update if exists logic needed in remote
            // val remoteResult = remoteDataSource.updateProductListing(productListing) // Choose based on desired sync behavior

            if (remoteResult is Result.Success) {
                // If remote save is successful, update local entity to set needsSync = false
                val entity = mapDomainToEntity(productListing, needsSync = false)
                localDataSource.insertListing(entity) // REPLACE will update it
 main
 main
                Result.Success(Unit)
            } else if (remoteResult is Result.Error) {
                Timber.e(remoteResult.exception, "Failed to sync listing ${productListing.id} to remote.")
                Result.Error(remoteResult.exception)
            } else {
 feature/phase1-foundations-community-likes
=======
 feature/phase1-foundations-community-likes
 main
                Timber.w("Remote sync for listing ${productListing.id} did not return a specific error but was not successful (e.g. null ID).")
                Result.Error(Exception("Unknown error or unsuccessful remote sync for listing ${productListing.id}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during remote listing sync for ${productListing.id}")
 feature/phase1-foundations-community-likes
=======
=======
                 Timber.e("Unknown error while syncing listing ${productListing.id} to remote.")
                Result.Error(Exception("Unknown error during listing sync"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during listing sync for ${productListing.id}")
 main
 main
            Result.Error(e)
        }
    }

 feature/phase1-foundations-community-likes
=======
 feature/phase1-foundations-community-likes
 main
    override suspend fun updateLocalListing(listingEntity: ProductListingEntity) {
        withContext(Dispatchers.IO) {
            localDataSource.insertListing(listingEntity) // Uses REPLACE strategy
        }
    }

    override fun mapListingEntityToDomain(listingEntity: ProductListingEntity): ProductListing {
        return mapEntityToDomain(listingEntity) // Call the existing private mapper
    }

 feature/phase1-foundations-community-likes
=======
=======
 main
 main
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
            // needsSync, syncAttempts, lastSyncAttemptTimestamp are local entity concerns, not part of domain model
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
