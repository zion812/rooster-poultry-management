package com.example.rooster.core.common.models.auction

import com.example.rooster.core.common.enums.BidStatus
import com.example.rooster.core.common.enums.DepositStatus
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class EnhancedAuctionBid(
    val bidId: String,
    val auctionId: String,
    val bidderId: String,
    val bidderName: String,
    val bidAmount: Double,
    val bidTime: Date, // Consider Long timestamp
    val isWinning: Boolean,
    val isProxyBid: Boolean,
    val proxyMaxAmount: Double?,
    val depositAmount: Double?,
    val depositStatus: DepositStatus?,
    val bidStatus: BidStatus,
    val bidMessage: String?,
    val bidderRating: Double?, // Made nullable as it might not always be present
    val previousBidCount: Int?, // Made nullable
)
