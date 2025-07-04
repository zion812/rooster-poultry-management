package com.rooster.buyerhome.domain.model

import java.util.Date

// For Marketplace Recommendations
data class MarketplaceRecommendationItem(
    val id: String,
    val productName: String,
    val sellerName: String,
    val price: String, // e.g., "₹1500 / quintal"
    val imageUrl: String?, // Optional image
    val location: String?
)

// For Order Tracking
enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}

data class OrderItem(
    val id: String,
    val productName: String,
    val quantity: String, // e.g., "10 quintals"
    val totalPrice: String, // e.g., "₹15000"
    val orderDate: Date,
    val expectedDeliveryDate: Date?,
    val status: OrderStatus,
    val sellerName: String
)

// For Price Comparison (Simplified)
data class PriceComparisonProduct(
    val productName: String,
    val averageMarketPrice: String,
    val yourLastPaidPrice: String?,
    val bestAvailablePrice: String?
)

// For Supplier Ratings
data class SupplierRatingInfo(
    val supplierId: String,
    val supplierName: String,
    val averageRating: Float, // e.g., 4.5f
    val numberOfReviews: Int,
    val profileImageUrl: String?
)
