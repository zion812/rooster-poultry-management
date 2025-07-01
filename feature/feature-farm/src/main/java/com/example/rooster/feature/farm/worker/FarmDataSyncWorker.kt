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
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
        private const val SYNCED_STATUS = "SYNCED"
        private const val MAX_BACKOFF_MS = 30_000L
        private val syncMutex = Mutex()
    }

    override suspend fun doWork(): WorkResult = withContext(Dispatchers.IO) {
        syncMutex.withLock {
            Timber.d("FarmDataSyncWorker started")
            return@withLock try {
                val unsynced = flockDao.getUnsyncedFlocksSuspend()
                if (unsynced.isEmpty()) {
                    Timber.d("No flocks to sync.")
                    WorkResult.success()
                } else {
                    var allSuccess = true
                    var needRetry = false
                    for (entity in unsynced) {
                        if (entity.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                            Timber.w("Flock ${entity.id} max attempts reached.")
                            flockDao.updateSyncStatus(entity.id, SYNC_FAILED_STATUS)
                            allSuccess = false
                            continue
                        }
                        val now = System.currentTimeMillis()
                        val waited = now - entity.lastSyncAttemptTimestamp
                        val backoff = calculateBackoff(entity.syncAttempts)
                        if (entity.syncAttempts > 0 && waited < backoff) {
                            Timber.d("Skipping ${entity.id}, backoff not elapsed.")
                            needRetry = true
                            allSuccess = false
                            continue
                        }
                        if (!syncOne(entity)) {
                            allSuccess = false
                            needRetry = true
                        }
                    }
                    when {
                        allSuccess -> WorkResult.success()
                        needRetry -> WorkResult.retry()
                        else -> WorkResult.success()
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected failure in sync worker.")
                WorkResult.retry()
            }
        }
    }

    private suspend fun syncOne(entity: FlockEntity): Boolean {
        // track attempt
        flockDao.updateSyncAttempts(entity.id, entity.syncAttempts + 1, System.currentTimeMillis())
        // prepare remote data without nulls
        val rawMap = FlockMapper.mapFlockToRemote(FlockMapper.mapEntityToFlock(entity))
        val remoteData = rawMap.mapValues { it.value!! }
        return when (val res = firebaseFarmDataSource.saveFlock(remoteData)) {
            is RemoteResult.Success -> {
                flockDao.updateSyncStatusAndReset(entity.id, SYNCED_STATUS)
                Timber.d("Synced flock ${entity.id}")
                true
            }

            is RemoteResult.Error -> {
                val err = res.exception
                if (isTransient(err)) {
                    Timber.w(err, "Transient error syncing ${entity.id}")
                    false
                } else {
                    Timber.e(err, "Permanent error syncing ${entity.id}")
                    flockDao.updateSyncStatus(entity.id, SYNC_FAILED_STATUS)
                    true
                }
            }
            is RemoteResult.Loading -> {
                Timber.w("saveFlock returned Loading for flock ${entity.id}; treating as transient")
                false
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
