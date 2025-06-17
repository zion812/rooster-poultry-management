package com.example.rooster.auction.model

import kotlinx.serialization.Serializable

/**
 * Represents a single bid update in an auction.
 */
@Serializable
data class BidUpdate(
    val auctionId: String,
    val bidderId: String,
    val amount: Double,
    val timestamp: Long,
)
