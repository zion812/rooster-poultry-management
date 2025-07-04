package com.rooster.farmerhome.data.repository

import com.rooster.farmerhome.data.source.WeatherRemoteDataSource
import com.rooster.farmerhome.domain.model.WeatherData
import com.rooster.farmerhome.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource
    // TODO: Add localDataSource for caching if needed as per AGENTS.md (Offline-First)
) : WeatherRepository {

    override fun getCurrentWeather(latitude: Double, longitude: Double): Flow<WeatherData> {
        return remoteDataSource.getCurrentWeather(latitude, longitude)
        // TODO: Implement caching logic here if localDataSource is added
        // e.g., fetch from remote, save to local, then emit from local.
        // Or, use a NetworkBoundResource pattern if more complex.
    }

    override fun getCurrentWeatherForFarm(farmLocation: String): Flow<WeatherData> {
        return remoteDataSource.getCurrentWeatherForFarm(farmLocation)
        // TODO: Implement caching logic
    }
}
