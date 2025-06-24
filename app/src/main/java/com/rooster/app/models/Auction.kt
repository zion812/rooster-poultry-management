package com.rooster.app.models

import java.util.*

data class Auction(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val breed: String = "",
    val age: Int = 0,
    val currentBid: Double = 0.0,
    val highestBidder: String = "",
    val sellerName: String = "",
    val timeRemaining: Long = 0L,
    val isEnded: Boolean = false,
    val startTime: Date = Date(),
    val endTime: Date = Date(),
    val minBidIncrement: Double = 100.0,
    val imageUrl: String = "",
    val quantity: Int = 1,
    val location: String = "",
)
