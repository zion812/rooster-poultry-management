package com.example.rooster.auction.remote

import kotlinx.serialization.Serializable

@Serializable
data class PlaceBidRequest(val amount: Double)
