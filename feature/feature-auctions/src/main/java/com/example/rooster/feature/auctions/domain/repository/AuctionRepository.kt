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

    /**
     * Submits a bid with deposit payment for an auction.
     * Uses atomic Cloud Function for data consistency.
     */
    suspend fun submitBidWithDeposit(
        auctionId: String,
        bidAmount: Double,
        depositAmount: Double,
        paymentId: String,
        bidderId: String,
        bidderName: String
    ): Result<Boolean>

    /**
     * Submits a regular bid without deposit.
     */
    suspend fun submitBid(
        auctionId: String,
        bidAmount: Double,
        bidderId: String,
        bidderName: String
    ): Result<Boolean>

    /**
     * Updates auction current bid amount.
     * Should be called via Cloud Function for atomicity.
     */
    suspend fun updateAuctionCurrentBid(
        auctionId: String,
        newBidAmount: Double
    ): Result<Unit>
}
