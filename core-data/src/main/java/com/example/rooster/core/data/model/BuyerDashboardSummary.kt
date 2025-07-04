package com.example.rooster.core.data.model

// Placeholder data classes for items. Real models would be more complex.
data class FeaturedProductTeaser(
    val id: String,
    val name: String,
    val imageUrl: String?, // Placeholder
    val price: String // Placeholder
)

data class OrderTeaser(
    val id: String,
    val orderDate: String, // Placeholder
    val status: String, // Placeholder
    val totalAmount: String // Placeholder
)

data class ProductCategoryTeaser(
    val id: String,
    val name: String,
    val iconUrl: String? // Placeholder
)

data class BuyerDashboardSummary(
    val userName: String,
    val featuredProducts: List<FeaturedProductTeaser>,
    val recentOrders: List<OrderTeaser>,
    val browseCategories: List<ProductCategoryTeaser>,
    val personalizedMessage: String? // e.g., "Deals for you today!"
)
