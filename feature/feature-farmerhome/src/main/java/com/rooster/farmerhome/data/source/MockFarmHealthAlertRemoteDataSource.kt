package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.AlertSeverity
import com.rooster.farmerhome.domain.model.FarmHealthAlert
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class MockFarmHealthAlertRemoteDataSource @Inject constructor() : FarmHealthAlertRemoteDataSource {

    private val mockAlerts = mutableListOf(
        FarmHealthAlert(
            id = UUID.randomUUID().toString(),
            farmId = "farm123",
            flockId = "flockA",
            title = "High Temperature Warning",
            description = "Temperature in Hen House 1 exceeded 30°C. Check ventilation.",
            severity = AlertSeverity.HIGH,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 30, // 30 mins ago
            recommendedAction = "Check ventilation system and provide cool water."
        ),
        FarmHealthAlert(
            id = UUID.randomUUID().toString(),
            farmId = "farm123",
            flockId = "flockB",
            title = "Low Feed Level",
            description = "Feed level in Silo 2 is below 10%. Schedule refill.",
            severity = AlertSeverity.MEDIUM,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2, // 2 hours ago
            recommendedAction = "Order new batch of feed.",
            isRead = true
        ),
        FarmHealthAlert(
            id = UUID.randomUUID().toString(),
            farmId = "farm456", // Different farm
            title = "Unusual Mortality Rate",
            description = "Observed higher than average mortality in Broiler Flock C.",
            severity = AlertSeverity.CRITICAL,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 5, // 5 mins ago
            recommendedAction = "Isolate affected birds and consult veterinarian immediately."
        )
    )

    override fun getHealthAlertsForFarm(farmId: String): Flow<List<FarmHealthAlert>> = flow {
        delay(500) // Simulate network delay
        emit(mockAlerts.filter { it.farmId == farmId }.sortedByDescending { it.timestamp })
    }

    override suspend fun markAlertAsRead(alertId: String): Result<Unit> {
        delay(200) // Simulate network delay
        val alert = mockAlerts.find { it.id == alertId }
        return if (alert != null) {
            val index = mockAlerts.indexOf(alert)
            mockAlerts[index] = alert.copy(isRead = true)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Alert not found"))
        }
    }
}
