package com.example.rooster.feature.farm.data.repository

import com.example.rooster.feature.farm.domain.model.Flock
import kotlinx.coroutines.flow.Flow

interface FarmRepository {
    fun getFlockById(id: String): Flow<Result<Flock>>
    fun getFlocksByType(type: String): Flow<Result<List<Flock>>>
    suspend fun registerFlock(data: com.example.rooster.feature.farm.domain.model.FlockRegistrationData): Result<Unit>

    // Lineage methods
    fun getLineageInfo(flockId: String, depthUp: Int = 2, depthDown: Int = 1): Flow<Result<com.example.rooster.feature.farm.domain.model.LineageInfo?>>
    suspend fun addParentChildLink(childFlockId: String, parentFlockId: String, type: com.example.rooster.feature.farm.data.local.RelationshipType): Result<Unit>
    suspend fun removeParentChildLink(childFlockId: String, parentFlockId: String, type: com.example.rooster.feature.farm.data.local.RelationshipType): Result<Unit>
}
