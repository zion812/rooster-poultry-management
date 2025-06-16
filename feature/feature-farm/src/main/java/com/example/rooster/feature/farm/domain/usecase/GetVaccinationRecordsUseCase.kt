package com.example.rooster.feature.farm.domain.usecase

import com.example.rooster.feature.farm.domain.model.VaccinationRecord
import kotlinx.coroutines.flow.Flow

interface GetVaccinationRecordsUseCase {
    operator fun invoke(fowlId: String): Flow<Result<List<VaccinationRecord>>>
}

class GetVaccinationRecordsUseCaseImpl(
    private val repository: com.example.rooster.feature.farm.data.repository.VaccinationRepository
) : GetVaccinationRecordsUseCase {
    override fun invoke(fowlId: String) = repository.getVaccinationsForFowl(fowlId)
}