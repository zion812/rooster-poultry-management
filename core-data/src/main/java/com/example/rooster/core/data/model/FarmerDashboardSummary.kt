package com.example.rooster.core.data.model

// Using basic types for mock data. Real implementation might have more complex types.
data class FarmerDashboardSummary(
    val userName: String,
    val farmCount: Int,
    val totalFlocks: Int,
    val totalBirds: Int,
    val upcomingAlerts: List<String>, // e.g., "Vaccination due for Flock A"
    val healthTips: List<String> // e.g., "Ensure clean water for poultry."
)
