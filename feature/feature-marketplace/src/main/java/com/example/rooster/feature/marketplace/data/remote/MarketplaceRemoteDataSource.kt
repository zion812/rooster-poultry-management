package com.example.rooster.feature.marketplace.data.remote

import com.example.rooster.core.common.Result
import com.example.rooster.core.common.model.Product // Changed to common Product model
import com.example.rooster.core.common.model.Review // Added Review model
import com.example.rooster.core.common.model.Supplier // Added Supplier model
import kotlinx.coroutines.flow.Flow

interface MarketplaceRemoteDataSource {
    // Product Listings
    fun getProductListingsStream(
        category: String? = null,
        sellerId: String? = null,
        searchTerm: String? = null,
        pageSize: Int = 20,
        lastVisibleTimestamp: Long? = null, // Timestamp of the last item on the previous page
        lastVisibleDocId: String? = null   // ID of the last item for tie-breaking
    ): Flow<Result<List<Product>>> // Changed to common Product model

    suspend fun getProductDetails(listingId: String): Result<Product?> // Changed to common Product model
    suspend fun createProductListing(listingData: Product): Result<String> // Returns ID, Changed to common Product model
    suspend fun updateProductListing(listingData: Product): Result<Unit> // Changed to common Product model
    suspend fun deleteProductListing(listingId: String): Result<Unit>

    // Supplier Operations
    suspend fun getSupplierDetails(supplierId: String): Result<Supplier?>
    // Assuming suppliers might be created/updated via a different admin interface or process for now

    // Review Operations
    suspend fun submitReview(review: Review): Result<String> // Returns Review ID
    suspend fun getReviewsForProduct(productId: String): Flow<Result<List<Review>>>
    suspend fun getReviewsForSupplier(supplierId: String): Flow<Result<List<Review>>>
    // Update/Delete reviews might also be needed

    // User specific interactions (e.g., seller's own listings) could be here or separate data source
    // fun getSellerListingsStream(sellerId: String): Flow<Result<List<Product>>>
}
