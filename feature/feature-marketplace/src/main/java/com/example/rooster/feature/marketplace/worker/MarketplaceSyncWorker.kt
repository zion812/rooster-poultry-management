package com.example.rooster.feature.marketplace.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
        private const val SYNC_FAILED_STATUS = "SYNC_FAILED"
    }

    private val syncMutex = Mutex()

    override suspend fun doWork(): Result = syncMutex.withLock {
        Timber.d("MarketplaceSyncWorker started")
        var overallSuccess = true

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
                    if (!productListingRepository.validateListingDomain(listingDomain)) {
                        Timber.e("Validation failed for product listing: ${entity.id}. Marking as SYNC_FAILED.")
                        productListingRepository.updateSyncStatus(entity.id, SYNC_FAILED_STATUS)
                        overallSuccess = false
                        continue
                    }
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
                                val ex = syncResult.exception
                                if (isTransientError(ex)) {
                                    throw ex
                                } else {
                                    Timber.e(ex, "Permanent error syncing product listing: ${entity.id}. Marking as SYNC_FAILED.")
                                    productListingRepository.updateSyncStatus(entity.id, SYNC_FAILED_STATUS)
                                    overallSuccess = false
                                    break
                                }
                            }
                        } catch (e: Exception) {
                            lastError = e
                            attempt++
                            val backoff = Math.pow(2.0, attempt.toDouble()).toLong() * 500L
                            Timber.w(e, "Sync attempt $attempt failed for product listing ${entity.id}, backing off $backoff ms")
                            kotlinx.coroutines.delay(backoff)
                        }
                    }
                    if (!success && lastError != null) {
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
                        Timber.w("Order ID ${entity.orderId} has reached max sync attempts (${entity.syncAttempts}). Marking as SYNC_FAILED.")
                        orderRepository.updateSyncStatus(entity.orderId, SYNC_FAILED_STATUS)
                        overallSuccess = false
                        continue
                    }
                    Timber.d("Attempting to sync order ID: ${entity.orderId}, attempt: ${entity.syncAttempts + 1}")
                    val entityToAttempt = entity.copy(
                        syncAttempts = entity.syncAttempts + 1,
                        lastSyncAttemptTimestamp = System.currentTimeMillis()
                    )
                    orderRepository.updateLocalOrder(entityToAttempt)
                    val orderDomain = orderRepository.mapOrderEntityToDomain(entityToAttempt)
                    if (!orderRepository.validateOrderDomain(orderDomain)) {
                        Timber.e("Validation failed for order: ${entity.orderId}. Marking as SYNC_FAILED.")
                        orderRepository.updateSyncStatus(entity.orderId, SYNC_FAILED_STATUS)
                        overallSuccess = false
                        continue
                    }
                    var attempt = 0
                    var success = false
                    var lastError: Exception? = null
                    while (attempt < MAX_SYNC_ATTEMPTS && !success) {
                        try {
                            val syncResult = orderRepository.syncOrderRemote(orderDomain)
                            if (syncResult is com.example.rooster.core.common.Result.Success) {
                                val syncedEntity = entityToAttempt.copy(needsSync = false, syncAttempts = 0)
                                orderRepository.updateLocalOrder(syncedEntity)
                                Timber.d("Successfully synced order: ${entity.orderId}")
                                success = true
                            } else if (syncResult is com.example.rooster.core.common.Result.Error) {
                                val ex = syncResult.exception
                                if (isTransientError(ex)) {
                                    throw ex
                                } else {
                                    Timber.e(ex, "Permanent error syncing order: ${entity.orderId}. Marking as SYNC_FAILED.")
                                    orderRepository.updateSyncStatus(entity.orderId, SYNC_FAILED_STATUS)
                                    overallSuccess = false
                                    break
                                }
                            }
                        } catch (e: Exception) {
                            lastError = e
                            attempt++
                            val backoff = Math.pow(2.0, attempt.toDouble()).toLong() * 500L
                            Timber.w(e, "Sync attempt $attempt failed for order ${entity.orderId}, backing off $backoff ms")
                            kotlinx.coroutines.delay(backoff)
                        }
                    }
                    if (!success && lastError != null) {
                        Timber.e(lastError, "All sync attempts failed for order ${entity.orderId}; marking as SYNC_FAILED.")
                        orderRepository.updateSyncStatus(entity.orderId, SYNC_FAILED_STATUS)
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

    private fun isTransientError(e: Exception?): Boolean {
        if (e == null) return false
        return e is java.io.IOException || e is retrofit2.HttpException || e.message?.contains("timeout", true) == true
    }
}
