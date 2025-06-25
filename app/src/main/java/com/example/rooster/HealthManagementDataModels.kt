package com.example.rooster

import com.parse.ParseFile
import com.parse.ParseObject
import java.util.*

/**
 * Health Management Data Models for Vaccination and Medication Tracking
 *
 * Moderate-level feature implementation for comprehensive fowl health management
 */

// Vaccination Record Data Model

/**
 * Vaccination Record Data Model
 */
data class VaccinationRecord(
    val id: String = "",
    val birdId: String,
    val birdName: String = "",
    val vaccineType: VaccineType,
    val vaccineName: String,
    val dosage: String,
    val administeredDate: Date,
    val nextDueDate: Date? = null,
    val administeredBy: String = "",
    val veterinarianName: String = "",
    val batchNumber: String = "",
    val notes: String = "",
    val sideEffects: String = "",
    val isCompleted: Boolean = true,
    val reminderSet: Boolean = false,
    val cost: Double = 0.0,
    val region: String = "",
    val createdBy: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val photo: ParseFile? = null,
) {
    companion object {
        fun fromParseObject(parseObject: ParseObject): VaccinationRecord {
            return try {
                VaccinationRecord(
                    id = parseObject.objectId ?: "",
                    birdId = parseObject.getString("birdId") ?: "",
                    birdName = parseObject.getString("birdName") ?: "Unknown Bird",
                    vaccineType =
                        VaccineType.valueOf(
                            parseObject.getString("vaccineType") ?: "ROUTINE",
                        ),
                    vaccineName = parseObject.getString("vaccineName") ?: "",
                    dosage = parseObject.getString("dosage") ?: "",
                    administeredDate = parseObject.getDate("administeredDate") ?: Date(),
                    nextDueDate = parseObject.getDate("nextDueDate"),
                    administeredBy = parseObject.getString("administeredBy") ?: "",
                    veterinarianName = parseObject.getString("veterinarianName") ?: "",
                    batchNumber = parseObject.getString("batchNumber") ?: "",
                    notes = parseObject.getString("notes") ?: "",
                    sideEffects = parseObject.getString("sideEffects") ?: "",
                    isCompleted = parseObject.getBoolean("isCompleted"),
                    reminderSet = parseObject.getBoolean("reminderSet"),
                    cost = parseObject.getDouble("cost"),
                    region = parseObject.getString("region") ?: "",
                    createdBy = parseObject.getParseUser("createdBy")?.objectId ?: "",
                    createdAt = parseObject.createdAt ?: Date(),
                    updatedAt = parseObject.updatedAt ?: Date(),
                    photo = parseObject.getParseFile("photo"),
                )
            } catch (e: Exception) {
                VaccinationRecord(
                    birdId = "unknown",
                    vaccineType = VaccineType.ROUTINE,
                    vaccineName = "Unknown Vaccine",
                    dosage = "Unknown",
                    administeredDate = Date(),
                )
            }
        }
    }

    fun toParseObject(): ParseObject {
        val parseObject = ParseObject("Vaccination")
        parseObject.put("birdId", birdId)
        parseObject.put("birdName", birdName)
        parseObject.put("vaccineType", vaccineType.name)
        parseObject.put("vaccineName", vaccineName)
        parseObject.put("dosage", dosage)
        parseObject.put("administeredDate", administeredDate)
        nextDueDate?.let { parseObject.put("nextDueDate", it) }
        parseObject.put("administeredBy", administeredBy)
        parseObject.put("veterinarianName", veterinarianName)
        parseObject.put("batchNumber", batchNumber)
        parseObject.put("notes", notes)
        parseObject.put("sideEffects", sideEffects)
        parseObject.put("isCompleted", isCompleted)
        parseObject.put("reminderSet", reminderSet)
        parseObject.put("cost", cost)
        parseObject.put("region", region)
        parseObject.put("updatedAt", Date())
        photo?.let { parseObject.put("photo", it) }
        return parseObject
    }
}

