package com.example.rooster.auction.remote

import kotlinx.serialization.Serializable

@Serializable
data class PlaceBidRequest(
    val auctionId: String,
    val amount: Double,
    val userId: String
)
