package com.example.rooster.core.data.fetcher

import com.example.rooster.core.data.model.AdminDashboardSummary
import com.example.rooster.core.data.model.ContentModerationInfo
import com.example.rooster.core.data.model.SystemStat
import com.example.rooster.core.data.model.UserActivitySummary
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockAdminDashboardDataFetcher @Inject constructor() : AdminDashboardDataFetcher {
    override suspend fun getDashboardSummary(adminId: String?): Result<AdminDashboardSummary> {
        delay(800) // Simulate network delay

        return if (adminId == "error_admin") {
            Result.failure(Exception("Mock fetch error for admin dashboard"))
        } else {
            Result.success(
                AdminDashboardSummary(
                    adminUserName = "SuperAdmin",
                    systemStats = listOf(
                        SystemStat("Total Active Listings", "1,250", "+50 today"),
                        SystemStat("Ongoing Auctions", "35"),
                        SystemStat("Transactions (24h)", "210", "+10%")
                    ),
                    userActivity = UserActivitySummary(
                        activeUsers = 850,
                        newRegistrationsToday = 25,
                        totalUsers = 5200
                    ),
                    contentModeration = ContentModerationInfo(
                        pendingReports = 12,
                        resolvedToday = 5
                    ),
                    criticalAlerts = listOf(
                        "High CPU usage on server B",
                        "Unusual sign-up spike from region X"
                    )
                )
            )
        }
    }
}
