package com.rooster.farmerhome.data.local.datasource

import com.rooster.farmerhome.data.local.model.FarmHealthAlertEntity
import kotlinx.coroutines.flow.Flow

interface FarmHealthAlertLocalDataSource {
    fun getHealthAlertsForFarm(farmId: String): Flow<List<FarmHealthAlertEntity>>
    suspend fun insertHealthAlertsForFarm(farmId: String, alerts: List<FarmHealthAlertEntity>) // Changed signature
    suspend fun markAlertAsRead(farmId: String, alertId: String): Int // Marks as read (true)
    suspend fun markAlertAsUnread(farmId: String, alertId: String): Int // Marks as unread (false)
    suspend fun deleteAlertsForFarm(farmId: String)
}
