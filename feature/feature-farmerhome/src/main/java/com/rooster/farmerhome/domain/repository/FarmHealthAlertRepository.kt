package com.rooster.farmerhome.domain.repository

 feature/dashboard-scaffolding-and-weather-api
import com.rooster.farmerhome.core.common.util.DataState

 main
import com.rooster.farmerhome.domain.model.FarmHealthAlert
import kotlinx.coroutines.flow.Flow

interface FarmHealthAlertRepository {
 feature/dashboard-scaffolding-and-weather-api
    fun getHealthAlertsForFarm(farmId: String): Flow<DataState<List<FarmHealthAlert>>> // Updated return type
    suspend fun markAlertAsRead(farmId: String, alertId: String): Result<Unit>

    fun getHealthAlertsForFarm(farmId: String): Flow<List<FarmHealthAlert>>
    suspend fun markAlertAsRead(alertId: String): Result<Unit>
 main
    // Add other methods like dismissing alerts, etc., if needed
}
