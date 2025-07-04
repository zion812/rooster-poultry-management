package com.rooster.farmerhome.domain.model

data class FarmBasicInfo(
    val farmId: String,
    val farmName: String,
    val location: String,
    val ownerName: String, // Or perhaps this comes from a separate UserProfile model
    val activeFlockCount: Int,
    val totalCapacity: Int, // e.g., bird capacity
    val lastHealthCheckDate: String? // Formatted date string
)
