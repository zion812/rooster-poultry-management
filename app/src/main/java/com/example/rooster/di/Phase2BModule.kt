@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.rooster.di

import android.content.Context
import com.example.rooster.services.DiskCacheManager
import com.example.rooster.services.PredictiveCacheEngine
import com.example.rooster.services.ReactiveDataFetcher
import com.example.rooster.services.SmartCacheManager
import com.example.rooster.services.localization.IntelligentLocalizationEngine
import com.example.rooster.services.optimized.ComplianceDataFetcher
import com.example.rooster.services.optimized.GeospatialDataFetcher
import com.example.rooster.services.optimized.IntelligentSearchFetcher
import com.example.rooster.services.optimized.PredictiveDataFetcher
import com.example.rooster.services.optimized.RealTimeCollaborationFetcher
import com.example.rooster.services.optimized.RuralConnectivityOptimizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Phase 2B Dependency Injection Module
 *
 * Provides optimized services for rural connectivity and enhanced UX:
 * - Smart Cache Management with multi-layer caching
 * - Reactive Data Fetchers with offline-first architecture
 * - Intelligent Telugu Localization Engine
 * - Rural Performance Optimization services
 * - Predictive data loading and synchronization
 *
 * Created as part of Next Development Phase Plan
 * Following clean architecture and SOLID principles
 */
@Module
@InstallIn(SingletonComponent::class)
object Phase2BModule {
    @Provides
    @Singleton
    fun provideSmartCacheManager(
        @ApplicationContext context: Context,
        diskCacheManager: DiskCacheManager,
        predictiveEngine: PredictiveCacheEngine,
    ): SmartCacheManager {
        return SmartCacheManager(context, diskCacheManager, predictiveEngine)
    }

    @Provides
    @Singleton
    fun provideReactiveDataFetcher(smartCacheManager: SmartCacheManager): ReactiveDataFetcher {
        return ReactiveDataFetcher(smartCacheManager)
    }

    @Provides
    @Singleton
    fun providePredictiveDataFetcher(
        cacheManager: SmartCacheManager,
        reactiveDataFetcher: ReactiveDataFetcher,
    ): PredictiveDataFetcher {
        return PredictiveDataFetcher(cacheManager, reactiveDataFetcher)
    }

    @Provides
    @Singleton
    fun provideIntelligentLocalizationEngine(
        @ApplicationContext context: Context,
    ): IntelligentLocalizationEngine {
        return IntelligentLocalizationEngine(context)
    }

    @Provides
    @Singleton
    fun provideRuralConnectivityOptimizer(
        @ApplicationContext context: Context,
        cacheManager: SmartCacheManager,
    ): RuralConnectivityOptimizer {
        return RuralConnectivityOptimizer(context, cacheManager)
    }

    @Provides
    @Singleton
    fun provideRealTimeCollaborationFetcher(reactiveDataFetcher: ReactiveDataFetcher): RealTimeCollaborationFetcher {
        return RealTimeCollaborationFetcher(reactiveDataFetcher)
    }

    @Provides
    @Singleton
    fun provideIntelligentSearchFetcher(
        localizationEngine: IntelligentLocalizationEngine,
        cacheManager: SmartCacheManager,
    ): IntelligentSearchFetcher {
        return IntelligentSearchFetcher(localizationEngine, cacheManager)
    }

    @Provides
    @Singleton
    fun provideGeospatialDataFetcher(cacheManager: SmartCacheManager): GeospatialDataFetcher {
        return GeospatialDataFetcher(cacheManager)
    }

    @Provides
    @Singleton
    fun provideComplianceDataFetcher(
        cacheManager: SmartCacheManager,
        localizationEngine: IntelligentLocalizationEngine,
    ): ComplianceDataFetcher {
        return ComplianceDataFetcher(cacheManager, localizationEngine)
    }
}
