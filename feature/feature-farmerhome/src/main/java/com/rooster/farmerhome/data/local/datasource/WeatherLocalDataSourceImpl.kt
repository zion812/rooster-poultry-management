package com.rooster.farmerhome.data.local.datasource

import com.rooster.farmerhome.data.local.dao.WeatherDao
import com.rooster.farmerhome.data.local.model.WeatherDataEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherLocalDataSourceImpl @Inject constructor(
    private val weatherDao: WeatherDao
) : WeatherLocalDataSource {

    override fun getWeatherData(): Flow<WeatherDataEntity?> {
        return weatherDao.getWeatherData() // Assumes default ID "current_weather"
    }

    // Removed getWeatherDataSnapshot() implementation

    override suspend fun insertWeatherData(weatherData: WeatherDataEntity) {
        weatherDao.insertWeatherData(weatherData)
    }

    override suspend fun deleteWeatherData() {
        weatherDao.deleteWeatherData() // Assumes default ID
    }
}
