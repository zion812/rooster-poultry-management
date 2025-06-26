package com.example.rooster.core.common.enums

import kotlinx.serialization.Serializable

@Serializable
enum class AuctionPaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    EXPIRED,
    CANCELLED,
}
