package com.rooster.farmerhome.di

import com.rooster.farmerhome.data.repository.WeatherRepositoryImpl
import com.rooster.farmerhome.data.repository.FarmHealthAlertRepositoryImpl
import com.rooster.farmerhome.data.repository.FarmApiRepositoryImpl
import com.rooster.farmerhome.data.source.FarmHealthAlertRemoteDataSource
import com.rooster.farmerhome.data.repository.ProductionMetricsRepositoryImpl
import com.rooster.farmerhome.data.source.MockFarmHealthAlertRemoteDataSource
import com.rooster.farmerhome.data.source.MockProductionMetricsRemoteDataSource
import com.rooster.farmerhome.data.repository.FarmDataRepositoryImpl
import com.rooster.farmerhome.data.source.FarmDataRemoteDataSource
import com.rooster.farmerhome.data.source.MockFarmDataRemoteDataSource
import com.rooster.farmerhome.data.local.datasource.FarmBasicInfoLocalDataSource
import com.rooster.farmerhome.data.local.datasource.FarmBasicInfoLocalDataSourceImpl
import com.rooster.farmerhome.data.local.datasource.FarmHealthAlertLocalDataSource
import com.rooster.farmerhome.data.local.datasource.FarmHealthAlertLocalDataSourceImpl
import com.rooster.farmerhome.data.local.datasource.ProductionSummaryLocalDataSource
import com.rooster.farmerhome.data.local.datasource.ProductionSummaryLocalDataSourceImpl
import com.rooster.farmerhome.data.local.datasource.WeatherLocalDataSource
import com.rooster.farmerhome.data.local.datasource.WeatherLocalDataSourceImpl
import com.rooster.farmerhome.data.source.MockProductionMetricsRemoteDataSource
import com.rooster.farmerhome.data.source.MockWeatherRemoteDataSource
import com.rooster.farmerhome.data.source.ProductionMetricsRemoteDataSource
import com.rooster.farmerhome.data.source.WeatherRemoteDataSource
import com.rooster.farmerhome.domain.repository.FarmDataRepository
import com.rooster.farmerhome.domain.repository.FarmHealthAlertRepository
import android.content.Context
import androidx.room.Room
import com.rooster.farmerhome.data.local.db.FarmerHomeDatabase
import com.rooster.farmerhome.data.local.dao.FarmBasicInfoDao
import com.rooster.farmerhome.data.local.dao.FarmHealthAlertDao
import com.rooster.farmerhome.data.local.dao.ProductionSummaryDao
import com.rooster.farmerhome.data.local.dao.WeatherDao
import com.rooster.farmerhome.domain.repository.ProductionMetricsRepository
import com.rooster.farmerhome.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class FarmerHomeRepositoryModule { // Renamed class

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository

    // Binding for the new API repository implementation (Python backend)
    @Binds
    @Singleton
    @ApiRepository
    abstract fun bindFarmApiRepository(
        farmApiRepositoryImpl: FarmApiRepositoryImpl
    ): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRemoteDataSource(
        retrofitWeatherRemoteDataSource: com.rooster.farmerhome.data.source.RetrofitWeatherRemoteDataSource
    ): WeatherRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindWeatherLocalDataSource( // Added this binding
        weatherLocalDataSourceImpl: WeatherLocalDataSourceImpl
    ): WeatherLocalDataSource

    // Farm Health Alerts
    @Binds
    @Singleton
    abstract fun bindFarmHealthAlertRepository(
        farmHealthAlertRepositoryImpl: FarmHealthAlertRepositoryImpl
    ): FarmHealthAlertRepository

    @Binds
    @Singleton
    abstract fun bindFarmHealthAlertRemoteDataSource(
        // Bind the new Retrofit implementation
        retrofitFarmHealthAlertRemoteDataSource: com.rooster.farmerhome.data.source.RetrofitFarmHealthAlertRemoteDataSource
    ): FarmHealthAlertRemoteDataSource

    // TODO: Add binds for local farm health alert data source when created

    @Binds
    @Singleton
    abstract fun bindProductionMetricsRepository(
        productionMetricsRepositoryImpl: ProductionMetricsRepositoryImpl
    ): ProductionMetricsRepository

    @Binds
    @Singleton
    abstract fun bindProductionMetricsRemoteDataSource(
        // Bind the new Retrofit implementation
        retrofitProductionMetricsRemoteDataSource: com.rooster.farmerhome.data.source.RetrofitProductionMetricsRemoteDataSource
    ): ProductionMetricsRemoteDataSource

    // TODO: Add binds for local production metrics data source when created

    @Binds
    @Singleton
    abstract fun bindFarmDataRepository(
        farmDataRepositoryImpl: FarmDataRepositoryImpl
    ): FarmDataRepository

    @Binds
    @Singleton
    abstract fun bindFarmDataRemoteDataSource(
        // Bind the new Retrofit implementation
        retrofitFarmDataRemoteDataSource: com.rooster.farmerhome.data.source.RetrofitFarmDataRemoteDataSource
    ): FarmDataRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFarmBasicInfoLocalDataSource(
        farmBasicInfoLocalDataSourceImpl: FarmBasicInfoLocalDataSourceImpl
    ): FarmBasicInfoLocalDataSource

    // Production Summary
    @Binds
    @Singleton
    abstract fun bindProductionSummaryLocalDataSource(
        productionSummaryLocalDataSourceImpl: ProductionSummaryLocalDataSourceImpl
    ): ProductionSummaryLocalDataSource

    // Farm Health Alerts Local
    @Binds
    @Singleton
    abstract fun bindFarmHealthAlertLocalDataSource(
        farmHealthAlertLocalDataSourceImpl: FarmHealthAlertLocalDataSourceImpl
    ): FarmHealthAlertLocalDataSource
}
