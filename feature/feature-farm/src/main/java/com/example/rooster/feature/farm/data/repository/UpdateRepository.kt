package com.example.rooster.feature.farm.data.repository

import com.example.rooster.feature.farm.domain.model.UpdateRecord
import kotlinx.coroutines.flow.Flow

interface UpdateRepository {
    fun getUpdatesForFowl(fowlId: String): Flow<Result<List<UpdateRecord>>>
    suspend fun saveUpdates(records: List<UpdateRecord>): Result<Unit>
    suspend fun deleteUpdate(id: String): Result<Unit>
}