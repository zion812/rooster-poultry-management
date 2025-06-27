package com.example.rooster.feature.marketplace.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Using listingId as PrimaryKey assumes one cart item per product listing.
// If variants were allowed, a composite key or a separate cartItemId would be needed.
@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val listingId: String,
    val sellerId: String,
    val title: String,
    val unitPrice: Double,
    var quantity: Int,
    val primaryImageUrl: String? = null,
    val stockAvailable: Int,
    val addedToCartTimestamp: Long,
    val userId: String // To associate cart with a user, important for multi-user or guest cart scenarios
    // needsSync could be added if cart needs to be synced across devices,
    // but for a simple local cart, it might not be necessary initially.
    // For now, assuming cart is device-local and doesn't require server sync.
)
