package com.example.rooster.core.network.di

import com.example.rooster.core.common.domain.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentRepositoryModule {

    // TODO: Re-enable when RazorpayPaymentRepositoryImpl is implemented
    /*
    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        razorpayPaymentRepositoryImpl: RazorpayPaymentRepositoryImpl
    ): PaymentRepository
    */
}
