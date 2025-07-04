package com.example.rooster.feature.iot.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// --- Type Converters ---
class MapConverter {
    @TypeConverter
    fun fromString(value: String?): Map<String, String>? {
        if (value == null) {
            return emptyMap()
        }
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromMap(map: Map<String, String>?): String? {
        if (map == null) {
            return null
        }
        return Gson().toJson(map)
    }
}


// --- Entities ---
@Entity(tableName = "iot_devices")
@TypeConverters(MapConverter::class)
data class DeviceInfoEntity(
    @PrimaryKey val deviceId: String,
    val name: String,
    val type: String,
    val location: String,
    val status: String,
    val lastSeen: Long,
    val batteryLevel: Int?,
    val customProperties: Map<String, String> = emptyMap()
)

@Entity(tableName = "temperature_readings")
data class TemperatureReadingEntity(
    @PrimaryKey val readingId: String,
    val deviceId: String,
    val timestamp: Long,
    val temperature: Double,
    val unit: String,
    val synced: Boolean
)

@Entity(tableName = "humidity_readings")
data class HumidityReadingEntity(
    @PrimaryKey val readingId: String,
    val deviceId: String,
    val timestamp: Long,
    val humidity: Double,
    val unit: String,
    val synced: Boolean
)

@Entity(tableName = "feed_level_readings")
data class FeedLevelReadingEntity(
    @PrimaryKey val readingId: String,
    val deviceId: String,
    val timestamp: Long,
    val levelPercentage: Double,
    val unit: String,
    val synced: Boolean
)

@Entity(tableName = "water_consumption_readings")
data class WaterConsumptionReadingEntity(
    @PrimaryKey val readingId: String,
    val deviceId: String,
    val timestamp: Long,
    val volumeConsumed: Double,
    val unit: String,
    val synced: Boolean
)

@Entity(tableName = "light_level_readings")
data class LightLevelReadingEntity(
    @PrimaryKey val readingId: String,
    val deviceId: String,
    val timestamp: Long,
    val lux: Double,
    val unit: String,
    val synced: Boolean
)

@Entity(tableName = "iot_alerts")
data class AlertInfoEntity(
    @PrimaryKey val alertId: String,
    val deviceId: String?,
    val flockId: String?,
    val alertType: String,
    val severity: String,
    val message: String,
    val timestamp: Long,
    val acknowledged: Boolean
)

@Entity(tableName = "iot_device_configs")
@TypeConverters(MapConverter::class)
data class DeviceConfigEntity(
    @PrimaryKey val deviceId: String,
    var displayName: String,
    var location: String,
    var reportingIntervalMs: Long?,
    var customSettings: Map<String, String> = emptyMap(),
    var needsSync: Boolean = false
)

// Mapper functions to convert between domain models and entities
fun com.example.rooster.feature.iot.data.model.DeviceInfo.toEntity() = DeviceInfoEntity(
    deviceId = deviceId,
    name = name,
    type = type,
    location = location,
    status = status,
    lastSeen = lastSeen,
    batteryLevel = batteryLevel,
    customProperties = customProperties
)

fun DeviceInfoEntity.toDomain() = com.example.rooster.feature.iot.data.model.DeviceInfo(
    deviceId = deviceId,
    name = name,
    type = type,
    location = location,
    status = status,
    lastSeen = lastSeen,
    batteryLevel = batteryLevel,
    customProperties = customProperties
)

fun com.example.rooster.feature.iot.data.model.TemperatureReading.toEntity() = TemperatureReadingEntity(readingId, deviceId, timestamp, temperature, unit, synced)
fun TemperatureReadingEntity.toDomain() = com.example.rooster.feature.iot.data.model.TemperatureReading(readingId, deviceId, timestamp, temperature, unit, synced)

fun com.example.rooster.feature.iot.data.model.HumidityReading.toEntity() = HumidityReadingEntity(readingId, deviceId, timestamp, humidity, unit, synced)
fun HumidityReadingEntity.toDomain() = com.example.rooster.feature.iot.data.model.HumidityReading(readingId, deviceId, timestamp, humidity, unit, synced)

fun com.example.rooster.feature.iot.data.model.FeedLevelReading.toEntity() = FeedLevelReadingEntity(readingId, deviceId, timestamp, levelPercentage, unit, synced)
fun FeedLevelReadingEntity.toDomain() = com.example.rooster.feature.iot.data.model.FeedLevelReading(readingId, deviceId, timestamp, levelPercentage, unit, synced)

fun com.example.rooster.feature.iot.data.model.WaterConsumptionReading.toEntity() = WaterConsumptionReadingEntity(readingId, deviceId, timestamp, volumeConsumed, unit, synced)
fun WaterConsumptionReadingEntity.toDomain() = com.example.rooster.feature.iot.data.model.WaterConsumptionReading(readingId, deviceId, timestamp, volumeConsumed, unit, synced)

fun com.example.rooster.feature.iot.data.model.LightLevelReading.toEntity() = LightLevelReadingEntity(readingId, deviceId, timestamp, lux, unit, synced)
fun LightLevelReadingEntity.toDomain() = com.example.rooster.feature.iot.data.model.LightLevelReading(readingId, deviceId, timestamp, lux, unit, synced)

fun com.example.rooster.feature.iot.data.model.AlertInfo.toEntity() = AlertInfoEntity(alertId, deviceId, flockId, alertType, severity, message, timestamp, acknowledged)
fun AlertInfoEntity.toDomain() = com.example.rooster.feature.iot.data.model.AlertInfo(alertId, deviceId, flockId, alertType, severity, message, timestamp, acknowledged)

fun com.example.rooster.feature.iot.data.model.DeviceConfig.toEntity() = DeviceConfigEntity(deviceId, displayName, location, reportingIntervalMs, customSettings, needsSync)
fun DeviceConfigEntity.toDomain() = com.example.rooster.feature.iot.data.model.DeviceConfig(deviceId, displayName, location, reportingIntervalMs, customSettings, needsSync)
