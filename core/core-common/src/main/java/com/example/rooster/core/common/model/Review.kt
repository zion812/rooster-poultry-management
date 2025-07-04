package com.example.rooster.core.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.SET_NULL // Or CASCADE, depending on desired behavior
        ),
        ForeignKey(
            entity = Supplier::class,
            parentColumns = ["id"],
            childColumns = ["supplier_id"],
            onDelete = ForeignKey.SET_NULL
        )
        // Consider a ForeignKey for User if you have a User entity
    ],
    indices = [
        Index(value = ["product_id"]),
        Index(value = ["supplier_id"]),
        Index(value = ["user_id"])
    ]
)
data class Review(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String, // ID of the user who wrote the review
    @ColumnInfo(name = "product_id") val productId: String?, // Reviewed product, if applicable
    @ColumnInfo(name = "supplier_id") val supplierId: String?, // Reviewed supplier, if applicable
    val rating: Int, // e.g., 1-5
    val comment: String,
    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_anonymous") val isAnonymous: Boolean = false,
    @ColumnInfo(name = "is_verified_purchase") val isVerifiedPurchase: Boolean = false, // If review is linked to an actual order
    @ColumnInfo(name = "order_id") val orderId: String? = null, // Link to order for verification
    @ColumnInfo(name = "needs_sync") val needsSync: Boolean = false,
    @ColumnInfo(name = "sync_attempts") val syncAttempts: Int = 0,
    @ColumnInfo(name = "last_sync_attempt_timestamp") val lastSyncAttemptTimestamp: Long = 0L
)
