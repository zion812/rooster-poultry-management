package com.example.rooster.feature.marketplace.di

import android.content.Context
import androidx.room.Room
import com.example.rooster.core.common.model.Product
import com.example.rooster.core.common.model.Review
import com.example.rooster.core.common.model.Supplier
import com.example.rooster.feature.marketplace.data.local.db.MarketplaceDatabase // Corrected import
import com.example.rooster.feature.marketplace.data.local.db.ProductDao // Corrected import
import com.example.rooster.feature.marketplace.data.local.db.ReviewDao // Corrected import
import com.example.rooster.feature.marketplace.data.local.db.SupplierDao // Corrected import
import com.example.rooster.feature.marketplace.data.local.dao.OrderDao // Kept for now
import com.example.rooster.feature.marketplace.data.remote.FirebaseMarketplaceDataSource
import com.example.rooster.feature.marketplace.data.remote.MarketplaceRemoteDataSource
import com.example.rooster.feature.marketplace.data.repository.MarketplaceRepositoryImpl // Corrected import
import com.example.rooster.feature.marketplace.data.repository.OrderRepositoryImpl // Kept for now
import com.example.rooster.feature.marketplace.domain.repository.MarketplaceRepository // Corrected import
import com.example.rooster.feature.marketplace.domain.repository.OrderRepository // Kept for now
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import com.example.rooster.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// Mock implementation for MarketplaceRemoteDataSource for now
class MockMarketplaceRemoteDataSource @Inject constructor() : MarketplaceRemoteDataSource {
    override fun getProductListingsStream(
        category: String?,
        sellerId: String?,
        searchTerm: String?,
        pageSize: Int,
        lastVisibleTimestamp: Long?,
        lastVisibleDocId: String?
    ): Flow<Result<List<Product>>> = flowOf(Result.Success(emptyList()))

    override suspend fun getProductDetails(listingId: String): Result<Product?> = Result.Success(null)
    override suspend fun createProductListing(listingData: Product): Result<String> = Result.Success("mock_product_id")
    override suspend fun updateProductListing(listingData: Product): Result<Unit> = Result.Success(Unit)
    override suspend fun deleteProductListing(listingId: String): Result<Unit> = Result.Success(Unit)
    override suspend fun getSupplierDetails(supplierId: String): Result<Supplier?> = Result.Success(null)
    override suspend fun submitReview(review: Review): Result<String> = Result.Success("mock_review_id")
    override fun getReviewsForProduct(productId: String): Flow<Result<List<Review>>> = flowOf(Result.Success(emptyList()))
    override fun getReviewsForSupplier(supplierId: String): Flow<Result<List<Review>>> = flowOf(Result.Success(emptyList()))
}

@Module
@InstallIn(SingletonComponent::class)
object MarketplaceProvidesModule { // Renamed object to avoid conflict if this file is treated as one unit

    @Provides
    @Singleton
    fun provideMarketplaceDatabase(@ApplicationContext context: Context): MarketplaceDatabase {
        return Room.databaseBuilder(
            context,
            MarketplaceDatabase::class.java,
            MarketplaceDatabase.DATABASE_NAME // Using constant from DB class
        )
            .fallbackToDestructiveMigration() // TODO: Add proper migrations for production
        .build()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: MarketplaceDatabase): ProductDao { // Changed from ProductListingDao
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideSupplierDao(database: MarketplaceDatabase): SupplierDao { // Added
        return database.supplierDao()
    }

    @Provides
    @Singleton
    fun provideReviewDao(database: MarketplaceDatabase): ReviewDao { // Added
        return database.reviewDao()
    }

    // OrderDao is kept for now, to be moved later
    @Provides
    @Singleton
    fun provideOrderDao(database: MarketplaceDatabase): OrderDao {
        return database.orderDao()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson { // Kept for now
        return Gson()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class MarketplaceBindsModule {

    @Binds
    @Singleton
    abstract fun bindMarketplaceRemoteDataSource(
        impl: MockMarketplaceRemoteDataSource // Binding mock for now
        // impl: FirebaseMarketplaceDataSource // TODO: Bind actual Firebase impl later
    ): MarketplaceRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindMarketplaceRepository( // Changed from bindProductListingRepository
        impl: MarketplaceRepositoryImpl
    ): MarketplaceRepository

    // OrderRepository binding is kept for now, to be moved later
    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        impl: OrderRepositoryImpl
    ): OrderRepository

    // CartRepository binding removed as it's in feature-cart's module
}