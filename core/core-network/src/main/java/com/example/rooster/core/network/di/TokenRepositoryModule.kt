package com.example.rooster.core.network.di

import com.example.rooster.core.common.domain.repository.TokenRepository
import com.example.rooster.core.network.repository.ParseTokenRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTokenRepository(
        parseTokenRepositoryImpl: ParseTokenRepositoryImpl
    ): TokenRepository
}
