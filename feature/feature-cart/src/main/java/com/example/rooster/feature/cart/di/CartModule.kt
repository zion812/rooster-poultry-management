package com.example.rooster.feature.cart.di

import android.content.Context
import androidx.room.Room
import com.example.rooster.feature.cart.data.local.db.CartDao
import com.example.rooster.feature.cart.data.local.db.CartDatabase
import com.example.rooster.feature.cart.data.local.db.WishlistDao
import com.example.rooster.feature.cart.data.repository.CartRepositoryImpl
import com.example.rooster.feature.cart.domain.repository.CartRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CartModule {

    @Provides
    @Singleton
    fun provideCartDatabase(@ApplicationContext context: Context): CartDatabase {
        return Room.databaseBuilder(
            context,
            CartDatabase::class.java,
            CartDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration() // As per AGENTS.md for initial dev, replace with proper migrations later
        .build()
    }

    @Provides
    @Singleton
    fun provideCartDao(database: CartDatabase): CartDao {
        return database.cartDao()
    }

    @Provides
    @Singleton
    fun provideWishlistDao(database: CartDatabase): WishlistDao {
        return database.wishlistDao()
    }

    @Provides
    @Singleton
    fun provideCartRepository(
        cartDao: CartDao,
        wishlistDao: WishlistDao
        // remoteDataSource: CartRemoteDataSource // Inject when remote source is created
    ): CartRepository {
        return CartRepositoryImpl(cartDao, wishlistDao /*, remoteDataSource */)
    }
}
