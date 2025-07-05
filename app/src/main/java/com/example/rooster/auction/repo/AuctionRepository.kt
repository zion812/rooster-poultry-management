package com.example.rooster.auction.repo

import com.example.rooster.auction.model.BidUpdate
import kotlinx.coroutines.flow.Flow

interface AuctionRepository {
    fun observeBids(auctionId: String): Flow<BidUpdate>

    suspend fun placeBid(
        auctionId: String,
        amount: Double,
    ): Result<Unit>
}
