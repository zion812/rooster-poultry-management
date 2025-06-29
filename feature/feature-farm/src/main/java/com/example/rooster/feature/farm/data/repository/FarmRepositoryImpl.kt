package com.example.rooster.feature.farm.data.repository

import com.example.rooster.core.common.Result // Ensure this is the correct Result type
import com.example.rooster.feature.farm.data.local.FlockDao
import com.example.rooster.feature.farm.data.local.FlockEntity
import com.example.rooster.feature.farm.data.remote.FirebaseFarmDataSource
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
// Removed kotlin.Result import to avoid ambiguity if core.common.Result is used.
import com.example.rooster.feature.farm.data.local.LineageDao
import com.example.rooster.feature.farm.data.local.LineageLinkEntity
import com.example.rooster.feature.farm.data.local.RelationshipType
import com.example.rooster.feature.farm.domain.model.LineageInfo
import com.example.rooster.feature.farm.domain.model.LineageNode
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import timber.log.Timber


class FarmRepositoryImpl @Inject constructor(
    private val flockDao: FlockDao,
    private val lineageDao: LineageDao,
    private val remoteDataSource: FirebaseFarmDataSource
) : FarmRepository {

    override fun getFlockById(id: String): Flow<Result<Flock?>> { // Return type includes nullable Flock
        return combine(
            flockDao.getById(id), // Corrected: Use flockDao
            remoteDataSource.getFlockRealTime(id) // Assuming this returns Flow<Result<Map<String, Any>>> from Firebase
        ) { localEntity, remoteFirebaseResult -> // remoteFirebaseResult is Result<Map<String, Any>> from FirebaseFarmDataSource

            // The remoteDataSource.getFlockRealTime(id) should ideally already return Flow<com.example.rooster.core.common.Result<Map<String, Any>?>>
            // For this example, I'll assume remoteFirebaseResult is of type com.example.rooster.core.common.Result<Map<String, Any>?>
            // If it's kotlin.Result, it needs conversion. Let's assume it's already the project's standard Result.

            when (remoteFirebaseResult) {
                is Result.Success -> {
                    val remoteDataMap = remoteFirebaseResult.data
                    if (remoteDataMap != null) {
                        val remoteFlockDomain = mapRemoteToFlock(remoteDataMap)
                        if (localEntity?.needsSync == true) {
                            Timber.w("Flock ID $id: Local data has unsynced changes. Emitting local data and skipping remote cache update for this emission.")
                            Result.Success(mapEntityToFlock(localEntity))
                        } else {
                            // Local is synced or doesn't exist; update cache with remote data.
                            val entityToCache = mapFlockToEntity(remoteFlockDomain, needsSync = false)
                            try {
                                flockDao.insert(entityToCache)
                                Timber.d("Flock ID $id: Cache updated from remote data for flock.")
                            } catch (e: Exception) {
                                Timber.e(e, "Flock ID $id: Error updating cache from remote data.")
                            }
                            Result.Success(remoteFlockDomain)
                        }
                    } else { // Remote success but remoteDataMap is null (document likely deleted remotely)
                        if (localEntity != null) {
                            if (localEntity.needsSync) { // Local unsynced and remote deleted
                                Timber.w("Flock ID $id: Remote data is null (possibly deleted), but local unsynced data exists. Emitting local.")
                                Result.Success(mapEntityToFlock(localEntity))
                            } else { // Local synced and remote deleted
                                Timber.d("Flock ID $id: Remote data is null (possibly deleted), local synced data exists. Deleting from local cache.")
                                try {
                                    flockDao.deleteById(id) // Remove from local cache
                                } catch (e: Exception) {
                                    Timber.e(e, "Flock ID $id: Error deleting from local cache after remote deletion confirmation.")
                                }
                                Result.Success(null) // Indicate not found
                            }
                        } else { // Not found locally or remotely
                            Result.Success(null)
                        }
                    }
                }
                is Result.Error -> {
                    if (localEntity != null) {
                        Timber.w(remoteFirebaseResult.exception, "Flock ID $id: Remote fetch failed. Emitting local data.")
                        Result.Success(mapEntityToFlock(localEntity))
                    } else {
                        Timber.e(remoteFirebaseResult.exception, "Flock ID $id: Remote fetch failed and no local data.")
                        Result.Error(remoteFirebaseResult.exception)
                    }
                }
                is Result.Loading -> {
                    if (localEntity != null) {
                        // Emit local data as success (stale) while remote is loading
                        Result.Success(mapEntityToFlock(localEntity))
                    } else {
                        Result.Loading // No local data, and remote is loading
                    }
                }
            }
        }
    }

    override fun getFlocksByType(type: String): Flow<Result<List<Flock>>> {
        return flockDao.getByType(type).map { entities ->
            val flocks = entities.map { entity -> mapEntityToFlock(entity) }
            Result.Success(flocks)
        }
    }

    override suspend fun registerFlock(data: FlockRegistrationData): com.example.rooster.core.common.Result<Unit> {
        return try {
            val id = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            val flock = Flock(
                id = id,
                ownerId = data.ownerId,
                fatherId = data.fatherId,
                motherId = data.motherId,
 feature/phase1-foundations-community-likes
                type = FlockType.valueOf(data.ageGroup.name.uppercase(Locale.ROOT)),
                name = "New Flock",
=======
                type = FlockType.valueOf(data.ageGroup.name.uppercase(Locale.ROOT)), // Ensure uppercase for enum
                name = "New Flock", // Consider making name part of registration data
 main
                breed = data.breed,
                weight = data.weight?.toFloat(),
                height = null,
                color = null,
                gender = null, // Consider adding to registration data
                certified = false,
                verified = false,
                verificationLevel = VerificationLevel.BASIC,
                traceable = false, // Depends on system capabilities
                ageGroup = data.ageGroup,
                dateOfBirth = null, // Consider adding to registration data
                placeOfBirth = null,
                currentAge = null, // Usually calculated
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

 feature/phase1-foundations-community-likes
            val entity = mapFlockToEntity(flock, needsSync = true)
            flockDao.insert(entity)

            try {
                val remoteData = mapFlockToRemote(flock)
                // remoteDataSource.saveFlock now returns com.example.rooster.core.common.Result<Unit>
                when (val remoteResult = remoteDataSource.saveFlock(remoteData)) {
                    is com.example.rooster.core.common.Result.Success -> {
                        flockDao.insert(mapFlockToEntity(flock, needsSync = false))
                        Timber.d("Flock ID $id: Registered and immediately synced.")
                    }
                    is com.example.rooster.core.common.Result.Error -> {
                        Timber.w(remoteResult.exception, "Flock ID $id: Registered locally, but immediate remote sync failed. Worker will retry.")
                        // Do not return error here, local registration succeeded.
                    }
                    is com.example.rooster.core.common.Result.Loading -> {
                        // This case should ideally not happen from a suspend fun like saveFlock
                        Timber.w("Flock ID $id: Remote sync returned Loading state unexpectedly.")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Flock ID $id: Exception during immediate remote sync attempt after registration.")
                // Do not return error here, local registration succeeded.
            }

            com.example.rooster.core.common.Result.Success(Unit) // Local registration is the primary success criteria here
        } catch (e: Exception) {
            Timber.e(e, "Failed to register flock locally.")
            com.example.rooster.core.common.Result.Error(e)
=======
            val entity = mapFlockToEntity(flock, needsSync = true) // New flocks always need sync
            flockDao.insert(entity)

            // Attempt immediate remote sync, but don't block/fail registration. Worker will handle it.
            try {
                val remoteData = mapFlockToRemote(flock)
                val remoteResult = remoteDataSource.saveFlock(remoteData) // Assuming saveFlock takes the Map
                if (remoteResult is Result.Success) {
                    flockDao.insert(mapFlockToEntity(flock, needsSync = false)) // Mark as synced
                    Timber.d("Flock ID $id: Registered and immediately synced.")
                } else if (remoteResult is Result.Error) {
                    Timber.w(remoteResult.exception, "Flock ID $id: Registered locally, but immediate remote sync failed. Worker will retry.")
                }
            } catch (e: Exception) {
                Timber.e(e, "Flock ID $id: Exception during immediate remote sync attempt after registration.")
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to register flock locally.")
            Result.Error(e)
 main
        }
    }

    private fun mapEntityToFlock(entity: FlockEntity): Flock {
        return Flock(
            id = entity.id,
            ownerId = entity.ownerId,
            fatherId = entity.fatherId,
            motherId = entity.motherId,
            type = try { FlockType.valueOf(entity.type.uppercase(Locale.ROOT)) } catch (e: Exception) { Timber.w("Invalid flock type in entity: ${entity.type}, defaulting to FOWL."); FlockType.FOWL },
            name = entity.name,
            breed = entity.breed,
            weight = entity.weight,
            height = null, // Populate from entity if field exists
            color = null, // Populate from entity if field exists
            gender = null, // Populate from entity if field exists
            certified = entity.certified,
            verified = entity.verified,
            verificationLevel = VerificationLevel.BASIC, // Populate from entity if field exists
            traceable = false, // Populate from entity if field exists
            ageGroup = AgeGroup.WEEKS_0_5, // Populate from entity if field exists or map from age
            dateOfBirth = null, // Populate from entity if field exists
            placeOfBirth = null, // Populate from entity if field exists
            currentAge = null,
            vaccinationStatus = VaccinationStatus.NOT_STARTED, // Populate from entity
            lastVaccinationDate = null, // Populate from entity
            healthStatus = HealthStatus.GOOD, // Populate from entity
            lastHealthCheck = null, // Populate from entity
            identification = null, // Populate from entity
            registryNumber = null, // Populate from entity
            proofs = null, // Populate from entity
            specialty = null, // Populate from entity
            productivityScore = null, // Populate from entity
            growthRate = null, // Populate from entity
            feedConversionRatio = null, // Populate from entity
            status = FlockStatus.ACTIVE, // Populate from entity
            forSale = false, // Populate from entity
            price = null, // Populate from entity
            purpose = null, // Populate from entity
            createdAt = Date(entity.createdAt),
            updatedAt = Date(entity.updatedAt)
            // needsSync is a local concern, not part of domain model
        )
    }

    private fun mapFlockToEntity(flock: Flock, needsSync: Boolean = true): FlockEntity {
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
            // Ensure all fields from Flock model that exist in FlockEntity are mapped here
        )
    }

    private fun mapRemoteToFlock(remote: Map<String, Any>): Flock {
         val flockTypeString = remote["type"] as? String ?: FlockType.FOWL.name
        return Flock(
            id = remote["id"] as? String ?: UUID.randomUUID().toString(),
            ownerId = remote["ownerId"] as? String ?: "unknown_owner",
            fatherId = remote["fatherId"] as? String,
            motherId = remote["motherId"] as? String,
            type = try { FlockType.valueOf(flockTypeString.uppercase(Locale.ROOT)) } catch (e: Exception) { Timber.w("Invalid flock type from remote: $flockTypeString, defaulting to FOWL."); FlockType.FOWL },
            name = remote["name"] as? String ?: "Unnamed Flock",
            breed = remote["breed"] as? String,
            weight = (remote["weight"] as? Number)?.toFloat(),
            height = (remote["height"] as? Number)?.toFloat(), // Assuming height might come from remote
            color = remote["color"] as? String, // Assuming color might come from remote
            gender = remote["gender"] as? String, // Assuming gender might come from remote
            certified = remote["certified"] as? Boolean ?: false,
            verified = remote["verified"] as? Boolean ?: false,
            verificationLevel = (remote["verificationLevel"] as? String)?.let { try { VerificationLevel.valueOf(it.uppercase(Locale.ROOT)) } catch (e: Exception) { VerificationLevel.BASIC } } ?: VerificationLevel.BASIC,
            traceable = remote["traceable"] as? Boolean ?: false,
            ageGroup = (remote["ageGroup"] as? String)?.let { try { AgeGroup.valueOf(it.uppercase(Locale.ROOT)) } catch (e: Exception) { AgeGroup.UNKNOWN } } ?: AgeGroup.UNKNOWN, // Add UNKNOWN to AgeGroup or handle
            dateOfBirth = (remote["dateOfBirth"] as? Long)?.let { Date(it) },
            placeOfBirth = remote["placeOfBirth"] as? String,
            currentAge = (remote["currentAge"] as? Number)?.toInt(), // Assuming currentAge might come from remote
            vaccinationStatus = (remote["vaccinationStatus"] as? String)?.let { try { VaccinationStatus.valueOf(it.uppercase(Locale.ROOT)) } catch (e: Exception) { VaccinationStatus.UNKNOWN } } ?: VaccinationStatus.UNKNOWN, // Add UNKNOWN
            lastVaccinationDate = (remote["lastVaccinationDate"] as? Long)?.let { Date(it) },
            healthStatus = (remote["healthStatus"] as? String)?.let { try { HealthStatus.valueOf(it.uppercase(Locale.ROOT)) } catch (e: Exception) { HealthStatus.UNKNOWN } } ?: HealthStatus.UNKNOWN, // Add UNKNOWN
            lastHealthCheck = (remote["lastHealthCheck"] as? Long)?.let { Date(it) },
            identification = remote["identification"] as? String,
            registryNumber = remote["registryNumber"] as? String,
            proofs = remote["proofs"] as? List<String>, // Assuming proofs are list of strings (e.g. URLs)
            specialty = remote["specialty"] as? String,
            productivityScore = (remote["productivityScore"] as? Number)?.toDouble(),
            growthRate = (remote["growthRate"] as? Number)?.toDouble(),
            feedConversionRatio = (remote["feedConversionRatio"] as? Number)?.toDouble(),
            status = (remote["status"] as? String)?.let { try { FlockStatus.valueOf(it.uppercase(Locale.ROOT)) } catch (e: Exception) { FlockStatus.UNKNOWN } } ?: FlockStatus.UNKNOWN, // Add UNKNOWN
            forSale = remote["forSale"] as? Boolean ?: false,
            price = (remote["price"] as? Number)?.toDouble(),
            purpose = remote["purpose"] as? String,
            createdAt = Date((remote["createdAt"] as? Long) ?: (remote["createdAt"] as? Timestamp)?.seconds?.times(1000) ?: System.currentTimeMillis()), // Handle Firebase Timestamp
            updatedAt = Date((remote["updatedAt"] as? Long) ?: (remote["updatedAt"] as? Timestamp)?.seconds?.times(1000) ?: System.currentTimeMillis())  // Handle Firebase Timestamp
        )
    }
     // Helper for Firebase Timestamp, assuming it might be used in remote map
    private data class Timestamp(val seconds: Long = 0, val nanoseconds: Int = 0)


    private fun mapFlockToRemote(flock: Flock): Map<String, Any?> { // Allow nulls for optional fields
        return mapOf(
            "id" to flock.id,
            "ownerId" to flock.ownerId,
            "fatherId" to flock.fatherId,
            "motherId" to flock.motherId,
            "type" to flock.type.name,
            "name" to flock.name,
            "breed" to flock.breed,
            "weight" to flock.weight,
            "height" to flock.height,
            "color" to flock.color,
            "gender" to flock.gender,
            "certified" to flock.certified,
            "verified" to flock.verified,
            "verificationLevel" to flock.verificationLevel.name,
            "traceable" to flock.traceable,
            "ageGroup" to flock.ageGroup.name,
            "dateOfBirth" to flock.dateOfBirth?.time,
            "placeOfBirth" to flock.placeOfBirth,
            "currentAge" to flock.currentAge,
            "vaccinationStatus" to flock.vaccinationStatus.name,
            "lastVaccinationDate" to flock.lastVaccinationDate?.time,
            "healthStatus" to flock.healthStatus.name,
            "lastHealthCheck" to flock.lastHealthCheck?.time,
            "identification" to flock.identification,
            "registryNumber" to flock.registryNumber,
            "proofs" to flock.proofs,
            "specialty" to flock.specialty,
            "productivityScore" to flock.productivityScore,
            "growthRate" to flock.growthRate,
            "feedConversionRatio" to flock.feedConversionRatio,
            "status" to flock.status.name,
            "forSale" to flock.forSale,
            "price" to flock.price,
            "purpose" to flock.purpose,
            "createdAt" to flock.createdAt.time, // Consider FieldValue.serverTimestamp() for new creates
            "updatedAt" to flock.updatedAt.time // Consider FieldValue.serverTimestamp()
        ).filterValues { it != null } // Remove null values before sending to Firebase if desired
    }

    override fun getLineageInfo(flockId: String, depthUp: Int, depthDown: Int): Flow<Result<LineageInfo?>> = flow {
        emit(Result.Loading)
        try {
            val centralFlockEntity = flockDao.getById(flockId).firstOrNull()
            if (centralFlockEntity == null) {
                emit(Result.Success(null))
                return@flow
            }
            val centralNode = mapEntityToLineageNode(centralFlockEntity)

            coroutineScope {
                val fatherNodeDeferred = if (depthUp > 0) async { buildAncestorTree(centralFlockEntity.fatherId, RelationshipType.FATHER, 0, depthUp) } else async { null }
                val motherNodeDeferred = if (depthUp > 0) async { buildAncestorTree(centralFlockEntity.motherId, RelationshipType.MOTHER, 0, depthUp) } else async { null }
                val childrenNodesDeferred = if (depthDown > 0) async { buildDescendantList(flockId, 0, depthDown) } else async { emptyList<LineageNode>() }

                centralNode.father = fatherNodeDeferred.await()
                centralNode.mother = motherNodeDeferred.await()
                centralNode.children = childrenNodesDeferred.await()
            }

            val lineageInfo = LineageInfo(
                centralFlockId = flockId,
                centralFlockNode = centralNode,
                generationDepthUp = depthUp, // This should reflect actual depth found
                generationDepthDown = depthDown // This should reflect actual depth found
            )
            emit(Result.Success(lineageInfo))
        } catch (e: Exception) {
            Timber.e(e, "Error getting lineage info for flock $flockId")
            emit(Result.Error(e))
        }
    }

    private suspend fun buildAncestorTree(
        flockId: String?, // Changed to nullable
        relationshipType: RelationshipType, // Not strictly needed if we just get father/mother IDs
        currentDepth: Int,
        maxDepth: Int,
        visited: MutableSet<String> = mutableSetOf()
    ): LineageNode? {
        if (flockId == null || currentDepth >= maxDepth || flockId in visited) {
            if (flockId != null && flockId in visited) Timber.d("Cycle detected or already visited ancestor: $flockId")
            return null
        }
        visited.add(flockId)

        val flockEntity = flockDao.getById(flockId).firstOrNull()
        if (flockEntity == null) {
            Timber.w("Ancestor flock with ID $flockId not found (broken lineage link). Relationship type resolution for this path will terminate.")
            return null // Broken link
        }
        val node = mapEntityToLineageNode(flockEntity)

        node.father = buildAncestorTree(flockEntity.fatherId, RelationshipType.FATHER, currentDepth + 1, maxDepth, visited)
        node.mother = buildAncestorTree(flockEntity.motherId, RelationshipType.MOTHER, currentDepth + 1, maxDepth, visited)

        visited.remove(flockId) // Remove from visited for this path to allow it in other branches
        return node
    }

    private suspend fun buildDescendantList(
        parentFlockId: String,
        currentDepth: Int,
        maxDepth: Int,
        visited: MutableSet<String> = mutableSetOf()
    ): List<LineageNode> {
        if (currentDepth >= maxDepth || parentFlockId in visited) {
            if (parentFlockId in visited) Timber.d("Cycle detected or already visited descendant: $parentFlockId")
            return emptyList()
        }
        visited.add(parentFlockId)

        val childrenLinks = lineageDao.getChildren(parentFlockId).firstOrNull() ?: emptyList()
        val childrenNodes = mutableListOf<LineageNode>()

        coroutineScope { // Use coroutineScope for concurrent fetching of children's details
            val childJobs = childrenLinks.map { link ->
                async {
                    val childEntity = flockDao.getById(link.childFlockId).firstOrNull()
                    if (childEntity != null) {
                        val childNode = mapEntityToLineageNode(childEntity)
                        childNode.children = buildDescendantList(link.childFlockId, currentDepth + 1, maxDepth, visited.toMutableSet()) // Pass copy of visited for parallel branches
                        childNode
                    } else {
                        Timber.w("Child flock with ID ${link.childFlockId} not found (broken lineage link).")
                        null
                    }
                }
            }
            childrenNodes.addAll(childJobs.awaitAll().filterNotNull())
        }
        visited.remove(parentFlockId) // Remove from visited for this path
        return childrenNodes
    }

    private fun mapEntityToLineageNode(entity: FlockEntity): LineageNode {
        val flockType = try { FlockType.valueOf(entity.type.uppercase(Locale.ROOT)) } catch (e: IllegalArgumentException) { Timber.w("Invalid flock type in lineage node: ${entity.type}"); FlockType.UNKNOWN }
        return LineageNode(
            flockId = entity.id,
            name = entity.name ?: "N/A",
            breed = entity.breed,
            type = flockType,
            profileImageUrl = null // TODO: Populate from FlockEntity if it has a primary image URL
        )
    }

    override suspend fun addParentChildLink(childFlockId: String, parentFlockId: String, type: RelationshipType): Result<Unit> {
        return try {
            val link = LineageLinkEntity(
                childFlockId = childFlockId,
                parentFlockId = parentFlockId,
                relationshipType = type,
                needsSync = true
            )
            lineageDao.insertLink(link)
            Timber.d("Local lineage link added: Child $childFlockId, Parent $parentFlockId, Type $type")

            val remoteResult = remoteDataSource.saveLineageLink(link)
            if (remoteResult is Result.Success) {
                lineageDao.insertLink(link.copy(needsSync = false))
                Timber.d("Lineage link synced to remote: Child $childFlockId, Parent $parentFlockId")
            } else if (remoteResult is Result.Error) {
                 Timber.w(remoteResult.exception, "Failed to sync lineage link Child $childFlockId, Parent $parentFlockId to remote. Worker will retry.")
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error adding parent-child link locally for Child $childFlockId, Parent $parentFlockId")
            Result.Error(e)
        }
    }

    override suspend fun removeParentChildLink(childFlockId: String, parentFlockId: String, type: RelationshipType): Result<Unit> {
        return try {
            lineageDao.deleteLink(childFlockId, parentFlockId, type)
            Timber.d("Local lineage link removed: Child $childFlockId, Parent $parentFlockId, Type $type")

            // TODO: Implement remote deletion for lineage links. This might involve setting a 'deleted' flag and syncing,
            // or direct deletion if the remote source supports it and it's safe.
            // For now, only local deletion is implemented. The link will remain on the server.
            // Consider how to handle this for sync (e.g. a 'deleted_links' table or field).
            val remoteDeleteResult = remoteDataSource.deleteLineageLink(childFlockId, parentFlockId, type.name)
            if (remoteDeleteResult is Result.Error) {
                Timber.w(remoteDeleteResult.exception, "Failed to delete lineage link from remote: Child $childFlockId, Parent $parentFlockId. Needs manual reconciliation or improved sync for deletes.")
                // Not returning error from this as local delete was successful.
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error removing parent-child link locally for Child $childFlockId, Parent $parentFlockId")
            Result.Error(e)
        }
    }
}
