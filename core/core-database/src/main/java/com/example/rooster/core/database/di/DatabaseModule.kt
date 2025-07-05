package com.example.rooster.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.rooster.core.database.AppDatabase
import com.example.rooster.core.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for providing database-related dependencies.
 *
 * This module provides the main AppDatabase instance and all related DAOs
 * for the Rooster Poultry Management System with offline-first capabilities.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the main AppDatabase instance.
     *
     * Configured for production use with:
     * - Proper migration support
     * - Database callbacks for optimization
     * - Fallback to destructive migration only in debug builds
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.create(
            context = context,
            databaseName = AppDatabase.DATABASE_NAME
        )
    }

    /**
     * Provides FlockDao for flock management operations
     */
    @Provides
    fun provideFlockDao(database: AppDatabase): FlockDao {
        return database.flockDao()
    }

    /**
     * Provides BirdDao for individual bird tracking operations
     */
    @Provides
    fun provideBirdDao(database: AppDatabase): BirdDao {
        return database.birdDao()
    }

    /**
     * Provides HealthRecordDao for health monitoring operations
     */
    @Provides
    fun provideHealthRecordDao(database: AppDatabase): HealthRecordDao {
        return database.healthRecordDao()
    }

    /**
     * Provides ProductionRecordDao for production tracking operations
     */
    @Provides
    fun provideProductionRecordDao(database: AppDatabase): ProductionRecordDao {
        return database.productionRecordDao()
    }

    /**
     * Provides SensorDataDao for IoT sensor data operations
     */
    @Provides
    fun provideSensorDataDao(database: AppDatabase): SensorDataDao {
        return database.sensorDataDao()
    }

    /**
     * Provides TraceabilityDao for supply chain traceability operations
     */
    @Provides
    fun provideTraceabilityDao(database: AppDatabase): TraceabilityDao {
        return database.traceabilityDao()
    }
}
