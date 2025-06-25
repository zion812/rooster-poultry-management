package com.example.rooster.di

import com.example.rooster.service.VetApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VetModule {
    @Provides
    @Singleton
    fun provideVetApiService(): VetApiService {
        // Return a stub implementation
        return object : VetApiService {}
    }
}
