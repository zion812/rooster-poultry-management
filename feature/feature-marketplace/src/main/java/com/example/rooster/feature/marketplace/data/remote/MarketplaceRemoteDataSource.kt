package com.example.rooster.feature.marketplace.data.remote

import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.domain.model.Order
import com.example.rooster.feature.marketplace.domain.model.ProductListing
import kotlinx.coroutines.flow.Flow

interface MarketplaceRemoteDataSource {
    // Product Listings
    fun getProductListingsStream(
        category: String? = null,
        sellerId: String? = null,
        searchTerm: String? = null, // For basic text search
        pageSize: Int,
        page: Int // Page number (1-indexed)
    ): Flow<Result<List<ProductListing>>> // For Parse, this might become suspend fun returning Result directly without Flow for simple fetch

    suspend fun getProductListingDetails(listingId: String): Result<ProductListing?>
    suspend fun createProductListing(listingData: ProductListing): Result<String> // Returns ID
    suspend fun updateProductListing(listingData: ProductListing): Result<Unit>
    suspend fun deleteProductListing(listingId: String): Result<Unit>

    // Orders
    suspend fun createOrder(orderData: Order): Result<String> // Returns Order ID
    fun getOrderDetailsStream(orderId: String): Flow<Result<Order?>>
    fun getOrdersForUserStream(
        userId: String,
        pageSize: Int,
        lastOrderTimestamp: Long?,
        lastOrderId: String?
    ): Flow<Result<List<Order>>>
    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> // Simplified
    suspend fun cancelOrder(orderId: String): Result<Unit>

    // User specific interactions (e.g., seller's own listings) could be here or separate data source
    // fun getSellerListingsStream(sellerId: String): Flow<Result<List<ProductListing>>>
}
