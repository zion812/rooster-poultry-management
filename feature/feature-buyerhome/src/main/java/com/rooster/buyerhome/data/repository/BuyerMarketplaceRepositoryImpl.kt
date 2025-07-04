package com.rooster.buyerhome.data.repository

import com.rooster.buyerhome.data.source.BuyerMarketplaceRemoteDataSource
import com.rooster.buyerhome.domain.model.MarketplaceRecommendationItem
import com.rooster.buyerhome.domain.repository.BuyerMarketplaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BuyerMarketplaceRepositoryImpl @Inject constructor(
    private val remoteDataSource: BuyerMarketplaceRemoteDataSource
    // TODO: Add localDataSource for caching if needed
) : BuyerMarketplaceRepository {
    override fun getMarketplaceRecommendations(buyerId: String): Flow<List<MarketplaceRecommendationItem>> {
        return remoteDataSource.getMarketplaceRecommendations(buyerId)
    }
}
