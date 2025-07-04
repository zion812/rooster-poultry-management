package com.example.rooster.core.common.model

import kotlinx.serialization.Serializable

@Serializable
data class Bird(
    val id: String = "",
    val tagId: String = "",
    val breed: String = "",
    val gender: Gender = Gender.UNKNOWN,
    val hatchDate: Long = 0L,
    val weight: Double = 0.0,
    val parentBirdIds: List<String> = emptyList(),
    val healthRecords: List<HealthRecord> = emptyList(),
    val productionRecords: List<ProductionRecord> = emptyList(),
    val status: BirdStatus = BirdStatus.ACTIVE,
    val flockId: String = "",
    val currentLocation: String = "",
    val lineage: LineageInfo = LineageInfo(),
    val geneticMarkers: List<String> = emptyList(),
    val microchipId: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Serializable
data class Flock(
    val id: String = "",
    val name: String = "",
    val farmId: String = "",
    val breed: String = "",
    val flockType: FlockType = FlockType.LAYER,
    val totalBirds: Int = 0,
    val activeBirds: Int = 0,
    val establishedDate: Long = 0L,
    val housingType: HousingType = HousingType.CAGE,
    val feedingSchedule: List<FeedingSchedule> = emptyList(),
    val averageWeight: Double = 0.0,
    val mortalityRate: Double = 0.0,
    val productionRate: Double = 0.0,
    val lastHealthCheck: Long = 0L,
    val status: FlockStatus = FlockStatus.ACTIVE,
    val environmentalConditions: EnvironmentalConditions = EnvironmentalConditions(),
    val biosecurityMeasures: List<BiosecurityMeasure> = emptyList(),

    // Fields from FlockRegistryScreen / feature-farm's Flock model
    val isTraceable: Boolean = false,
    val ageGroup: AgeGroup? = null, // Using the AgeGroup enum defined in this file
    val sireId: String? = null,
    val damId: String? = null,
    val dateOfBirthTimestamp: Long? = null, // Storing as Long timestamp
    val placeOfBirth: String? = null,
    val identificationTag: String? = null, // Assuming a primary tag for the flock for now
    val colors: List<String>? = null, // List of dominant colors
    val currentWeight: Double? = null, // Current average or representative weight, distinct from overall averageWeight
    val height: Double? = null, // Average or representative height
    val specialty: String? = null,
    val proofImageUrls: List<String>? = null,
    val vaccinationHistory: List<VaccinationRecord>? = null, // Using VaccinationRecord defined in this file
    val genderDistribution: String? = null // E.g., "Mixed", "Mostly Hens", "All Roosters". Or use separate counts.
                                         // The `Gender` enum is for individual birds.
                                         // For now, a string. Could be more structured.
)

@Serializable
data class HealthRecord(
    val id: String = "",
    val birdId: String = "",
    val recordType: HealthRecordType = HealthRecordType.CHECKUP,
    val date: Long = System.currentTimeMillis(),
    val veterinarianId: String = "",
    val symptoms: List<String> = emptyList(),
    val diagnosis: String = "",
    val treatment: String = "",
    val medication: List<Medication> = emptyList(),
    val followUpDate: Long? = null,
    val notes: String = "",
    val attachments: List<String> = emptyList(),
    val severity: HealthSeverity = HealthSeverity.NORMAL,
    val cost: Double = 0.0
)

@Serializable
data class ProductionRecord(
    val id: String = "",
    val birdId: String = "",
    val date: Long = System.currentTimeMillis(),
    val eggsProduced: Int = 0,
    val eggWeight: Double = 0.0,
    val eggGrade: EggGrade = EggGrade.GRADE_A,
    val feedConsumption: Double = 0.0,
    val waterConsumption: Double = 0.0,
    val environmentalConditions: EnvironmentalConditions = EnvironmentalConditions(),
    val behaviorNotes: String = "",
    val performanceScore: Double = 0.0
)

@Serializable
data class FeedingSchedule(
    val feedType: String = "",
    val quantity: Double = 0.0,
    val frequency: Int = 0,
    val timing: List<String> = emptyList(),
    val nutritionalInfo: NutritionalInfo = NutritionalInfo(),
    val cost: Double = 0.0,
    val supplier: String = ""
)

@Serializable
data class Medication(
    val name: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val duration: Int = 0,
    val administrationMethod: String = "",
    val withdrawalPeriod: Int = 0,
    val batchNumber: String = "",
    val expiryDate: Long = 0L,
    val veterinarianApproval: Boolean = false
)

@Serializable
data class EnvironmentalConditions(
    val temperature: Double = 0.0,
    val humidity: Double = 0.0,
    val lightHours: Double = 0.0,
    val ventilation: String = "",
    val airQuality: AirQuality = AirQuality.GOOD,
    val noiseLevel: Double = 0.0,
    val recordedAt: Long = System.currentTimeMillis()
)

@Serializable
data class NutritionalInfo(
    val protein: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0,
    val vitamins: Map<String, Double> = emptyMap(),
    val minerals: Map<String, Double> = emptyMap(),
    val energy: Double = 0.0,
    val moisture: Double = 0.0
)

@Serializable
data class LineageInfo(
    val generation: Int = 0,
    val breedingProgram: String = "",
    val geneticQuality: GeneticQuality = GeneticQuality.STANDARD,
    val inbreedingCoefficient: Double = 0.0,
    val pedigreeComplete: Boolean = false
)

@Serializable
data class BiosecurityMeasure(
    val type: BiosecurityType,
    val description: String = "",
    val implementedDate: Long = System.currentTimeMillis(),
    val lastAudit: Long = 0L,
    val complianceLevel: ComplianceLevel = ComplianceLevel.FULL
)

enum class Gender {
    MALE, FEMALE, UNKNOWN
}

enum class BirdStatus {
    ACTIVE, SOLD, DECEASED, QUARANTINED, BREEDING, RETIRED
}

enum class FlockType {
    LAYER, BROILER, BREEDING, MIXED, CHICK, PULLET
}

enum class HousingType {
    CAGE, FREE_RANGE, BARN, ORGANIC, SEMI_INTENSIVE, INTENSIVE
}

enum class FlockStatus {
    ACTIVE, SOLD, COMPLETED, QUARANTINED, UNDER_TREATMENT
}

enum class HealthRecordType {
    CHECKUP, VACCINATION, TREATMENT, SURGERY, DEATH, QUARANTINE, RECOVERY
}

enum class HealthSeverity {
    NORMAL, MILD, MODERATE, SEVERE, CRITICAL
}

enum class EggGrade {
    GRADE_AA, GRADE_A, GRADE_B, ORGANIC, FREE_RANGE, PREMIUM
}

enum class AirQuality {
    EXCELLENT, GOOD, FAIR, POOR, HAZARDOUS
}

enum class GeneticQuality {
    SUPERIOR, HIGH, STANDARD, BELOW_AVERAGE
}

enum class BiosecurityType {
    DISINFECTION, QUARANTINE, ACCESS_CONTROL, WASTE_MANAGEMENT,
    FEED_SECURITY, WATER_TREATMENT, PEST_CONTROL
}

enum class ComplianceLevel {
    FULL, PARTIAL, NON_COMPLIANT, UNDER_REVIEW
}

// Enums from feature-farm/domain/model/Flock.kt & FlockRegistrationData.kt
// (to be consolidated or ensure they match if already present)

// Already in core-common: enum class Gender { MALE, FEMALE, UNKNOWN }
// No, core-common Gender is MALE, FEMALE, UNKNOWN. feature-farm Gender is MALE, FEMALE, UNKNOWN. They are compatible.

// Add AgeGroup from feature-farm
@Serializable // If it's part of other serializable classes
enum class AgeGroup {
    CHICKS,           // 0-2 weeks (example, align with FlockRegistryScreen)
    WEEKS_0_5,        // 0-5 weeks
    WEEKS_5_5MONTHS,  // 5 weeks - 5 months
    MONTHS_5_12PLUS,  // 5-12+ months
    UNKNOWN
}

// Consolidate FlockType. The one in feature-farm is more granular for bird types.
// The existing core-common one is: LAYER, BROILER, BREEDING, MIXED, CHICK, PULLET.
// feature-farm one is: FOWL, HEN, BREEDER, CHICK, ROOSTER, PULLET.
// Let's use a more comprehensive one, perhaps merging ideas or choosing one.
// For now, I'll keep the existing core-common FlockType and assume it's sufficient,
// or that `FlockRegistrationData.flockType` would map to it.
// If `feature-farm.FlockType` is preferred, this enum definition here needs to change.
// For this step, I will assume the existing core-common FlockType is the canonical one.

// VaccinationRecord (simple version for now)
@Serializable
data class VaccinationRecord(
    val vaccineName: String,
    val dateTimestamp: Long,
    val notes: String? = null
)


// Traceability Models
@Serializable
data class TraceabilityRecord(
    val id: String = "",
    val birdId: String = "",
    val eventType: TraceabilityEvent,
    val timestamp: Long = System.currentTimeMillis(),
    val location: String = "",
    val description: String = "",
    val verificationHash: String = "",
    val documentUrls: List<String> = emptyList(),
    val verifiedBy: String = ""
)

enum class TraceabilityEvent {
    HATCHING, VACCINATION, MOVEMENT, TREATMENT, SALE, PROCESSING, TESTING
}

// IoT Integration Models
@Serializable
data class SensorData(
    val id: String = "",
    val sensorId: String = "",
    val sensorType: SensorType,
    val reading: Double = 0.0,
    val unit: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val location: String = "",
    val status: SensorStatus = SensorStatus.ACTIVE,
    val calibrationDate: Long = 0L
)

enum class SensorType {
    TEMPERATURE, HUMIDITY, AMMONIA, CO2, WEIGHT, SOUND, MOTION, LIGHT
}

enum class SensorStatus {
    ACTIVE, INACTIVE, MAINTENANCE, ERROR
}