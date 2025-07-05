package com.rooster.farmerhome.data.local.datasource

import com.rooster.farmerhome.data.local.model.WeatherDataEntity
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    fun getWeatherData(): Flow<WeatherDataEntity?>
    // suspend fun getWeatherDataSnapshot(): WeatherDataEntity? // Removed for now
    suspend fun insertWeatherData(weatherData: WeatherDataEntity)
    suspend fun deleteWeatherData()
}
