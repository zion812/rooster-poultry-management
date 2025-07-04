package com.rooster.farmerhome.domain.repository

import com.rooster.farmerhome.core.common.util.DataState // Import DataState
import com.rooster.farmerhome.domain.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather(latitude: Double, longitude: Double): Flow<DataState<WeatherData>> // Updated return type
    fun getCurrentWeatherForFarm(farmLocation: String): Flow<DataState<WeatherData>> // Updated return type
}
