package com.example.rooster.feature.marketplace.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val orderItemId: String, // Unique ID for this line item in the order
    val listingId: String,
    val sellerId: String,
    val title: String,
    val descriptionSnapshot: String?, // Product description at time of order
    val unitPriceAtPurchase: Double,
    val quantityOrdered: Int,
    val totalPrice: Double, // quantityOrdered * unitPriceAtPurchase
    val primaryImageUrl: String?
    // Potentially add variant details if applicable
    // val variantId: String? = null,
    // val variantDescription: String? = null
)
