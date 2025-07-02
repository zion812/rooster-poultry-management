package com.example.rooster.feature.farm.data.repository

import com.example.rooster.core.common.Result
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
import com.example.rooster.feature.farm.data.repository.FarmRepository
import com.example.rooster.feature.farm.domain.model.LineageInfo
import com.example.rooster.feature.farm.domain.model.LineageNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class FarmRepositoryImpl @Inject constructor(
    private val flockDao: FlockDao,
    private val lineageDao: LineageDao,
    private val remoteDataSource: FirebaseFarmDataSource
) : FarmRepository {

    override fun getFlockById(id: String): Flow<Result<Flock?>> {
        return combine(
            flockDao.getById(id),
            remoteDataSource.getFlockRealTime(id)
        ) { localEntity, remoteFirebaseResult ->
            when (remoteFirebaseResult) {
                is Result.Success -> {
                    val remoteDataMap = remoteFirebaseResult.data
                    if (remoteDataMap != null) {
                        val remoteFlockDomain = FlockMapper.mapRemoteToFlock(remoteDataMap)
                        if (localEntity?.needsSync == true) {
                            Timber.w("Flock ID $id: Local data has unsynced changes. Emitting local data.")
                            Result.Success(FlockMapper.mapEntityToFlock(localEntity))
                        } else {
                            val entityToCache = FlockMapper.mapFlockToEntity(remoteFlockDomain, needsSync = false)
                            try {
                                flockDao.insert(entityToCache)
                                Timber.d("Flock ID $id: Cache updated from remote data.")
                            } catch (e: Exception) {
                                Timber.e(e, "Flock ID $id: Error updating cache from remote data.")
                            }
                            Result.Success(remoteFlockDomain)
                        }
                    } else {
                        if (localEntity != null) {
                            if (localEntity.needsSync) {
                                Timber.w("Flock ID $id: Remote data is null, but local unsynced data exists.")
                                Result.Success(FlockMapper.mapEntityToFlock(localEntity))
                            } else {
                                Timber.d("Flock ID $id: Remote data is null, deleting from local cache.")
                                try {
                                    flockDao.deleteById(id)
                                } catch (e: Exception) {
                                    Timber.e(e, "Flock ID $id: Error deleting from local cache.")
                                }
                                Result.Success(null)
                            }
                        } else {
                            Result.Success(null)
                        }
                    }
                }
                is Result.Error -> {
                    if (localEntity != null) {
                        Timber.w(
                            remoteFirebaseResult.exception,
                            "Flock ID $id: Remote fetch failed. Using local data."
                        )
                        Result.Success(FlockMapper.mapEntityToFlock(localEntity))
                    } else {
                        Timber.e(remoteFirebaseResult.exception, "Flock ID $id: Remote fetch failed and no local data.")
                        Result.Error(remoteFirebaseResult.exception)
                    }
                }
                is Result.Loading -> {
                    if (localEntity != null) {
                        Result.Success(FlockMapper.mapEntityToFlock(localEntity))
                    } else {
                        Result.Loading
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

    override suspend fun registerFlock(data: FlockRegistrationData): Result<Unit> {
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

            val entity = FlockMapper.mapFlockToEntity(flock, needsSync = true)
            flockDao.insert(entity)

            try {
                val remoteData = FlockMapper.mapFlockToRemote(flock)
                val remoteResult = remoteDataSource.saveFlock(remoteData)
                if (remoteResult is Result.Success) {
                    flockDao.insert(FlockMapper.mapFlockToEntity(flock, needsSync = false))
                    Timber.d("Flock ID $id: Registered and synced.")
                } else if (remoteResult is Result.Error) {
                    Timber.w(remoteResult.exception, "Flock ID $id: Failed to sync to remote.")
                }
            } catch (e: Exception) {
                Timber.e(e, "Flock ID $id: Exception during remote sync.")
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to register flock locally.")
            Result.Error(e)
        }
    }

    override fun getLineageInfo(flockId: String, depthUp: Int, depthDown: Int): Flow<Result<LineageInfo?>> = flow {
        emit(Result.Loading)
        try {
            val centralFlock = flockDao.getById(flockId).firstOrNull()
            if (centralFlock == null) {
                emit(Result.Success(null))
                return@flow
            }

            val centralNode = mapEntityToLineageNode(centralFlock)
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

    private fun mapEntityToLineageNode(entity: FlockEntity): LineageNode {
        return LineageNode(
            flockId = entity.id,
            name = entity.name,
            breed = entity.breed,
            type = try {
                FlockType.valueOf(entity.type.uppercase(Locale.ROOT))
            } catch (e: Exception) {
                FlockType.FOWL
            }
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
                Timber.w(remoteResult.exception, "Failed to sync lineage link to remote.")
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error adding parent-child link locally.")
            Result.Error(e)
        }
    }

    override suspend fun removeParentChildLink(childFlockId: String, parentFlockId: String, type: RelationshipType): Result<Unit> {
        return try {
            lineageDao.deleteLink(childFlockId, parentFlockId, type)
            Timber.d("Local lineage link removed: Child $childFlockId, Parent $parentFlockId, Type $type")

            val remoteDeleteResult = remoteDataSource.deleteLineageLink(childFlockId, parentFlockId, type.name)
            if (remoteDeleteResult is Result.Error) {
                Timber.w(remoteDeleteResult.exception, "Failed to delete lineage link from remote.")
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error removing parent-child link locally.")
            Result.Error(e)
        }
    }

    override suspend fun deleteFlock(flockId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteDataSource.deleteFlock(flockId)
            if (remoteResult is Result.Error) {
                return@withContext remoteResult
            }

            flockDao.deleteById(flockId)

            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete flock $flockId")
            Result.Error(e)
        }
    }

    fun getSyncFailedFlocks(): Flow<List<Flock>> {
        return flockDao.getFlocksBySyncStatus("SYNC_FAILED").map { list ->
            list.map { FlockMapper.mapEntityToFlock(it) }
        }
    }
}
