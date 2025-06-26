package com.example.rooster.core.common.models.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RazorpayOrderResponse(
    val id: String, // Razorpay Order ID
    val entity: String? = "order",
    val amount: Int, // Amount in paise
    @SerialName("amount_paid")
    val amountPaid: Int? = 0,
    @SerialName("amount_due")
    val amountDue: Int? = 0,
    val currency: String, // e.g., INR
    val receipt: String?,
    val status: String? = "created",
    val attempts: Int? = 0,
    val notes: Map<String, String?>?, // Consider making value non-nullable if API guarantees it: Map<String, String>
    @SerialName("created_at")
    val createdAt: Long? = null, // Timestamp in seconds
    @SerialName("key_id") // Align with what backend service now sends
    val keyId: String? = null
)
