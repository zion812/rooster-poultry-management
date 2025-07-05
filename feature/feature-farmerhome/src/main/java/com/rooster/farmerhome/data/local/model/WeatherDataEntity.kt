package com.rooster.farmerhome.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_data")
data class WeatherDataEntity(
    @PrimaryKey
    val id: String = "current_weather", // Fixed ID for single cache entry
    val temperature: String,
    val humidity: String,
    val precipitation: String,
    val windSpeed: String,
    val description: String,
    val location: String?,
    val timestamp: Long, // To check for staleness
    val apiError: String? = null // Store API-level errors if any
)

// Mapper functions
fun WeatherDataEntity.toDomain(): com.rooster.farmerhome.domain.model.WeatherData {
    return com.rooster.farmerhome.domain.model.WeatherData(
        temperature = temperature,
        humidity = humidity,
        precipitation = precipitation,
        windSpeed = windSpeed,
        description = description,
        location = location,
        error = apiError // Map the stored API error back to the domain model's error field
    )
}

fun com.rooster.farmerhome.domain.model.WeatherData.toEntity(locationName: String?): WeatherDataEntity {
    return WeatherDataEntity(
        temperature = temperature,
        humidity = humidity,
        precipitation = precipitation,
        windSpeed = windSpeed,
        description = description,
        location = this.location ?: locationName, // Use location from domain if present, else from parameter
        timestamp = System.currentTimeMillis(),
        apiError = this.error // Store the error from the domain model if present
    )
}
