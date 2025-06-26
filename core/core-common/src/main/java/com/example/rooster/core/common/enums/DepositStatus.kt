package com.example.rooster.core.common.enums

import kotlinx.serialization.Serializable

@Serializable
enum class DepositStatus {
    NOT_REQUIRED,
    PENDING,
    PAID,
    FORFEITED,
    REFUNDED,
}
