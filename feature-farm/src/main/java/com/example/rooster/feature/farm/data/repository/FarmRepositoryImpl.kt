package com.example.rooster.feature.farm.data.repository

import android.util.Log
import com.example.rooster.core.common.RoosterResult
import com.example.rooster.core.network.dto.ApiErrorResponseDto
import com.example.rooster.core.network.service.FarmApiService
import com.example.rooster.feature.farm.data.local.db.dao.FarmDao
import com.example.rooster.feature.farm.data.local.db.entity.FarmEntity
import com.example.rooster.feature.farm.data.local.db.entity.SyncState
import com.example.rooster.feature.farm.domain.model.Farm
import com.example.rooster.feature.farm.domain.repository.FarmRepository
import com.example.rooster.feature.farm.domain.repository.SyncSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import com.example.rooster.feature.farm.data.mapper.toDomainModel
import com.example.rooster.feature.farm.data.mapper.toEntity
import com.example.rooster.feature.farm.data.mapper.toFarmInputDto

class FarmRepositoryImpl @Inject constructor(
    private val farmDao: FarmDao,
    private val farmApiService: FarmApiService,
    private val json: Json // For parsing error bodies
) : FarmRepository {

    private val TAG = "FarmRepositoryImpl"

    // TODO: Inject Dispatcher for IO operations (Dispatchers.IO)

    override fun getFarmsStream(searchQuery: String?): Flow<List<Farm>> {
        return if (searchQuery.isNullOrBlank()) {
            farmDao.getAllFarms()
        } else {
            farmDao.searchFarms("%${searchQuery}%") // Add wildcards for LIKE query
        }.map { entities -> entities.map { it.toDomainModel() } }
    }

    override suspend fun getFarmById(farmId: String): RoosterResult<Farm?> {
        return try {
            val entity = farmDao.getFarmById(farmId)
            RoosterResult.Success(entity?.toDomainModel())
        } catch (e: Exception) {
            Log.e(TAG, "Error getting farm by ID $farmId from DB", e)
            RoosterResult.Error("Failed to get farm from local DB: ${e.message}")
        }
    }

    override suspend fun addNewFarm(
        name: String,
        location: String,
        owner: String,
        capacity: Int,
        establishedDate: String?,
        notes: String?
    ): RoosterResult<String> {
        val clientGeneratedId = UUID.randomUUID().toString()
        val currentTime = System.currentTimeMillis()
        val newFarmEntity = FarmEntity(
            farmId = clientGeneratedId,
            name = name,
            location = location,
            owner = owner,
            capacity = capacity,
            establishedDate = establishedDate,
            notes = notes,
            lastClientUpdate = currentTime,
            serverLastUpdated = null,
            needsSync = true,
            syncState = SyncState.PENDING_CREATE.name
        )
        return try {
            farmDao.insertFarm(newFarmEntity)
            // Optional: Trigger immediate sync or let WorkManager handle it
            // synchronizeFarms()
            RoosterResult.Success(clientGeneratedId)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding new farm to DB", e)
            RoosterResult.Error("Failed to add new farm locally: ${e.message}")
        }
    }

    override suspend fun updateFarmDetails(farm: Farm): RoosterResult<Unit> {
        val entity = farm.toEntity().copy(
            needsSync = true,
            syncState = if (farm.syncState == SyncState.PENDING_CREATE.name) SyncState.PENDING_CREATE.name else SyncState.PENDING_UPDATE.name,
            lastClientUpdate = System.currentTimeMillis() // Update client timestamp
        )
        return try {
            farmDao.updateFarm(entity)
            // Optional: Trigger immediate sync
            RoosterResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating farm in DB: ${farm.farmId}", e)
            RoosterResult.Error("Failed to update farm locally: ${e.message}")
        }
    }

    override suspend fun deleteFarm(farmId: String): RoosterResult<Unit> {
        // Option 1: Hard delete locally and mark for server deletion if it was synced
        // Option 2: Soft delete locally (add 'isDeleted' flag to FarmEntity)
        // For now, assuming hard delete locally and try to delete from server if it exists.
        return try {
            val farm = farmDao.getFarmById(farmId)
            if (farm != null) {
                if (farm.syncState != SyncState.PENDING_CREATE.name) { // If it was never synced, just delete locally
                    val updatedFarm = farm.copy(
                        needsSync = true,
                        syncState = SyncState.PENDING_DELETE.name,
                        lastClientUpdate = System.currentTimeMillis()
                    )
                    farmDao.updateFarm(updatedFarm)
                } else {
                    farmDao.deleteFarmById(farmId) // Was never on server, just delete
                }
            }
            RoosterResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking farm for deletion or deleting from DB: $farmId", e)
            RoosterResult.Error("Failed to delete farm locally: ${e.message}")
        }
    }

    override suspend fun synchronizeFarms(): RoosterResult<SyncSummary> {
        val summary = SyncSummaryInternal()
        try {
            uploadPendingChanges(summary)
            downloadServerChanges(summary)
        } catch (e: Exception) { // Catch any unexpected errors during the broader sync process
            Log.e(TAG, "Synchronization failed with exception", e)
            summary.addError("Overall sync failed: ${e.message}")
            return RoosterResult.Error("Synchronization failed: ${e.message}", e)
        }

        Log.d(TAG, "Sync summary: Uploaded New: ${summary.newItemsUploaded}, Updated Upstream: ${summary.itemsUpdatedUpstream}, Deleted Upstream: ${summary.itemsDeletedUpstream}. Downloaded New: ${summary.newItemsDownloaded}, Updated Locally: ${summary.itemsUpdatedLocallyFromServer}")
        if (summary.errors.isNotEmpty()) {
            return RoosterResult.Error("Sync completed with errors: ${summary.errors.joinToString()}", Exception(summary.errors.joinToString("\n")))
        }
        return RoosterResult.Success(summary.toPublicSummary())
    }

    private suspend fun uploadPendingChanges(summary: SyncSummaryInternal) {
        val pendingFarms = farmDao.getFarmsRequiringSync()
        for (farmEntity in pendingFarms) {
            try {
                when (farmEntity.syncState) {
                    SyncState.PENDING_CREATE.name -> {
                        Log.d(TAG, "Uploading PENDING_CREATE: ${farmEntity.farmId}")
                        val response = farmApiService.createFarm(farmEntity.toDomainModel().toFarmInputDto())
                        if (response.isSuccessful && response.body() != null) {
                            val serverFarm = response.body()!!
                            // Server might assign a new ID or confirm client's if it was UUID
                            // Update local entity with server's ID and clear sync flags
                            farmDao.deleteFarmById(farmEntity.farmId) // Delete old client-generated ID record
                            val newSyncedEntity = serverFarm.toEntity(null) // serverFarm.lastUpdated will be used
                                .copy(needsSync = false, syncState = SyncState.OK.name, syncAttempts = 0, serverLastUpdated = System.currentTimeMillis()) // TODO: use serverFarm.lastUpdated
                            farmDao.insertFarm(newSyncedEntity)
                            summary.newItemsUploaded++
                            Log.i(TAG, "Farm ${farmEntity.farmId} created on server as ${serverFarm.farmId}")
                        } else {
                            handleApiError(response.code(), response.errorBody()?.string(), farmEntity.farmId, summary)
                        }
                    }
                    SyncState.PENDING_UPDATE.name -> {
                        Log.d(TAG, "Uploading PENDING_UPDATE: ${farmEntity.farmId}")
                        // TODO: Implement If-Unmodified-Since or ETag if API supports it
                        val response = farmApiService.updateFarm(farmEntity.farmId, farmEntity.toDomainModel().toFarmInputDto())
                        if (response.isSuccessful && response.body() != null) {
                            val serverFarm = response.body()!!
                             val updatedEntity = serverFarm.toEntity(farmEntity) // serverFarm.lastUpdated will be used
                                .copy(needsSync = false, syncState = SyncState.OK.name, syncAttempts = 0, serverLastUpdated = System.currentTimeMillis()) // TODO: use serverFarm.lastUpdated
                            farmDao.updateFarm(updatedEntity)
                            summary.itemsUpdatedUpstream++
                            Log.i(TAG, "Farm ${farmEntity.farmId} updated on server.")
                        } else {
                            // Handle conflict (412 Precondition Failed) or other errors
                            if (response.code() == 412) {
                                summary.conflictsEncountered++
                                farmDao.updateFarmSyncError(farmEntity.farmId, SyncState.ERROR.name, "Conflict: Server data changed.")
                                Log.w(TAG, "Conflict updating farm ${farmEntity.farmId}. Server data has changed.")
                                // Trigger download or specific conflict resolution UI if needed
                            } else {
                                handleApiError(response.code(), response.errorBody()?.string(), farmEntity.farmId, summary)
                            }
                        }
                    }
                    SyncState.PENDING_DELETE.name -> {
                        Log.d(TAG, "Uploading PENDING_DELETE: ${farmEntity.farmId}")
                        val response = farmApiService.deleteFarm(farmEntity.farmId)
                        if (response.isSuccessful) {
                            farmDao.deleteFarmById(farmEntity.farmId)
                            summary.itemsDeletedUpstream++
                            Log.i(TAG, "Farm ${farmEntity.farmId} deleted on server.")
                        } else {
                             if (response.code() == 404) { // Already deleted on server or never existed
                                farmDao.deleteFarmById(farmEntity.farmId)
                                summary.itemsDeletedUpstream++ // Count it as successfully processed
                                Log.i(TAG, "Farm ${farmEntity.farmId} already deleted or not found on server. Removed locally.")
                            } else {
                                handleApiError(response.code(), response.errorBody()?.string(), farmEntity.farmId, summary)
                            }
                        }
                    }
                }
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP error syncing farm ${farmEntity.farmId}: ${e.code()}", e)
                handleApiError(e.code(), e.response()?.errorBody()?.string(), farmEntity.farmId, summary)
            } catch (e: IOException) { // Network error
                Log.e(TAG, "Network error syncing farm ${farmEntity.farmId}", e)
                summary.addError("Network error for ${farmEntity.farmId}: ${e.message}")
                // Don't update DAO error state here, WorkManager will retry
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error syncing farm ${farmEntity.farmId}", e)
                farmDao.updateFarmSyncError(farmEntity.farmId, SyncState.ERROR.name, e.message ?: "Unknown error")
                summary.addError("Unexpected error for ${farmEntity.farmId}: ${e.message}")
            }
        }
    }

    private suspend fun downloadServerChanges(summary: SyncSummaryInternal) {
        val lastSyncTimestamp = farmDao.getLatestServerTimestampForSyncedItems() // This needs to be stored/retrieved reliably
        // TODO: Convert Long timestamp to ISO 8601 string for API if API expects that format
        // For now, assuming API will be enhanced to take a Long or this conversion happens.
        // The current FarmApiService.getFarms does not support updated_since, so this is a full fetch.
        // This is where API enhancement for delta sync is CRITICAL.
        // If API provides `updated_since`, use it: farmApiService.getFarms(updatedSince = lastSyncTimestamp)

        Log.d(TAG, "Downloading server changes. Last known server timestamp for synced items: $lastSyncTimestamp")
        try {
            val response = farmApiService.getFarms(searchQuery = null) // Full fetch for now
            if (response.isSuccessful && response.body() != null) {
                val serverFarms = response.body()!!
                val localFarmMap = farmDao.getFarmsRequiringSync().associateBy { it.farmId } // For quick conflict check

                for (farmDto in serverFarms) {
                    val existingLocalFarm = farmDao.getFarmById(farmDto.farmId)
                    val serverLastUpdated = System.currentTimeMillis() // Placeholder: DTO needs farmDto.lastUpdated

                    if (existingLocalFarm == null) {
                        // New farm from server
                        val entity = farmDto.toEntity(null).copy(serverLastUpdated = serverLastUpdated)
                        farmDao.insertFarm(entity)
                        summary.newItemsDownloaded++
                        Log.i(TAG, "New farm ${farmDto.farmId} downloaded from server.")
                    } else {
                        // Existing farm, check for updates/conflicts
                        if (existingLocalFarm.needsSync) {
                            // Conflict: Local changes pending.
                            // AGENTS.md: "prioritize emitting the local data and log a warning" for UI.
                            // For sync: Server Wins for now if server is newer.
                            // A more robust strategy would be LWW based on reliable timestamps or manual resolution.
                            if ((serverLastUpdated) > (existingLocalFarm.serverLastUpdated ?: 0L)) { // Compare with server's idea of last update
                                Log.w(TAG, "Conflict for farm ${farmDto.farmId}. Local has pending changes. Server is newer. Applying server version but keeping needsSync for next upload attempt or manual review.")
                                val updatedEntity = farmDto.toEntity(existingLocalFarm)
                                    .copy(
                                        serverLastUpdated = serverLastUpdated,
                                        needsSync = true, // Keep needsSync true, so local changes are attempted again
                                        syncState = SyncState.PENDING_UPDATE.name, // Re-mark as pending update
                                        lastSyncError = "Conflict: Server version was newer. Local changes might need re-evaluation."
                                    )
                                farmDao.updateFarm(updatedEntity)
                                summary.conflictsEncountered++
                                summary.itemsUpdatedLocallyFromServer++ // Technically updated from server, but conflict remains
                            } else {
                                Log.i(TAG, "Conflict for farm ${farmDto.farmId}. Local has pending changes and local/server timestamp indicates local might be same or newer. Upload will handle.")
                                // Let the upload process handle this.
                            }
                        } else {
                            // No pending local changes, safe to update if server is newer
                            if ((serverLastUpdated) > (existingLocalFarm.serverLastUpdated ?: 0L)) {
                                val entity = farmDto.toEntity(existingLocalFarm).copy(serverLastUpdated = serverLastUpdated)
                                farmDao.updateFarm(entity)
                                summary.itemsUpdatedLocallyFromServer++
                                Log.i(TAG, "Farm ${farmDto.farmId} updated locally from server.")
                            }
                        }
                    }
                }
                // TODO: Handle deletions. If API provided a list of deleted IDs, process them here.
                // Or, if full fetch, items not in serverFarms but in local DB (and not PENDING_CREATE) could be candidates for local deletion.
            } else {
                handleApiError(response.code(), response.errorBody()?.string(), "download_farms", summary)
            }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error downloading farms: ${e.code()}", e)
            handleApiError(e.code(), e.response()?.errorBody()?.string(), "download_farms", summary)
        } catch (e: IOException) {
            Log.e(TAG, "Network error downloading farms", e)
            summary.addError("Network error downloading farms: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error downloading farms", e)
            summary.addError("Unexpected error downloading farms: ${e.message}")
        }
    }

    private suspend fun handleApiError(code: Int, errorBody: String?, itemId: String, summary: SyncSummaryInternal) {
        val errorMessage = parseApiError(errorBody, code)
        Log.w(TAG, "API Error for item $itemId (Code: $code): $errorMessage")
        farmDao.updateFarmSyncError(itemId, SyncState.ERROR.name, errorMessage)
        summary.addError("API Error for $itemId (Code $code): $errorMessage")
    }

    private fun parseApiError(errorBody: String?, httpCode: Int): String {
        if (errorBody == null) return "API returned HTTP $httpCode with no error body."
        return try {
            val errorDto = json.decodeFromString(ApiErrorResponseDto.serializer(), errorBody)
            "(${errorDto.errorCode}): ${errorDto.message} ${errorDto.details?.toString() ?: ""}"
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse error body: $errorBody", e)
            "HTTP $httpCode. Failed to parse error body: $errorBody"
        }
    }

    // Internal mutable summary for easier updates during sync
    private class SyncSummaryInternal {
        var newItemsUploaded: Int = 0
        var itemsUpdatedUpstream: Int = 0
        var itemsDeletedUpstream: Int = 0
        var newItemsDownloaded: Int = 0
        var itemsUpdatedLocallyFromServer: Int = 0
        var itemsDeletedLocallyFromServer: Int = 0
        var conflictsEncountered: Int = 0
        val errors: MutableList<String> = mutableListOf()

        fun addError(message: String) {
            errors.add(message)
        }

        fun toPublicSummary(): SyncSummary = SyncSummary(
            newItemsUploaded, itemsUpdatedUpstream, itemsDeletedUpstream,
            newItemsDownloaded, itemsUpdatedLocallyFromServer, itemsDeletedLocallyFromServer,
            conflictsEncountered, errors.toList()
        )
    }
}
