package com.example.rooster.feature.farm.data.mapper

import com.example.rooster.core.network.dto.FarmDto
import com.example.rooster.core.network.dto.FarmInputDto
import com.example.rooster.feature.farm.data.local.db.entity.FarmEntity
import com.example.rooster.feature.farm.data.local.db.entity.SyncState
import com.example.rooster.feature.farm.domain.model.Farm
import java.util.UUID

// Mappers for Farm data transformations

fun FarmEntity.toDomainModel(): Farm {
    return Farm(
        farmId = this.farmId,
        name = this.name,
        location = this.location,
        owner = this.owner,
        capacity = this.capacity,
        establishedDate = this.establishedDate,
        notes = this.notes,
        lastClientUpdate = this.lastClientUpdate,
        serverLastUpdated = this.serverLastUpdated,
        needsSync = this.needsSync,
        syncState = this.syncState,
        lastSyncError = this.lastSyncError
    )
}

fun Farm.toEntity(): FarmEntity {
    return FarmEntity(
        farmId = this.farmId,
        name = this.name,
        location = this.location,
        owner = this.owner,
        capacity = this.capacity,
        establishedDate = this.establishedDate,
        notes = this.notes,
        lastClientUpdate = this.lastClientUpdate,
        serverLastUpdated = this.serverLastUpdated,
        needsSync = this.needsSync,
        syncState = this.syncState,
        lastSyncError = this.lastSyncError
    )
}

fun FarmDto.toDomainModel(localFarmEntity: FarmEntity?): Farm {
    // If we have a corresponding local entity, preserve its sync status and client update times
    // Server's farmId is authoritative.
    return Farm(
        farmId = this.farmId, // Server's ID
        name = this.name,
        location = this.location,
        owner = this.owner,
        capacity = this.capacity,
        establishedDate = this.establishedDate,
        notes = this.notes,
        lastClientUpdate = localFarmEntity?.lastClientUpdate ?: 0L, // Or current time if no local
        serverLastUpdated = this.establishedDate?.let { System.currentTimeMillis() } ?: System.currentTimeMillis(), // Placeholder: API needs to provide last_updated
        needsSync = localFarmEntity?.needsSync ?: false,
        syncState = localFarmEntity?.syncState ?: SyncState.OK.name,
        lastSyncError = localFarmEntity?.lastSyncError
    )
}

fun FarmDto.toEntity(existingEntity: FarmEntity?): FarmEntity {
    // When converting DTO to Entity for saving after fetch,
    // we need to be careful about overwriting local sync state.
    // This mapper assumes the DTO is fresh from server and should reflect server state.
    // The repository logic will handle conflict resolution before calling this.
    val serverTime = System.currentTimeMillis() // Placeholder: Use actual 'last_updated' from DTO
    return FarmEntity(
        farmId = this.farmId,
        name = this.name,
        location = this.location,
        owner = this.owner,
        capacity = this.capacity,
        establishedDate = this.establishedDate,
        notes = this.notes,
        lastClientUpdate = existingEntity?.lastClientUpdate ?: serverTime, // Preserve client update unless explicitly overwritten
        serverLastUpdated = serverTime, // This should come from FarmDto.lastUpdated from API
        needsSync = false, // Data from server is considered synced unless it conflicts with a pending local op
        syncState = SyncState.OK.name,
        lastSyncError = null, // Clear previous errors on successful fetch
        syncAttempts = 0 // Reset attempts
    )
}

fun Farm.toFarmInputDto(): FarmInputDto {
    // Used for creating or updating a farm on the server
    return FarmInputDto(
        name = this.name,
        location = this.location,
        owner = this.owner,
        capacity = this.capacity,
        establishedDate = this.establishedDate,
        notes = this.notes
    )
}

// For creating a new FarmEntity from user input before first sync
fun com.example.rooster.feature.farm.domain.model.requests.CreateFarmRequest.toNewFarmEntity(): FarmEntity {
    val currentTime = System.currentTimeMillis()
    return FarmEntity(
        farmId = UUID.randomUUID().toString(), // Client-generated temporary ID
        name = this.name,
        location = this.location,
        owner = this.owner,
        capacity = this.capacity,
        establishedDate = this.establishedDate,
        notes = this.notes,
        lastClientUpdate = currentTime,
        serverLastUpdated = null,
        needsSync = true,
        syncState = SyncState.PENDING_CREATE.name,
        lastSyncError = null,
        syncAttempts = 0
    )
}

// This would be defined in domain.model.requests package
// package com.example.rooster.feature.farm.domain.model.requests
// data class CreateFarmRequest(
//    val name: String,
//    val location: String,
//    val owner: String,
//    val capacity: Int,
//    val establishedDate: String?,
//    val notes: String?
// )

// Placeholder for server-provided last_updated in DTO
// Modify FarmDto in core-network to include this:
// @Serializable
// data class FarmDto(
//    @SerialName("farm_id") val farmId: String,
//    @SerialName("name") val name: String,
// ...
//    @SerialName("last_updated") val lastUpdated: String? = null // ISO 8601 timestamp from server
// )
// Then FarmDto.toEntity and FarmDto.toDomainModel should use this server timestamp.
// For now, using System.currentTimeMillis() as a placeholder for serverLastUpdated.
// This is a CRITICAL piece for proper sync.
