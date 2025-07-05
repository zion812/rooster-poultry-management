package com.example.rooster.feature.farm.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rooster.feature.farm.data.local.db.entity.FarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarm(farm: FarmEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarms(farms: List<FarmEntity>)

    @Update
    suspend fun updateFarm(farm: FarmEntity)

    @Delete
    suspend fun deleteFarm(farm: FarmEntity)

    @Query("DELETE FROM farms WHERE farmId = :farmId")
    suspend fun deleteFarmById(farmId: String)

    @Query("SELECT * FROM farms WHERE farmId = :farmId")
    suspend fun getFarmById(farmId: String): FarmEntity?

    @Query("SELECT * FROM farms ORDER BY name ASC")
    fun getAllFarms(): Flow<List<FarmEntity>>

    @Query("SELECT * FROM farms WHERE name LIKE :searchQuery OR location LIKE :searchQuery ORDER BY name ASC")
    fun searchFarms(searchQuery: String): Flow<List<FarmEntity>>

    // --- Synchronization specific queries ---

    @Query("SELECT * FROM farms WHERE needsSync = 1")
    suspend fun getFarmsRequiringSync(): List<FarmEntity>

    @Query("SELECT * FROM farms WHERE syncState = :syncState")
    suspend fun getFarmsBySyncState(syncState: String): List<FarmEntity>

    @Query("UPDATE farms SET needsSync = :needsSync, syncState = :syncState, serverLastUpdated = :serverLastUpdated, farmId = :newFarmId, lastSyncError = NULL, syncAttempts = 0 WHERE farmId = :oldFarmId")
    suspend fun updateFarmSyncStatus(oldFarmId: String, newFarmId: String, needsSync: Boolean, syncState: String, serverLastUpdated: Long?)

    @Query("UPDATE farms SET needsSync = :needsSync, syncState = :syncState, serverLastUpdated = :serverLastUpdated, lastSyncError = NULL, syncAttempts = 0 WHERE farmId = :farmId")
    suspend fun updateFarmSyncStatus(farmId: String, needsSync: Boolean, syncState: String, serverLastUpdated: Long?)


    @Query("UPDATE farms SET syncState = :syncState, lastSyncError = :errorMessage, syncAttempts = syncAttempts + 1 WHERE farmId = :farmId")
    suspend fun updateFarmSyncError(farmId: String, syncState: String, errorMessage: String)

    @Query("SELECT MAX(serverLastUpdated) FROM farms WHERE needsSync = 0") // Get the latest server timestamp from successfully synced items
    suspend fun getLatestServerTimestampForSyncedItems(): Long?

}
