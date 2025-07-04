package com.example.rooster.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherInfoDto(
    @SerialName("location") val location: String? = null, // API might not always return location name if queried by coords
    @SerialName("temperature_celsius") val temperatureCelsius: Double,
    @SerialName("condition_text") val conditionText: String,
    @SerialName("humidity_percent") val humidityPercent: Int,
    @SerialName("wind_kph") val windKph: Double,
    @SerialName("last_updated") val lastUpdated: String // ISO 8601 DateTime string
)
