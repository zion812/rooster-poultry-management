package com.example.rooster.feature.marketplace.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class PaymentStatus {
    PENDING,
    SUCCESSFUL,
    FAILED,
    REFUNDED,
    PARTIALLY_REFUNDED,
    AWAITING_CAPTURE // For payments that are authorized but not yet captured
}
