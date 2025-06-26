package com.example.rooster.core.common.models.auction

import com.example.rooster.core.common.enums.AuctionPaymentStatus
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class AuctionWinner(
    val auctionId: String,
    val winnerId: String,
    val winnerName: String,
    val winningBid: Double,
    val paymentDeadline: Date, // Consider Long
    val paymentStatus: AuctionPaymentStatus,
    val backupBidders: List<BackupBidder>,
)
