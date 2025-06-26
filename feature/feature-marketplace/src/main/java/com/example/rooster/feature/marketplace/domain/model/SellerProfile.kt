package com.example.rooster.feature.marketplace.domain.model

import kotlinx.serialization.Serializable

// Simplified Seller Profile for display on listings or cart.
// A more complete User/Farmer profile would exist in a user or farm feature module.
@Serializable
data class SellerProfile(
    val sellerId: String,
    val displayName: String, // Could be individual's name or farm name
    val farmName: String? = null,
    val city: String?,
    val district: String?,
    val averageRating: Double? = null, // Calculated value
    val totalRatings: Int = 0,
    val isVerified: Boolean = false, // e.g., KYC verified, admin verified
    val profileImageUrl: String? = null
)
