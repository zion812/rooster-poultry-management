package com.example.rooster.models

// Represents a user's order in the marketplace
data class UserOrder(
    val id: String,
    val productName: String,
    val orderDate: Long,
    val status: UserOrderStatus,
    val totalAmount: Double,
    val buyerId: String = "",
)