// Medication Record Data Model
data class MedicationRecord(
    val id: String = "",
    val birdId: String,
    val birdName: String = "",
    val medicationType: MedicationType,
    val medicineName: String,
    val dosage: String,
    val frequency: String,
    // in days
    val duration: Int,
    val startDate: Date,
    val endDate: Date? = null,
    val administeredBy: String = "",
    val veterinarianName: String = "",
    val purpose: String = "",
    val instructions: String = "",
    val sideEffects: String = "",
    val isCompleted: Boolean = false,
    val reminderSet: Boolean = false,
    val cost: Double = 0.0,
    val region: String = "",
    val createdBy: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    var photo: ParseFile? = null,
) {
    companion object {
        fun fromParseObject(parseObject: ParseObject): MedicationRecord {
            return try {
                MedicationRecord(
                    id = parseObject.objectId ?: "",
                    birdId = parseObject.getString("birdId") ?: "",
                    birdName = parseObject.getString("birdName") ?: "Unknown Bird",
                    medicationType =
                        MedicationType.valueOf(
                            parseObject.getString("medicationType") ?: "ANTIBIOTIC",
                        ),
                    medicineName = parseObject.getString("medicineName") ?: "",
                    dosage = parseObject.getString("dosage") ?: "",
                    frequency = parseObject.getString("frequency") ?: "",
                    duration = parseObject.getInt("duration"),
                    startDate = parseObject.getDate("startDate") ?: Date(),
                    endDate = parseObject.getDate("endDate"),
                    administeredBy = parseObject.getString("administeredBy") ?: "",
                    veterinarianName = parseObject.getString("veterinarianName") ?: "",
                    purpose = parseObject.getString("purpose") ?: "",
                    instructions = parseObject.getString("instructions") ?: "",
                    sideEffects = parseObject.getString("sideEffects") ?: "",
                    isCompleted = parseObject.getBoolean("isCompleted"),
                    reminderSet = parseObject.getBoolean("reminderSet"),
                    cost = parseObject.getDouble("cost"),
                    region = parseObject.getString("region") ?: "",
                    createdBy = parseObject.getParseUser("createdBy")?.objectId ?: "",
                    createdAt = parseObject.createdAt ?: Date(),
                    updatedAt = parseObject.updatedAt ?: Date(),
                    photo = parseObject.getParseFile("photo"),
                )
            } catch (e: Exception) {
                MedicationRecord(
                    birdId = "unknown",
                    medicationType = MedicationType.ANTIBIOTIC,
                    medicineName = "Unknown Medicine",
                    dosage = "Unknown",
                    frequency = "Unknown",
                    duration = 0,
                    startDate = Date(),
                )
            }
        }
    }

    fun toParseObject(): ParseObject {
        val parseObject = ParseObject("Medication")
        parseObject.put("birdId", birdId)
        parseObject.put("birdName", birdName)
        parseObject.put("medicationType", medicationType.name)
        parseObject.put("medicineName", medicineName)
        parseObject.put("dosage", dosage)
        parseObject.put("frequency", frequency)
        parseObject.put("duration", duration)
        parseObject.put("startDate", startDate)
        endDate?.let { parseObject.put("endDate", it) }
        parseObject.put("administeredBy", administeredBy)
        parseObject.put("veterinarianName", veterinarianName)
        parseObject.put("purpose", purpose)
        parseObject.put("instructions", instructions)
        parseObject.put("sideEffects", sideEffects)
        parseObject.put("isCompleted", isCompleted)
        parseObject.put("reminderSet", reminderSet)
        parseObject.put("cost", cost)
        parseObject.put("region", region)
        parseObject.put("updatedAt", Date())
        photo?.let { parseObject.put("photo", it) }
        return parseObject
    }
}

// Health Schedule Data Model for FCM Reminders
data class HealthSchedule(
    val id: String = "",
    val birdId: String,
    val birdName: String = "",
    val scheduleType: HealthScheduleType,
    val title: String,
    val description: String,
    val scheduledDate: Date,
    val isCompleted: Boolean = false,
    val reminderSent: Boolean = false,
    val priority: HealthPriority = HealthPriority.MEDIUM,
    val region: String = "",
    val createdBy: String = "",
    val createdAt: Date = Date(),
) {
    companion object {
        fun fromParseObject(parseObject: ParseObject): HealthSchedule {
            return try {
                HealthSchedule(
                    id = parseObject.objectId ?: "",
                    birdId = parseObject.getString("birdId") ?: "",
                    birdName = parseObject.getString("birdName") ?: "Unknown Bird",
                    scheduleType =
                        HealthScheduleType.valueOf(
                            parseObject.getString("scheduleType") ?: "VACCINATION",
                        ),
                    title = parseObject.getString("title") ?: "",
                    description = parseObject.getString("description") ?: "",
                    scheduledDate = parseObject.getDate("scheduledDate") ?: Date(),
                    isCompleted = parseObject.getBoolean("isCompleted"),
                    reminderSent = parseObject.getBoolean("reminderSent"),
                    priority =
                        HealthPriority.valueOf(
                            parseObject.getString("priority") ?: "MEDIUM",
                        ),
                    region = parseObject.getString("region") ?: "",
                    createdBy = parseObject.getParseUser("createdBy")?.objectId ?: "",
                    createdAt = parseObject.createdAt ?: Date(),
                )
            } catch (e: Exception) {
                HealthSchedule(
                    birdId = "unknown",
                    scheduleType = HealthScheduleType.VACCINATION,
                    title = "Unknown Schedule",
                    description = "Error loading schedule",
                    scheduledDate = Date(),
                )
            }
        }
    }

    fun toParseObject(): ParseObject {
        val parseObject = ParseObject("HealthSchedule")
        parseObject.put("birdId", birdId)
        parseObject.put("birdName", birdName)
        parseObject.put("scheduleType", scheduleType.name)
        parseObject.put("title", title)
        parseObject.put("description", description)
        parseObject.put("scheduledDate", scheduledDate)
        parseObject.put("isCompleted", isCompleted)
        parseObject.put("reminderSent", reminderSent)
        parseObject.put("priority", priority.name)
        parseObject.put("region", region)
        return parseObject
    }
}

