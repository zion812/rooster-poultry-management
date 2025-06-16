package com.example.rooster.feature.farm.domain.usecase

import com.example.rooster.feature.farm.domain.model.UpdateRecord
import kotlinx.coroutines.flow.Flow

/** Fetches update records for a given fowl ID */
interface GetUpdateRecordsUseCase {
    operator fun invoke(fowlId: String): Flow<Result<List<UpdateRecord>>>
}

/** Saves a list of update records */
interface SaveUpdateRecordsUseCase {
    suspend operator fun invoke(records: List<UpdateRecord>): Result<Unit>
}

/** Deletes an update record by ID */
interface DeleteUpdateRecordUseCase {
    suspend operator fun invoke(id: String): Result<Unit>
}

// Implementations
class GetUpdateRecordsUseCaseImpl(
    private val repository: com.example.rooster.feature.farm.data.repository.UpdateRepository
) : GetUpdateRecordsUseCase {
    override fun invoke(fowlId: String) = repository.getUpdatesForFowl(fowlId)
}

class SaveUpdateRecordsUseCaseImpl(
    private val repository: com.example.rooster.feature.farm.data.repository.UpdateRepository
) : SaveUpdateRecordsUseCase {
    override suspend fun invoke(records: List<UpdateRecord>) = repository.saveUpdates(records)
}

class DeleteUpdateRecordUseCaseImpl(
    private val repository: com.example.rooster.feature.farm.data.repository.UpdateRepository
) : DeleteUpdateRecordUseCase {
    override suspend fun invoke(id: String) = repository.deleteUpdate(id)
}