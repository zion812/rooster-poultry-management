package com.example.rooster.feature.farm.data.mapper

import com.example.rooster.feature.farm.data.local.FlockEntity
import com.example.rooster.feature.farm.domain.model.*
import java.util.*

/**
 * Mapper to convert between different representations of Flock data
 * - Domain model (business logic)
 * - Entity (local database)
 * - Remote data (API responses)
 */
object FlockMapper {

    /**
     * Convert FlockEntity (Room database) to Flock (domain model)
     */
    fun mapEntityToFlock(entity: FlockEntity): Flock {
        return Flock(
            id = entity.id,
            ownerId = entity.ownerId,
            fatherId = entity.fatherId,
            motherId = entity.motherId,
            type = FlockType.valueOf(entity.type.uppercase()),
            name = entity.name,
            breed = entity.breed,
            weight = entity.weight,
            height = null, // Not in entity
            color = null, // Not in entity
            gender = null, // Not in entity
            certified = entity.certified,
            verified = entity.verified,
            verificationLevel = VerificationLevel.UNVERIFIED, // Default
            traceable = true, // Default
            ageGroup = AgeGroup.UNKNOWN, // Default
            dateOfBirth = null, // Not in entity
            placeOfBirth = null, // Not in entity
            currentAge = null, // Not in entity
            vaccinationStatus = VaccinationStatus.UNKNOWN, // Default
            lastVaccinationDate = null, // Not in entity
            healthStatus = HealthStatus.UNKNOWN, // Default
            lastHealthCheck = null, // Not in entity
            identification = null, // Not in entity
            registryNumber = null, // Not in entity
            proofs = null, // Not in entity
            specialty = null, // Not in entity
            productivityScore = null, // Not in entity
            growthRate = null, // Not in entity
            feedConversionRatio = null, // Not in entity
            status = FlockStatus.ACTIVE, // Default
            forSale = false, // Default
            price = null, // Not in entity
            purpose = null, // Not in entity
            createdAt = Date(entity.createdAt),
            updatedAt = Date(entity.updatedAt)
        )
    }

    /**
     * Convert Flock (domain model) to FlockEntity (Room database)
     */
    fun mapFlockToEntity(flock: Flock, needsSync: Boolean = false): FlockEntity {
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
            updatedAt = flock.updatedAt.time,
            needsSync = needsSync
        )
    }

    /**
     * Convert remote API data (Map) to Flock (domain model)
     */
    fun mapRemoteToFlock(remoteData: Map<String, Any?>): Flock {
        return Flock(
            id = remoteData["id"] as? String ?: UUID.randomUUID().toString(),
            ownerId = remoteData["ownerId"] as? String ?: "",
            fatherId = remoteData["fatherId"] as? String,
            motherId = remoteData["motherId"] as? String,
            type = FlockType.valueOf((remoteData["type"] as? String ?: "FOWL").uppercase()),
            name = remoteData["name"] as? String ?: "",
            breed = remoteData["breed"] as? String,
            weight = (remoteData["weight"] as? Number)?.toFloat(),
            height = (remoteData["height"] as? Number)?.toFloat(),
            color = remoteData["color"] as? String,
            gender = (remoteData["gender"] as? String)?.let { Gender.valueOf(it.uppercase()) },
            certified = remoteData["certified"] as? Boolean ?: false,
            verified = remoteData["verified"] as? Boolean ?: false,
            verificationLevel = VerificationLevel.UNVERIFIED,
            traceable = remoteData["traceable"] as? Boolean ?: true,
            ageGroup = AgeGroup.UNKNOWN,
            dateOfBirth = (remoteData["dateOfBirth"] as? Long)?.let { Date(it) },
            placeOfBirth = remoteData["placeOfBirth"] as? String,
            currentAge = (remoteData["currentAge"] as? Number)?.toInt(),
            vaccinationStatus = VaccinationStatus.UNKNOWN,
            lastVaccinationDate = (remoteData["lastVaccinationDate"] as? Long)?.let { Date(it) },
            healthStatus = HealthStatus.UNKNOWN,
            lastHealthCheck = (remoteData["lastHealthCheck"] as? Long)?.let { Date(it) },
            identification = remoteData["identification"] as? String,
            registryNumber = remoteData["registryNumber"] as? String,
            proofs = null, // TODO: Handle list conversion
            specialty = remoteData["specialty"] as? String,
            productivityScore = (remoteData["productivityScore"] as? Number)?.toInt(),
            growthRate = (remoteData["growthRate"] as? Number)?.toDouble(),
            feedConversionRatio = (remoteData["feedConversionRatio"] as? Number)?.toDouble(),
            status = FlockStatus.ACTIVE,
            forSale = remoteData["forSale"] as? Boolean ?: false,
            price = (remoteData["price"] as? Number)?.toDouble(),
            purpose = null, // TODO: Handle list conversion
            createdAt = (remoteData["createdAt"] as? Long)?.let { Date(it) } ?: Date(),
            updatedAt = (remoteData["updatedAt"] as? Long)?.let { Date(it) } ?: Date()
        )
    }

    /**
     * Convert Flock (domain model) to remote API data (Map)
     */
    fun mapFlockToRemote(flock: Flock): Map<String, Any> {
        val dataMap = mutableMapOf<String, Any>()

        dataMap["id"] = flock.id
        dataMap["ownerId"] = flock.ownerId
        flock.fatherId?.let { dataMap["fatherId"] = it }
        flock.motherId?.let { dataMap["motherId"] = it }
        dataMap["type"] = flock.type.name
        dataMap["name"] = flock.name
        flock.breed?.let { dataMap["breed"] = it }
        flock.weight?.let { dataMap["weight"] = it }
        flock.height?.let { dataMap["height"] = it }
        flock.color?.let { dataMap["color"] = it }
        flock.gender?.let { dataMap["gender"] = it.name }
        dataMap["certified"] = flock.certified
        dataMap["verified"] = flock.verified
        dataMap["traceable"] = flock.traceable
        flock.dateOfBirth?.let { dataMap["dateOfBirth"] = it.time }
        flock.placeOfBirth?.let { dataMap["placeOfBirth"] = it }
        flock.currentAge?.let { dataMap["currentAge"] = it }
        flock.identification?.let { dataMap["identification"] = it }
        flock.registryNumber?.let { dataMap["registryNumber"] = it }
        flock.specialty?.let { dataMap["specialty"] = it }
        flock.productivityScore?.let { dataMap["productivityScore"] = it }
        flock.growthRate?.let { dataMap["growthRate"] = it }
        flock.feedConversionRatio?.let { dataMap["feedConversionRatio"] = it }
        dataMap["forSale"] = flock.forSale
        flock.price?.let { dataMap["price"] = it }
        dataMap["createdAt"] = flock.createdAt.time
        dataMap["updatedAt"] = flock.updatedAt.time

        return dataMap
    }

    /**
     * Convert a list of FlockEntity to a list of Flock
     */
    fun mapEntitiesToFlocks(entities: List<FlockEntity>): List<Flock> {
        return entities.map { mapEntityToFlock(it) }
    }

    /**
     * Convert a list of Flock to a list of FlockEntity
     */
    fun mapFlocksToEntities(flocks: List<Flock>, needsSync: Boolean = false): List<FlockEntity> {
        return flocks.map { mapFlockToEntity(it, needsSync) }
    }
}