package com.example.rooster.feature.marketplace.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.domain.repository.OrderRepository
import com.example.rooster.feature.marketplace.domain.repository.ProductListingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

@HiltWorker
class MarketplaceSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val productListingRepository: ProductListingRepository,
    private val orderRepository: OrderRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "MarketplaceSyncWorker"
        private const val MAX_SYNC_ATTEMPTS = 5
    }

    private val syncMutex = Mutex()

    override suspend fun doWork(): androidx.work.ListenableWorker.Result = syncMutex.withLock {
        Timber.d("MarketplaceSyncWorker started")
        var overallSuccess = true

        // Sync Product Listings
        try {
            val unsyncedListings = productListingRepository.getUnsyncedProductListings()
            if (unsyncedListings.isNotEmpty()) {
                Timber.d("Found ${unsyncedListings.size} unsynced product listings.")
                for (listing in unsyncedListings) {
                    try {
                        val syncResult = productListingRepository.syncListing(listing)
                        when (syncResult) {
                            is com.example.rooster.core.common.Result.Success -> {
                                Timber.d("Successfully synced product listing: ${listing.id}")
                            }
                            is com.example.rooster.core.common.Result.Error -> {
                                Timber.e(
                                    syncResult.exception,
                                    "Failed to sync product listing: ${listing.id}"
                                )
                                overallSuccess = false
                            }
                            is com.example.rooster.core.common.Result.Loading -> {
                                Timber.w("Unexpected loading state for product listing sync: ${listing.id}")
                                overallSuccess = false
                            }
                            else -> {
                                Timber.e("Unknown result for product listing sync: $syncResult")
                                overallSuccess = false
                            }
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Exception syncing product listing: ${listing.id}")
                        overallSuccess = false
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "MarketplaceSyncWorker failed during product listing sync")
            overallSuccess = false
        }

        // Sync Orders
        try {
            val unsyncedOrders = orderRepository.getUnsyncedOrders()
            if (unsyncedOrders.isNotEmpty()) {
                Timber.d("Found ${unsyncedOrders.size} unsynced orders.")
                for (order in unsyncedOrders) {
                    try {
                        val syncResult = orderRepository.syncOrder(order)
                        when (syncResult) {
                            is com.example.rooster.core.common.Result.Success -> {
                                Timber.d("Successfully synced order: ${order.orderId}")
                            }
                            is com.example.rooster.core.common.Result.Error -> {
                                Timber.e(
                                    syncResult.exception,
                                    "Failed to sync order: ${order.orderId}"
                                )
                                overallSuccess = false
                            }
                            is com.example.rooster.core.common.Result.Loading -> {
                                Timber.w("Unexpected loading state for order sync: ${order.orderId}")
                                overallSuccess = false
                            }
                            else -> {
                                Timber.e("Unknown result for order sync: $syncResult")
                                overallSuccess = false
                            }
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Exception syncing order: ${order.orderId}")
                        overallSuccess = false
                    }
                }
            } else {
                Timber.d("No unsynced orders to sync.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error processing orders for sync")
            overallSuccess = false
        }

        return if (overallSuccess) {
            Timber.d("MarketplaceSyncWorker completed successfully")
            androidx.work.ListenableWorker.Result.success()
        } else {
            Timber.w("MarketplaceSyncWorker completed with errors. Retrying.")
            androidx.work.ListenableWorker.Result.retry()
        }
    }
}