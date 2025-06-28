package com.example.rooster.feature.marketplace.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.rooster.feature.marketplace.data.local.MarketplaceTypeConverters
import com.example.rooster.feature.marketplace.domain.model.OrderStatus
import com.example.rooster.feature.marketplace.domain.model.PaymentDetails // Assuming PaymentDetails will be TypeConverted

@Entity(tableName = "orders")
@TypeConverters(MarketplaceTypeConverters::class) // For OrderStatus, PaymentDetails
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val buyerId: String,
    // List<OrderItemEntity> will be handled by a separate DAO query, not stored as a field here.
    val subTotalAmount: Double,
    val shippingCost: Double = 0.0,
    val discountAmount: Double = 0.0,
    val taxAmount: Double = 0.0,
    val totalOrderAmount: Double,
    val currency: String = "INR",
    val orderTimestamp: Long,
    var lastUpdatedTimestamp: Long,
    var status: OrderStatus, // Handled by TypeConverter

    @Embedded(prefix = "shipping_") // Prefix to avoid column name clashes if billing_ is also embedded
    val shippingAddress: ShippingAddressEntity,

    @Embedded(prefix = "billing_")
    val billingAddress: ShippingAddressEntity? = null, // Optional

    // PaymentDetails could be complex. Storing as JSON string via TypeConverter for simplicity.
    // Alternatively, it could be its own table with a foreign key if more query capabilities are needed.
    val paymentDetailsJson: String? = null, // JSON string of PaymentDetails domain model

    val deliveryInstructions: String? = null,
    val expectedDeliveryDate: Long? = null,
    val shipmentProvider: String? = null,
    val trackingNumber: String? = null,
    var needsSync: Boolean = true // For offline created/updated orders
)
