package com.example.rooster.feature.farm.domain.usecase

import com.example.rooster.feature.farm.domain.model.Flock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GetFlocksByTypeUseCase {
    operator fun invoke(type: String): Flow<Result<List<Flock>>>
}

class GetFlocksByTypeUseCaseImpl(
    private val repository: com.example.rooster.feature.farm.data.repository.FarmRepository
) : GetFlocksByTypeUseCase {
    override fun invoke(type: String): Flow<Result<List<Flock>>> = repository.getFlocksByType(type).map { result ->
        result.map { flocks -> flocks ?: emptyList() }
    }
}