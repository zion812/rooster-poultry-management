package com.example.rooster.feature.auctions.data.repository

import com.example.rooster.core.common.Result
import com.example.rooster.core.common.asResult
import com.example.rooster.core.common.models.auction.AuctionListing
import com.example.rooster.core.common.models.auction.AuctionWinner
import com.example.rooster.core.common.models.auction.EnhancedAuctionBid
import com.example.rooster.core.common.enums.AuctionStatus
import com.example.rooster.core.common.enums.BidMonitoringCategory
import com.example.rooster.core.common.enums.DepositStatus
import com.example.rooster.core.common.enums.BidStatus
import com.example.rooster.core.common.enums.OfferResponse
import com.example.rooster.core.common.enums.AuctionPaymentStatus
import com.example.rooster.core.common.models.auction.BackupBidder
import com.example.rooster.feature.auctions.domain.repository.AuctionRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import com.parse.coroutines.suspendFind
import com.parse.coroutines.suspendGet
import com.parse.coroutines.suspendCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class ParseAuctionRepositoryImpl @Inject constructor() : AuctionRepository {

    override fun getActiveAuctions(): Flow<Result<List<AuctionListing>>> = callbackFlow {
        trySend(Result.Loading)
        val query = ParseQuery.getQuery<ParseObject>("AuctionListing") // Use constant for class name
        query.whereEqualTo("isActive", true)
        query.whereGreaterThanOrEqualTo("endTime", Date())
        query.include("seller")
        query.orderByAscending("endTime")
        query.limit = 50

        try {
            val objects = query.suspendFind() // Using com.parse.coroutines.suspendFind
            val auctions = objects.mapNotNull { obj ->
                try {
                    val seller = obj.getParseUser("seller")
                    AuctionListing(
                        auctionId = obj.objectId,
                        title = obj.getString("title") ?: "",
                        description = obj.getString("description") ?: "",
                        startingPrice = obj.getDouble("startingPrice"),
                        currentBid = obj.getDouble("currentBid"),
                        minimumIncrement = obj.getDouble("minimumIncrement"),
                        startTime = obj.getDate("startTime") ?: Date(),
                        endTime = obj.getDate("endTime") ?: Date(),
                        sellerId = seller?.objectId ?: "",
                        sellerName = seller?.username ?: "Unknown Seller",
                        fowlId = obj.getString("fowlId") ?: "",
                        bidCount = obj.getInt("bidCount"),
                        isReserveSet = obj.getBoolean("isReserveSet"),
                        reservePrice = obj.getDouble("reservePrice"),
                        imageUrls = obj.getList<String>("imageUrls") ?: emptyList(),
                        status = AuctionStatus.valueOf(obj.getString("status") ?: AuctionStatus.ACTIVE.name),
                        location = obj.getString("location") ?: "",
                        category = obj.getString("category") ?: "",
                        customDurationHours = obj.getInt("customDurationHours"),
                        minimumBidPrice = obj.getDouble("minimumBidPrice"),
                        requiresBidderDeposit = obj.getBoolean("requiresBidderDeposit"),
                        bidderDepositPercentage = obj.getDouble("bidderDepositPercentage"),
                        allowsProxyBidding = obj.getBoolean("allowsProxyBidding"),
                        sellerBidMonitoring = BidMonitoringCategory.valueOf(obj.getString("sellerBidMonitoring") ?: BidMonitoringCategory.ALL_BIDS.name),
                        autoExtendOnLastMinuteBid = obj.getBoolean("autoExtendOnLastMinuteBid"),
                        extensionMinutes = obj.getInt("extensionMinutes"),
                        buyNowPrice = obj.getDouble("buyNowPrice"),
                        watchers = obj.getInt("watchers")
                    )
                } catch (ex: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(ex)
                    null
                }
            }
            trySend(Result.Success(auctions)).isSuccess
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            trySend(Result.Error(e)).isFailure
        }
        awaitClose { /* Optional: cancel query if needed */ }
    }.flowOn(Dispatchers.IO)

    override fun getEnhancedAuctionBids(auctionId: String): Flow<Result<List<EnhancedAuctionBid>>> = callbackFlow {
        trySend(Result.Loading)
        val query = ParseQuery.getQuery<ParseObject>("Bid") // Use constant for class name "EnhancedAuctionBid" might be better
        query.whereEqualTo("listingId", auctionId) // Assuming "listingId" is the field for auctionId
        query.include("bidder")
        query.orderByDescending("bidAmount")
        query.limit = 100

        try {
            val objects = query.suspendFind()
            val bids = objects.mapNotNull { obj ->
                try {
                    val bidderUser = obj.getParseUser("bidder")
                    EnhancedAuctionBid(
                        bidId = obj.objectId,
                        auctionId = obj.getString("listingId") ?: "",
                        bidderId = bidderUser?.objectId ?: "",
                        bidderName = bidderUser?.username ?: "Unknown Bidder",
                        bidAmount = obj.getDouble("bidAmount"),
                        bidTime = obj.createdAt ?: Date(),
                        isWinning = obj.getBoolean("isWinning"),
                        isProxyBid = obj.getBoolean("isProxyBid"),
                        proxyMaxAmount = obj.getDouble("proxyMaxAmount"),
                        depositAmount = obj.getDouble("depositAmount"),
                        depositStatus = obj.getString("depositStatus")?.let { DepositStatus.valueOf(it) },
                        bidStatus = BidStatus.valueOf(obj.getString("bidStatus") ?: BidStatus.ACTIVE.name),
                        bidMessage = obj.getString("bidMessage"),
                        bidderRating = obj.getDouble("bidderRating"),
                        previousBidCount = obj.getInt("previousBidCount")
                    )
                } catch (ex: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(ex)
                    null
                }
            }
            trySend(Result.Success(bids)).isSuccess
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            trySend(Result.Error(e)).isFailure
        }
        awaitClose { }
    }.flowOn(Dispatchers.IO)

    override fun getAuctionWinner(auctionId: String): Flow<Result<AuctionWinner?>> = callbackFlow {
        trySend(Result.Loading)
        val query = ParseQuery.getQuery<ParseObject>("AuctionWinner") // Use constant
        query.whereEqualTo("auctionId", auctionId)
        query.include("winnerUser") // Assuming "winnerUser" is the Pointer to User for winner

        try {
            val obj = query.suspendGetFirst() // Use suspendGetFirst if only one winner is expected
            if (obj != null) {
                val winnerUser = obj.getParseUser("winnerUser")
                val backupRaw = obj.getList<Map<String, Any>>("backupBidders") ?: emptyList()
                val backups = backupRaw.mapNotNull { item ->
                    try {
                        BackupBidder(
                            bidderId = item["bidderId"] as? String ?: "",
                            bidderName = item["bidderName"] as? String ?: "",
                            bidAmount = (item["bidAmount"] as? Number)?.toDouble() ?: 0.0,
                            offerSentTime = item["offerSentTime"] as? Date,
                            offerResponse = (item["offerResponse"] as? String)?.let { OfferResponse.valueOf(it) },
                            responseDeadline = item["responseDeadline"] as? Date
                        )
                    } catch (e: Exception) { null }
                }
                val auctionWinner = AuctionWinner(
                    auctionId = obj.getString("auctionId") ?: "",
                    winnerId = winnerUser?.objectId ?: "",
                    winnerName = winnerUser?.username ?: "",
                    winningBid = obj.getDouble("winningBid"),
                    paymentDeadline = obj.getDate("paymentDeadline") ?: Date(),
                    paymentStatus = AuctionPaymentStatus.valueOf(obj.getString("paymentStatus") ?: AuctionPaymentStatus.PENDING.name),
                    backupBidders = backups
                )
                trySend(Result.Success(auctionWinner)).isSuccess
            } else {
                trySend(Result.Success(null)).isSuccess // No winner found
            }
        } catch (e: com.parse.ParseException) {
            if (e.code == com.parse.ParseException.OBJECT_NOT_FOUND) {
                trySend(Result.Success(null)).isSuccess // No winner found is not an error for this call
            } else {
                FirebaseCrashlytics.getInstance().recordException(e)
                trySend(Result.Error(e)).isFailure
            }
        } catch (e: Exception) {
             FirebaseCrashlytics.getInstance().recordException(e)
             trySend(Result.Error(e)).isFailure
        }
        awaitClose { }
    }.flowOn(Dispatchers.IO)

    override suspend fun submitBidWithDeposit(
        auctionId: String,
        bidAmount: Double,
        depositAmount: Double,
        paymentId: String,
        bidderId: String,
        bidderName: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = hashMapOf(
                "auctionId" to auctionId,
                "bidAmount" to bidAmount,
                "depositAmount" to depositAmount,
                "paymentId" to paymentId,
                "bidderId" to bidderId,
                "bidderName" to bidderName
            )
            // Using Parse Cloud function for atomic operation
            val result = com.parse.ParseCloud.callFunction<Boolean>("submitBidWithDeposit", params)
            Result.Success(result ?: false)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.Error(e)
        }
    }

    override suspend fun submitBid(
        auctionId: String,
        bidAmount: Double,
        bidderId: String,
        bidderName: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = hashMapOf(
                "auctionId" to auctionId,
                "bidAmount" to bidAmount,
                "bidderId" to bidderId,
                "bidderName" to bidderName
            )
            // Using Parse Cloud function for atomic operation
            val result = com.parse.ParseCloud.callFunction<Boolean>("submitBid", params)
            Result.Success(result ?: false)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.Error(e)
        }
    }

    override suspend fun updateAuctionCurrentBid(
        auctionId: String,
        newBidAmount: Double
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val params = hashMapOf(
                "auctionId" to auctionId,
                "newBidAmount" to newBidAmount
            )
            // Using Parse Cloud function for atomic operation
            com.parse.ParseCloud.callFunction<Any>("updateAuctionCurrentBid", params)
            Result.Success(Unit)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.Error(e)
        }
    }
}

// Helper extension for suspendGet that returns null if not found, rather than throwing.
suspend fun <T : ParseObject> ParseQuery<T>.suspendGetFirst(): T? {
    return try {
        limit = 1
        val results = suspendFind()
        results.firstOrNull()
    } catch (e: com.parse.ParseException) {
        if (e.code == com.parse.ParseException.OBJECT_NOT_FOUND) {
            null
        } else {
            throw e
        }
    }
}
