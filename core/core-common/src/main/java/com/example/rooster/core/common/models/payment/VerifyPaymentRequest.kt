package com.example.rooster.core.common.models.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyPaymentRequest(
    @SerialName("razorpay_order_id")
    val razorpayOrderId: String,
    @SerialName("razorpay_payment_id")
    val razorpayPaymentId: String,
    @SerialName("razorpay_signature")
    val razorpaySignature: String,
    val auctionId: String? = null // Optional context
    // Add any other relevant data client needs to send for verification context
)
