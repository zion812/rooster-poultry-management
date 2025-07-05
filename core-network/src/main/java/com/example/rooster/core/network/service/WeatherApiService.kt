package com.example.rooster.core.network.service

import com.example.rooster.core.network.dto.WeatherInfoDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather/current_by_coords")
    suspend fun getCurrentWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): Response<WeatherInfoDto>

    @GET("weather/current_by_location")
    suspend fun getCurrentWeatherByLocation(@Query("location") locationName: String): Response<WeatherInfoDto>
}
