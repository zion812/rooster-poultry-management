package com.example.rooster.models

/**
 * Represents a veterinary consultation request submitted by a farmer.
 */
data class VetConsultationRequest(
    val id: String,
    val animalId: String,
    val issueDescription: String,
    val photoUrls: List<String> = emptyList(),
    val preferredDate: Long,
    val status: String = "PENDING",
)
