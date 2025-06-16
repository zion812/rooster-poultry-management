package com.example.rooster.feature.farm.domain.usecase

import com.example.rooster.feature.farm.domain.model.SensorData
import kotlinx.coroutines.flow.Flow

interface GetAllSensorDataUseCase {
    operator fun invoke(): Flow<Result<List<SensorData>>>
}

class GetAllSensorDataUseCaseImpl(
    private val repository: com.example.rooster.feature.farm.data.repository.SensorDataRepository
) : GetAllSensorDataUseCase {
    override fun invoke() = repository.getAllSensorData()
}