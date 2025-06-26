package com.example.rooster.core.common.enums

import kotlinx.serialization.Serializable

@Serializable
enum class BidStatus {
    ACTIVE,
    OUTBID,
    WINNING,
    CANCELLED,
    INVALID,
}
