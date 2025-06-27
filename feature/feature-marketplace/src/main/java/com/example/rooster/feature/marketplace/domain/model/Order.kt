package com.example.rooster.feature.marketplace.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val orderId: String, // Platform-generated unique order ID
    val buyerId: String, // User ID of the buyer
    val items: List<OrderItem>,
    val subTotalAmount: Double, // Sum of OrderItem.totalPrice
    val shippingCost: Double = 0.0, // Can be calculated based on address/seller policies
    val discountAmount: Double = 0.0, // If any promotions applied
    val taxAmount: Double = 0.0, // If applicable
    val totalOrderAmount: Double, // subTotal + shipping + tax - discount
    val currency: String = "INR",
    val orderTimestamp: Long,
    var lastUpdatedTimestamp: Long,
    var status: OrderStatus,
    val shippingAddress: ShippingAddress, // Structured address
    val billingAddress: ShippingAddress? = null, // Optional, if different from shipping
    val paymentDetails: PaymentDetails? = null, // Populated after payment attempt
    val deliveryInstructions: String? = null,
    val expectedDeliveryDate: Long? = null, // Estimated
    // Tracking info if applicable
    val shipmentProvider: String? = null,
    val trackingNumber: String? = null
)

@Serializable
data class ShippingAddress(
    val fullName: String,
    val addressLine1: String,
    val addressLine2: String? = null,
    val city: String,
    val district: String,
    val state: String,
    val postalCode: String,
    val country: String = "IN",
    val phoneNumber: String,
    val landmark: String? = null
)
