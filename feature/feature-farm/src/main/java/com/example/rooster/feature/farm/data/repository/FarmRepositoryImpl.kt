package com.example.rooster.feature.farm.data.repository

import com.example.rooster.core.common.Result // Ensure this is the correct Result type
import com.example.rooster.feature.farm.data.local.FlockDao
import com.example.rooster.feature.farm.data.local.FlockEntity
import com.example.rooster.feature.farm.data.local.LineageDao
import com.example.rooster.feature.farm.data.local.LineageLinkEntity
import com.example.rooster.feature.farm.data.local.RelationshipType
import com.example.rooster.feature.farm.data.mapper.FlockMapper
import com.example.rooster.feature.farm.data.remote.FirebaseFarmDataSource
import com.example.rooster.feature.farm.domain.model.AgeGroup
import com.example.rooster.feature.farm.domain.model.Flock
import com.example.rooster.feature.farm.domain.model.FlockType
import com.example.rooster.feature.farm.domain.model.FlockRegistrationData
import com.example.rooster.feature.farm.domain.model.VerificationLevel
import com.example.rooster.feature.farm.domain.model.VaccinationStatus
import com.example.rooster.feature.farm.domain.model.HealthStatus
import com.example.rooster.feature.farm.domain.model.FlockStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
// Removed kotlin.Result import to avoid ambiguity if core.common.Result is used.
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
                        val remoteFlockDomain = FlockMapper.mapRemoteToFlock(remoteDataMap)
                        if (localEntity?.needsSync == true) {
                            Timber.w("Flock ID $id: Local data has unsynced changes. Emitting local data and skipping remote cache update for this emission.")
                            Result.Success(FlockMapper.mapEntityToFlock(localEntity))
                        } else {
                            // Local is synced or doesn't exist; update cache with remote data.
                            val entityToCache = FlockMapper.mapFlockToEntity(remoteFlockDomain, needsSync = false)
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
                                Result.Success(FlockMapper.mapEntityToFlock(localEntity))
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
                        Result.Success(FlockMapper.mapEntityToFlock(localEntity))
                    } else {
                        Timber.e(remoteFirebaseResult.exception, "Flock ID $id: Remote fetch failed and no local data.")
                        Result.Error(remoteFirebaseResult.exception)
                    }
                }
                is Result.Loading -> {
                    if (localEntity != null) {
                        // Emit local data as success (stale) while remote is loading
                        Result.Success(FlockMapper.mapEntityToFlock(localEntity))
                    } else {
                        Result.Loading // No local data, and remote is loading
                    }
                }
            }
        }
    }

    override fun getFlocksByType(type: String): Flow<Result<List<Flock>>> {
        return flockDao.getByType(type).map { entities ->
            val flocks = entities.map { entity -> FlockMapper.mapEntityToFlock(entity) }
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
                type = FlockType.valueOf(data.ageGroup.name.uppercase(Locale.ROOT)),
                name = "New Flock",
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

            val entity = FlockMapper.mapFlockToEntity(flock, needsSync = true)
            flockDao.insert(entity)

            try {
                val remoteData = FlockMapper.mapFlockToRemote(flock)
                val remoteResult = remoteDataSource.saveFlock(remoteData) // Assuming saveFlock takes the Map
                if (remoteResult is Result.Success) {
                    flockDao.insert(FlockMapper.mapFlockToEntity(flock, needsSync = false)) // Mark as synced
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
        }
    }

    /**
     * Fetches lineage information in an optimized manner, avoiding N+1 query problems.
     * It fetches ancestors and descendants layer by layer, then builds the tree in memory.
     */
    override fun getLineageInfo(flockId: String, depthUp: Int, depthDown: Int): Flow<Result<LineageInfo?>> = flow {
        emit(Result.Loading)
        try {
            val centralFlock = flockDao.getById(flockId).firstOrNull()
            if (centralFlock == null) {
                emit(Result.Success(null))
                return@flow
            }

            // 1. Collect all ancestor and descendant IDs iteratively.
            val flockIdsToFetch = mutableSetOf(flockId)
            val ancestorIds = if (depthUp > 0) collectAncestorIdsBulk(centralFlock, depthUp) else emptySet()
            val descendantIds = if (depthDown > 0) collectDescendantIdsBulk(flockId, depthDown) else emptySet()
            flockIdsToFetch.addAll(ancestorIds)
            flockIdsToFetch.addAll(descendantIds)

            // 2. Fetch all required flocks and lineage links in bulk.
            // Assumes flockDao.getByIds and lineageDao.getLinksForFlocks exist for efficiency.
            val allFlocksMap = flockDao.getByIds(flockIdsToFetch.toList()).firstOrNull()?.associateBy { it.id } ?: emptyMap()
            val allLinks = lineageDao.getLinksForFlocks(flockIdsToFetch.toList()).firstOrNull() ?: emptyList()
            val childrenByParentId = allLinks.groupBy { it.parentFlockId }

            // 3. Build the tree completely in-memory from the fetched maps.
            val centralNode = buildTreeFromMaps(flockId, allFlocksMap, childrenByParentId)

            val lineageInfo = LineageInfo(
                centralFlockId = flockId,
                centralFlockNode = centralNode,
                generationDepthUp = depthUp,
                generationDepthDown = depthDown
            )
            emit(Result.Success(lineageInfo))

        } catch (e: Exception) {
            Timber.e(e, "Error getting lineage info for flock $flockId")
            emit(Result.Error(e))
        }
    }

    private suspend fun collectAncestorIdsBulk(startFlock: FlockEntity, maxDepth: Int): Set<String> {
        val collectedIds = mutableSetOf<String>()
        var currentLayerIds = listOfNotNull(startFlock.fatherId, startFlock.motherId).toSet()

        for (depth in 1..maxDepth) {
            if (currentLayerIds.isEmpty()) break
            collectedIds.addAll(currentLayerIds)
            // This performs one query per layer, which is a significant improvement.
            val parents = flockDao.getByIds(currentLayerIds.toList()).firstOrNull() ?: emptyList()
            currentLayerIds = parents.flatMap { listOfNotNull(it.fatherId, it.motherId) }.toSet() - collectedIds
        }
        return collectedIds
    }

    private suspend fun collectDescendantIdsBulk(startFlockId: String, maxDepth: Int): Set<String> {
        val collectedIds = mutableSetOf<String>()
        var currentLayerIds = setOf(startFlockId)

        for (depth in 1..maxDepth) {
            if (currentLayerIds.isEmpty()) break
            // One query per layer to get the next generation of children.
            val childrenIds = lineageDao.getChildrenIds(currentLayerIds.toList()).firstOrNull()?.toSet() ?: emptySet()
            if (childrenIds.isEmpty()) break
            collectedIds.addAll(childrenIds)
            currentLayerIds = childrenIds
        }
        return collectedIds
    }

    private fun buildTreeFromMaps(
        currentFlockId: String?,
        flocksMap: Map<String, FlockEntity>,
        childrenMap: Map<String, List<LineageLinkEntity>>,
        visited: MutableSet<String> = mutableSetOf()
    ): LineageNode? {
        if (currentFlockId == null || currentFlockId in visited) return null

        val entity = flocksMap[currentFlockId] ?: return null
        visited.add(currentFlockId)

        val node = mapEntityToLineageNode(entity)

        // Recursively build using the pre-fetched maps, which is very fast.
        node.father = buildTreeFromMaps(entity.fatherId, flocksMap, childrenMap, visited)
        node.mother = buildTreeFromMaps(entity.motherId, flocksMap, childrenMap, visited)

        val childLinks = childrenMap[currentFlockId] ?: emptyList()
        node.children = childLinks.mapNotNull { link ->
            buildTreeFromMaps(link.childId, flocksMap, childrenMap, visited)
        }

        visited.remove(currentFlockId) // Allow node to be visited in different branches
        return node
    }

    private fun mapEntityToLineageNode(entity: FlockEntity): LineageNode {
        return LineageNode(
            flockId = entity.id,
            name = entity.name,
            breed = entity.breed,
            type = try { FlockType.valueOf(entity.type.uppercase(Locale.ROOT)) } catch (e: Exception) { FlockType.FOWL },
            gender = "Unknown", // Not in entity
            fatherId = entity.fatherId,
            motherId = entity.motherId
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

    override suspend fun deleteFlock(flockId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // First, delete the remote record. The Cloud Function will handle cleanup.
            val remoteResult = remoteDataSource.deleteFlock(flockId)
            if (remoteResult is Result.Error) {
                return@withContext remoteResult // Propagate the error
            }

            // If remote deletion is successful, delete the local record.
            flockDao.deleteById(flockId)

            // Also, clean up local lineage links manually as a fallback and for local consistency.
            lineageDao.deleteByChildId(flockId)
            lineageDao.deleteByParentId(flockId)

            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete flock $flockId")
            Result.Error(e)
        }
    }

    private fun mapEntityToLineageNode(entity: FlockEntity): LineageNode {
        return LineageNode(
            flockId = entity.id,
            name = entity.name,
            breed = entity.breed,
            type = try { FlockType.valueOf(entity.type.uppercase(Locale.ROOT)) } catch (e: Exception) { FlockType.FOWL },
            gender = "Unknown", // Not in entity
            fatherId = entity.fatherId,
            motherId = entity.motherId
        )
    }

    // Add a method to expose permanently failed syncs
    fun getSyncFailedFlocks(): Flow<List<Flock>> {
        return flockDao.getFlocksBySyncStatus("SYNC_FAILED").map { list ->
            list.map { FlockMapper.mapEntityToFlock(it) }
        }
    }
}
