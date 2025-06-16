package com.example.rooster.feature.farm.data.repository

import com.example.rooster.feature.farm.domain.model.SensorData
import kotlinx.coroutines.flow.Flow

interface SensorDataRepository {
    fun getAllSensorData(limit: Int = 1000): Flow<Result<List<SensorData>>>
    fun getSensorDataByDevice(deviceId: String, limit: Int = 100): Flow<Result<List<SensorData>>>
    fun getSensorDataByTimeRange(startTime: Long, endTime: Long): Flow<Result<List<SensorData>>>
    suspend fun saveSensorData(sensorData: SensorData): Result<Unit>
    suspend fun deleteOldSensorData(cutoffTime: Long): Result<Unit>
}
