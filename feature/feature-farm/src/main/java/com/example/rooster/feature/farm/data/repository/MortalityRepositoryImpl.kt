package com.example.rooster.feature.farm.data.repository

import com.example.rooster.feature.farm.data.local.MortalityDao
import com.example.rooster.feature.farm.data.local.MortalityEntity
import com.example.rooster.feature.farm.domain.model.MortalityRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class MortalityRepositoryImpl @Inject constructor(
    private val dao: MortalityDao
) : MortalityRepository {

    override fun getMortalityForFowl(fowlId: String): Flow<Result<List<MortalityRecord>>> {
        return dao.getMortalityForFowl(fowlId).map { list ->
            val records = list.map { entity ->
                MortalityRecord(
                    id = entity.id,
                    fowlId = entity.fowlId,
                    cause = entity.cause,
                    description = entity.description,
                    weight = entity.weight,
                    photos = entity.photos?.split(",")?.map { it.trim() },
                    recordedAt = Date(entity.recordedAt),
                    createdAt = Date(entity.createdAt)
                )
            }
            Result.success(records)
        }
    }

    override suspend fun saveMortality(records: List<MortalityRecord>): Result<Unit> {
        return try {
            val entities = records.map { record ->
                MortalityEntity(
                    id = record.id,
                    fowlId = record.fowlId,
                    cause = record.cause,
                    description = record.description,
                    weight = record.weight,
                    photos = record.photos?.joinToString(","),
                    recordedAt = record.recordedAt.time,
                    createdAt = record.createdAt.time
                )
            }
            entities.forEach { dao.insert(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMortality(id: String): Result<Unit> {
        return try {
            dao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
