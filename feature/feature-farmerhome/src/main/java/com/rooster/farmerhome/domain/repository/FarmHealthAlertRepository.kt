package com.rooster.farmerhome.domain.repository

import com.rooster.farmerhome.domain.model.FarmHealthAlert
import kotlinx.coroutines.flow.Flow

interface FarmHealthAlertRepository {
    fun getHealthAlertsForFarm(farmId: String): Flow<List<FarmHealthAlert>>
    suspend fun markAlertAsRead(alertId: String): Result<Unit>
    // Add other methods like dismissing alerts, etc., if needed
}
