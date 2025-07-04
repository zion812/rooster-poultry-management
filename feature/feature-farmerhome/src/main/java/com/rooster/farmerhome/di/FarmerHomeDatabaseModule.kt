package com.rooster.farmerhome.di

import android.content.Context
import androidx.room.Room
import com.rooster.farmerhome.data.local.db.FarmerHomeDatabase
import com.rooster.farmerhome.data.local.dao.FarmBasicInfoDao
import com.rooster.farmerhome.data.local.dao.FarmHealthAlertDao
import com.rooster.farmerhome.data.local.dao.ProductionSummaryDao
import com.rooster.farmerhome.data.local.dao.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FarmerHomeDatabaseModule {

    @Provides
    @Singleton
    fun provideFarmerHomeDatabase(@ApplicationContext context: Context): FarmerHomeDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            FarmerHomeDatabase::class.java,
            FarmerHomeDatabase.DATABASE_NAME
        )
        // TODO: Add migrations if version > 1. For now, version 1.
        // .addMigrations(...)
        // AGENTS.md: "For development, fallbackToDestructiveMigration may be used temporarily"
        // Using it here for now as schema might change during dev, but MUST be removed for prod.
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideWeatherDao(database: FarmerHomeDatabase): WeatherDao = database.weatherDao()

    @Provides
    @Singleton
    fun provideFarmBasicInfoDao(database: FarmerHomeDatabase): FarmBasicInfoDao = database.farmBasicInfoDao()

    @Provides
    @Singleton
    fun provideProductionSummaryDao(database: FarmerHomeDatabase): ProductionSummaryDao = database.productionSummaryDao()

    @Provides
    @Singleton
    fun provideFarmHealthAlertDao(database: FarmerHomeDatabase): FarmHealthAlertDao = database.farmHealthAlertDao()
}
