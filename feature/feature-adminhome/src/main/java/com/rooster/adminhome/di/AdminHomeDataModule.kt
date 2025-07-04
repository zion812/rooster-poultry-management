package com.rooster.adminhome.di

import com.rooster.adminhome.data.repository.AdminContentModerationRepositoryImpl
import com.rooster.adminhome.data.repository.AdminFinancialRepositoryImpl
import com.rooster.adminhome.data.repository.AdminSystemRepositoryImpl
import com.rooster.adminhome.data.repository.AdminUserRepositoryImpl
import com.rooster.adminhome.data.source.AdminContentModerationRemoteDataSource
import com.rooster.adminhome.data.source.AdminFinancialRemoteDataSource
import com.rooster.adminhome.data.source.AdminSystemRemoteDataSource
import com.rooster.adminhome.data.source.AdminUserRemoteDataSource
import com.rooster.adminhome.data.source.MockAdminContentModerationRemoteDataSource
import com.rooster.adminhome.data.source.MockAdminFinancialRemoteDataSource
import com.rooster.adminhome.data.source.MockAdminSystemRemoteDataSource
import com.rooster.adminhome.data.source.MockAdminUserRemoteDataSource
import com.rooster.adminhome.domain.repository.AdminContentModerationRepository
import com.rooster.adminhome.domain.repository.AdminFinancialRepository
import com.rooster.adminhome.domain.repository.AdminSystemRepository
import com.rooster.adminhome.domain.repository.AdminUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Or ActivityRetainedComponent if ViewModel scoped
abstract class AdminHomeDataModule {

    // System Monitoring
    @Binds
    @Singleton
    abstract fun bindAdminSystemRepository(impl: AdminSystemRepositoryImpl): AdminSystemRepository
    @Binds
    @Singleton
    abstract fun bindAdminSystemRemoteDataSource(impl: MockAdminSystemRemoteDataSource): AdminSystemRemoteDataSource

    // User Management
    @Binds
    @Singleton
    abstract fun bindAdminUserRepository(impl: AdminUserRepositoryImpl): AdminUserRepository
    @Binds
    @Singleton
    abstract fun bindAdminUserRemoteDataSource(impl: MockAdminUserRemoteDataSource): AdminUserRemoteDataSource

    // Financial Analytics
    @Binds
    @Singleton
    abstract fun bindAdminFinancialRepository(impl: AdminFinancialRepositoryImpl): AdminFinancialRepository
    @Binds
    @Singleton
    abstract fun bindAdminFinancialRemoteDataSource(impl: MockAdminFinancialRemoteDataSource): AdminFinancialRemoteDataSource

    // Content Moderation
    @Binds
    @Singleton
    abstract fun bindAdminContentModerationRepository(impl: AdminContentModerationRepositoryImpl): AdminContentModerationRepository
    @Binds
    @Singleton
    abstract fun bindAdminContentModerationRemoteDataSource(impl: MockAdminContentModerationRemoteDataSource): AdminContentModerationRemoteDataSource
}
