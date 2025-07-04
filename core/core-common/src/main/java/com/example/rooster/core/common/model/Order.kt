package com.example.rooster.core.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.rooster.core.common.persistence.common.OrderItemListConverter
import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val pricePerUnit: Double,
    val imageUrl: String? = null
)

@Serializable
enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED, REFUNDED, AWAITING_PAYMENT
}

@Serializable
@Entity(tableName = "orders")
@TypeConverters(OrderItemListConverter::class) // For orderItems
data class Order(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "order_items") val orderItems: List<OrderItem>, // Could be stored as JSON string or in a separate table
    @ColumnInfo(name = "total_amount") val totalAmount: Double,
    @ColumnInfo(name = "order_date") val orderDate: Long = System.currentTimeMillis(),
    val status: OrderStatus,
    @ColumnInfo(name = "shipping_address") val shippingAddress: String, // Simplified, could be an Address object
    @ColumnInfo(name = "payment_method") val paymentMethod: String?, // e.g., "Razorpay", "COD"
    @ColumnInfo(name = "payment_id") val paymentId: String?, // From payment gateway
    @ColumnInfo(name = "transaction_id") val transactionId: String?, // Internal or gateway transaction ID
    @ColumnInfo(name = "delivery_option") val deliveryOption: String?,
    @ColumnInfo(name = "estimated_delivery_date") val estimatedDeliveryDate: Long?,
    @ColumnInfo(name = "needs_sync") val needsSync: Boolean = false,
    @ColumnInfo(name = "sync_attempts") val syncAttempts: Int = 0,
    @ColumnInfo(name = "last_sync_attempt_timestamp") val lastSyncAttemptTimestamp: Long = 0L
)
