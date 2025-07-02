package com.example.rooster.feature.marketplace.di

import android.content.Context
import androidx.room.Room
import com.example.rooster.feature.marketplace.data.local.MarketplaceDatabase
import com.example.rooster.feature.marketplace.data.local.dao.CartDao
import com.example.rooster.feature.marketplace.data.local.dao.OrderDao
import com.example.rooster.feature.marketplace.data.local.dao.ProductListingDao
import com.example.rooster.feature.marketplace.data.remote.FirebaseMarketplaceDataSource
import com.example.rooster.feature.marketplace.data.remote.MarketplaceRemoteDataSource
import com.example.rooster.feature.marketplace.data.repository.CartRepositoryImpl
import com.example.rooster.feature.marketplace.data.repository.OrderRepositoryImpl
import com.example.rooster.feature.marketplace.data.repository.ProductListingRepositoryImpl
import com.example.rooster.feature.marketplace.domain.repository.CartRepository
import com.example.rooster.feature.marketplace.domain.repository.OrderRepository
import com.example.rooster.feature.marketplace.domain.repository.ProductListingRepository
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MarketplaceProvidesModule {

    @Provides
    @Singleton
    fun provideMarketplaceDatabase(@ApplicationContext context: Context): MarketplaceDatabase {
        return Room.databaseBuilder(
            context,
            MarketplaceDatabase::class.java,
            "marketplace_database.db"
        )
            .fallbackToDestructiveMigration() // TODO: Add proper migrations for production
        .build()
    }

    @Provides
    @Singleton
    fun provideProductListingDao(database: MarketplaceDatabase): ProductListingDao {
        return database.productListingDao()
    }

    @Provides
    @Singleton
    fun provideCartDao(database: MarketplaceDatabase): CartDao {
        return database.cartDao()
    }

    @Provides
    @Singleton
    fun provideOrderDao(database: MarketplaceDatabase): OrderDao {
        return database.orderDao()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class MarketplaceBindsModule {

    @Binds
    @Singleton
    abstract fun bindMarketplaceRemoteDataSource(
        impl: FirebaseMarketplaceDataSource
    ): MarketplaceRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindProductListingRepository(
        impl: ProductListingRepositoryImpl
    ): ProductListingRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        impl: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        impl: OrderRepositoryImpl
    ): OrderRepository
}