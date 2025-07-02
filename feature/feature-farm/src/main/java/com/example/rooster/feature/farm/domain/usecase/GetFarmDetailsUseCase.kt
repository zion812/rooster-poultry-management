package com.example.rooster.feature.farm.domain.usecase

import com.example.rooster.feature.farm.domain.model.Flock
import com.example.rooster.feature.farm.data.repository.FarmRepository
import com.example.rooster.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GetFarmDetailsUseCase {
    operator fun invoke(farmId: String): Flow<Result<Flock>>
}

class GetFarmDetailsUseCaseImpl(
    private val repository: FarmRepository
) : GetFarmDetailsUseCase {
    override fun invoke(farmId: String): Flow<Result<Flock>> = repository.getFlockById(farmId).map { result ->
        when (result) {
            is Result.Success -> {
                val flock = result.data
                if (flock != null) {
                    Result.Success(flock)
                } else {
                    Result.Error(NoSuchElementException("Flock not found with id: $farmId"))
                }
            }

            is Result.Error -> Result.Error(result.exception)
            is Result.Loading -> Result.Loading
        }
    }
}