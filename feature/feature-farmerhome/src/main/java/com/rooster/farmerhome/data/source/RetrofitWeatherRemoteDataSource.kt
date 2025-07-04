package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RetrofitWeatherRemoteDataSource @Inject constructor(
    private val weatherApiService: WeatherApiService
) : WeatherRemoteDataSource {

    override fun getCurrentWeather(latitude: Double, longitude: Double): Flow<WeatherData> = flow {
        try {
            val response = weatherApiService.getCurrentWeatherByCoordinates(latitude, longitude)
            if (response.isSuccessful) {
                response.body()?.let {
                    // If the Python API includes an "error" field in successful (200) responses
                    // that still mean an operational error, WeatherData's nullable error field will pick it up.
                    emit(it)
                } ?: run {
                    // This case means a 200 OK but an empty body, which is unexpected for this API.
                    emit(WeatherData(temperature = "", humidity = "", precipitation = "", windSpeed = "", description = "", error = "Empty response body"))
                }
            } else {
                // Handle HTTP error codes (4xx, 5xx)
                val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
                // Attempt to parse errorBody if it's expected to be a JSON with an "error" field,
                // or just use a generic message. For now, a generic one.
                // If WeatherData could also represent the error structure, could try to parse to it.
                emit(WeatherData(temperature = "", humidity = "", precipitation = "", windSpeed = "", description = "", error = "API Error: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            // Handle network exceptions, etc.
            emit(WeatherData(temperature = "", humidity = "", precipitation = "", windSpeed = "", description = "", error = "Network Error: ${e.message ?: "Unknown error"}"))
        }
    }.flowOn(Dispatchers.IO) // Perform network operations on IO dispatcher

    override fun getCurrentWeatherForFarm(farmLocation: String): Flow<WeatherData> = flow {
        try {
            val response = weatherApiService.getCurrentWeatherByLocation(farmLocation)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(it)
                } ?: run {
                    emit(WeatherData(temperature = "", humidity = "", precipitation = "", windSpeed = "", description = "", error = "Empty response body"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
                emit(WeatherData(temperature = "", humidity = "", precipitation = "", windSpeed = "", description = "", error = "API Error: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            emit(WeatherData(temperature = "", humidity = "", precipitation = "", windSpeed = "", description = "", error = "Network Error: ${e.message ?: "Unknown error"}"))
        }
    }.flowOn(Dispatchers.IO)
}
