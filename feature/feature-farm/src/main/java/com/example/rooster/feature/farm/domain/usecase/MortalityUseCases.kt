package com.example.rooster.feature.farm.domain.usecase

import com.example.rooster.feature.farm.domain.model.MortalityRecord
import kotlinx.coroutines.flow.Flow

/** Fetches mortality records for a given fowl ID */
interface GetMortalityRecordsUseCase {
    operator fun invoke(fowlId: String): Flow<Result<List<MortalityRecord>>>
}

/** Saves a list of mortality records */
interface SaveMortalityRecordsUseCase {
    suspend operator fun invoke(records: List<MortalityRecord>): Result<Unit>
}

/** Deletes a mortality record by ID */
interface DeleteMortalityRecordUseCase {
    suspend operator fun invoke(id: String): Result<Unit>
}

// Implementations
class GetMortalityRecordsUseCaseImpl(
    private val repository: com.example.rooster.feature.farm.data.repository.MortalityRepository
) : GetMortalityRecordsUseCase {
    override fun invoke(fowlId: String) = repository.getMortalityForFowl(fowlId)
}

class SaveMortalityRecordsUseCaseImpl(
    private val repository: com.example.rooster.feature.farm.data.repository.MortalityRepository
) : SaveMortalityRecordsUseCase {
    override suspend fun invoke(records: List<MortalityRecord>) = repository.saveMortality(records)
}

class DeleteMortalityRecordUseCaseImpl(
    private val repository: com.example.rooster.feature.farm.data.repository.MortalityRepository
) : DeleteMortalityRecordUseCase {
    override suspend fun invoke(id: String) = repository.deleteMortality(id)
}