package com.example.rooster.core.common.enums

import kotlinx.serialization.Serializable

@Serializable
enum class OfferResponse {
    PENDING,
    ACCEPTED,
    DECLINED,
    EXPIRED,
}
