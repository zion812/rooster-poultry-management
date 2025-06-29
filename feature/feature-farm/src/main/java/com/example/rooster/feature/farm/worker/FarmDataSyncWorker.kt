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
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Date

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
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Timber.d("FarmDataSyncWorker started")
        try {
            val unsyncedFlocks = flockDao.getUnsyncedFlocksSuspend() // Assuming this method will be added to DAO

            if (unsyncedFlocks.isEmpty()) {
                Timber.d("No flocks to sync.")
                return@withContext Result.success()
            }

            Timber.d("Found ${unsyncedFlocks.size} flocks to sync.")
            var allItemsSyncedSuccessfully = true

            for (entity in unsyncedFlocks) {
                if (entity.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                    Timber.w("Flock ID ${entity.id} has reached max sync attempts (${entity.syncAttempts}). Skipping for this run.")
                    // Optionally, mark as SYNC_FAILED here if that state is introduced.
                    // For now, it will remain needsSync=true but won't be actively tried by this worker instance beyond MAX_SYNC_ATTEMPTS.
                    // The overall work might still be retried by WorkManager later, which would re-evaluate this flock.
                    // To truly stop it from being picked up by future worker runs, needsSync would need to be false or a SYNC_FAILED status used.
                    // For this iteration, we just log and effectively skip, relying on overall job retry.
                    // If even one skipped item exists that still needs sync, the job should ideally retry.
                    allItemsSyncedSuccessfully = false // Ensure worker retries if skippable items still need sync.
                    continue
                }

                Timber.d("Attempting to sync flock ID: ${entity.id}, attempt: ${entity.syncAttempts + 1}")
                val updatedEntity = entity.copy(
                    syncAttempts = entity.syncAttempts + 1,
                    lastSyncAttemptTimestamp = System.currentTimeMillis()
                )
                flockDao.update(updatedEntity) // Update attempts and timestamp before trying

                try {
                    val flockDomain = mapEntityToFlock(updatedEntity) // Use updated entity for mapping
                    val remoteData = mapFlockToRemote(flockDomain)

                    val saveResult = firebaseFarmDataSource.saveFlock(remoteData)

                    if (saveResult.isSuccess) {
                        val syncedEntity = updatedEntity.copy(needsSync = false, syncAttempts = 0) // Reset attempts on success
                        flockDao.update(syncedEntity)
                        Timber.d("Successfully synced flock ID: ${entity.id}")
                    } else {
                        allItemsSyncedSuccessfully = false
                        Timber.e(saveResult.exceptionOrNull(), "Failed to sync flock ID: ${entity.id}, attempt: ${updatedEntity.syncAttempts}")
                        // Entity already updated with increased syncAttempts
                    }
                } catch (e: Exception) {
                    allItemsSyncedSuccessfully = false
                    Timber.e(e, "Exception while syncing flock ID: ${entity.id}, attempt: ${updatedEntity.syncAttempts}")
                    // Entity already updated with increased syncAttempts
                }
            }

            if (allItemsSyncedSuccessfully) {
                Timber.d("FarmDataSyncWorker completed successfully.")
                Result.success()
            } else {
                Timber.w("FarmDataSyncWorker completed with errors or pending retries for some items. Requesting retry.")
                Result.retry()
            }

        } catch (e: Exception) {
            Timber.e(e, "FarmDataSyncWorker failed")
            Result.retry() // Prefer retry over failure for transient issues
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
