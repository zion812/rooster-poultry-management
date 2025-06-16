package com.example.rooster.feature.farm.data.repository

import com.example.rooster.feature.farm.domain.model.VaccinationRecord
import kotlinx.coroutines.flow.Flow

interface VaccinationRepository {
    fun getVaccinationsForFowl(fowlId: String): Flow<Result<List<VaccinationRecord>>>
    fun getUpcomingVaccinations(date: Long): Flow<Result<List<VaccinationRecord>>>
    suspend fun saveVaccination(record: VaccinationRecord): Result<Unit>
    suspend fun saveVaccinations(records: List<VaccinationRecord>): Result<Unit>
    suspend fun deleteVaccination(id: String): Result<Unit>
}
