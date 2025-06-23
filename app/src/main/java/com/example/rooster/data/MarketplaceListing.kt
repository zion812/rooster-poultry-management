package com.example.rooster.data

data class MarketplaceListing(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String? = null,
    val sellerId: String,
    val contactInfo: String
)
