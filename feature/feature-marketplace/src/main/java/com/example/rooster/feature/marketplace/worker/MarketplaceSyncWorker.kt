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
        private const val MAX_SYNC_ATTEMPTS = 5
        private const val SYNC_FAILED_STATUS = "SYNC_FAILED"
    }

    override suspend fun doWork(): Result {
        Timber.d("MarketplaceSyncWorker started")
        var overallSuccess = true // Tracks if all items were synced or skipped correctly

        // Sync Product Listings
        try {
            val unsyncedListingEntities = productListingRepository.getUnsyncedProductListingEntities()
            if (unsyncedListingEntities.isNotEmpty()) {
                Timber.d("Found ${unsyncedListingEntities.size} unsynced product listings.")
                for (entity in unsyncedListingEntities) {
                    if (entity.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                        Timber.w("ProductListing ID ${entity.id} has reached max sync attempts (${entity.syncAttempts}). Marking as SYNC_FAILED.")
                        productListingRepository.updateSyncStatus(entity.id, SYNC_FAILED_STATUS)
                        overallSuccess = false
                        continue
                    }
                    Timber.d("Attempting to sync product listing ID: ${entity.id}, attempt: ${entity.syncAttempts + 1}")
                    val entityToAttempt = entity.copy(
                        syncAttempts = entity.syncAttempts + 1,
                        lastSyncAttemptTimestamp = System.currentTimeMillis()
                    )
                    productListingRepository.updateLocalListing(entityToAttempt)
                    val listingDomain = productListingRepository.mapListingEntityToDomain(entityToAttempt)
                    var attempt = 0
                    var success = false
                    var lastError: Exception? = null
                    while (attempt < MAX_SYNC_ATTEMPTS && !success) {
                        try {
                            val syncResult = productListingRepository.syncListingRemote(listingDomain)
                            if (syncResult is com.example.rooster.core.common.Result.Success) {
                                val syncedEntity = entityToAttempt.copy(needsSync = false, syncAttempts = 0)
                                productListingRepository.updateLocalListing(syncedEntity)
                                Timber.d("Successfully synced product listing: ${entity.id}")
                                success = true
                            } else if (syncResult is com.example.rooster.core.common.Result.Error) {
                                throw syncResult.exception
                            }
                        } catch (e: Exception) {
                            lastError = e
                            attempt++
                            val backoff = Math.pow(2.0, attempt.toDouble()).toLong() * 500L
                            Timber.w(e, "Sync attempt $attempt failed for product listing ${entity.id}, backing off $backoff ms")
                            kotlinx.coroutines.delay(backoff)
                        }
                    }
                    if (!success) {
                        Timber.e(lastError, "All sync attempts failed for product listing ${entity.id}; marking as SYNC_FAILED.")
                        productListingRepository.updateSyncStatus(entity.id, SYNC_FAILED_STATUS)
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
                    }
                }
            } else {
                Timber.d("No unsynced orders to sync.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error processing orders for sync")
            overallSuccess = false
        }

        if (overallSuccess) {
            Timber.d("MarketplaceSyncWorker completed successfully")
            return Result.success()
        } else {
            Timber.w("MarketplaceSyncWorker completed with errors or items still needing sync. Retrying.")
            return Result.retry()
        }
    }
}
