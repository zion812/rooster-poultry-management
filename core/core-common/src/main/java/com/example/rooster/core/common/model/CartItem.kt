package com.example.rooster.core.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["product_id"])]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String, // To associate cart item with a user
    @ColumnInfo(name = "product_id") val productId: String,
    val quantity: Int,
    @ColumnInfo(name = "added_at") val addedAt: Long = System.currentTimeMillis(),
    // Store product details directly or fetch separately? Storing some basic info can be useful for quick display.
    @ColumnInfo(name = "product_name") val productName: String,
    @ColumnInfo(name = "product_price") val productPrice: Double,
    @ColumnInfo(name = "product_image_url") val productImageUrl: String? = null,
    @ColumnInfo(name = "needs_sync") val needsSync: Boolean = false,
    @ColumnInfo(name = "sync_attempts") val syncAttempts: Int = 0,
    @ColumnInfo(name = "last_sync_attempt_timestamp") val lastSyncAttemptTimestamp: Long = 0L
)

@Serializable
@Entity(
    tableName = "wishlist_items",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["product_id"]), Index(value = ["user_id", "product_id"], unique = true)]
)
data class WishlistItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "product_id") val productId: String,
    @ColumnInfo(name = "added_at") val addedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "product_name") val productName: String, // For easier display
    @ColumnInfo(name = "product_price") val productPrice: Double, // To track price when added or current
    @ColumnInfo(name = "product_image_url") val productImageUrl: String? = null,
    @ColumnInfo(name = "needs_sync") val needsSync: Boolean = false,
    @ColumnInfo(name = "sync_attempts") val syncAttempts: Int = 0,
    @ColumnInfo(name = "last_sync_attempt_timestamp") val lastSyncAttemptTimestamp: Long = 0L
)
