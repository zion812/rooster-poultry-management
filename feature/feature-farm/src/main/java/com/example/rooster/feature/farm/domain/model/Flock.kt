package com.example.rooster.feature.farm.domain.model

import java.util.*

/**
 * Enhanced Flock model with enterprise-grade traceability and comprehensive metadata
 */
data class Flock(
    val id: String,
    val ownerId: String,
    val fatherId: String?,
    val motherId: String?,
    val type: FlockType,
    val name: String,
    val breed: String?,
    val weight: Float?,
    val height: Float?,
    val color: String?,
    val gender: Gender?,

    // Verification & Certification
    val certified: Boolean,
    val verified: Boolean,
    val verificationLevel: VerificationLevel,
    val traceable: Boolean,

    // Age & Development
    val ageGroup: AgeGroup,
    val dateOfBirth: Date?,
    val placeOfBirth: String?,
    val currentAge: Int?, // in days

    // Health & Medical
    val vaccinationStatus: VaccinationStatus,
    val lastVaccinationDate: Date?,
    val healthStatus: HealthStatus,
    val lastHealthCheck: Date?,

    // Identification & Documentation
    val identification: String?, // Unique identifier like tag/band
    val registryNumber: String?,
    val proofs: List<String>?, // Photo/document URLs
    val specialty: String?, // Special characteristics

    // Performance Metrics
    val productivityScore: Int?, // 0-100
    val growthRate: Double?,
    val feedConversionRatio: Double?,

    // Status & Availability
    val status: FlockStatus,
    val forSale: Boolean,
    val price: Double?,
    val purpose: List<Purpose>?,

    val createdAt: Date,
    val updatedAt: Date
)

enum class FlockType {
    FOWL,
    HEN,
    BREEDER,
    CHICK,
    ROOSTER,
    PULLET
}

enum class AgeGroup {
    CHICKS,           // 0-2 weeks
    WEEKS_0_5,        // 0-5 weeks
    WEEKS_5_5MONTHS,  // 5 weeks - 5 months
    MONTHS_5_12PLUS,  // 5-12+ months
    UNKNOWN
}

enum class Gender {
    MALE,
    FEMALE,
    UNKNOWN
}

enum class VaccinationStatus {
    UP_TO_DATE,
    OVERDUE,
    PARTIAL,
    NOT_STARTED,
    UNKNOWN
}

enum class HealthStatus {
    EXCELLENT,
    GOOD,
    FAIR,
    POOR,
    SICK,
    QUARANTINED,
    UNKNOWN
}

enum class FlockStatus {
    ACTIVE,
    SOLD,
    DECEASED,
    TRANSFERRED,
    BREEDING,
    INCUBATING,
    UNKNOWN
}

enum class VerificationLevel {
    UNVERIFIED,
    BASIC,
    STANDARD,
    PREMIUM,
    ENTERPRISE
}

enum class Purpose {
    BREEDING_STOCK,
    MEAT_PRODUCTION,
    EGG_PRODUCTION,
    SHOW_COMPETITION,
    GENETIC_PRESERVATION
}
