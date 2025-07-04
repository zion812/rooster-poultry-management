package com.example.rooster.feature.marketplace.domain.repository

import com.example.rooster.core.common.model.Product
import com.example.rooster.core.common.model.Review
import com.example.rooster.core.common.model.Supplier
import kotlinx.coroutines.flow.Flow
import com.example.rooster.core.common.Result

interface MarketplaceRepository {

    // Product Operations
    fun getProductDetails(productId: String): Flow<Result<Product?>>
    fun getProductsByCategory(categoryId: String): Flow<Result<List<Product>>>
    fun getAllProducts(page: Int? = null, pageSize: Int? = null): Flow<Result<List<Product>>> // Added pagination optional params
    fun searchProducts(query: String, filters: Map<String, String>? = null): Flow<Result<List<Product>>>
    suspend fun createProductListing(product: Product): Result<String> // Returns product ID
    suspend fun updateProductListing(product: Product): Result<Unit>

    // Supplier Operations
    fun getSupplierProfile(supplierId: String): Flow<Result<Supplier?>>
    fun getProductsBySupplier(supplierId: String): Flow<Result<List<Product>>>

    // Review Operations
    fun getProductReviews(productId: String): Flow<Result<List<Review>>>
    fun getSupplierReviews(supplierId: String): Flow<Result<List<Review>>>
    suspend fun submitReview(review: Review): Result<String> // Returns review ID
    suspend fun updateReview(review: Review): Result<Unit>
    suspend fun deleteReview(reviewId: String): Result<Unit>

    // Sync operations (Conceptual)
    suspend fun syncPendingProducts(): Result<Unit>
    suspend fun syncPendingReviews(): Result<Unit>
}
