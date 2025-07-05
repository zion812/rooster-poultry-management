package com.example.rooster.feature.farm.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Represents a Farm in the local Room database
@Entity(tableName = "farms")
data class FarmEntity(
    @PrimaryKey val farmId: String, // Can be client-generated UUID for new farms, then updated by server's ID if different post-sync
    val name: String,
    val location: String,
    val owner: String,
    val capacity: Int,
    val establishedDate: String?, // YYYY-MM-DD
    val notes: String?,

    // Synchronization fields
    val lastClientUpdate: Long, // Timestamp of the last meaningful update on the client
    val serverLastUpdated: Long?, // Timestamp of when this record was last updated on the server (from API)

    val needsSync: Boolean = false, // True if local changes need to be pushed to the server

    // More detailed sync state, could combine with needsSync
    // PENDING_CREATE, PENDING_UPDATE, PENDING_DELETE, OK, ERROR
    val syncState: String = SyncState.OK.name,

    val lastSyncError: String? = null, // Store message from last failed sync attempt
    val syncAttempts: Int = 0 // Number of failed sync attempts for this item
)

enum class SyncState {
    OK, // Synced with server or local only, no pending operations
    PENDING_CREATE, // Newly created locally, needs to be POSTed
    PENDING_UPDATE, // Updated locally, needs to be PUT
    PENDING_DELETE, // Marked for deletion, needs to be DELETEed on server
    ERROR // Last sync attempt resulted in an error
}

// Helper to create a new FarmEntity for local creation before first sync
fun createNewFarmEntity(
    name: String,
    location: String,
    owner: String,
    capacity: Int,
    establishedDate: String?,
    notes: String?,
    clientGeneratedId: String // e.g., UUID.randomUUID().toString()
): FarmEntity {
    val currentTime = System.currentTimeMillis()
    return FarmEntity(
        farmId = clientGeneratedId,
        name = name,
        location = location,
        owner = owner,
        capacity = capacity,
        establishedDate = establishedDate,
        notes = notes,
        lastClientUpdate = currentTime,
        serverLastUpdated = null, // Not yet synced with server
        needsSync = true,
        syncState = SyncState.PENDING_CREATE.name,
        lastSyncError = null,
        syncAttempts = 0
    )
}
