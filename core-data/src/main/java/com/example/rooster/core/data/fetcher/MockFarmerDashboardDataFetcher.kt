package com.example.rooster.core.data.fetcher

import com.example.rooster.core.data.model.FarmerDashboardSummary
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockFarmerDashboardDataFetcher @Inject constructor() : FarmerDashboardDataFetcher {
    override suspend fun getDashboardSummary(userId: String): Result<FarmerDashboardSummary> {
        delay(1000) // Simulate network delay

        // Simulate success or failure based on userId or other conditions for testing
        return if (userId == "error_user") {
            Result.failure(Exception("Mock fetch error for user $userId"))
        } else {
            Result.success(
                FarmerDashboardSummary(
                    userName = "Suresh Kumar", // Mocked name
                    farmCount = 2,
                    totalFlocks = 15,
                    totalBirds = 750,
                    upcomingAlerts = listOf(
                        "Vaccination for Flock B2 due on 25th Dec",
                        "Low feed stock for Farm Alpha"
                    ),
                    healthTips = listOf(
                        "Ensure fresh water is available at all times.",
                        "Check for signs of mites regularly.",
                        "Maintain good ventilation in the coop."
                    )
                )
            )
        }
    }
}
