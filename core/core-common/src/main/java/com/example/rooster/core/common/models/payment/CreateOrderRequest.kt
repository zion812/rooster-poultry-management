package com.example.rooster.core.common.models.payment

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val amount: Int, // Amount in paise
    val currency: String, // e.g., "INR"
    val receiptId: String,
    val notes: Map<String, String?>? = null
)
