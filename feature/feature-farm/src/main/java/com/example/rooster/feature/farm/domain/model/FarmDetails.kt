package com.example.rooster.feature.farm.domain.model

import java.util.*

/**
 * Domain model representing comprehensive Farm Details for enterprise traceability
 */
data class FarmDetails(
    val id: String,
    val ownerId: String,
    val name: String,
    val location: String,
    val establishedDate: Date,
    val registrationNumber: String?,
    val licenseNumber: String?,

    // Verification & Certification Badges
    val verified: Boolean,
    val certified: Boolean,
    val verificationLevel: VerificationLevel,
    val certificationAgency: String?,
    val certificationDate: Date?,
    val certificationExpiryDate: Date?,

    // Farm Statistics
    val totalFowls: Int,
    val totalHens: Int,
    val totalBreeders: Int,
    val totalChicks: Int,
    val activeFlocks: Int,

    // Health & Monitoring
    val lastHealthCheck: Date?,
    val vaccinationCompliance: Double, // percentage
    val mortalityRate: Double, // percentage

    // Productivity Metrics
    val eggProductionRate: Double?,
    val hatchingSuccessRate: Double?,
    val feedConversionRatio: Double?,

    // Compliance & Quality
    val biosecurityScore: Int, // 0-100
    val animalWelfareScore: Int, // 0-100
    val traceabilityScore: Int, // 0-100

    // Contact & Documentation
    val contactEmail: String?,
    val contactPhone: String?,
    val documents: List<String>?, // URLs to certification documents
    val photos: List<String>?, // Farm photos

    val createdAt: Date,
    val updatedAt: Date
)

data class FarmBadge(
    val type: BadgeType,
    val level: String,
    val description: String,
    val earnedDate: Date,
    val iconUrl: String?
)

enum class BadgeType {
    VERIFIED,
    CERTIFIED,
    ORGANIC,
    BIOSECURE,
    ANIMAL_WELFARE,
    TRACEABILITY,
    PRODUCTIVITY,
    SUSTAINABILITY
}