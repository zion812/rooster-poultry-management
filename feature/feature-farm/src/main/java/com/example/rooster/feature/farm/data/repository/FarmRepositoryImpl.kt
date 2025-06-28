package com.example.rooster.feature.farm.data.repository

import com.example.rooster.feature.farm.data.local.FlockDao
import com.example.rooster.feature.farm.data.local.FlockEntity
import com.example.rooster.feature.farm.data.remote.IFarmRemoteDataSource // Changed to interface
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
import timber.log.Timber


class FarmRepositoryImpl @Inject constructor(
    private val flockDao: FlockDao,
    private val lineageDao: LineageDao,
    private val remoteDataSource: IFarmRemoteDataSource // Changed to interface
) : FarmRepository {

    override fun getFlockById(id: String): Flow<Result<Flock?>> { // Return type changed to Flock?
        // Strategy: Emit local, then fetch remote, save to local (triggers local flow update)
        // This uses a simplified localBackedRemoteResource-like pattern directly here
        // as the remote source now returns Flow<Result<Flock?>>.
        // A more robust helper could be made for Flow<Result<T>> sources.

        val localFlow = flockDao.getById(id).map { entity -> entity?.let { mapEntityToFlock(it) } }

        return localFlow.combine(remoteDataSource.getFlockStream(id)) { localFlock, remoteFetchResult ->
            when (remoteFetchResult) {
                is Result.Success -> {
                    val remoteFlock = remoteFetchResult.data
                    if (remoteFlock != null) {
                        // Remote has data, update local cache and return remote data
                        try {
                            flockDao.insert(mapFlockToEntity(remoteFlock, needsSync = false))
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to cache remote flock: ${remoteFlock.id}")
                        }
                        Result.Success(remoteFlock)
                    } else {
                        // Remote has no data (e.g. 404), rely on local
                        Result.Success(localFlock)
                    }
                }
                is Result.Error -> {
                    // Remote fetch failed, return local data if available, plus error
                    Timber.e(remoteFetchResult.exception, "Remote fetch failed for flock $id, serving local: $localFlock")
                    if (localFlock != null) {
                         Result.Success(localFlock) // Or Result.Error with localFlock as stale data
                    } else {
                         Result.Error(remoteFetchResult.exception)
                    }
                }
                is Result.Loading -> {
                    if (localFlock != null) Result.Success(localFlock) // Show local while remote is loading
                    else Result.Loading
                }
            }
        }.onStart { emit(Result.Loading) } // Start with loading
         .catch { e -> Timber.e(e, "Error in getFlockById flow"); emit(Result.Error(e)) }
         .flowOn(Dispatchers.IO)
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

            // Save to remote
            val remoteResult = remoteDataSource.saveFlock(flock)
            if (remoteResult is Result.Success) {
                // Mark as synced if remote save is successful
                flockDao.insert(entity.copy(needsSync = false))
            }
            // If remote save fails, needsSync remains true for worker.
            // The overall result depends on whether local save is enough or remote must succeed.
            // For now, local success is primary.
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Mappers are now more direct as remote source deals with domain model or ParseObject directly
    // mapRemoteToFlock is no longer needed here as remoteDataSource returns domain model Flow<Result<Flock?>>

    private fun mapEntityToFlock(entity: FlockEntity): Flock {
        return Flock(
            id = entity.id,
            ownerId = entity.ownerId, // This should align with ParseUser objectId
            fatherId = entity.fatherId, // This should align with ParseFlock objectId
            motherId = entity.motherId, // This should align with ParseFlock objectId
            type = try { FlockType.valueOf(entity.type) } catch (e: Exception) { FlockType.UNKNOWN },
            name = entity.name,
            breed = entity.breed,
            weight = entity.weight,
            // Map other fields from FlockEntity to Flock domain model
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
            height = null, // Add if in FlockEntity
            color = null,  // Add if in FlockEntity
            gender = null, // Add if in FlockEntity & convert from String
            certified = entity.certified,
            verified = entity.verified,
            verificationLevel = VerificationLevel.BASIC, // Add if in FlockEntity & convert
            traceable = false, // Add if in FlockEntity
            ageGroup = AgeGroup.WEEKS_0_5, // Add if in FlockEntity & convert
            dateOfBirth = Date(entity.createdAt), // Placeholder, map actual if exists
            placeOfBirth = null, // Add if in FlockEntity
            currentAge = null, // Calculated
            vaccinationStatus = VaccinationStatus.NOT_STARTED, // Add if in FlockEntity & convert
            lastVaccinationDate = null, // Add if in FlockEntity
            healthStatus = HealthStatus.GOOD, // Add if in FlockEntity & convert
            lastHealthCheck = null, // Add if in FlockEntity
            identification = null, // Add if in FlockEntity
            registryNumber = null, // Add if in FlockEntity
            proofs = emptyList(), // Add if in FlockEntity (List<String>)
            specialty = null, // Add if in FlockEntity
            productivityScore = null, // Add if in FlockEntity
            growthRate = null, // Add if in FlockEntity
            feedConversionRatio = null, // Add if in FlockEntity
            status = FlockStatus.ACTIVE, // Add if in FlockEntity & convert
            forSale = false, // Add if in FlockEntity
            price = null, // Add if in FlockEntity
            purpose = emptyList(), // Add if in FlockEntity (List<Purpose>)
            createdAt = Date(entity.createdAt),
            updatedAt = Date(entity.updatedAt)
        )
    }

    private fun mapFlockToEntity(flock: Flock, needsSync: Boolean = true): FlockEntity {
        // Ensure all relevant Flock domain model fields are mapped to FlockEntity
        return FlockEntity(
            id = flock.id.ifBlank { UUID.randomUUID().toString() },
            ownerId = flock.ownerId, // Ensure this is just the ID string
            fatherId = flock.fatherId,
            motherId = flock.motherId,
            type = flock.type.name,
            name = flock.name,
            breed = flock.breed,
            weight = flock.weight,
            certified = flock.certified,
            verified = flock.verified,
            createdAt = flock.createdAt.time,
            // Map other fields from Flock domain model to FlockEntity
            // For example:
            // fatherId = flock.fatherId, (ensure these are just IDs if entity stores them so)
            // motherId = flock.motherId,
            type = flock.type.name,
            name = flock.name,
            breed = flock.breed,
            weight = flock.weight,
            certified = flock.certified,
            verified = flock.verified,
            // ... map all other relevant fields from Flock domain to FlockEntity ...
            createdAt = flock.createdAt.time,
            updatedAt = flock.updatedAt.time,
            needsSync = needsSync
        )
    }

    // mapFlockToRemote is no longer needed here if ParseFarmDataSource handles mapping from domain to ParseObject
    // If ParseFarmDataSource expects a Map, this can be kept or adapted.
    // Assuming ParseFarmDataSource.saveFlock now takes a Flock domain model.
    /*
    private fun mapFlockToRemote(flock: Flock): Map<String, Any> { // This would be mapFlockToParseObjectData
        return mapOf(
            "id" to flock.id, // Parse handles objectId, this might be a custom ID field if used
            "ownerId" to flock.ownerId, // This should be a Pointer in Parse
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
            )
            emit(com.example.rooster.core.common.Result.Success(lineageInfo))
        } catch (e: Exception) {
            emit(com.example.rooster.core.common.Result.Error(e))
        }
    }

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
