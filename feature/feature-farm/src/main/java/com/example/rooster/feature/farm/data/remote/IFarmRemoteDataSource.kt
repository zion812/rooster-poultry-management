package com.example.rooster.feature.farm.data.remote

import com.example.rooster.core.common.Result
import com.example.rooster.feature.farm.data.local.LineageLinkEntity // For saving/deleting links
import com.example.rooster.feature.farm.domain.model.Flock
// Import other domain models if needed (MortalityRecord, SensorData etc.)
import kotlinx.coroutines.flow.Flow

interface IFarmRemoteDataSource {
    // Flock methods
    // For real-time, Parse Live Queries would be used. For now, suspend fun for one-time fetch.
    // If FarmRepositoryImpl's combine logic is to be kept, this needs to be a Flow.
    // Let's keep it a Flow to match existing repository structure, Parse implementation will use LiveQuery or repeatedly fetch.
    fun getFlockStream(flockId: String): Flow<Result<Flock?>> // Changed from Map to Flock
    fun getFlocksByOwnerStream(ownerId: String): Flow<Result<List<Flock>>> // Changed from Map to Flock

    suspend fun saveFlock(flock: Flock): Result<Unit> // Takes domain model
    suspend fun deleteFlock(flockId: String): Result<Unit>

    // Lineage Link methods
    suspend fun saveLineageLink(link: LineageLinkEntity): Result<Unit> // Uses local entity for simplicity
    suspend fun deleteLineageLink(childFlockId: String, parentFlockId: String, relationshipTypeName: String): Result<Unit>
    fun getLineageLinksForChildStream(childFlockId: String): Flow<Result<List<LineageLinkEntity>>>
    fun getLineageLinksForParentStream(parentFlockId: String): Flow<Result<List<LineageLinkEntity>>>


    // TODO: Add methods for other farm entities if they are on Parse & need remote sync
    // suspend fun saveMortalityRecord(record: MortalityRecord): Result<Unit>
    // fun getMortalityRecordsStream(flockId: String): Flow<Result<List<MortalityRecord>>>
    // ... and so on for Vaccination, SensorData, Update Records if they are to be on Parse.
    // For now, focusing on Flock and Lineage as per plan.
}
