package com.example.rooster.feature.marketplace.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rooster.feature.marketplace.domain.repository.OrderRepository
import com.example.rooster.feature.marketplace.domain.repository.ProductListingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class MarketplaceSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    // Using repositories as they encapsulate DAO and remote data source interactions
    private val productListingRepository: ProductListingRepository,
    private val orderRepository: OrderRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "MarketplaceSyncWorker"
 feature/phase1-foundations-community-likes
        private const val MAX_SYNC_ATTEMPTS = 5
=======
 feature/phase1-foundations-community-likes
        private const val MAX_SYNC_ATTEMPTS = 5
=======
 main
 main
    }

    override suspend fun doWork(): Result {
        Timber.d("MarketplaceSyncWorker started")
 feature/phase1-foundations-community-likes
=======
 feature/phase1-foundations-community-likes
 main
        var overallSuccess = true // Tracks if all items were synced or skipped correctly

        // Sync Product Listings
        try {
            // Fetch entities directly to manage their syncAttempts and lastSyncAttemptTimestamp
            val unsyncedListingEntities = productListingRepository.getUnsyncedProductListingEntities()
            if (unsyncedListingEntities.isNotEmpty()) {
                Timber.d("Found ${unsyncedListingEntities.size} unsynced product listings.")
                for (entity in unsyncedListingEntities) {
                    if (entity.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                        Timber.w("ProductListing ID ${entity.id} has reached max sync attempts (${entity.syncAttempts}). Skipping.")
                        overallSuccess = false // Ensure worker retries if skippable items still need sync eventually
                        continue
                    }

                    Timber.d("Attempting to sync product listing ID: ${entity.id}, attempt: ${entity.syncAttempts + 1}")
                    val entityToAttempt = entity.copy(
                        syncAttempts = entity.syncAttempts + 1,
                        lastSyncAttemptTimestamp = System.currentTimeMillis()
                    )
                    productListingRepository.updateLocalListing(entityToAttempt) // Persist attempt details

                    // Map to domain model for the remote call
                    val listingDomain = productListingRepository.mapListingEntityToDomain(entityToAttempt) // Requires this mapper in repo

                    val syncResult = productListingRepository.syncListingRemote(listingDomain)

                    if (syncResult is com.example.rooster.core.common.Result.Success) {
                        val syncedEntity = entityToAttempt.copy(needsSync = false, syncAttempts = 0)
                        productListingRepository.updateLocalListing(syncedEntity)
                        Timber.d("Successfully synced product listing: ${entity.id}")
                    } else {
                        val error = (syncResult as? com.example.rooster.core.common.Result.Error)?.exception
                        Timber.e(error, "Failed to sync product listing: ${entity.id}, attempt: ${entityToAttempt.syncAttempts}")
                        overallSuccess = false
 feature/phase1-foundations-community-likes
=======
=======

        var success = true

        // Sync Product Listings
        try {
            val unsyncedListings = productListingRepository.getUnsyncedProductListings() // Needs to be added to repo
            if (unsyncedListings.isNotEmpty()) {
                Timber.d("Found ${unsyncedListings.size} unsynced product listings.")
                // In a real scenario, we might want to upload them one by one or in batches
                // and handle individual failures.
                // For simplicity, let's assume a bulk sync attempt or iterate.
                for (listing in unsyncedListings) {
                    // The repository's save method should handle setting needsSync = false on successful remote save.
                    // Or we might need a specific sync method in the repository.
                    // For now, assuming createOrUpdateProductListing handles this by re-fetching or updating local.
                    // This part needs careful implementation in the repository.
                    // A more robust approach would be a dedicated sync method in the repository.
                    // Let's assume a simple re-save for now if createOrUpdate handles local update post-sync.
                    // This is a placeholder for more robust repository interaction.
                    // productListingRepository.createOrUpdateProductListing(listing) // This might not be ideal.
                    // A better approach:
                    val syncResult = productListingRepository.syncListing(listing) // Requires syncListing in Repository
                    if (syncResult is com.example.rooster.core.common.Result.Error) {
                        Timber.e(syncResult.exception, "Failed to sync product listing: ${listing.id}")
                        success = false
                        // Decide if we should continue or retry this specific item later.
                        // For now, we'll mark the overall work as failed if any item fails.
                    } else {
                        Timber.d("Successfully synced product listing: ${listing.id}")
 main
 main
                    }
                }
            } else {
                Timber.d("No unsynced product listings to sync.")
            }
        } catch (e: Exception) {
 feature/phase1-foundations-community-likes
            Timber.e(e, "Error processing product listings for sync")
            overallSuccess = false
=======
 feature/phase1-foundations-community-likes
            Timber.e(e, "Error processing product listings for sync")
            overallSuccess = false
=======
            Timber.e(e, "Error syncing product listings")
            success = false
 main
 main
        }

        // Sync Orders
        try {
 feature/phase1-foundations-community-likes
=======
 feature/phase1-foundations-community-likes
 main
            val unsyncedOrderEntities = orderRepository.getUnsyncedOrderEntities()
            if (unsyncedOrderEntities.isNotEmpty()) {
                Timber.d("Found ${unsyncedOrderEntities.size} unsynced orders.")
                for (entity in unsyncedOrderEntities) {
                    if (entity.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                        Timber.w("Order ID ${entity.orderId} has reached max sync attempts (${entity.syncAttempts}). Skipping.")
                        overallSuccess = false
                        continue
                    }

                    Timber.d("Attempting to sync order ID: ${entity.orderId}, attempt: ${entity.syncAttempts + 1}")
                    val entityToAttempt = entity.copy(
                        syncAttempts = entity.syncAttempts + 1,
                        lastSyncAttemptTimestamp = System.currentTimeMillis()
                    )
                    orderRepository.updateLocalOrder(entityToAttempt) // Persist attempt details

                    // Map to domain model for the remote call
                    val orderDomain = orderRepository.mapOrderEntityToDomain(entityToAttempt) // Requires this mapper in repo

                    val syncResult = orderRepository.syncOrderRemote(orderDomain)

                    if (syncResult is com.example.rooster.core.common.Result.Success) {
                        val syncedEntity = entityToAttempt.copy(needsSync = false, syncAttempts = 0)
                        orderRepository.updateLocalOrder(syncedEntity) // This needs to also handle items if OrderWithItems is used
                        Timber.d("Successfully synced order: ${entity.orderId}")
                    } else {
                        val error = (syncResult as? com.example.rooster.core.common.Result.Error)?.exception
                        Timber.e(error, "Failed to sync order: ${entity.orderId}, attempt: ${entityToAttempt.syncAttempts}")
                        overallSuccess = false
 feature/phase1-foundations-community-likes
=======
=======
            val unsyncedOrders = orderRepository.getUnsyncedOrders() // Needs to be added to repo
            if (unsyncedOrders.isNotEmpty()) {
                Timber.d("Found ${unsyncedOrders.size} unsynced orders.")
                for (order in unsyncedOrders) {
                    val syncResult = orderRepository.syncOrder(order) // Requires syncOrder in Repository
                    if (syncResult is com.example.rooster.core.common.Result.Error) {
                        Timber.e(syncResult.exception, "Failed to sync order: ${order.id}")
                        success = false
                    } else {
                        Timber.d("Successfully synced order: ${order.id}")
 main
 main
                    }
                }
            } else {
                Timber.d("No unsynced orders to sync.")
            }
        } catch (e: Exception) {
 feature/phase1-foundations-community-likes
=======
 feature/phase1-foundations-community-likes
 main
            Timber.e(e, "Error processing orders for sync")
            overallSuccess = false
        }

        return if (overallSuccess) {
            Timber.d("MarketplaceSyncWorker completed successfully")
            Result.success()
        } else {
            Timber.w("MarketplaceSyncWorker completed with errors or items still needing sync. Retrying.")
            Result.retry()
 feature/phase1-foundations-community-likes
=======
=======
            Timber.e(e, "Error syncing orders")
            success = false
        }

        return if (success) {
            Timber.d("MarketplaceSyncWorker completed successfully")
            Result.success()
        } else {
            Timber.w("MarketplaceSyncWorker completed with errors, retrying.")
            Result.retry() // Retry if any part failed
 main
 main
        }
    }
}
