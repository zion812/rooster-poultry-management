package com.example.rooster.feature.marketplace.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus {
    PENDING_PAYMENT,    // Order created, awaiting payment
    AWAITING_CONFIRMATION, // Payment received, awaiting seller confirmation
    CONFIRMED,          // Seller confirmed, preparing for shipment/pickup
    PROCESSING,         // Being prepared
    READY_FOR_PICKUP,
    OUT_FOR_DELIVERY,
    SHIPPED,            // Handed over to delivery partner
    DELIVERED,          // Customer received the order
    CANCELLED_BY_USER,
    CANCELLED_BY_SELLER,
    CANCELLED_BY_ADMIN,
    REFUND_INITIATED,
    REFUNDED,
    FAILED              // Order failed due to payment or other issues before processing
}
