package com.example.rooster.feature.marketplace.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentDetails(
    val paymentId: String?, // From payment gateway, could be null if COD or not yet paid
    val orderId: String, // Platform's order ID
    val method: String?, // e.g., "RAZORPAY_CARD", "RAZORPAY_UPI", "COD"
    val status: PaymentStatus,
    val amountPaid: Double,
    val currency: String = "INR",
    val transactionTimestamp: Long?,
    val gatewayResponseCode: String? = null,
    val gatewayResponseMessage: String? = null
)
