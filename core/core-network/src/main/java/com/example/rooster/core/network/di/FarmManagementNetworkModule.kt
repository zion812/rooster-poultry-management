package com.example.rooster.core.network.di

import com.example.rooster.core.network.FarmManagementRetrofit
import com.example.rooster.core.network.WeatherRetrofit
import com.example.rooster.core.network.api.FarmDataApiService
import com.example.rooster.core.network.api.WeatherApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Farm Management Network Module
 * Provides API services for farm management operations
 * Uses the Retrofit instances from the main NetworkModule
 */
@Module
@InstallIn(SingletonComponent::class)
object FarmManagementNetworkModule {

    @Provides
    @Singleton
    fun provideWeatherApiService(@WeatherRetrofit retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFarmDataApiService(@WeatherRetrofit retrofit: Retrofit): FarmDataApiService {
        return retrofit.create(FarmDataApiService::class.java)
    }
}
