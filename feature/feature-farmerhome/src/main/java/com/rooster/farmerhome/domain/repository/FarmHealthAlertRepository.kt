package com.rooster.farmerhome.domain.repository

import com.rooster.farmerhome.core.common.util.DataState
import com.rooster.farmerhome.domain.model.FarmHealthAlert
import kotlinx.coroutines.flow.Flow

interface FarmHealthAlertRepository {
    fun getHealthAlertsForFarm(farmId: String): Flow<DataState<List<FarmHealthAlert>>> // Updated return type
    suspend fun markAlertAsRead(farmId: String, alertId: String): Result<Unit>
    // Add other methods like dismissing alerts, etc., if needed
}
