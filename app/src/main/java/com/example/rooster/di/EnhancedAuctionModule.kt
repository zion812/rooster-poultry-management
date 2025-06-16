package com.example.rooster.di

import com.example.rooster.data.repository.EnhancedAuctionRepository
import com.example.rooster.data.repository.EnhancedAuctionRepositoryImpl
import com.example.rooster.services.Back4AppService
import com.example.rooster.services.EnhancedAuctionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for enhanced auction functionality and Back4App integration
 */
@Module
@InstallIn(SingletonComponent::class)
object EnhancedAuctionModule {
    @Provides
    @Singleton
    fun provideBack4AppService(): Back4AppService {
        return Back4AppService()
    }

    @Provides
    @Singleton
    fun provideEnhancedAuctionService(): EnhancedAuctionService {
        return EnhancedAuctionService()
    }

    @Provides
    @Singleton
    fun provideEnhancedAuctionRepository(auctionService: EnhancedAuctionService): EnhancedAuctionRepository {
        return EnhancedAuctionRepositoryImpl(auctionService)
    }
}
