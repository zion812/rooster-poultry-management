package com.example.rooster.auction.model

import kotlinx.serialization.Serializable

/**
 * Represents a single bid update in an auction.
 */
@Serializable
data class BidUpdate(
    val auctionId: String,
    val bidAmount: Double,
    val bidderName: String,
    val timestamp: Long = System.currentTimeMillis(),
)
