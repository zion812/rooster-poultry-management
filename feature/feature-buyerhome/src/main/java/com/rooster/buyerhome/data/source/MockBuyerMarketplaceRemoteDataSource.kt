package com.rooster.buyerhome.data.source

import com.rooster.buyerhome.domain.model.MarketplaceRecommendationItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random

class MockBuyerMarketplaceRemoteDataSource @Inject constructor() : BuyerMarketplaceRemoteDataSource {
    override fun getMarketplaceRecommendations(buyerId: String): Flow<List<MarketplaceRecommendationItem>> = flow {
        delay(700) // Simulate delay
        val recommendations = listOf(
            MarketplaceRecommendationItem(
                id = "prod101",
                productName = "Organic Broilers (Grade A)",
                sellerName = "Krishna Poultry Farms",
                price = "₹180/kg",
                imageUrl = null, // TODO: Add placeholder image URL if available
                location = "Gudivada"
            ),
            MarketplaceRecommendationItem(
                id = "prod102",
                productName = "Country Chicken Eggs (Large)",
                sellerName = "Rural Farms Collective",
                price = "₹90/dozen",
                imageUrl = null,
                location = "Nuzvid"
            ),
            MarketplaceRecommendationItem(
                id = "prod103",
                productName = "Poultry Feed (High Protein)",
                sellerName = "AgriSupplies Inc.",
                price = "₹2200/quintal",
                imageUrl = null,
                location = "Vijayawada"
            )
        )
        emit(recommendations.shuffled().take(Random.nextInt(1, recommendations.size + 1)))
    }
}
