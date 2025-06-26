package com.example.rooster.core.common.models.payment

import kotlinx.serialization.Serializable

@Serializable
data class VerifyPaymentResponse(
    val success: Boolean,
    val message: String, // Could be a non-localized key or a direct message
    val data: PaymentVerificationData? = null // Present on success
)
