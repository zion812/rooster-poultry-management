package com.example.rooster.feature.farm.domain.model

import java.util.*

/**
 * Represents a mortality event for a fowl.
 */
data class MortalityRecord(
    val id: String,
    val fowlId: String,
    val cause: String,
    val description: String?,
    val weight: Float?,
    val photos: List<String>?,
    val recordedAt: Date,
    val createdAt: Date
)
