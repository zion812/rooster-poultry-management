package com.example.rooster.feature.farm.domain.usecase

import com.example.rooster.feature.farm.data.repository.SensorDataRepository
import com.example.rooster.feature.farm.domain.model.SensorData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetSensorDataByDeviceUseCase {
    fun execute(deviceId: String, limit: Int = 100): Flow<Result<List<SensorData>>>
}

class GetSensorDataByDeviceUseCaseImpl @Inject constructor(
    private val repository: SensorDataRepository
) : GetSensorDataByDeviceUseCase {
    override fun execute(deviceId: String, limit: Int): Flow<Result<List<SensorData>>> {
        return repository.getSensorDataByDevice(deviceId, limit)
    }
}