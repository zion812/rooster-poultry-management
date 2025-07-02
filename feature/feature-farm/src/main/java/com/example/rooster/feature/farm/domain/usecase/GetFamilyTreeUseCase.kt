package com.example.rooster.feature.farm.domain.usecase

import com.example.rooster.feature.farm.domain.model.Flock
import com.example.rooster.feature.farm.data.repository.FarmRepository
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
            result.map { flock -> flock?.let { listOf(it) } ?: emptyList() }
        }
    }
}
