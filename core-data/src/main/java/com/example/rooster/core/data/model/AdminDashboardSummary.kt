package com.example.rooster.core.data.model

// Placeholder data classes for admin-specific summary items
data class SystemStat(
    val name: String,
    val value: String,
    val trend: String? = null // e.g., "+5% last week"
)

data class UserActivitySummary(
    val activeUsers: Int,
    val newRegistrationsToday: Int,
    val totalUsers: Int
)

data class ContentModerationInfo(
    val pendingReports: Int,
    val resolvedToday: Int
)

data class AdminDashboardSummary(
    val adminUserName: String,
    val systemStats: List<SystemStat>, // e.g., Total Listings, Active Auctions, Transactions Today
    val userActivity: UserActivitySummary,
    val contentModeration: ContentModerationInfo,
    val criticalAlerts: List<String> // e.g., "Server load high", "Unusual transaction volume"
)
