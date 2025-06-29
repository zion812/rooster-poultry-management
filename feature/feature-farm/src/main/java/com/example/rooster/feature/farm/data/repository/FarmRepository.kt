package com.example.rooster.feature.farm.data.repository

import com.example.rooster.feature.farm.domain.model.Flock
import kotlinx.coroutines.flow.Flow

interface FarmRepository {
    fun getFlockById(id: String): Flow<com.example.rooster.core.common.Result<Flock?>> // Already updated in previous step
    fun getFlocksByType(type: String): Flow<com.example.rooster.core.common.Result<List<Flock>>> // Should also be updated
    suspend fun registerFlock(data: com.example.rooster.feature.farm.domain.model.FlockRegistrationData): com.example.rooster.core.common.Result<Unit> // Changed to common.Result

    // Lineage methods
    fun getLineageInfo(flockId: String, depthUp: Int = 2, depthDown: Int = 1): Flow<com.example.rooster.core.common.Result<com.example.rooster.feature.farm.domain.model.LineageInfo?>> // Ensure common.Result
    suspend fun addParentChildLink(childFlockId: String, parentFlockId: String, type: com.example.rooster.feature.farm.data.local.RelationshipType): Result<Unit>
    suspend fun removeParentChildLink(childFlockId: String, parentFlockId: String, type: com.example.rooster.feature.farm.data.local.RelationshipType): Result<Unit>
}
