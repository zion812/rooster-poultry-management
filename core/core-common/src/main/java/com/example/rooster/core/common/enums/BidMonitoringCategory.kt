package com.example.rooster.core.common.enums

import kotlinx.serialization.Serializable

@Serializable
enum class BidMonitoringCategory {
    ALL_BIDS, // Show all bids to seller
    WINNING_BIDS_ONLY, // Show only winning bids
    PRIVATE_BIDDING, // Hide all bidder details except highest
    SELLER_NOTIFICATIONS_ONLY, // Only notify seller, no live monitoring
}
