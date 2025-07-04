package com.example.rooster.core.data.fetcher

import com.example.rooster.core.data.model.BuyerDashboardSummary
import com.example.rooster.core.data.model.FeaturedProductTeaser
import com.example.rooster.core.data.model.OrderTeaser
import com.example.rooster.core.data.model.ProductCategoryTeaser
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockBuyerDashboardDataFetcher @Inject constructor() : BuyerDashboardDataFetcher {
    override suspend fun getDashboardSummary(userId: String): Result<BuyerDashboardSummary> {
        delay(1200) // Simulate network delay

        return if (userId == "error_buyer") {
            Result.failure(Exception("Mock fetch error for buyer $userId"))
        } else {
            Result.success(
                BuyerDashboardSummary(
                    userName = "Priya Sharma", // Mocked name
                    featuredProducts = listOf(
                        FeaturedProductTeaser("prod1", "Healthy Kadaknath Chicks (1 month)", null, "₹250/chick"),
                        FeaturedProductTeaser("prod2", "Organic Poultry Feed (50kg)", null, "₹1800"),
                        FeaturedProductTeaser("prod3", "Automatic Drinker System", null, "₹3500")
                    ),
                    recentOrders = listOf(
                        OrderTeaser("order123", "20 Dec 2023", "Shipped", "₹2750"),
                        OrderTeaser("order121", "15 Dec 2023", "Delivered", "₹1200")
                    ),
                    browseCategories = listOf(
                        ProductCategoryTeaser("cat1", "Live Birds", null),
                        ProductCategoryTeaser("cat2", "Poultry Feed", null),
                        ProductCategoryTeaser("cat3", "Equipment", null),
                        ProductCategoryTeaser("cat4", "Medicines", null)
                    ),
                    personalizedMessage = "Special discounts on Aseel breed this week!"
                )
            )
        }
    }
}
