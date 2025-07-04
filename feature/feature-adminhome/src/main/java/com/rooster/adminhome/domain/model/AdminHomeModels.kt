package com.rooster.adminhome.domain.model

import java.util.Date

// For System Monitoring Dashboard
enum class SystemStatus {
    OPERATIONAL, DEGRADED, OFFLINE, MAINTENANCE
}

data class SystemMetric(
    val id: String,
    val name: String, // e.g., "API Latency", "Database Connections", "CPU Usage"
    val value: String, // e.g., "120ms", "85/100", "65%"
    val status: SystemStatus,
    val lastUpdated: Date
)

// For User Management Interface (Summary)
data class UserManagementInfo(
    val totalUsers: Int,
    val newUsersToday: Int,
    val activeUsers: Int,
    val pendingVerifications: Int
)

// For Financial Analytics Display (Highlights)
data class FinancialAnalyticHighlight(
    val title: String, // e.g., "Total Revenue (MTD)", "New Subscriptions", "Transaction Volume"
    val value: String, // e.g., "₹1,50,000", "120", "5,670"
    val trendPercentage: Double?, // e.g., 5.2 (for +5.2%), -2.1 (for -2.1%)
    val period: String // e.g., "Month-to-Date", "Last 24h"
)

// For Content Moderation Tools (Queue Item Summary)
enum class ContentType {
    POST, COMMENT, USER_PROFILE, PRODUCT_LISTING
}

enum class ModerationStatus {
    PENDING_REVIEW, APPROVED, REJECTED, ESCALATED
}

data class ContentModerationItem(
    val id: String,
    val contentType: ContentType,
    val contentSnippet: String, // e.g., first 100 chars of a post
    val reportedByUserId: String?,
    val reasonForFlag: String?,
    val submissionDate: Date,
    val status: ModerationStatus
)
