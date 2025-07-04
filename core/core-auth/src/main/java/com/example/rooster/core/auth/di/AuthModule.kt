package com.example.rooster.core.auth.di

import com.example.rooster.core.auth.data.repository.AuthRepositoryImpl
import com.example.rooster.core.auth.data.repository.ParseTokenRepositoryImpl
import com.example.rooster.core.auth.domain.repository.AuthRepository
import com.example.rooster.core.auth.domain.repository.TokenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for providing authentication and token related repository dependencies.
 *
 * This module uses `@Binds` to provide concrete implementations for [AuthRepository]
 * and [TokenRepository] interfaces. All bindings are scoped as Singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    /**
     * Binds [AuthRepositoryImpl] as the concrete implementation for [AuthRepository].
     * This repository handles Firebase-based authentication.
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    /**
     * Binds [ParseTokenRepositoryImpl] as the concrete implementation for [TokenRepository].
     * This repository handles Parse-based app-specific token management.
     */
    @Binds
    @Singleton
    abstract fun bindTokenRepository(
        parseTokenRepositoryImpl: ParseTokenRepositoryImpl
    ): TokenRepository
}