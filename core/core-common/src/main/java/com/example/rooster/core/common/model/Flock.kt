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
    val biosecurityMeasures: List<BiosecurityMeasure> = emptyList()
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