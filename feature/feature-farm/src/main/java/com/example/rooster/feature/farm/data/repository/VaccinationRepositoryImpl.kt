package com.example.rooster.feature.farm.data.repository

import com.example.rooster.feature.farm.data.local.VaccinationDao
import com.example.rooster.feature.farm.data.local.VaccinationEntity
import com.example.rooster.feature.farm.domain.model.VaccinationRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class VaccinationRepositoryImpl @Inject constructor(
    private val dao: VaccinationDao
) : VaccinationRepository {

    override fun getVaccinationsForFowl(fowlId: String): Flow<Result<List<VaccinationRecord>>> {
        return dao.getVaccinationForFowl(fowlId).map { entities ->
            Result.success(entities.map { it.toDomain() })
        }
    }

    override fun getUpcomingVaccinations(date: Long): Flow<Result<List<VaccinationRecord>>> {
        return dao.getUpcomingVaccinations(date).map { entities ->
            Result.success(entities.map { it.toDomain() })
        }
    }

    override suspend fun saveVaccination(record: VaccinationRecord): Result<Unit> {
        return try {
            dao.insert(record.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveVaccinations(records: List<VaccinationRecord>): Result<Unit> {
        return try {
            records.forEach { dao.insert(it.toEntity()) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteVaccination(id: String): Result<Unit> {
        return try {
            dao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun VaccinationEntity.toDomain() = VaccinationRecord(
    id = id,
    fowlId = fowlId,
    vaccineName = vaccineName,
    dosage = dosage,
    veterinarian = veterinarian,
    nextDueDate = nextDueDate?.let { Date(it) },
    notes = notes,
    photos = photos?.split(",") ?: emptyList(),
    recordedAt = Date(recordedAt)
)

private fun VaccinationRecord.toEntity() = VaccinationEntity(
    id = id,
    fowlId = fowlId,
    vaccineName = vaccineName,
    dosage = dosage,
    veterinarian = veterinarian,
    nextDueDate = nextDueDate?.time,
    notes = notes,
    photos = photos.joinToString(","),
    recordedAt = recordedAt.time,
    createdAt = System.currentTimeMillis()
)
