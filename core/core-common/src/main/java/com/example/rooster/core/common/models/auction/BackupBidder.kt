package com.example.rooster.core.common.models.auction

import com.example.rooster.core.common.enums.OfferResponse
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class BackupBidder(
    val bidderId: String,
    val bidderName: String,
    val bidAmount: Double,
    val offerSentTime: Date?, // Consider Long
    val offerResponse: OfferResponse?,
    val responseDeadline: Date?, // Consider Long
)
