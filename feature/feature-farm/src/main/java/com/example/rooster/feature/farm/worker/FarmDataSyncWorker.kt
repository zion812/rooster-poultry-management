package com.example.rooster.feature.farm.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rooster.feature.farm.data.local.FlockDao
import com.example.rooster.feature.farm.data.local.FlockEntity
import com.example.rooster.feature.farm.data.remote.IFarmRemoteDataSource // Changed to interface
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
    private val remoteDataSource: IFarmRemoteDataSource // Changed to interface
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
                    val flockDomain = mapEntityToFlock(entity) // Mapper from Entity to Domain

                    // remoteDataSource.saveFlock now takes the domain model directly
                    val saveResult = remoteDataSource.saveFlock(flockDomain)

                    if (saveResult.isSuccess) {
                        val syncedEntity = entity.copy(needsSync = false)
                        flockDao.insert(syncedEntity) // Update local entity
                        Timber.d("Successfully synced flock ID: ${entity.id} to Parse")
                    } else {
                        allItemsSyncedSuccessfully = false
                        Timber.e((saveResult as? Result.Error)?.exception, "Failed to sync flock ID: ${entity.id} to Parse")
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
            gender = entity.gender?.let { Gender.valueOf(it) }, // Assuming gender is stored as String in entity
            certified = entity.certified,
            verified = entity.verified,
            verificationLevel = entity.verificationLevel?.let { VerificationLevel.valueOf(it) } ?: VerificationLevel.BASIC,
            traceable = entity.traceable ?: false,
            ageGroup = entity.ageGroup?.let { AgeGroup.valueOf(it) } ?: AgeGroup.CHICKS,
            dateOfBirth = entity.dateOfBirth?.let { Date(it) },
            placeOfBirth = entity.placeOfBirth,
            currentAge = entity.currentAge, // Assuming currentAge is in entity
            vaccinationStatus = entity.vaccinationStatus?.let { VaccinationStatus.valueOf(it) } ?: VaccinationStatus.NOT_STARTED,
            lastVaccinationDate = entity.lastVaccinationDate?.let { Date(it) },
            healthStatus = entity.healthStatus?.let { HealthStatus.valueOf(it) } ?: HealthStatus.GOOD,
            lastHealthCheck = entity.lastHealthCheck?.let { Date(it) },
            identification = entity.identificationTag, // Map from entity field if name differs
            registryNumber = entity.registryNumber,
            proofs = entity.proofs ?: emptyList(), // Assuming proofs is List<String> in entity
            specialty = entity.specialty,
            productivityScore = entity.productivityScore,
            growthRate = entity.growthRate,
            feedConversionRatio = entity.feedConversionRatio,
            status = entity.flockStatus?.let { FlockStatus.valueOf(it) } ?: FlockStatus.ACTIVE, // Assuming flockStatus in entity
            forSale = entity.forSale ?: false,
            price = entity.price,
            purpose = entity.purpose?.mapNotNull { Purpose.valueOf(it) } ?: emptyList(), // Assuming purpose is List<String> in entity
            createdAt = Date(entity.createdAt),
            updatedAt = Date(entity.updatedAt)
        )
    }

    // mapFlockToRemote is no longer needed here as ParseFarmDataSource.saveFlock takes a Flock domain model.
    // The ParseFarmDataSource will handle mapping the Flock domain model to a ParseObject.
}
