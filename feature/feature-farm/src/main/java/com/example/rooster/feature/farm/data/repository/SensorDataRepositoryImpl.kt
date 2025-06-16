package com.example.rooster.feature.farm.data.repository

import com.example.rooster.feature.farm.data.local.SensorDataDao
import com.example.rooster.feature.farm.data.local.SensorDataEntity
import com.example.rooster.feature.farm.domain.model.SensorData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class SensorDataRepositoryImpl @Inject constructor(
    private val dao: SensorDataDao
) : SensorDataRepository {

    override fun getAllSensorData(limit: Int): Flow<Result<List<SensorData>>> {
        return dao.getAll(limit).map { entities ->
            Result.success(entities.map { it.toDomain() })
        }
    }

    override fun getSensorDataByDevice(
        deviceId: String,
        limit: Int
    ): Flow<Result<List<SensorData>>> {
        return dao.getByDevice(deviceId, limit).map { entities ->
            Result.success(entities.map { it.toDomain() })
        }
    }

    override fun getSensorDataByTimeRange(
        startTime: Long,
        endTime: Long
    ): Flow<Result<List<SensorData>>> {
        return dao.getByTimeRange(startTime, endTime).map { entities ->
            Result.success(entities.map { it.toDomain() })
        }
    }

    override suspend fun saveSensorData(sensorData: SensorData): Result<Unit> {
        return try {
            dao.insert(sensorData.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteOldSensorData(cutoffTime: Long): Result<Unit> {
        return try {
            dao.deleteOldData(cutoffTime)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun SensorDataEntity.toDomain() = SensorData(
    id = id,
    deviceId = deviceId,
    temperature = temperature,
    humidity = humidity,
    airQuality = airQuality,
    lightLevel = lightLevel,
    noiseLevel = noiseLevel,
    timestamp = Date(timestamp)
)

private fun SensorData.toEntity() = SensorDataEntity(
    id = id,
    deviceId = deviceId,
    temperature = temperature,
    humidity = humidity,
    airQuality = airQuality,
    lightLevel = lightLevel,
    noiseLevel = noiseLevel,
    timestamp = timestamp.time,
    createdAt = System.currentTimeMillis()
)
