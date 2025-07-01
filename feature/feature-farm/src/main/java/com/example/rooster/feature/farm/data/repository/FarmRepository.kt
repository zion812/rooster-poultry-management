package com.example.rooster.feature.farm.data.repository

import com.example.rooster.feature.farm.domain.model.Flock
import com.example.rooster.feature.farm.domain.model.FlockRegistrationData
import com.example.rooster.feature.farm.domain.model.LineageInfo
import kotlinx.coroutines.flow.Flow

interface FarmRepository {
    fun getFlockById(id: String): Flow<com.example.rooster.core.common.Result<Flock?>> 
    fun getFlocksByType(type: String): Flow<com.example.rooster.core.common.Result<List<Flock>>> 
    suspend fun registerFlock(data: com.example.rooster.feature.farm.domain.model.FlockRegistrationData): com.example.rooster.core.common.Result<Unit> 

    // Lineage methods
    fun getLineageInfo(flockId: String, depthUp: Int = 2, depthDown: Int = 1): Flow<com.example.rooster.core.common.Result<com.example.rooster.feature.farm.domain.model.LineageInfo?>> 
    suspend fun addParentChildLink(childFlockId: String, parentFlockId: String, type: com.example.rooster.feature.farm.data.local.RelationshipType): com.example.rooster.core.common.Result<Unit>
    suspend fun removeParentChildLink(childFlockId: String, parentFlockId: String, type: com.example.rooster.feature.farm.data.local.RelationshipType): com.example.rooster.core.common.Result<Unit>
    suspend fun deleteFlock(flockId: String): com.example.rooster.core.common.Result<Unit>
}
