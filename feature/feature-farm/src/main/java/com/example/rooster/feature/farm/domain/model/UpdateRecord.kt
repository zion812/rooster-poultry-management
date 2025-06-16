package com.example.rooster.feature.farm.domain.model

import java.util.*

/**
 * Represents an operational update for a fowl or section.
 */
data class UpdateRecord(
    val id: String,
    val fowlId: String,
    val type: UpdateType,
    val date: Date,
    val details: String,
    val attachmentUrl: String?
)

enum class UpdateType {
    CHICKS,
    ADULTS,
    BREEDING_SECTION,
    INCUBATION,
    BREEDERS,
    EGGS
}