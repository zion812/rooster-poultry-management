package com.example.rooster.feature.farm.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rooster.feature.farm.data.local.FlockDao
import com.example.rooster.feature.farm.data.local.FlockEntity
import com.example.rooster.feature.farm.data.remote.FirebaseFarmDataSource
import com.example.rooster.feature.farm.domain.model.Flock
import com.example.rooster.feature.farm.domain.model.FlockType
import com.example.rooster.feature.farm.domain.model.VerificationLevel
import com.example.rooster.feature.farm.domain.model.AgeGroup
import com.example.rooster.feature.farm.domain.model.VaccinationStatus
import com.example.rooster.feature.farm.domain.model.HealthStatus
import com.example.rooster.feature.farm.domain.model.FlockStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Date
import com.example.rooster.util.Result

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
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Timber.d("FarmDataSyncWorker started")
        try {
            val unsyncedFlocks = flockDao.getUnsyncedFlocksSuspend()
            if (unsyncedFlocks.isEmpty()) {
                Timber.d("No flocks to sync.")
                return@withContext Result.success()
            }
            Timber.d("Found ${unsyncedFlocks.size} flocks to sync.")
            var allItemsSyncedSuccessfully = true
            for (entity in unsyncedFlocks) {
                if (entity.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                    Timber.w("Flock ID ${entity.id} has reached max sync attempts (${entity.syncAttempts}). Marking as SYNC_FAILED.")
                    flockDao.updateSyncStatus(entity.id, SYNC_FAILED_STATUS)
                    allItemsSyncedSuccessfully = false
                    continue
                }
                var attempt = 0
                var success = false
                var lastError: Exception? = null
                while (attempt < MAX_SYNC_ATTEMPTS && !success) {
                    try {
                        firebaseFarmDataSource.syncFlock(entity)
                        flockDao.markAsSynced(entity.id)
                        success = true
                    } catch (e: Exception) {
                        lastError = e
                        attempt++
                        val backoff = Math.pow(2.0, attempt.toDouble()).toLong() * 500L
                        Timber.w(e, "Sync attempt $attempt failed for flock ${entity.id}, backing off $backoff ms")
                        delay(backoff)
                    }
                }
                if (!success) {
                    Timber.e(lastError, "All sync attempts failed for flock ${entity.id}; marking as SYNC_FAILED.")
                    flockDao.updateSyncStatus(entity.id, SYNC_FAILED_STATUS)
                    allItemsSyncedSuccessfully = false
                }
            }
            if (allItemsSyncedSuccessfully) {
                Timber.d("FarmDataSyncWorker completed successfully.")
                Result.success()
            } else {
                Timber.w("FarmDataSyncWorker completed with errors or permanent failures. Not all items synced.")
                Result.retry()
            }
        } catch (e: Exception) {
            Timber.e(e, "FarmDataSyncWorker failed")
            Result.retry()
        }
    }

    // --- Mapping functions (adapted from FarmRepositoryImpl) ---
    // These should ideally be in a common mapper utility class accessible by both repository and worker.
    // For now, they are duplicated/adapted here for simplicity.

    private fun mapEntityToFlock(entity: FlockEntity): Flock {
        return Flock(
            id = entity.id,
            ownerId = entity.ownerId,
            fatherId = entity.fatherId,
            motherId = entity.motherId,
            type = try {
                FlockType.valueOf(entity.type.uppercase(java.util.Locale.ROOT))
            } catch (e: Exception) {
                Timber.w("Invalid flock type in entity for sync: ${entity.type}, defaulting to FOWL.")
                FlockType.FOWL // Default fallback consistent with repository
            },
            name = entity.name,
            breed = entity.breed,
            weight = entity.weight,
            height = null, // Assuming not stored in entity or not needed for sync object
            color = null,
            gender = null,
            certified = entity.certified,
            verified = entity.verified,
            verificationLevel = VerificationLevel.BASIC, // Default or map if available
            traceable = false, // Default or map if available
            ageGroup = AgeGroup.WEEKS_0_5, // Default or map if available
            dateOfBirth = null,
            placeOfBirth = null,
            currentAge = null,
            vaccinationStatus = VaccinationStatus.NOT_STARTED, // Default or map
            lastVaccinationDate = null,
            healthStatus = HealthStatus.GOOD, // Default or map
            lastHealthCheck = null,
            identification = null,
            registryNumber = null,
            proofs = null,
            specialty = null,
            productivityScore = null,
            growthRate = null,
            feedConversionRatio = null,
            status = FlockStatus.ACTIVE, // Default or map
            forSale = false, // Default or map
            price = null,
            purpose = null,
            createdAt = Date(entity.createdAt),
            updatedAt = Date(entity.updatedAt)
        )
    }

    private fun mapFlockToRemote(flock: Flock): Map<String, Any> {
        return mapOfNotNull(
            "id" to flock.id,
            "ownerId" to flock.ownerId,
            "fatherId" to flock.fatherId,
            "motherId" to flock.motherId,
            "type" to flock.type.name,
            "name" to flock.name,
            "breed" to flock.breed,
            "weight" to flock.weight,
            "certified" to flock.certified,
            "verified" to flock.verified,
            // "verificationLevel" // Not in current remote map in repo
            // "traceable"
            // "ageGroup"
            // "dateOfBirth"
            // "placeOfBirth"
            // "currentAge"
            // "vaccinationStatus"
            // "lastVaccinationDate"
            // "healthStatus"
            // "lastHealthCheck"
            // "identification"
            // "registryNumber"
            // "proofs"
            // "specialty"
            // "productivityScore"
            // "growthRate"
            // "feedConversionRatio"
            // "status"
            // "forSale"
            // "price"
            // "purpose"
            "createdAt" to flock.createdAt.time,
            "updatedAt" to flock.updatedAt.time
        ).filterValues { it != null } // Ensure only non-null values are sent if some are optional
    }
}
