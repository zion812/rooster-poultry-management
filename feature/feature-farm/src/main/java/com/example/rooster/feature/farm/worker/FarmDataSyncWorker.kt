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
                Timber.d("Attempting to sync flock ID: ${entity.id}")
                try {
                    val flockDomain = mapEntityToFlock(entity) // Mapper
                    val remoteData = mapFlockToRemote(flockDomain) // Mapper

                    val saveResult = firebaseFarmDataSource.saveFlock(remoteData) // Firebase call

                    if (saveResult.isSuccess) {
                        val syncedEntity = entity.copy(needsSync = false)
                        flockDao.insert(syncedEntity) // Update local entity
                        Timber.d("Successfully synced flock ID: ${entity.id}")
                    } else {
                        allItemsSyncedSuccessfully = false
                        Timber.e(saveResult.exceptionOrNull(), "Failed to sync flock ID: ${entity.id}")
                        // Optionally, implement more sophisticated error handling here,
                        // like incrementing a retry count on the entity.
                    }
                } catch (e: Exception) {
                    allItemsSyncedSuccessfully = false
                    Timber.e(e, "Exception while syncing flock ID: ${entity.id}")
                }
            }

            if (allItemsSyncedSuccessfully) {
                Timber.d("FarmDataSyncWorker completed successfully.")
                Result.success()
            } else {
                Timber.w("FarmDataSyncWorker completed with some failures. Retrying.")
                Result.retry() // Retry if any item failed
            }

        } catch (e: Exception) {
            Timber.e(e, "FarmDataSyncWorker failed")
            Result.failure() // Or Result.retry() depending on the nature of the exception
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
            type = FlockType.valueOf(entity.type), // Potential crash if type is invalid
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
