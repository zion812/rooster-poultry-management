package com.rooster.farmerhome.domain.model

import java.util.Date

enum class AlertSeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

data class FarmHealthAlert(
    val id: String,
    val flockId: String?, // Optional: if the alert is specific to a flock
    val farmId: String,   // Id of the farm this alert belongs to
    val title: String,
    val description: String,
    val severity: AlertSeverity,
    val timestamp: Long = System.currentTimeMillis(),
    val recommendedAction: String? = null,
    val isRead: Boolean = false
) {
    val alertDate: Date
        get() = Date(timestamp)
}
