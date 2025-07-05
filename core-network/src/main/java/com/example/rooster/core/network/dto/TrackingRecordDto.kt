package com.example.rooster.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- Health Records ---

@Serializable
data class HealthRecordInputDto(
    @SerialName("record_type") val recordType: String, // "Disease Incident", "Vaccination", "Mortality", "General Checkup"
    @SerialName("record_date") val recordDate: String, // ISO 8601 DateTime string e.g., "YYYY-MM-DDTHH:MM:SS" or "YYYY-MM-DD HH:MM"
    @SerialName("details") val details: String,
    @SerialName("veterinarian") val veterinarian: String? = null,
    @SerialName("cost") val cost: Double? = null,

    // Disease Incident specific
    @SerialName("disease_name") val diseaseName: String? = null,
    @SerialName("symptoms") val symptoms: List<String>? = null, // Enum values as strings
    @SerialName("treatment_administered") val treatmentAdministered: String? = null,
    @SerialName("affected_count") val affectedCount: Int? = null,

    // Vaccination specific
    @SerialName("vaccine_name") val vaccineName: String? = null,
    @SerialName("administered_by") val administeredBy: String? = null,
    @SerialName("dosage") val dosage: String? = null,
    @SerialName("vaccinated_count") val vaccinatedCount: Int? = null,

    // Mortality specific
    @SerialName("cause_of_death") val causeOfDeath: String? = null,
    @SerialName("number_of_deaths") val numberOfDeaths: Int? = null,
    @SerialName("post_mortem_findings") val postMortemFindings: String? = null
)

@Serializable
sealed class HealthRecordDto {
    @SerialName("record_id") abstract val recordId: String
    @SerialName("flock_id") abstract val flockId: String
    @SerialName("record_type") abstract val recordType: String
    @SerialName("record_date") abstract val recordDate: String // ISO 8601 DateTime string
    @SerialName("details") abstract val details: String
    @SerialName("veterinarian") abstract val veterinarian: String?
    @SerialName("cost") abstract val cost: Double?
}

@Serializable
@SerialName("General Checkup") // Matches record_type value
data class GeneralCheckupRecordDto(
    override val recordId: String,
    override val flockId: String,
    override val recordType: String = "General Checkup",
    override val recordDate: String,
    override val details: String,
    override val veterinarian: String? = null,
    override val cost: Double? = null
) : HealthRecordDto()

@Serializable
@SerialName("Disease Incident") // Matches record_type value
data class DiseaseIncidentRecordDto(
    override val recordId: String,
    override val flockId: String,
    override val recordType: String = "Disease Incident",
    override val recordDate: String,
    override val details: String,
    override val veterinarian: String? = null,
    override val cost: Double? = null,
    @SerialName("disease_name") val diseaseName: String,
    @SerialName("symptoms") val symptoms: List<String>, // Enum values as strings
    @SerialName("treatment_administered") val treatmentAdministered: String? = null,
    @SerialName("affected_count") val affectedCount: Int? = null
) : HealthRecordDto()

@Serializable
@SerialName("Vaccination") // Matches record_type value
data class VaccinationRecordDto(
    override val recordId: String,
    override val flockId: String,
    override val recordType: String = "Vaccination",
    override val recordDate: String,
    override val details: String,
    override val veterinarian: String? = null,
    override val cost: Double? = null,
    @SerialName("vaccine_name") val vaccineName: String,
    @SerialName("administered_by") val administeredBy: String,
    @SerialName("dosage") val dosage: String? = null,
    @SerialName("vaccinated_count") val vaccinatedCount: Int? = null
) : HealthRecordDto()

@Serializable
@SerialName("Mortality") // Matches record_type value
data class MortalityRecordDto(
    override val recordId: String,
    override val flockId: String,
    override val recordType: String = "Mortality",
    override val recordDate: String,
    override val details: String,
    override val veterinarian: String? = null,
    override val cost: Double? = null,
    @SerialName("cause_of_death") val causeOfDeath: String,
    @SerialName("number_of_deaths") val numberOfDeaths: Int,
    @SerialName("post_mortem_findings") val postMortemFindings: String? = null
) : HealthRecordDto()


