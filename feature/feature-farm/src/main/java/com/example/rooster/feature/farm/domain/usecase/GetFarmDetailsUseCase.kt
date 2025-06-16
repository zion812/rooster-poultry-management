package com.example.rooster.feature.farm.domain.usecase

import com.example.rooster.feature.farm.domain.model.Flock
import com.example.rooster.feature.farm.data.repository.FarmRepository
import kotlinx.coroutines.flow.Flow

interface GetFarmDetailsUseCase {
    operator fun invoke(farmId: String): Flow<Result<Flock>>
}

class GetFarmDetailsUseCaseImpl(
    private val repository: FarmRepository
) : GetFarmDetailsUseCase {
    override fun invoke(farmId: String) = repository.getFlockById(farmId)
}
