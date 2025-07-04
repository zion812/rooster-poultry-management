package com.example.rooster.core.payment.di

import com.example.rooster.core.payment.data.remote.PaymentApiService
import com.example.rooster.core.payment.data.repository.RazorpayPaymentRepositoryImpl
import com.example.rooster.core.payment.domain.repository.PaymentRepository
import com.example.rooster.core.network.qualifiers.PaymentApiBaseUrl // Assuming this qualifier is accessible
                                                                // If not, it might need to be defined in core-payment
                                                                // or core-common if used by multiple modules.
                                                                // For now, let's assume it's in core-network and core-payment
                                                                // will have a dependency on core-network for this.
                                                                // OR, the app module provides it via a common mechanism.
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import javax.inject.Qualifier
import javax.inject.Singleton


// Qualifier for Payment specific Retrofit instance, if different from general OkHttpClient needs
@Qualifier
@Retention(AnnotationRetention.BINARY)annotation class PaymentApiRetrofit

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentBindingsModule {

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        razorpayPaymentRepositoryImpl: RazorpayPaymentRepositoryImpl
    ): PaymentRepository
}

@Module
@InstallIn(SingletonComponent::class)
object PaymentNetworkModule {

    @Provides
    @Singleton
    @PaymentApiRetrofit // Use the locally defined or imported qualifier
    fun providePaymentApiRetrofit(
        okHttpClient: OkHttpClient, // General OkHttpClient from core-network
        @PaymentApiBaseUrl baseUrl: String, // Provided by app module or buildConfig
        json: Json // General Json from core-network or core-common
    ): Retrofit {
        if (baseUrl.isBlank()) {
            // This check might be better placed where baseUrl is provided or consumed more directly
            // For now, keeping it here as it was in the original NetworkModule.
            throw IllegalStateException("Payment API Base URL is not configured or blank in PaymentNetworkModule.")
        }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient) // Reuses the common OkHttpClient
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun providePaymentApiService(@PaymentApiRetrofit retrofit: Retrofit): PaymentApiService {
        return retrofit.create(PaymentApiService::class.java)
    }
}
