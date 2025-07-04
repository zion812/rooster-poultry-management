package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.FarmHealthAlert
import kotlinx.coroutines.flow.Flow

interface FarmHealthAlertRemoteDataSource {
    fun getHealthAlertsForFarm(farmId: String): Flow<List<FarmHealthAlert>>
    suspend fun markAlertAsRead(alertId: String): Result<Unit>
    // TODO: Define how alerts are fetched (e.g., from Firestore, REST API connected to Python backend)
}
