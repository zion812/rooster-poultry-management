package com.example.rooster.feature.farm.data.repository

import com.example.rooster.feature.farm.domain.model.MortalityRecord
import kotlinx.coroutines.flow.Flow

interface MortalityRepository {
    fun getMortalityForFowl(fowlId: String): Flow<Result<List<MortalityRecord>>>
    suspend fun saveMortality(records: List<MortalityRecord>): Result<Unit>
    suspend fun deleteMortality(id: String): Result<Unit>
}