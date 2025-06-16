package com.example.rooster.feature.farm.domain.model

import java.util.*

/**
 * Enhanced Flock Registration Data for Traceable/Non-Traceable Registry System
 */
data class FlockRegistrationData(
    val ownerId: String,
    val registryType: RegistryType,
    val ageGroup: AgeGroup,

    // Basic Required Fields
    val breed: String?,
    val weight: Double?,
    val colors: List<String>?,
    val gender: Gender?,
    val identification: String?,
    val size: Int?,
    val specialty: String?,
    val proofs: List<String>?, // Photo/document URLs

    // Traceable-Specific Fields
    val fatherId: String?,
    val motherId: String?,
    val placeOfBirth: String?,
    val dateOfBirth: Date?,
    val vaccinationRecords: List<VaccinationRecord>?,
    val height: Double?,

    // Verification Status
    val requiresVerification: Boolean = true,
    val verificationNotes: String?
)

/**
 * Registry Type Classification
 */
enum class RegistryType {
    TRACEABLE,
    NON_TRACEABLE
}

/**
 * Registry Requirements by Age Group and Type
 */
object RegistryRequirements {

    fun getRequiredFields(registryType: RegistryType, ageGroup: AgeGroup): Set<String> {
        return when (registryType) {
            RegistryType.TRACEABLE -> getTraceableRequirements(ageGroup)
            RegistryType.NON_TRACEABLE -> getNonTraceableRequirements()
        }
    }

    private fun getTraceableRequirements(ageGroup: AgeGroup): Set<String> {
        val baseFields = setOf(
            "breed", "colors", "proofs", "placeOfBirth",
            "dateOfBirth", "fatherId", "motherId", "vaccinationRecords"
        )

        return when (ageGroup) {
            AgeGroup.CHICKS -> baseFields

            AgeGroup.WEEKS_0_5 -> baseFields + setOf(
                "weight", "height", "gender", "identification"
            )

            AgeGroup.WEEKS_5_5MONTHS -> baseFields + setOf(
                "weight", "height", "gender", "identification",
                "size", "specialty"
            )

            AgeGroup.MONTHS_5_12PLUS -> baseFields + setOf(
                "weight", "height", "gender", "identification",
                "size", "specialty"
            )
        }
    }

    private fun getNonTraceableRequirements(): Set<String> {
        return setOf(
            "colors", "weight", "height", "gender",
            "identification", "size", "proofs", "specialty"
        )
    }

    fun getOptionalFields(registryType: RegistryType): Set<String> {
        return when (registryType) {
            RegistryType.TRACEABLE -> emptySet() // All required for traceable
            RegistryType.NON_TRACEABLE -> setOf(
                "fatherId", "motherId", "placeOfBirth",
                "dateOfBirth", "vaccinationRecords"
            )
        }
    }
}
