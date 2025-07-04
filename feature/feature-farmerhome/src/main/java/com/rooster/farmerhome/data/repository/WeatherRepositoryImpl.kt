package com.rooster.farmerhome.data.repository

import com.rooster.farmerhome.core.common.util.DataState // Import DataState
import com.rooster.farmerhome.data.local.datasource.WeatherLocalDataSource
import com.rooster.farmerhome.data.local.model.toDomain
import com.rooster.farmerhome.data.local.model.toEntity
import com.rooster.farmerhome.data.source.WeatherRemoteDataSource
import com.rooster.farmerhome.domain.model.WeatherData
import com.rooster.farmerhome.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import java.time.Duration // For cache staleness
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource
) : WeatherRepository {

    // Define cache duration, e.g., 30 minutes
    private val weatherCacheDuration = Duration.ofMinutes(30)

    private fun isCacheStale(timestamp: Long): Boolean {
        return (System.currentTimeMillis() - timestamp) > weatherCacheDuration.toMillis()
    }

    // Helper function to manage the fetch-and-cache logic for a single WeatherData item
    private fun getCachedOrFetchWeatherData(
        locationIdentifier: String, // e.g., farmLocation or "lat_lon_key"
        fetchRemote: suspend () -> Flow<WeatherData>
    ): Flow<DataState<WeatherData>> {
        // Using a fixed ID for weather cache for simplicity, as discussed.
        val cacheKey = "current_weather"

        return localDataSource.getWeatherData().flatMapLatest { localEntity ->
            val localDomainData = localEntity?.toDomain()
            if (localEntity != null && !isCacheStale(localEntity.timestamp)) {
                flow { emit(DataState.Success(localDomainData!!, isFromCache = true, isStale = false)) }
            } else {
                flow<DataState<WeatherData>> {
                    emit(DataState.Loading(localDomainData)) // Emit loading with potentially stale data
                    try {
                        fetchRemote().collect { remoteWeatherData ->
                            if (remoteWeatherData.error == null) {
                                // Use locationIdentifier if remoteWeatherData.location is null.
                                // For getCurrentWeather, locationIdentifier might be a synthetic key.
                                // For getCurrentWeatherForFarm, it's farmLocation.
                                val effectiveLocation = remoteWeatherData.location ?: if(locationIdentifier != cacheKey) locationIdentifier else null
                                localDataSource.insertWeatherData(remoteWeatherData.toEntity(effectiveLocation))
                                emit(DataState.Success(remoteWeatherData, isFromCache = false, isStale = false))
                            } else {
                                val errorException = Exception(remoteWeatherData.error)
                                if (localDomainData != null) {
                                    emit(DataState.Error(errorException, localDomainData, Duration.ofMillis(System.currentTimeMillis() - (localEntity?.timestamp ?: 0))))
                                } else {
                                    emit(DataState.Error(errorException, null, null))
                                }
                            }
                        }
                    } catch (e: Exception) {
                        if (localDomainData != null) {
                            emit(DataState.Error(e, localDomainData, Duration.ofMillis(System.currentTimeMillis() - (localEntity?.timestamp ?: 0))))
                        } else {
                            emit(DataState.Error(e, null, null))
                        }
                    }
                }
            }
        }.catch { e -> // Catch exceptions from localDataSource.getWeatherData() or unhandled upstream
            emit(DataState.Error(e, null, null, "Failed to load weather data from cache or network."))
        }
    }

    override fun getCurrentWeather(latitude: Double, longitude: Double): Flow<DataState<WeatherData>> {
        // Using a synthetic key for coordinate-based weather, or could decide to always overwrite "current_weather"
        // For now, let's assume it updates the same "current_weather" cache entry.
        return getCachedOrFetchWeatherData("coords_${latitude}_${longitude}") { // locationIdentifier for context if needed
            remoteDataSource.getCurrentWeather(latitude, longitude)
        }
    }

    override fun getCurrentWeatherForFarm(farmLocation: String): Flow<DataState<WeatherData>> {
        return getCachedOrFetchWeatherData(farmLocation) {
            remoteDataSource.getCurrentWeatherForFarm(farmLocation)
        }
    }
}
