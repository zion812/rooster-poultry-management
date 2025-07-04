package com.example.rooster.core.data.fetcher

import com.example.rooster.core.data.model.AdminDashboardSummary

interface AdminDashboardDataFetcher {
    /**
     * Fetches a summary of dashboard data for an admin user.
     * @param adminId The ID of the admin user (or could be system-wide, no ID needed).
     * @return Result containing AdminDashboardSummary on success, or an exception on failure.
     */
    suspend fun getDashboardSummary(adminId: String?): Result<AdminDashboardSummary> // adminId might be optional

    // Potential future methods:
    // suspend fun getDetailedUserReport(filters: Map<String, Any>): Result<UserReport>
    // suspend fun getSystemHealth(): Result<SystemHealthStatus>
}
