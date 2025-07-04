package com.rooster.vethome.domain.model

import java.util.Date

// For Consultation Queue Management
enum class ConsultationRequestStatus {
    PENDING, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED
}

data class ConsultationQueueItem(
    val id: String,
    val farmerName: String,
    val farmLocation: String,
    val flockType: String, // e.g., "Broilers", "Layers"
    val issueSummary: String,
    val requestTime: Date,
    val status: ConsultationRequestStatus,
    val priority: Int // e.g., 1 (High) to 5 (Low)
)

// For Patient History Access (Summary for home screen)
data class PatientHistorySummary(
    val patientId: String, // Could be a flock ID or individual animal ID if tracked
    val farmName: String,
    val lastVisitDate: Date?,
    val briefDiagnosis: String?, // Last significant diagnosis
    val species: String // e.g., "Chicken - Broiler", "Chicken - Layer"
)

// For Health Alert Monitoring (Vet-specific view)
// This might reuse or adapt FarmHealthAlert from FarmerHome, or be a distinct model
// For simplicity, let's assume a slightly different view for vets.
enum class VetAlertSeverity {
    INFO, WARNING, URGENT, CRITICAL
}

data class VetHealthAlert(
    val alertId: String,
    val farmId: String,
    val farmName: String,
    val title: String,
    val description: String,
    val severity: VetAlertSeverity,
    val timestamp: Date,
    val suggestedActionsForVet: List<String>? = null,
    val isAcknowledged: Boolean = false
)
