package com.example.rooster.core.data.fetcher

import com.example.rooster.core.data.model.FarmerDashboardSummary

interface FarmerDashboardDataFetcher {
    /**
     * Fetches a summary of dashboard data for a given farmer.
     * @param userId The ID of the farmer.
     * @return Result containing FarmerDashboardSummary on success, or an exception on failure.
     */
    suspend fun getDashboardSummary(userId: String): Result<FarmerDashboardSummary>

    // Potentially other methods for more specific dashboard data pieces if not all come at once
    // suspend fun getFarmerAlerts(userId: String): Result<List<Alert>>
    // suspend fun getFarmerHealthTips(userId: String): Result<List<Tip>>
}
