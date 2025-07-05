package com.example.rooster.feature.farm.di

import android.content.Context
import androidx.room.Room
import com.example.rooster.feature.farm.data.local.db.FarmDatabase
import com.example.rooster.feature.farm.data.local.db.dao.FarmDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FarmDatabaseModule {

    @Provides
    @Singleton
    fun provideFarmDatabase(@ApplicationContext appContext: Context): FarmDatabase {
        return Room.databaseBuilder(
            appContext,
            FarmDatabase::class.java,
            FarmDatabase.DATABASE_NAME
        )
        // .addMigrations(...) // Add migrations here if schema changes later
        .fallbackToDestructiveMigration() // For development, replace with proper migrations for production
        .build()
    }

    @Provides
    @Singleton
    fun provideFarmDao(farmDatabase: FarmDatabase): FarmDao {
        return farmDatabase.farmDao()
    }
}
