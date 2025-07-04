package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.WeatherData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MockWeatherRemoteDataSource @Inject constructor() : WeatherRemoteDataSource {
    override fun getCurrentWeather(latitude: Double, longitude: Double): Flow<WeatherData> = flow {
        delay(1000) // Simulate network delay
        emit(
            WeatherData(
                temperature = "25°C",
                humidity = "60%",
                precipitation = "0 mm",
                windSpeed = "10 km/h",
                description = "Sunny",
                location = "Lat: $latitude, Lon: $longitude"
            )
        )
    }

    override fun getCurrentWeatherForFarm(farmLocation: String): Flow<WeatherData> = flow {
        delay(1000) // Simulate network delay
        // Simulate different weather for different locations or error
        if (farmLocation.equals("error_location", ignoreCase = true)) {
            emit(
                WeatherData(
                    temperature = "",
                    humidity = "",
                    precipitation = "",
                    windSpeed = "",
                    description = "",
                    error = "Could not fetch weather for $farmLocation"
                )
            )
        } else {
            emit(
                WeatherData(
                    temperature = "28°C",
                    humidity = "55%",
                    precipitation = "0.2 mm",
                    windSpeed = "12 km/h",
                    description = "Partly Cloudy",
                    location = farmLocation
                )
            )
        }
    }
}
