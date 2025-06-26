package com.example.rooster.core.common.models.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentVerificationData(
    @SerialName("order_id")
    val orderId: String,
    @SerialName("payment_id")
    val paymentId: String,
    val status: String // e.g., "VERIFIED", "FAILED"
)
