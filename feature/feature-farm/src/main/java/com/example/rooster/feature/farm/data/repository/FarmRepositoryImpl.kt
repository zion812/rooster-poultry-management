package com.example.rooster.feature.farm.data.repository

import com.example.rooster.feature.farm.data.local.FlockDao
import com.example.rooster.feature.farm.data.local.FlockEntity
import com.example.rooster.feature.farm.data.remote.FirebaseFarmDataSource // Changed from FarmRemoteDataSource
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
import com.example.rooster.feature.farm.data.local.LineageDao // Added
import com.example.rooster.feature.farm.data.local.LineageLinkEntity
import com.example.rooster.feature.farm.data.local.RelationshipType
import com.example.rooster.feature.farm.domain.model.LineageInfo
import com.example.rooster.feature.farm.domain.model.LineageNode
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow


class FarmRepositoryImpl @Inject constructor(
    private val flockDao: FlockDao, // Renamed dao to flockDao for clarity
    private val lineageDao: LineageDao, // Added LineageDao
    private val remoteDataSource: FirebaseFarmDataSource
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

                        // Update local cache asynchronously, marking it as synced
                        val entity = mapFlockToEntity(flock)
                        try {
                            flockDao.insert(entity) // Use flockDao
                        } catch (e: Exception) {
                            // Log but don't fail on cache update
                        }

                        Result.success(flock)
                    } else {
                        // Fall back to local data
                        localEntity?.let { entity ->
                            val flock = mapEntityToFlock(entity)
                            Result.success(flock)
                        } ?: Result.failure(Exception("Flock not found: $id")) // Added ID to message
                    }
                }

                localEntity != null -> {
                    // Use local data when remote fails
                    val flock = mapEntityToFlock(localEntity)
                    Result.success(flock)
                }

                else -> Result.failure(Exception("Flock not found: $id")) // Added ID to message
            }
        }
    }

    override fun getFlocksByType(type: String): Flow<Result<List<Flock>>> {
        // For now, return local data - can be enhanced with remote filtering
        return flockDao.getByType(type).map { entities -> // Use flockDao
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
            val entity = mapFlockToEntity(flock) // needsSync will default to true
            flockDao.insert(entity) // Use flockDao

            // Save to remote (async - don't block on failure)
            // The worker will pick it up if this fails or if we choose not to sync immediately.
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

    // Helper mapping functions (incorporating robustness from NetworkAwareFarmRepository)
    private fun mapEntityToFlock(entity: FlockEntity): Flock {
        return Flock(
            id = entity.id,
            ownerId = entity.ownerId,
            fatherId = entity.fatherId,
            motherId = entity.motherId,
            type = try {
                FlockType.valueOf(entity.type)
            } catch (e: Exception) {
                FlockType.FOWL // Default fallback
            },
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

    // Added needsSync parameter, defaulting to true for new entities.
    // When data comes from remote and is being cached, needsSync should be set to false.
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
            needsSync = needsSync // Use the passed or default value
        )
    }

    private fun mapRemoteToFlock(remote: Map<String, Any>): Flock {
        return Flock(
            id = remote["id"] as? String ?: UUID.randomUUID().toString(), // Provide a fallback ID if missing
            ownerId = remote["ownerId"] as? String ?: "unknown_owner", // Fallback
            fatherId = remote["fatherId"] as? String,
            motherId = remote["motherId"] as? String,
            type = try {
                FlockType.valueOf(remote["type"] as? String ?: FlockType.FOWL.name)
            } catch (e: Exception) {
                FlockType.FOWL // Default fallback
            },
            name = remote["name"] as? String ?: "Unnamed Flock", // Fallback
            breed = remote["breed"] as? String,
            weight = (remote["weight"] as? Number)?.toFloat(),
            height = null, // Assuming these are not in remote or handled elsewhere
            color = null,
            gender = null,
            certified = remote["certified"] as? Boolean ?: false,
            verified = remote["verified"] as? Boolean ?: false,
            verificationLevel = VerificationLevel.BASIC, // Default, assuming not in this specific remote map
            traceable = false, // Default
            ageGroup = AgeGroup.WEEKS_0_5, // Default
            dateOfBirth = (remote["dateOfBirth"] as? Long)?.let { Date(it) }, // Example if present
            placeOfBirth = remote["placeOfBirth"] as? String, // Example if present
            currentAge = null, // Usually calculated or from a different source
            vaccinationStatus = VaccinationStatus.NOT_STARTED, // Default
            lastVaccinationDate = (remote["lastVaccinationDate"] as? Long)?.let { Date(it) }, // Example
            healthStatus = HealthStatus.GOOD, // Default
            lastHealthCheck = (remote["lastHealthCheck"] as? Long)?.let { Date(it) }, // Example
            identification = remote["identification"] as? String, // Example
            registryNumber = remote["registryNumber"] as? String, // Example
            proofs = null, // Assuming not in this specific remote map
            specialty = null,
            productivityScore = null,
            growthRate = null,
            feedConversionRatio = null,
            status = FlockStatus.ACTIVE, // Default
            forSale = remote["forSale"] as? Boolean ?: false, // Example
            price = (remote["price"] as? Number)?.toDouble(), // Example
            purpose = remote["purpose"] as? String, // Example
            createdAt = Date((remote["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()),
            updatedAt = Date((remote["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis())
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

    // --- Lineage Methods ---

    override fun getLineageInfo(flockId: String, depthUp: Int, depthDown: Int): Flow<Result<LineageInfo?>> = flow {
        // Using core.common.Result for consistency if it's the project standard
        emit(com.example.rooster.core.common.Result.Loading)
        try {
            val centralFlockEntity = flockDao.getById(flockId).firstOrNull() // Fetch once for central node
            if (centralFlockEntity == null) {
                emit(com.example.rooster.core.common.Result.Success(null))
                return@flow
            }
            // Basic node for now, recursive tree building is complex and needs careful depth management
            val centralNode = mapEntityToLineageNode(centralFlockEntity)

            // Placeholder: Actual recursive fetching of parents and children needs to be implemented
 jules/arch-assessment-1
            // Build the tree structure
            coroutineScope {
                val fatherNodeDeferred = if (depthUp > 0) async { buildAncestorTree(flockId, RelationshipType.FATHER, 0, depthUp) } else async { null }
                val motherNodeDeferred = if (depthUp > 0) async { buildAncestorTree(flockId, RelationshipType.MOTHER, 0, depthUp) } else async { null }
                val childrenNodesDeferred = if (depthDown > 0) async { buildDescendantList(flockId, 0, depthDown) } else async { emptyList<LineageNode>() }

                centralNode.father = fatherNodeDeferred.await()
                centralNode.mother = motherNodeDeferred.await()
                // The LineageNode.children is for direct children of *that* node in a display tree.
                // The LineageInfo might want a flat list of all descendants up to a certain depth, or a tree.
                // For now, populating direct children of the central node.
                centralNode.children = childrenNodesDeferred.await()
            }

            // Actual depth achieved might be less than requested if lineage ends or maxDepth hit.
            // This calculation would be more complex if returning the full tree structure within LineageInfo.
            // For now, using requested depth as a simplification.
            val lineageInfo = LineageInfo(
                centralFlockId = flockId,
                centralFlockNode = centralNode,
                generationDepthUp = depthUp,
                generationDepthDown = depthDown
=======
            // For parents (depthUp):
            // Call a recursive helper function: fetchAncestors(centralNode, depthUp)
            // For children (depthDown):
            // Call a recursive helper function: fetchDescendants(centralNode, depthDown)
            // These helpers would use lineageDao and flockDao to build the tree structure.

            val lineageInfo = LineageInfo(
                centralFlockId = flockId,
                centralFlockNode = centralNode, // This node will be populated by recursive helpers
                generationDepthUp = 0, // Actual depth achieved by fetchAncestors
                generationDepthDown = 0 // Actual depth achieved by fetchDescendants
 main
            )
            emit(com.example.rooster.core.common.Result.Success(lineageInfo))
        } catch (e: Exception) {
            emit(com.example.rooster.core.common.Result.Error(e))
        }
    }

 jules/arch-assessment-1
    private suspend fun buildAncestorTree(
        childFlockId: String,
        relationshipType: RelationshipType,
        currentDepth: Int,
        maxDepth: Int,
        visited: MutableSet<String> = mutableSetOf()
    ): LineageNode? {
        if (currentDepth >= maxDepth || childFlockId in visited) {
            return null
        }
        visited.add(childFlockId) // Add before fetching to break cycles early for this path

        val parentLink = lineageDao.getSpecificParentOfType(childFlockId, relationshipType)
        val parentFlockId = parentLink?.parentFlockId ?: return null

        val parentEntity = flockDao.getById(parentFlockId).firstOrNull() ?: return null
        val parentNode = mapEntityToLineageNode(parentEntity)

        // Recursively find grandparents
        parentNode.father = buildAncestorTree(parentFlockId, RelationshipType.FATHER, currentDepth + 1, maxDepth, visited.toMutableSet()) // Pass copy of visited
        parentNode.mother = buildAncestorTree(parentFlockId, RelationshipType.MOTHER, currentDepth + 1, maxDepth, visited.toMutableSet()) // Pass copy of visited

        return parentNode
    }

    private suspend fun buildDescendantList(
        parentFlockId: String,
        currentDepth: Int,
        maxDepth: Int,
        visited: MutableSet<String> = mutableSetOf()
    ): List<LineageNode> {
        if (currentDepth >= maxDepth || parentFlockId in visited) {
            return emptyList()
        }
        visited.add(parentFlockId)

        val childrenLinks = lineageDao.getChildren(parentFlockId).firstOrNull() ?: emptyList()
        val childrenNodes = mutableListOf<LineageNode>()

        for (link in childrenLinks) {
            val childEntity = flockDao.getById(link.childFlockId).firstOrNull()
            if (childEntity != null) {
                val childNode = mapEntityToLineageNode(childEntity)
                // Recursively find grandchildren
                childNode.children = buildDescendantList(link.childFlockId, currentDepth + 1, maxDepth, visited.toMutableSet()) // Pass copy
                childrenNodes.add(childNode)
            }
        }
        return childrenNodes
    }


=======
 main
    private fun mapEntityToLineageNode(entity: FlockEntity): LineageNode {
        // Ensure FlockType.valueOf is handled safely if entity.type could be invalid
        val flockType = try { FlockType.valueOf(entity.type) } catch (e: IllegalArgumentException) { FlockType.UNKNOWN }

        return LineageNode(
            flockId = entity.id,
            name = entity.name,
            breed = entity.breed,
            type = flockType,
            profileImageUrl = null // TODO: Link to actual image URL if available for a flock
        )
    }

    override suspend fun addParentChildLink(childFlockId: String, parentFlockId: String, type: RelationshipType): Result<Unit> {
        return try {
            val link = LineageLinkEntity(
                childFlockId = childFlockId,
                parentFlockId = parentFlockId,
                relationshipType = type,
                needsSync = true // New link needs to be synced
            )
            lineageDao.insertLink(link)

            // Attempt to save to remote data source immediately
            val remoteResult = remoteDataSource.saveLineageLink(link) // Assuming this method exists
            if (remoteResult.isSuccess) {
                lineageDao.insertLink(link.copy(needsSync = false)) // Update local to synced
            }
            // If remote save fails, needsSync remains true for the worker.

            com.example.rooster.core.common.Result.Success(Unit)
        } catch (e: Exception) {
            com.example.rooster.core.common.Result.Error(e)
        }
    }

    override suspend fun removeParentChildLink(childFlockId: String, parentFlockId: String, type: RelationshipType): Result<Unit> {
        return try {
            // For removal, we might need a soft delete + sync approach or direct delete
            // For now, direct local delete then attempt remote.
            lineageDao.deleteLink(childFlockId, parentFlockId, type)

            // Attempt to delete from remote data source
            remoteDataSource.deleteLineageLink(childFlockId, parentFlockId, type.name) // Assuming this method exists
            // If remote delete fails, there's no straightforward "needsSync" for deletion with current model.
            // This might require a "deleted" flag on LineageLinkEntity or a separate "pendingDeletes" table for worker.

            com.example.rooster.core.common.Result.Success(Unit)
        } catch (e: Exception) {
            com.example.rooster.core.common.Result.Error(e)
        }
    }
}
