package com.example.rooster.feature.marketplace.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val listingId: String, // Corresponds to ProductListing.id
    val sellerId: String,
    val title: String,
    val unitPrice: Double, // Price per unit at the time of adding to cart
    var quantity: Int,
    val primaryImageUrl: String? = null,
    // Potentially add product variant details here if applicable (e.g., size, color for supplies)
    // val variantId: String? = null,
    // val variantDescription: String? = null,
    val stockAvailable: Int, // To check against when increasing quantity or at checkout
    val addedToCartTimestamp: Long
)
