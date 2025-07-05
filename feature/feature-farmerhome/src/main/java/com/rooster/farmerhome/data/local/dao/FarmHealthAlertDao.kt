package com.rooster.farmerhome.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rooster.farmerhome.data.local.model.FarmHealthAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmHealthAlertDao {

    @Query("SELECT * FROM farm_health_alert WHERE farmId = :farmId ORDER BY timestamp DESC")
    fun getHealthAlertsForFarm(farmId: String): Flow<List<FarmHealthAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthAlerts(alerts: List<FarmHealthAlertEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthAlert(alert: FarmHealthAlertEntity)

    // Update an existing alert, typically to mark as read
    @Update
    suspend fun updateHealthAlert(alert: FarmHealthAlertEntity) // General update

    @Query("UPDATE farm_health_alert SET isRead = :isRead WHERE id = :alertId") // More generic
    suspend fun updateReadStatus(alertId: String, isRead: Boolean): Int

    // Keep this specific one if it's used by a direct "mark as read" without needing farmId at DAO level
    // However, the new updateReadStatus is more flexible.
    // For consistency with repository needing farmId for remote, let's assume higher layers handle farmId checks.
    // @Query("UPDATE farm_health_alert SET isRead = 1 WHERE id = :alertId AND farmId = :farmId")
    // suspend fun markAlertAsRead(farmId: String, alertId: String): Int

    @Query("DELETE FROM farm_health_alert WHERE farmId = :farmId")
    suspend fun deleteAlertsForFarm(farmId: String)

    @Query("DELETE FROM farm_health_alert WHERE id = :alertId")
    suspend fun deleteAlert(alertId: String)
}
