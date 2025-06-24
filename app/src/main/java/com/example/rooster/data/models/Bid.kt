package com.example.rooster.data.models

import java.util.*

data class Bid(
    val id: String = "",
    val auctionId: String = "",
    val bidderId: String = "",
    val bidderName: String = "",
    val amount: Double = 0.0,
    val timestamp: Date = Date(),
    val isValid: Boolean = true,
)
