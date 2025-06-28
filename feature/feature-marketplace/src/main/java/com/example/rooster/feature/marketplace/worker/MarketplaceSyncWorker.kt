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
    }

    override suspend fun doWork(): Result {
        Timber.d("MarketplaceSyncWorker started")

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
                    }
                }
            } else {
                Timber.d("No unsynced product listings to sync.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error syncing product listings")
            success = false
        }

        // Sync Orders
        try {
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
                    }
                }
            } else {
                Timber.d("No unsynced orders to sync.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error syncing orders")
            success = false
        }

        return if (success) {
            Timber.d("MarketplaceSyncWorker completed successfully")
            Result.success()
        } else {
            Timber.w("MarketplaceSyncWorker completed with errors, retrying.")
            Result.retry() // Retry if any part failed
        }
    }
}
