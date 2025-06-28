package com.example.rooster.core.auth.di

import com.example.rooster.core.auth.repository.AuthRepository
import com.example.rooster.core.auth.repository.AuthRepositoryImpl
import com.example.rooster.core.auth.repository.UserProfileRepository
import com.example.rooster.core.auth.repository.UserProfileRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        userProfileRepositoryImpl: UserProfileRepositoryImpl
    ): UserProfileRepository
}
