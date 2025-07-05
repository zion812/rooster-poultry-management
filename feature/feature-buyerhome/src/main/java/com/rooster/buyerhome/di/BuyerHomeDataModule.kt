package com.rooster.buyerhome.di

import com.rooster.buyerhome.data.repository.BuyerMarketplaceRepositoryImpl
import com.rooster.buyerhome.data.repository.BuyerOrderRepositoryImpl
import com.rooster.buyerhome.data.repository.BuyerSupplierRepositoryImpl
import com.rooster.buyerhome.data.repository.PriceComparisonRepositoryImpl
import com.rooster.buyerhome.data.source.BuyerMarketplaceRemoteDataSource
import com.rooster.buyerhome.data.source.BuyerOrderRemoteDataSource
import com.rooster.buyerhome.data.source.BuyerSupplierRemoteDataSource
import com.rooster.buyerhome.data.source.MockBuyerMarketplaceRemoteDataSource
import com.rooster.buyerhome.data.source.MockBuyerOrderRemoteDataSource
import com.rooster.buyerhome.data.source.MockBuyerSupplierRemoteDataSource
import com.rooster.buyerhome.data.source.MockPriceComparisonRemoteDataSource
import com.rooster.buyerhome.data.source.PriceComparisonRemoteDataSource
import com.rooster.buyerhome.domain.repository.BuyerMarketplaceRepository
import com.rooster.buyerhome.domain.repository.BuyerOrderRepository
import com.rooster.buyerhome.domain.repository.BuyerSupplierRepository
import com.rooster.buyerhome.domain.repository.PriceComparisonRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Or ActivityRetainedComponent if ViewModel scoped
abstract class BuyerHomeDataModule {

    @Binds
    @Singleton
    abstract fun bindBuyerMarketplaceRepository(
        impl: BuyerMarketplaceRepositoryImpl
    ): BuyerMarketplaceRepository

    @Binds
    @Singleton
    abstract fun bindBuyerMarketplaceRemoteDataSource(
        impl: MockBuyerMarketplaceRemoteDataSource
    ): BuyerMarketplaceRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindBuyerOrderRepository(
        impl: BuyerOrderRepositoryImpl
    ): BuyerOrderRepository

    @Binds
    @Singleton
    abstract fun bindBuyerOrderRemoteDataSource(
        impl: MockBuyerOrderRemoteDataSource
    ): BuyerOrderRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindPriceComparisonRepository(
        impl: PriceComparisonRepositoryImpl
    ): PriceComparisonRepository

    @Binds
    @Singleton
    abstract fun bindPriceComparisonRemoteDataSource(
        impl: MockPriceComparisonRemoteDataSource
    ): PriceComparisonRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindBuyerSupplierRepository(
        impl: BuyerSupplierRepositoryImpl
    ): BuyerSupplierRepository

    @Binds
    @Singleton
    abstract fun bindBuyerSupplierRemoteDataSource(
        impl: MockBuyerSupplierRemoteDataSource
    ): BuyerSupplierRemoteDataSource
}
