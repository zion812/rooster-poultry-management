package com.example.rooster.feature.auctions.domain.repository

import com.example.rooster.core.common.Result
import com.example.rooster.core.common.models.auction.AuctionListing
import com.example.rooster.core.common.models.auction.AuctionWinner
import com.example.rooster.core.common.models.auction.EnhancedAuctionBid
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for fetching auction-related data.
 */
interface AuctionRepository {

    /**
     * Fetches active auction listings.
     * Using Flow for a more reactive approach.
     */
    fun getActiveAuctions(): Flow<Result<List<AuctionListing>>>

    /**
     * Fetches enhanced bid details for a specific auction.
     */
    fun getEnhancedAuctionBids(auctionId: String): Flow<Result<List<EnhancedAuctionBid>>>

    /**
     * Fetches the winner details for a specific auction.
     */
    fun getAuctionWinner(auctionId: String): Flow<Result<AuctionWinner?>> // Winner might be null

    // TODO: Add methods for submitting bids (which would interact with ParseObjects or Cloud Functions)
    // e.g., suspend fun submitBid(auctionId: String, bidAmount: Double, /* ... other params ... */): Result<Boolean>
    // e.g., suspend fun submitBidWithDeposit(auctionId: String, bidAmount: Double, depositAmount: Double, paymentId: String): Result<Boolean>
    // TODO: Add method for updating auction current bid (likely via Cloud Function)
    // e.g., suspend fun updateAuctionCurrentBid(auctionId: String, newBidAmount: Double): Result<Unit>

}