// --- Production Records ---
@Serializable
data class ProductionRecordDto(
    @SerialName("record_id") val recordId: String,
    @SerialName("flock_id") val flockId: String,
    @SerialName("record_date") val recordDate: String, // YYYY-MM-DD
    @SerialName("total_eggs_laid") val totalEggsLaid: Int,
    @SerialName("damaged_eggs") val damagedEggs: Int? = null,
    @SerialName("average_egg_weight_gm") val averageEggWeightGm: Double? = null,
    @SerialName("notes") val notes: String? = null
)

@Serializable
data class ProductionRecordInputDto(
    @SerialName("record_date") val recordDate: String, // YYYY-MM-DD
    @SerialName("total_eggs_laid") val totalEggsLaid: Int,
    @SerialName("damaged_eggs") val damagedEggs: Int? = null,
    @SerialName("average_egg_weight_gm") val averageEggWeightGm: Double? = null,
    @SerialName("notes") val notes: String? = null
)

// --- Feed Consumption Records ---
@Serializable
data class FeedConsumptionRecordDto(
    @SerialName("record_id") val recordId: String,
    @SerialName("flock_id") val flockId: String,
    @SerialName("record_date") val recordDate: String, // YYYY-MM-DD
    @SerialName("feed_type") val feedType: String,
    @SerialName("quantity_kg") val quantityKg: Double,
    @SerialName("cost_per_kg") val costPerKg: Double? = null,
    @SerialName("notes") val notes: String? = null
)

@Serializable
data class FeedConsumptionRecordInputDto(
    @SerialName("record_date") val recordDate: String, // YYYY-MM-DD
    @SerialName("feed_type") val feedType: String,
    @SerialName("quantity_kg") val quantityKg: Double,
    @SerialName("cost_per_kg") val costPerKg: Double? = null,
    @SerialName("notes") val notes: String? = null
)

// --- Growth Records ---
@Serializable
data class GrowthRecordDto(
    @SerialName("record_id") val recordId: String,
    @SerialName("flock_id") val flockId: String,
    @SerialName("record_date") val recordDate: String, // YYYY-MM-DD
    @SerialName("average_weight_grams") val averageWeightGrams: Double,
    @SerialName("number_of_birds_weighed") val numberOfBirdsWeighed: Int,
    @SerialName("feed_conversion_ratio") val feedConversionRatio: Double? = null,
    @SerialName("notes") val notes: String? = null
)

@Serializable
data class GrowthRecordInputDto(
    @SerialName("record_date") val recordDate: String, // YYYY-MM-DD
    @SerialName("average_weight_grams") val averageWeightGrams: Double,
    @SerialName("number_of_birds_weighed") val numberOfBirdsWeighed: Int,
    @SerialName("feed_conversion_ratio") val feedConversionRatio: Double? = null,
    @SerialName("notes") val notes: String? = null
)

// --- Environment Records ---
@Serializable
data class EnvironmentRecordDto(
    @SerialName("record_id") val recordId: String,
    @SerialName("flock_id") val flockId: String,
    @SerialName("record_date") val recordDate: String, // ISO 8601 DateTime string
    @SerialName("temperature_celsius") val temperatureCelsius: Double? = null,
    @SerialName("humidity_percent") val humidityPercent: Double? = null,
    @SerialName("ammonia_ppm") val ammoniaPpm: Double? = null,
    @SerialName("carbon_dioxide_ppm") val carbonDioxidePpm: Double? = null,
    @SerialName("light_intensity_lux") val lightIntensityLux: Double? = null,
    @SerialName("notes") val notes: String? = null
    // Removed sensor_id to align with the provided openapi.yaml
)

@Serializable
data class EnvironmentRecordInputDto(
    @SerialName("record_date") val recordDate: String, // ISO 8601 DateTime string
    @SerialName("temperature_celsius") val temperatureCelsius: Double? = null,
    @SerialName("humidity_percent") val humidityPercent: Double? = null,
    @SerialName("ammonia_ppm") val ammoniaPpm: Double? = null,
    @SerialName("carbon_dioxide_ppm") val carbonDioxidePpm: Double? = null,
    @SerialName("light_intensity_lux") val lightIntensityLux: Double? = null,
    @SerialName("notes") val notes: String? = null
    // Removed sensor_id to align with the provided openapi.yaml
)

// --- Alert Response ---
@Serializable
data class AlertCheckResponseDto(
    @SerialName("alert") val alert: Boolean,
    @SerialName("message") val message: String
)
