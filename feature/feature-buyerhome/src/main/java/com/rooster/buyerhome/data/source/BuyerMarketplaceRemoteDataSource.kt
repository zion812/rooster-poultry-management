package com.rooster.buyerhome.data.source

import com.rooster.buyerhome.domain.model.MarketplaceRecommendationItem
import kotlinx.coroutines.flow.Flow

interface BuyerMarketplaceRemoteDataSource {
    fun getMarketplaceRecommendations(buyerId: String): Flow<List<MarketplaceRecommendationItem>>
}
