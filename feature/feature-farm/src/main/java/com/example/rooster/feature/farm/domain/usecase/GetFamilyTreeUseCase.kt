package com.example.rooster.feature.farm.domain.usecase

import com.example.rooster.feature.farm.domain.model.Flock
import com.example.rooster.feature.farm.data.repository.FarmRepository
import com.example.rooster.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Returns the lineage (ancestors) for a given fowl, up to a depth of generations.
 */
interface GetFamilyTreeUseCase {
    operator fun invoke(fowlId: String, generations: Int = 3): Flow<Result<List<Flock>>>
}

class GetFamilyTreeUseCaseImpl(
    private val repository: FarmRepository
) : GetFamilyTreeUseCase {
    override fun invoke(fowlId: String, generations: Int): Flow<Result<List<Flock>>> {
        // TODO: recursively fetch parents up to 'generations'
        return repository.getFlockById(fowlId).map { result ->
            when (result) {
                is Result.Success -> {
                    val flock = result.data
                    Result.Success(if (flock != null) listOf(flock) else emptyList())
                }
                is Result.Error -> Result.Error(result.exception)
                is Result.Loading -> Result.Loading
            }
        }
    }
}
