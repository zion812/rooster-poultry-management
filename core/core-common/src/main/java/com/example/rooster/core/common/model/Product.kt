package com.example.rooster.core.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    @ColumnInfo(name = "category_id") val categoryId: String,
    @ColumnInfo(name = "supplier_id") val supplierId: String,
    @ColumnInfo(name = "image_urls") val imageUrls: List<String> = emptyList(),
    @ColumnInfo(name = "stock_quantity") val stockQuantity: Int = 0,
    val rating: Double? = null, // Average rating
    @ColumnInfo(name = "needs_sync") val needsSync: Boolean = false,
    @ColumnInfo(name = "sync_attempts") val syncAttempts: Int = 0,
    @ColumnInfo(name = "last_sync_attempt_timestamp") val lastSyncAttemptTimestamp: Long = 0L
)
