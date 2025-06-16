package com.example.rooster.feature.farm.domain.model

import java.util.*

/**
 * Represents a comprehensive vaccination event for a fowl.
 */
data class VaccinationRecord(
    val id: String,
    val fowlId: String,
    val vaccineName: String,
    val dosage: String?,
    val veterinarian: String?,
    val nextDueDate: Date?,
    val notes: String?,
    val photos: List<String>,
    val recordedAt: Date
)
