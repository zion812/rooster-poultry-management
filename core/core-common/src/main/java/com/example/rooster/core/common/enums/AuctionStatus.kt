package com.example.rooster.core.common.enums

import kotlinx.serialization.Serializable

@Serializable
enum class AuctionStatus {
    PENDING,
    ACTIVE,
    ENDED,
    CANCELLED,
    SETTLED,
    EXTENDED, // For auto-extension scenarios
}
