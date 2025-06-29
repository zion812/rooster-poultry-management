package com.example.rooster.core.analytics.di

import com.example.rooster.core.analytics.AnalyticsService
import com.example.rooster.core.analytics.FirebaseAnalyticsService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsService(
        firebaseAnalyticsService: FirebaseAnalyticsService
    ): AnalyticsService
}
