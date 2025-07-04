package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.WeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather/current_by_coords") // Matches Flask route
    suspend fun getCurrentWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): Response<WeatherData> // Assuming direct mapping to WeatherData

    @GET("weather/current_by_location") // Matches Flask route
    suspend fun getCurrentWeatherByLocation(
        @Query("location") locationName: String
    ): Response<WeatherData> // Assuming direct mapping to WeatherData
}
