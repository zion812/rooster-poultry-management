package com.example.rooster.feature.farm.domain.usecase

import com.example.rooster.feature.farm.data.repository.VaccinationRepository
import com.example.rooster.feature.farm.domain.model.VaccinationRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface SaveVaccinationRecordsUseCase {
    suspend fun execute(record: VaccinationRecord): Result<Unit>
}

class SaveVaccinationRecordsUseCaseImpl @Inject constructor(
    private val repository: VaccinationRepository
) : SaveVaccinationRecordsUseCase {
    override suspend fun execute(record: VaccinationRecord): Result<Unit> {
        return repository.saveVaccination(record)
    }
}

interface DeleteVaccinationRecordUseCase {
    suspend fun execute(recordId: String): Result<Unit>
}

class DeleteVaccinationRecordUseCaseImpl @Inject constructor(
    private val repository: VaccinationRepository
) : DeleteVaccinationRecordUseCase {
    override suspend fun execute(recordId: String): Result<Unit> {
        return repository.deleteVaccination(recordId)
    }
}