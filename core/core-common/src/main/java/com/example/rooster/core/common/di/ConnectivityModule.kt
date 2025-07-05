package com.example.rooster.core.common.di

import android.content.Context
import com.example.rooster.core.common.connectivity.ConnectivityRepository
import com.example.rooster.core.common.connectivity.ConnectivityRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConnectivityModule {

    @Provides
    @Singleton
    fun provideConnectivityRepository(
        @ApplicationContext context: Context
    ): ConnectivityRepository {
        return ConnectivityRepositoryImpl(context)
    }
}
