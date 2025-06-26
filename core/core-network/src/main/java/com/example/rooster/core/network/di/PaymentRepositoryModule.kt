package com.example.rooster.core.network.di

import com.example.rooster.core.common.domain.repository.PaymentRepository
import com.example.rooster.core.network.repository.RazorpayPaymentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        razorpayPaymentRepositoryImpl: RazorpayPaymentRepositoryImpl
    ): PaymentRepository
}
