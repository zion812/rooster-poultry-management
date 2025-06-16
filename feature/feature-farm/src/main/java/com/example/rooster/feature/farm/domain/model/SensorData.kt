package com.example.rooster.feature.farm.domain.model

import java.util.*

/**
 * Represents comprehensive sensor readings from the farm's IoT devices.
 */
data class SensorData(
    val id: String,
    val deviceId: String,
    val temperature: Float?,
    val humidity: Float?,
    val airQuality: Float?,
    val lightLevel: Float?,
    val noiseLevel: Float?,
    val timestamp: Date
)

enum class SensorType {
    TEMPERATURE,
    HUMIDITY,
    AIR_QUALITY,
    LIGHT_LEVEL,
    NOISE_LEVEL,
    FEED_LEVEL,
    WATER_LEVEL,
    MOTION
}
