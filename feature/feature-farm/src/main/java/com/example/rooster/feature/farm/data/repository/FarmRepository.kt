package com.example.rooster.feature.farm.data.repository

import com.example.rooster.feature.farm.domain.model.Flock
import kotlinx.coroutines.flow.Flow

interface FarmRepository {
    fun getFlockById(id: String): Flow<Result<Flock>>
    fun getFlocksByType(type: String): Flow<Result<List<Flock>>>
    suspend fun registerFlock(data: com.example.rooster.feature.farm.domain.model.FlockRegistrationData): Result<Unit>
}
