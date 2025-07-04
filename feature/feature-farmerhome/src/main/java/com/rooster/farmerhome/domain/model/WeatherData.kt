package com.rooster.farmerhome.domain.model

data class WeatherData(
    val temperature: String,
    val humidity: String,
    val precipitation: String,
    val windSpeed: String,
    val description: String,
    val location: String? = null, // Optional: if we want to display the location for which weather is shown
    val error: String? = null // To hold any error messages
)
