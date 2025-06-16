package com.example.rooster.feature.farm.data.repository

import com.example.rooster.feature.farm.data.local.FlockDao
import com.example.rooster.feature.farm.data.local.FlockEntity
import com.example.rooster.feature.farm.data.remote.FarmRemoteDataSource
import com.example.rooster.feature.farm.domain.model.Flock
import com.example.rooster.feature.farm.domain.model.FlockType
import com.example.rooster.feature.farm.domain.model.FlockRegistrationData
import com.example.rooster.feature.farm.domain.model.VerificationLevel
import com.example.rooster.feature.farm.domain.model.AgeGroup
import com.example.rooster.feature.farm.domain.model.VaccinationStatus
import com.example.rooster.feature.farm.domain.model.HealthStatus
import com.example.rooster.feature.farm.domain.model.FlockStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import kotlin.Result

class FarmRepositoryImpl @Inject constructor(
    private val dao: FlockDao,
    private val remoteDataSource: FarmRemoteDataSource
) : FarmRepository {

    override fun getFlockById(id: String): Flow<Result<Flock>> {
        // Combine local and remote data for real-time updates
        return combine(
            dao.getById(id),
            remoteDataSource.getFlockRealTime(id)
        ) { localEntity, remoteResult ->
            when {
                remoteResult.isSuccess -> {
                    val remoteData = remoteResult.getOrNull()
                    if (remoteData != null) {
                        // Convert remote data to domain model
                        val flock = mapRemoteToFlock(remoteData)

                        // Update local cache asynchronously
                        val entity = mapFlockToEntity(flock)
                        try {
                            dao.insert(entity)
                        } catch (e: Exception) {
                            // Log but don't fail on cache update
                        }

                        Result.success(flock)
                    } else {
                        // Fall back to local data
                        localEntity?.let { entity ->
                            val flock = mapEntityToFlock(entity)
                            Result.success(flock)
                        } ?: Result.failure(Exception("Flock not found"))
                    }
                }

                localEntity != null -> {
                    // Use local data when remote fails
                    val flock = mapEntityToFlock(localEntity)
                    Result.success(flock)
                }

                else -> Result.failure(Exception("Flock not found"))
            }
        }
    }

    override fun getFlocksByType(type: String): Flow<Result<List<Flock>>> {
        // For now, return local data - can be enhanced with remote filtering
        return dao.getByType(type).map { entities ->
            val flocks = entities.map { entity -> mapEntityToFlock(entity) }
            Result.success(flocks)
        }
    }

    override suspend fun registerFlock(data: FlockRegistrationData): Result<Unit> {
        return try {
            val id = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()

            // Create domain model
            val flock = Flock(
                id = id,
                ownerId = data.ownerId,
                fatherId = data.fatherId,
                motherId = data.motherId,
                type = FlockType.valueOf(data.ageGroup.name),
                name = "New Flock",
                breed = data.breed,
                weight = data.weight?.toFloat(),
                height = null,
                color = null,
                gender = null,
                certified = false,
                verified = false,
                verificationLevel = VerificationLevel.BASIC,
                traceable = false,
                ageGroup = data.ageGroup,
                dateOfBirth = null,
                placeOfBirth = null,
                currentAge = null,
                vaccinationStatus = VaccinationStatus.NOT_STARTED,
                lastVaccinationDate = null,
                healthStatus = HealthStatus.GOOD,
                lastHealthCheck = null,
                identification = null,
                registryNumber = null,
                proofs = null,
                specialty = null,
                productivityScore = null,
                growthRate = null,
                feedConversionRatio = null,
                status = FlockStatus.ACTIVE,
                forSale = false,
                price = null,
                purpose = null,
                createdAt = Date(now),
                updatedAt = Date(now)
            )

            // Save to local database
            val entity = mapFlockToEntity(flock)
            dao.insert(entity)

            // Save to remote (async - don't block on failure)
            val remoteData = mapFlockToRemote(flock)
            try {
                remoteDataSource.saveFlock(remoteData)
            } catch (e: Exception) {
                // Log but don't fail registration on remote save failure
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Helper mapping functions
    private fun mapEntityToFlock(entity: FlockEntity): Flock {
        return Flock(
            id = entity.id,
            ownerId = entity.ownerId,
            fatherId = entity.fatherId,
            motherId = entity.motherId,
            type = FlockType.valueOf(entity.type),
            name = entity.name,
            breed = entity.breed,
            weight = entity.weight,
            height = null,
            color = null,
            gender = null,
            certified = entity.certified,
            verified = entity.verified,
            verificationLevel = VerificationLevel.BASIC,
            traceable = false,
            ageGroup = AgeGroup.WEEKS_0_5,
            dateOfBirth = null,
            placeOfBirth = null,
            currentAge = null,
            vaccinationStatus = VaccinationStatus.NOT_STARTED,
            lastVaccinationDate = null,
            healthStatus = HealthStatus.GOOD,
            lastHealthCheck = null,
            identification = null,
            registryNumber = null,
            proofs = null,
            specialty = null,
            productivityScore = null,
            growthRate = null,
            feedConversionRatio = null,
            status = FlockStatus.ACTIVE,
            forSale = false,
            price = null,
            purpose = null,
            createdAt = Date(entity.createdAt),
            updatedAt = Date(entity.updatedAt)
        )
    }

    private fun mapFlockToEntity(flock: Flock): FlockEntity {
        return FlockEntity(
            id = flock.id,
            ownerId = flock.ownerId,
            fatherId = flock.fatherId,
            motherId = flock.motherId,
            type = flock.type.name,
            name = flock.name,
            breed = flock.breed,
            weight = flock.weight,
            certified = flock.certified,
            verified = flock.verified,
            createdAt = flock.createdAt.time,
            updatedAt = flock.updatedAt.time
        )
    }

    private fun mapRemoteToFlock(remote: Map<String, Any>): Flock {
        return Flock(
            id = remote["id"] as String,
            ownerId = remote["ownerId"] as String,
            fatherId = remote["fatherId"] as? String,
            motherId = remote["motherId"] as? String,
            type = FlockType.valueOf(remote["type"] as String),
            name = remote["name"] as String,
            breed = remote["breed"] as? String,
            weight = (remote["weight"] as? Number)?.toFloat(),
            height = null,
            color = null,
            gender = null,
            certified = remote["certified"] as? Boolean ?: false,
            verified = remote["verified"] as? Boolean ?: false,
            verificationLevel = VerificationLevel.BASIC,
            traceable = false,
            ageGroup = AgeGroup.WEEKS_0_5,
            dateOfBirth = null,
            placeOfBirth = null,
            currentAge = null,
            vaccinationStatus = VaccinationStatus.NOT_STARTED,
            lastVaccinationDate = null,
            healthStatus = HealthStatus.GOOD,
            lastHealthCheck = null,
            identification = null,
            registryNumber = null,
            proofs = null,
            specialty = null,
            productivityScore = null,
            growthRate = null,
            feedConversionRatio = null,
            status = FlockStatus.ACTIVE,
            forSale = false,
            price = null,
            purpose = null,
            createdAt = Date((remote["createdAt"] as Number).toLong()),
            updatedAt = Date((remote["updatedAt"] as Number).toLong())
        )
    }

    private fun mapFlockToRemote(flock: Flock): Map<String, Any> {
        return mapOf(
            "id" to flock.id,
            "ownerId" to flock.ownerId,
            "fatherId" to (flock.fatherId ?: ""),
            "motherId" to (flock.motherId ?: ""),
            "type" to flock.type.name,
            "name" to flock.name,
            "breed" to (flock.breed ?: ""),
            "weight" to (flock.weight ?: 0.0),
            "certified" to flock.certified,
            "verified" to flock.verified,
            "createdAt" to flock.createdAt.time,
            "updatedAt" to flock.updatedAt.time
        )
    }
}
