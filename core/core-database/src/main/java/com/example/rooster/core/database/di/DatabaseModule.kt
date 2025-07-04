package com.example.rooster.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.rooster.core.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for providing database-related dependencies within the `core-database` module.
 *
 * Currently, this module serves as a placeholder for potential shared database utilities
 * or a common [AppDatabase] instance if one were to be provided application-wide from this core module.
 * As feature modules typically define and provide their own specific Room database instances,
 * the direct provision of [AppDatabase] is commented out to avoid unused bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // Provider for a generic AppDatabase instance.
    // This is commented out as the current architecture expects feature modules to provide
    // their own specific database instances (e.g., FarmDatabase, MarketplaceDatabase).
    // If a common, shared database provided by `core-database` becomes necessary,
    // this provider can be enabled and configured appropriately.
    /*
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "rooster_core_app_database.db" // Example name for a shared core DB
        )
            .fallbackToDestructiveMigration() // IMPORTANT: Replace with proper migration strategies for production.
            .build()
    }
    */

    // This module can also be used to provide other common database-related utilities
    // such as shared query executors, migration helpers, or pre-populated database callbacks
    // if such common functionalities are identified across multiple feature databases.
    // For now, it remains minimal.
}
