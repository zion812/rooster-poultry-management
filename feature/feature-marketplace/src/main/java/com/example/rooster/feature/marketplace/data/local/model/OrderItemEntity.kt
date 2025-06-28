package com.example.rooster.feature.marketplace.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["orderId"],
            childColumns = ["parentOrderId"],
            onDelete = ForeignKey.CASCADE // If an order is deleted, its items are also deleted
        )
    ],
    indices = [Index(value = ["parentOrderId"])]
)
data class OrderItemEntity(
    @PrimaryKey val orderItemId: String,
    val parentOrderId: String, // Foreign key to OrderEntity
    val listingId: String,
    val sellerId: String,
    val title: String,
    val descriptionSnapshot: String?,
    val unitPriceAtPurchase: Double,
    val quantityOrdered: Int,
    val totalPrice: Double,
    val primaryImageUrl: String?
)
