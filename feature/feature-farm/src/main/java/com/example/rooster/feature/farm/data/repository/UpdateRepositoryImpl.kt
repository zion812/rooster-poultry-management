package com.example.rooster.feature.farm.data.repository

import com.example.rooster.feature.farm.data.local.UpdateDao
import com.example.rooster.feature.farm.data.local.UpdateEntity
import com.example.rooster.feature.farm.domain.model.UpdateRecord
import com.example.rooster.feature.farm.domain.model.UpdateType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class UpdateRepositoryImpl @Inject constructor(
    private val dao: UpdateDao
) : UpdateRepository {
    override fun getUpdatesForFowl(fowlId: String): Flow<Result<List<UpdateRecord>>> {
        return dao.getUpdatesForFowl(fowlId).map { list ->
            try {
                val records = list.map { entity ->
                    UpdateRecord(
                        id = entity.id,
                        fowlId = entity.fowlId,
                        type = UpdateType.valueOf(entity.updateType),
                        date = Date(entity.recordedAt),
                        details = entity.description,
                        attachmentUrl = entity.photos
                    )
                }
                Result.success(records)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun saveUpdates(records: List<UpdateRecord>): Result<Unit> {
        return try {
            val entities = records.map { record ->
                UpdateEntity(
                    id = record.id,
                    fowlId = record.fowlId,
                    updateType = record.type.name,
                    title = record.type.name,
                    description = record.details,
                    weight = null,
                    photos = record.attachmentUrl,
                    recordedAt = record.date.time,
                    createdAt = System.currentTimeMillis()
                )
            }
            entities.forEach { dao.insert(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUpdate(id: String): Result<Unit> {
        return try {
            dao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
