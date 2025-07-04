package com.rooster.farmerhome.di

import com.rooster.farmerhome.data.repository.WeatherRepositoryImpl
import com.rooster.farmerhome.data.repository.FarmHealthAlertRepositoryImpl
import com.rooster.farmerhome.data.source.FarmHealthAlertRemoteDataSource
import com.rooster.farmerhome.data.repository.ProductionMetricsRepositoryImpl
import com.rooster.farmerhome.data.source.MockFarmHealthAlertRemoteDataSource
import com.rooster.farmerhome.data.source.MockProductionMetricsRemoteDataSource
import com.rooster.farmerhome.data.repository.FarmDataRepositoryImpl
import com.rooster.farmerhome.data.source.FarmDataRemoteDataSource
import com.rooster.farmerhome.data.source.MockFarmDataRemoteDataSource
import com.rooster.farmerhome.data.source.MockProductionMetricsRemoteDataSource
import com.rooster.farmerhome.data.source.MockWeatherRemoteDataSource
import com.rooster.farmerhome.data.source.ProductionMetricsRemoteDataSource
import com.rooster.farmerhome.data.source.WeatherRemoteDataSource
import com.rooster.farmerhome.domain.repository.FarmDataRepository
import com.rooster.farmerhome.domain.repository.FarmHealthAlertRepository
import com.rooster.farmerhome.domain.repository.ProductionMetricsRepository
import com.rooster.farmerhome.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Or ActivityRetainedComponent if ViewModel scoped
abstract class FarmerHomeDataModule {

    @Binds
    @Singleton // Or appropriate scope
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    @Singleton // Or appropriate scope
    abstract fun bindWeatherRemoteDataSource(
        // Corrected the type to the new Retrofit implementation
        retrofitWeatherRemoteDataSource: com.rooster.farmerhome.data.source.RetrofitWeatherRemoteDataSource
    ): WeatherRemoteDataSource

    // TODO: Add binds for local weather data source when created

    @Binds
    @Singleton
    abstract fun bindFarmHealthAlertRepository(
        farmHealthAlertRepositoryImpl: FarmHealthAlertRepositoryImpl
    ): FarmHealthAlertRepository

    @Binds
    @Singleton
    abstract fun bindFarmHealthAlertRemoteDataSource(
        mockFarmHealthAlertRemoteDataSource: MockFarmHealthAlertRemoteDataSource
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
        mockProductionMetricsRemoteDataSource: MockProductionMetricsRemoteDataSource
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
        mockFarmDataRemoteDataSource: MockFarmDataRemoteDataSource
    ): FarmDataRemoteDataSource

    // TODO: Add binds for local farm data source when created
}
