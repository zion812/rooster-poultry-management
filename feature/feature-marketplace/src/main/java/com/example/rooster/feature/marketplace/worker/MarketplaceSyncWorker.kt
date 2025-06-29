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
    }

    override suspend fun doWork(): Result {
        Timber.d("MarketplaceSyncWorker started")
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
                    }
                }
            } else {
                Timber.d("No unsynced product listings to sync.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error processing product listings for sync")
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

        return if (overallSuccess) {
            Timber.d("MarketplaceSyncWorker completed successfully")
            Result.success()
        } else {
            Timber.w("MarketplaceSyncWorker completed with errors or items still needing sync. Retrying.")
            Result.retry()
        }
    }
}
