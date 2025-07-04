package com.example.rooster.core.network.di

import com.example.rooster.core.network.interceptors.AuthInterceptor
import com.example.rooster.core.network.service.AuthApiService
import com.example.rooster.core.network.service.FarmApiService
import com.example.rooster.core.network.service.FlockApiService
import com.example.rooster.core.network.service.TrackingApiService
import com.example.rooster.core.network.service.WeatherApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

// This object is a placeholder for BuildConfig values that would normally be generated.
// In a real application, these should be provided through Gradle's buildConfigField.
object CoreNetworkBuildConfig {
    const val DEBUG = true // Set to false for release builds
    const val PYTHON_API_BASE_URL = "http://10.0.2.2:5000/api/" // Default for Android emulator
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = CoreNetworkBuildConfig.DEBUG // Enable pretty print for debug builds
        encodeDefaults = true // Include default values during serialization if needed
        classDiscriminator = "record_type" // For HealthRecordDto sealed class
    }

    @Provides
    @Singleton
    @Named("PythonApiBaseUrl")
    fun providePythonApiBaseUrl(): String {
        return CoreNetworkBuildConfig.PYTHON_API_BASE_URL
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(/* TODO: Inject token provider/repository */): AuthInterceptor {
        return AuthInterceptor {
            // Placeholder: No token added yet. Implement logic to retrieve stored JWT.
            // Example: val token = runBlocking { tokenRepository.getAuthToken() }; "Bearer $token"
            null
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (CoreNetworkBuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
        @Named("PythonApiBaseUrl") baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // --- API Service Providers ---

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFarmApiService(retrofit: Retrofit): FarmApiService {
        return retrofit.create(FarmApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFlockApiService(retrofit: Retrofit): FlockApiService {
        return retrofit.create(FlockApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTrackingApiService(retrofit: Retrofit): TrackingApiService {
        return retrofit.create(TrackingApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }
}
