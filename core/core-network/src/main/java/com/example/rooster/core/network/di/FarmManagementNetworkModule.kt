package com.example.rooster.core.network.di

import com.example.rooster.core.network.BuildConfig
import com.example.rooster.core.network.qualifiers.FarmManagementApi
import com.example.rooster.core.network.qualifiers.FarmManagementApiBaseUrl
 feature/dashboard-scaffolding-and-weather-api
import com.rooster.farmerhome.data.source.FarmDataApiService
import com.rooster.farmerhome.data.source.FarmHealthAlertApiService // Import for FarmHealthAlertApiService
import com.rooster.farmerhome.data.source.ProductionMetricsApiService
import com.rooster.farmerhome.data.source.WeatherApiService

import com.rooster.farmerhome.data.source.WeatherApiService // Correct import for WeatherApiService
 main
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FarmManagementNetworkModule {

    @Provides
    @Singleton
    @FarmManagementApiBaseUrl
    fun provideFarmManagementApiBaseUrl(): String = BuildConfig.FARM_MGMT_API_BASE_URL

    @Provides
    @Singleton
    @FarmManagementApi // Qualify the Retrofit instance
    fun provideFarmManagementRetrofit(
        @FarmManagementApiBaseUrl baseUrl: String,
        okHttpClient: OkHttpClient, // Assuming a general OkHttpClient is provided elsewhere
        json: Json // Assuming Json for kotlinx.serialization is provided elsewhere
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(@FarmManagementApi retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

 feature/dashboard-scaffolding-and-weather-api
    @Provides
    @Singleton
    fun provideFarmDataApiService(@FarmManagementApi retrofit: Retrofit): FarmDataApiService {
        return retrofit.create(FarmDataApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductionMetricsApiService(@FarmManagementApi retrofit: Retrofit): ProductionMetricsApiService {
        return retrofit.create(ProductionMetricsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFarmHealthAlertApiService(@FarmManagementApi retrofit: Retrofit): FarmHealthAlertApiService {
        return retrofit.create(FarmHealthAlertApiService::class.java)
    }


 main
    // TODO: If a general OkHttpClient and Json are not provided globally,
    // they need to be provided here or in a more general NetworkModule.
    // For now, assume they are.
    // Example of providing them if not already available:
    /*
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json { ignoreUnknownKeys = true; isLenient = true }
    }
    */
}
