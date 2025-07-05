package com.rooster.buyerhome.domain.repository

import com.rooster.buyerhome.domain.model.MarketplaceRecommendationItem
import kotlinx.coroutines.flow.Flow

interface BuyerMarketplaceRepository {
    fun getMarketplaceRecommendations(buyerId: String): Flow<List<MarketplaceRecommendationItem>>
}
