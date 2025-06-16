package com.example.rooster.di

import com.example.rooster.services.DiskCacheManager
import com.example.rooster.services.PredictiveCacheEngine
import com.example.rooster.services.ReactiveDataFetcher
import com.example.rooster.services.SmartCacheManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Phase 2A Smart Cache System
 * Provides multi-layer caching with predictive prefetching and reactive data fetching
 */
@Module
@InstallIn(SingletonComponent::class)
object SmartCacheModule {

    @Provides
    @Singleton
    fun provideDiskCacheManager(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): DiskCacheManager {
        return DiskCacheManager(context)
    }

    @Provides
    @Singleton
    fun providePredictiveCacheEngine(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): PredictiveCacheEngine {
        return PredictiveCacheEngine(context)
    }

    @Provides
    @Singleton
    fun provideSmartCacheManager(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context,
        diskCacheManager: DiskCacheManager,
        predictiveEngine: PredictiveCacheEngine
    ): SmartCacheManager {
        return SmartCacheManager(context, diskCacheManager, predictiveEngine)
    }

    @Provides
    @Singleton
    fun provideReactiveDataFetcher(
        smartCacheManager: SmartCacheManager
    ): ReactiveDataFetcher {
        return ReactiveDataFetcher(smartCacheManager)
    }
}
