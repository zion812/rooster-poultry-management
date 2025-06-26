package com.example.rooster.core.common.models

/**
 * Data class for bid statistics, potentially reusable if other bidding features exist.
 * If strictly for auctions, could reside in feature-auctions domain/model.
 * Moving to core-common for now if it's simple and might be referenced elsewhere (e.g. admin dashboards).
 */
data class BidStatistics(
    val totalBids: Int,
    val bidsAboveMinimum: Int, // This implies a concept of "minimum" which might be auction-specific
    val bidsBelowMinimum: Int, // Same as above
    val highestBid: Double,
    val averageBid: Double,
    val uniqueBidders: Int,
)
