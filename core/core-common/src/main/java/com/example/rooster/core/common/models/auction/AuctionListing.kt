package com.example.rooster.core.common.models.auction

import com.example.rooster.core.common.enums.AuctionStatus // Updated import
import com.example.rooster.core.common.enums.BidMonitoringCategory // Updated import
import kotlinx.serialization.Serializable
import java.util.Date

// TODO: Review all model fields for appropriate nullability and default values.
// TODO: Ensure AuctionStatus and BidMonitoringCategory are moved to a shared location (e.g., core.common.enums)
// For now, assuming they are accessible via com.example.rooster.models.*

@Serializable
data class AuctionListing(
    val auctionId: String,
    val title: String,
    val description: String,
    val startingPrice: Double,
    val currentBid: Double,
    val minimumIncrement: Double,
    val startTime: Date, // Consider using Long for timestamps for easier serialization/Parcelization
    val endTime: Date,   // Consider using Long
    val sellerId: String,
    val sellerName: String,
    val fowlId: String, // Could be a more complex Fowl object/ID if needed
    val bidCount: Int,
    val isReserveSet: Boolean,
    val reservePrice: Double,
    val imageUrls: List<String> = emptyList(),
    val status: AuctionStatus, // Enum, needs to be accessible
    val location: String,
    val category: String, // Consider an enum if categories are fixed

    // Fields from EnhancedAuction (if this model is meant to cover that too)
    val customDurationHours: Int? = null,
    val minimumBidPrice: Double? = null,
    val requiresBidderDeposit: Boolean? = null,
    val bidderDepositPercentage: Double? = null,
    val allowsProxyBidding: Boolean? = null,
    val sellerBidMonitoring: BidMonitoringCategory? = null, // Enum, needs to be accessible
    val autoExtendOnLastMinuteBid: Boolean? = null,
    val extensionMinutes: Int? = null,
    val buyNowPrice: Double? = null, // Nullable if not always present
    val watchers: Int? = null
)
