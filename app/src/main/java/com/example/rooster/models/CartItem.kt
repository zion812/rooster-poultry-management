package com.example.rooster.models

import java.util.Date

/**
 * Represents an item in the user's shopping cart.
 * Consolidated from ComprehensiveDataModels.kt
 */
data class CartItem(
    val objectId: String, // Unique ID for the cart item itself
    val listingId: String, // ID of the marketplace listing
    val title: String,
    val price: Double,
    val quantity: Int,
    val sellerId: String,
    val sellerName: String,
    val imageUrl: String?,
    val addedAt: Date,
    val isAvailable: Boolean, // To check if the listing is still active/available
)