// Health Summary for Dashboard
data class HealthSummary(
    val totalBirds: Int,
    val healthyBirds: Int,
    val sickBirds: Int,
    // Within next 7 days
    val upcomingVaccinations: Int,
    val ongoingMedications: Int,
    // Last 30 days
    val mortalityRate: Double,
)

// Enums for Health Management
enum class VaccineType(val displayName: String, val description: String) {
    ROUTINE("Routine Vaccination", "Regular scheduled vaccinations"),
    EMERGENCY("Emergency Vaccination", "Emergency or outbreak response"),
    SEASONAL("Seasonal Vaccination", "Season-specific vaccinations"),
    PREVENTIVE("Preventive Vaccination", "Preventive care vaccinations"),
    BOOSTER("Booster Shot", "Follow-up booster vaccinations"),
}

enum class MedicationType(val displayName: String, val description: String) {
    ANTIBIOTIC("Antibiotic", "Bacterial infection treatment"),
    ANTIVIRAL("Antiviral", "Viral infection treatment"),
    ANTIFUNGAL("Antifungal", "Fungal infection treatment"),
    PAIN_RELIEF("Pain Relief", "Pain management medication"),
    SUPPLEMENT("Supplement", "Nutritional supplements"),
    DEWORMING("Deworming", "Parasite treatment"),
    VITAMIN("Vitamin", "Vitamin supplementation"),
    OTHER("Other", "Other medication types"),
}

enum class HealthScheduleType(val displayName: String) {
    VACCINATION("Vaccination"),
    MEDICATION("Medication"),
    HEALTH_CHECK("Health Check"),
    DEWORMING("Deworming"),
    SUPPLEMENT("Supplement"),
    VET_VISIT("Vet Visit"),
}

enum class HealthPriority(val displayName: String, val color: String) {
    LOW("Low Priority", "#4CAF50"),
    MEDIUM("Medium Priority", "#FF9800"),
    HIGH("High Priority", "#F44336"),
    URGENT("Urgent", "#D32F2F"),
}

// Vaccination Templates for Common Schedules
data class VaccinationTemplate(
    val name: String,
    val description: String,
    val ageWeeks: Int,
    val vaccineType: VaccineType,
    val dosage: String,
    val notes: String,
)

// Predefined Vaccination Schedules
object VaccinationSchedules {
    val STANDARD_POULTRY_SCHEDULE =
        listOf(
            VaccinationTemplate(
                "Marek's Disease",
                "Day-old chick vaccination at hatchery",
                ageWeeks = 0,
                vaccineType = VaccineType.ROUTINE,
                dosage = "0.2ml subcutaneous",
                notes = "Usually done at hatchery",
            ),
            VaccinationTemplate(
                "Newcastle Disease (ND)",
                "First ND vaccination",
                ageWeeks = 1,
                vaccineType = VaccineType.ROUTINE,
                dosage = "Eye drop or drinking water",
                notes = "Critical for respiratory protection",
            ),
            VaccinationTemplate(
                "Infectious Bursal Disease (IBD)",
                "First IBD vaccination",
                ageWeeks = 2,
                vaccineType = VaccineType.ROUTINE,
                dosage = "Drinking water",
                notes = "Protects immune system",
            ),
            VaccinationTemplate(
                "Newcastle Disease Booster",
                "Second ND vaccination",
                ageWeeks = 4,
                vaccineType = VaccineType.BOOSTER,
                dosage = "Eye drop or injection",
                notes = "Booster for continued protection",
            ),
            VaccinationTemplate(
                "Fowl Pox",
                "Fowl pox vaccination",
                ageWeeks = 8,
                vaccineType = VaccineType.ROUTINE,
                dosage = "Wing web injection",
                notes = "Protects against pox virus",
            ),
        )
}
