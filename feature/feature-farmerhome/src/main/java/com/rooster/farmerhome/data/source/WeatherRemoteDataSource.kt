package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {
    fun getCurrentWeather(latitude: Double, longitude: Double): Flow<WeatherData>
    fun getCurrentWeatherForFarm(farmLocation: String): Flow<WeatherData>
}
