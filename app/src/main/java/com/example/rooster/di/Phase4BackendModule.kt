@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.rooster.di

import android.content.Context
import com.example.rooster.services.SmartCacheManager
import com.example.rooster.services.backend.Back4AppMCPService
import com.example.rooster.services.mcp.MemoryBankMCPService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Phase 4 Backend & AI Integration Module
 *
 * Provides cloud backend integration and AI context services:
 * - Back4App MCP Service for cloud database operations
 * - Memory Bank MCP Service for persistent AI context storage
 * - Integration with existing Phase 2B services
 * - Rural optimization with cloud backend synchronization
 * - Telugu localization with cloud-based preferences
 *
 * Extends Phase 2B architecture with intelligent cloud capabilities
 */
@Module
@InstallIn(SingletonComponent::class)
object Phase4BackendModule {
    @Provides
    @Singleton
    fun provideBack4AppMCPService(cacheManager: SmartCacheManager): Back4AppMCPService {
        return Back4AppMCPService(cacheManager)
    }

    @Provides
    @Singleton
    fun provideMemoryBankMCPService(
        @ApplicationContext context: Context,
        cacheManager: SmartCacheManager,
    ): MemoryBankMCPService {
        return MemoryBankMCPService(context, cacheManager)
    }
}
