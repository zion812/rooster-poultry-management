package com.example.rooster.core.data.fetcher

import com.example.rooster.core.data.model.BuyerDashboardSummary

interface BuyerDashboardDataFetcher {
    /**
     * Fetches a summary of dashboard data for a given buyer.
     * @param userId The ID of the buyer.
     * @return Result containing BuyerDashboardSummary on success, or an exception on failure.
     */
    suspend fun getDashboardSummary(userId: String): Result<BuyerDashboardSummary>

    // Potential future methods:
    // suspend fun getBuyerNotifications(userId: String): Result<List<Notification>>
    // suspend fun getRecommendedProducts(userId: String): Result<List<ProductTeaser>>
}
