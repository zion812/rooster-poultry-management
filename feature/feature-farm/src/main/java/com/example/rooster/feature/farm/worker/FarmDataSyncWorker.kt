package com.example.rooster.feature.farm.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rooster.feature.farm.data.local.FlockDao
import com.example.rooster.feature.farm.data.local.FlockEntity
import com.example.rooster.feature.farm.data.mapper.FlockMapper
import com.example.rooster.feature.farm.data.remote.FirebaseFarmDataSource
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import kotlin.math.min
import kotlin.math.pow
import androidx.work.ListenableWorker.Result as WorkResult
import com.example.rooster.core.common.Result as RemoteResult

@HiltWorker
class FarmDataSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val flockDao: FlockDao,
    private val firebaseFarmDataSource: FirebaseFarmDataSource
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "FarmDataSyncWorker"
        private const val MAX_SYNC_ATTEMPTS = 5
        private const val SYNC_FAILED_STATUS = "SYNC_FAILED"
        private const val MAX_BACKOFF_MS = 30_000L // 30 seconds max backoff
        private val syncMutex = Mutex() // Shared mutex to prevent concurrent sync operations
    }

    override suspend fun doWork(): WorkResult = withContext(Dispatchers.IO) {
        Timber.d("FarmDataSyncWorker started")

        // Use mutex to prevent concurrent sync operations
        syncMutex.withLock {
            try {
                val unsyncedFlocks = flockDao.getUnsyncedFlocksSuspend()
                if (unsyncedFlocks.isEmpty()) {
                    Timber.d("No flocks to sync.")
                    return@withLock WorkResult.success()
                }

                Timber.d("Found ${unsyncedFlocks.size} flocks to sync.")
                var allItemsSyncedSuccessfully = true
                val currentTime = System.currentTimeMillis()

                for (entity in unsyncedFlocks) {
                    // Skip items that have exceeded max attempts
                    if (entity.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                        Timber.w("Flock ID ${entity.id} has reached max sync attempts (${entity.syncAttempts}). Marking as SYNC_FAILED.")
                        flockDao.updateSyncStatus(entity.id, SYNC_FAILED_STATUS)
                        allItemsSyncedSuccessfully = false
                        continue
                    }

                    // Rate limiting: skip if last attempt was too recent
                    if (entity.lastSyncAttemptTimestamp > 0) {
                        val timeSinceLastAttempt = currentTime - entity.lastSyncAttemptTimestamp
                        val minInterval = min(
                            2.0.pow(entity.syncAttempts.toDouble()).toLong() * 1000L,
                            MAX_BACKOFF_MS
                        )

                        if (timeSinceLastAttempt < minInterval) {
                            Timber.d("Skipping flock ${entity.id} due to rate limiting. Next attempt in ${minInterval - timeSinceLastAttempt}ms")
                            allItemsSyncedSuccessfully = false
                            continue
                        }
                    }

                    var success = false
                    var lastError: Exception? = null

                    try {
                        // Update sync attempt tracking before attempting sync
                        flockDao.updateSyncAttempts(entity.id, entity.syncAttempts + 1, currentTime)

                        val flock = FlockMapper.mapEntityToFlock(entity)
                        val remoteData = FlockMapper.mapFlockToRemote(flock)
                            .filterValues { it != null } as Map<String, Any>

                        // Validate required fields before sync
                        validateFlockData(remoteData)

                        when (val result = firebaseFarmDataSource.saveFlock(remoteData)) {
                            is com.example.rooster.core.common.Result.Success -> {
                                // Reset sync tracking on success
                                flockDao.updateSyncStatusAndReset(entity.id)
                                success = true
                                Timber.d("Successfully synced flock ${entity.id}")
                            }
                            is com.example.rooster.core.common.Result.Error -> {
                                lastError = result.exception as? Exception
                                    ?: Exception(result.exception.message)
                                if (isTransientError(result.exception)) {
                                    Timber.w(
                                        result.exception,
                                        "Transient sync failure for flock ${entity.id} (attempt ${entity.syncAttempts + 1})"
                                    )
                                } else {
                                    Timber.e(
                                        result.exception,
                                        "Permanent sync failure for flock ${entity.id}, marking as failed."
                                    )
                                    flockDao.updateSyncStatus(entity.id, SYNC_FAILED_STATUS)
                                }
                            }
                            is com.example.rooster.core.common.Result.Loading -> {
                                // This shouldn't happen in our suspend function context, but handle it
                                Timber.w("Unexpected Loading result for flock ${entity.id}")
                                lastError = IllegalStateException("Unexpected Loading state")
                            }
                        }
                    } catch (e: Exception) {
                        lastError = e
                        Timber.e(e, "Unexpected error during sync for flock ${entity.id}")
                        if (!isTransientError(e)) {
                            flockDao.updateSyncStatus(entity.id, SYNC_FAILED_STATUS)
                        }
                    }

                    if (!success) {
                        allItemsSyncedSuccessfully = false
                    }
                }

                if (allItemsSyncedSuccessfully) {
                    Timber.d("FarmDataSyncWorker completed successfully.")
                    WorkResult.success()
                } else {
                    Timber.w("FarmDataSyncWorker completed with some failures. Retrying later.")
                    WorkResult.retry()
                }
            } catch (e: Exception) {
                Timber.e(e, "FarmDataSyncWorker failed unexpectedly.")
                WorkResult.retry()
            }
        }
    }

    /**
     * Validates flock data before attempting sync to prevent backend rejections
     */
    private fun validateFlockData(flockData: Map<String, Any?>) {
        val requiredFields = listOf("id", "ownerId", "type", "name")
        for (field in requiredFields) {
            val value = flockData[field]
            if (value == null || (value is String && value.isBlank())) {
                throw IllegalArgumentException("Required field '$field' is missing or empty")
            }
        }
    }

    /**
     * Enhanced error classification for better retry logic
     */
    private fun isTransientError(e: Throwable?): Boolean {
        return when (e) {
            is IOException -> true // Network errors
            is FirebaseFirestoreException -> {
                when (e.code) {
                    FirebaseFirestoreException.Code.UNAVAILABLE,
                    FirebaseFirestoreException.Code.DEADLINE_EXCEEDED,
                    FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED,
                    FirebaseFirestoreException.Code.INTERNAL -> true // Added INTERNAL for Firebase server errors
                    else -> false // Other Firestore errors are likely permanent
                }
            }
            is IllegalArgumentException -> false // Validation errors are permanent
            else -> {
                // Check for common transient error patterns in message
                val message = e?.message?.lowercase() ?: ""
                message.contains("timeout") ||
                        message.contains("network") ||
                        message.contains("connection") ||
                        message.contains("unavailable")
            }
        }
    }

    private fun calculateBackoff(attempts: Int): Long {
        val ms = 2.0.pow(attempts.toDouble()) * 1000.0
        return min(ms.toLong(), MAX_BACKOFF_MS)
    }

    private fun isTransient(e: Throwable?): Boolean = when (e) {
        is IOException -> true
        is FirebaseFirestoreException -> when (e.code) {
            FirebaseFirestoreException.Code.UNAVAILABLE,
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED,
            FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED,
            FirebaseFirestoreException.Code.INTERNAL -> true
            else -> false
        }
        else -> false
    }
}
