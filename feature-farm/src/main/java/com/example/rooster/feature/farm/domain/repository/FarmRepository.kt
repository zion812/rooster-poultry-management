package com.example.rooster.feature.farm.domain.repository

import com.example.rooster.core.common.RoosterResult
import com.example.rooster.feature.farm.domain.model.Farm // Assuming a domain model
import kotlinx.coroutines.flow.Flow

interface FarmRepository {

    fun getFarmsStream(searchQuery: String? = null): Flow<List<Farm>>

    suspend fun getFarmById(farmId: String): RoosterResult<Farm?>

    suspend fun addNewFarm(
        name: String,
        location: String,
        owner: String,
        capacity: Int,
        establishedDate: String?,
        notes: String?
    ): RoosterResult<String> // Returns ID of the newly created farm

    suspend fun updateFarmDetails(farm: Farm): RoosterResult<Unit>

    suspend fun deleteFarm(farmId: String): RoosterResult<Unit>

    // Synchronization methods
    suspend fun synchronizeFarms(): RoosterResult<SyncSummary> // Returns a summary of sync operations

    // May also expose methods to get individual farm sync status or trigger sync for one item.
}

data class SyncSummary(
    val newItemsUploaded: Int = 0,
    val itemsUpdatedUpstream: Int = 0,
    val itemsDeletedUpstream: Int = 0,
    val newItemsDownloaded: Int = 0,
    val itemsUpdatedLocallyFromServer: Int = 0,
    val itemsDeletedLocallyFromServer: Int = 0, // If server can indicate deletions
    val conflictsEncountered: Int = 0,
    val errors: List<String> = emptyList()
)

// Domain model for Farm - this would be in a 'domain/model' package
// For brevity, defining it here. Ideally, it's separate.
data class Farm(
    val farmId: String,
    val name: String,
    val location: String,
    val owner: String,
    val capacity: Int,
    val establishedDate: String?,
    val notes: String?,
    val lastClientUpdate: Long,
    val serverLastUpdated: Long?,
    val needsSync: Boolean,
    val syncState: String, // From FarmEntity.SyncState
    val lastSyncError: String?
)
