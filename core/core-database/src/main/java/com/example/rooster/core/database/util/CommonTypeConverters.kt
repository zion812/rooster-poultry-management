package com.example.rooster.core.database.util

import androidx.room.TypeConverter
import com.example.rooster.core.common.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CommonTypeConverters {

    private val gson = Gson()

    // String List Converters
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            val listType = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, listType)
        }
    }

    // Enum Converters
    @TypeConverter
    fun fromFlockType(value: FlockType?): String? = value?.name

    @TypeConverter
    fun toFlockType(value: String?): FlockType? =
        value?.let { enumValueOf<FlockType>(it) }

    @TypeConverter
    fun fromHousingType(value: HousingType?): String? = value?.name

    @TypeConverter
    fun toHousingType(value: String?): HousingType? =
        value?.let { enumValueOf<HousingType>(it) }

    @TypeConverter
    fun fromFlockStatus(value: FlockStatus?): String? = value?.name

    @TypeConverter
    fun toFlockStatus(value: String?): FlockStatus? =
        value?.let { enumValueOf<FlockStatus>(it) }

    @TypeConverter
    fun fromAgeGroup(value: AgeGroup?): String? = value?.name

    @TypeConverter
    fun toAgeGroup(value: String?): AgeGroup? =
        value?.let { enumValueOf<AgeGroup>(it) }

    @TypeConverter
    fun fromGender(value: Gender?): String? = value?.name

    @TypeConverter
    fun toGender(value: String?): Gender? =
        value?.let { enumValueOf<Gender>(it) }

    @TypeConverter
    fun fromBirdStatus(value: BirdStatus?): String? = value?.name

    @TypeConverter
    fun toBirdStatus(value: String?): BirdStatus? =
        value?.let { enumValueOf<BirdStatus>(it) }

    @TypeConverter
    fun fromHealthRecordType(value: HealthRecordType?): String? = value?.name

    @TypeConverter
    fun toHealthRecordType(value: String?): HealthRecordType? =
        value?.let { enumValueOf<HealthRecordType>(it) }

    @TypeConverter
    fun fromHealthSeverity(value: HealthSeverity?): String? = value?.name

    @TypeConverter
    fun toHealthSeverity(value: String?): HealthSeverity? =
        value?.let { enumValueOf<HealthSeverity>(it) }

    @TypeConverter
    fun fromEggGrade(value: EggGrade?): String? = value?.name

    @TypeConverter
    fun toEggGrade(value: String?): EggGrade? =
        value?.let { enumValueOf<EggGrade>(it) }

    @TypeConverter
    fun fromSensorType(value: SensorType?): String? = value?.name

    @TypeConverter
    fun toSensorType(value: String?): SensorType? =
        value?.let { enumValueOf<SensorType>(it) }

    @TypeConverter
    fun fromSensorStatus(value: SensorStatus?): String? = value?.name

    @TypeConverter
    fun toSensorStatus(value: String?): SensorStatus? =
        value?.let { enumValueOf<SensorStatus>(it) }

    @TypeConverter
    fun fromTraceabilityEvent(value: TraceabilityEvent?): String? = value?.name

    @TypeConverter
    fun toTraceabilityEvent(value: String?): TraceabilityEvent? =
        value?.let { enumValueOf<TraceabilityEvent>(it) }

    // Sync Status Converter
    @TypeConverter
    fun fromSyncStatus(value: com.example.rooster.core.database.entity.SyncStatus?): String? =
        value?.name

    @TypeConverter
    fun toSyncStatus(value: String?): com.example.rooster.core.database.entity.SyncStatus? =
        value?.let { enumValueOf<com.example.rooster.core.database.entity.SyncStatus>(it) }

    // Complex Object Converters
    @TypeConverter
    fun fromFeedingScheduleList(value: List<FeedingSchedule>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toFeedingScheduleList(value: String?): List<FeedingSchedule>? {
        return value?.let {
            val listType = object : TypeToken<List<FeedingSchedule>>() {}.type
            gson.fromJson(it, listType)
        }
    }

    @TypeConverter
    fun fromEnvironmentalConditions(value: EnvironmentalConditions?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toEnvironmentalConditions(value: String?): EnvironmentalConditions? {
        return value?.let { gson.fromJson(it, EnvironmentalConditions::class.java) }
    }

    @TypeConverter
    fun fromBiosecurityMeasureList(value: List<BiosecurityMeasure>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toBiosecurityMeasureList(value: String?): List<BiosecurityMeasure>? {
        return value?.let {
            val listType = object : TypeToken<List<BiosecurityMeasure>>() {}.type
            gson.fromJson(it, listType)
        }
    }

    @TypeConverter
    fun fromVaccinationRecordList(value: List<VaccinationRecord>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toVaccinationRecordList(value: String?): List<VaccinationRecord>? {
        return value?.let {
            val listType = object : TypeToken<List<VaccinationRecord>>() {}.type
            gson.fromJson(it, listType)
        }
    }

    @TypeConverter
    fun fromHealthRecordList(value: List<HealthRecord>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toHealthRecordList(value: String?): List<HealthRecord>? {
        return value?.let {
            val listType = object : TypeToken<List<HealthRecord>>() {}.type
            gson.fromJson(it, listType)
        }
    }

    @TypeConverter
    fun fromProductionRecordList(value: List<ProductionRecord>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toProductionRecordList(value: String?): List<ProductionRecord>? {
        return value?.let {
            val listType = object : TypeToken<List<ProductionRecord>>() {}.type
            gson.fromJson(it, listType)
        }
    }

    @TypeConverter
    fun fromMedicationList(value: List<Medication>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toMedicationList(value: String?): List<Medication>? {
        return value?.let {
            val listType = object : TypeToken<List<Medication>>() {}.type
            gson.fromJson(it, listType)
        }
    }

    // Long nullable converters for dates
    @TypeConverter
    fun fromNullableLong(value: Long?): Long? = value

    @TypeConverter
    fun toNullableLong(value: Long?): Long? = value

    // Double nullable converters
    @TypeConverter
    fun fromNullableDouble(value: Double?): Double? = value

    @TypeConverter
    fun toNullableDouble(value: Double?): Double? = value
}