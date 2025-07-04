package com.rooster.farmerhome.domain.repository

 feature/dashboard-scaffolding-and-weather-api
import com.rooster.farmerhome.core.common.util.DataState // Import DataState

 main
import com.rooster.farmerhome.domain.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
 feature/dashboard-scaffolding-and-weather-api
    fun getCurrentWeather(latitude: Double, longitude: Double): Flow<DataState<WeatherData>> // Updated return type
    fun getCurrentWeatherForFarm(farmLocation: String): Flow<DataState<WeatherData>> // Updated return type

    fun getCurrentWeather(latitude: Double, longitude: Double): Flow<WeatherData>
    fun getCurrentWeatherForFarm(farmLocation: String): Flow<WeatherData>
 main
}
