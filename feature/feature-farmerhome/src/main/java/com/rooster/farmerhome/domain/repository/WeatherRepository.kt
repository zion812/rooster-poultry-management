package com.rooster.farmerhome.domain.repository

import com.rooster.farmerhome.domain.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather(latitude: Double, longitude: Double): Flow<WeatherData>
    fun getCurrentWeatherForFarm(farmLocation: String): Flow<WeatherData>
}
