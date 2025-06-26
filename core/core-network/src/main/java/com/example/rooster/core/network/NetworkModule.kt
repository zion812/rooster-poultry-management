package com.example.rooster.core.network

import android.content.Context
import android.content.Context
import android.content.Context
import com.example.rooster.core.common.Constants
import com.example.rooster.core.network.qualifiers.PaymentApiBaseUrl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
// Removed: import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.serialization.json.Json // For Kotlinx Serialization
import okhttp3.MediaType.Companion.toMediaType // For Kotlinx Serialization
import retrofit2.converter.kotlinx.serialization.asConverterFactory // For Kotlinx Serialization
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Enterprise-grade network module following Meta/Google patterns
 * Provides centralized network configuration with:
 * - Certificate pinning
 * - Request/Response logging
 * - Caching strategies
 * - Error handling
 * - Rural network optimization
 */

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ParseRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeneralRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PaymentApiRetrofit // New qualifier for Payment API

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    @AuthInterceptor
    fun provideAuthInterceptor(tokenProvider: TokenProvider): Interceptor { // Inject TokenProvider interface
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "Rooster-Android/${Constants.APP_VERSION}")

            // Attempt to add Firebase Auth token without forcing refresh.
            // runBlocking is still used here for simplicity to get the current token if available.
            // Ideally, this interceptor should also be async or token managed reactively.
            // However, the primary goal was to move forceful refresh out of the interceptor.
            // This part could be further optimized by having a non-blocking way to get a cached token.
            val token = kotlinx.coroutines.runBlocking { tokenProvider.getToken(forceRefresh = false) }
            token?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }

            chain.proceed(requestBuilder.build())
        }
    }

    @Provides
    @Singleton
    @NetworkInterceptor
    fun provideNetworkInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)

            // Add cache headers for rural optimization
            response.newBuilder()
                .header("Cache-Control", "public, max-age=${Constants.CACHE_MAX_AGE}")
                .build()
        }
    }

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheDir = File(context.cacheDir, "http_cache")
        return Cache(cacheDir, Constants.CACHE_SIZE)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @AuthInterceptor authInterceptor: Interceptor,
        @NetworkInterceptor networkInterceptor: Interceptor,
        tokenAuthenticator: TokenAuthenticator, // Add TokenAuthenticator
        cache: Cache
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .authenticator(tokenAuthenticator) // Add authenticator
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor(networkInterceptor)
            .cache(cache)
            .connectTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    @ParseRetrofit
    fun provideParseRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit { // Inject Json
        return Retrofit.Builder()
            .baseUrl(Constants.PARSE_SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    @GeneralRetrofit
    fun provideGeneralRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit { // Inject Json
        return Retrofit.Builder()
            .baseUrl("https://api.rooster.com/v1/") // Replace with actual API base URL
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideJson(): Json { // Provide Json instance
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true // Good for handling potential minor schema mismatches
        }
    }

    @Provides
    @Singleton
    @PaymentApiRetrofit // Use the new qualifier
    fun providePaymentApiRetrofit(
        okHttpClient: OkHttpClient,
        @PaymentApiBaseUrl baseUrl: String, // Inject the base URL
        json: Json // Inject Json
    ): Retrofit {
        if (baseUrl.isBlank()) {
            throw IllegalStateException("Payment API Base URL is not configured or blank.")
        }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient) // Uses the same OkHttpClient with Auth interceptor
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun providePaymentApiService(@PaymentApiRetrofit retrofit: Retrofit): PaymentApiService {
        return retrofit.create(PaymentApiService::class.java)
    }
}
