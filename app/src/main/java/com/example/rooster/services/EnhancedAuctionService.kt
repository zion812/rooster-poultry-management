package com.example.rooster.services

import com.example.rooster.AuctionListing
import com.example.rooster.AuctionPaymentStatus
import com.example.rooster.AuctionSettings
import com.example.rooster.AuctionStatus
import com.example.rooster.AuctionWinner
import com.example.rooster.BackupBidder
import com.example.rooster.BidMonitoringCategory
import com.example.rooster.BidStatus
import com.example.rooster.DepositStatus
import com.example.rooster.EnhancedAuctionBid
import com.example.rooster.OfferResponse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import kotlin.math.max
import kotlin.random.Random

/**
 * Enhanced Auction Service implementing all advanced auction mechanics:
 * - Seller bid controls and custom duration
 * - Bidder deposit system (5-25%)
 * - Post-auction payment flow with 10-minute window
 * - Automatic backup bidder cascade system
 * - Proxy bidding and auto-extension
 */
class EnhancedAuctionService {
    companion object {
        private const val AUCTION_CLASS = "AuctionListing"
        private const val BID_CLASS = "AuctionBid"
        private const val DEPOSIT_CLASS = "BidDeposit"

        // Duration presets in hours
        val AUCTION_DURATION_PRESETS = listOf(12, 24, 48, 72, 96, 120, 168)

        // Deposit percentage limits
        const val MIN_DEPOSIT_PERCENTAGE = 5.0
        const val MAX_DEPOSIT_PERCENTAGE = 25.0
    }

    /**
     * Creates a new auction with enhanced seller controls
     */
    suspend fun createAuction(settings: AuctionSettings): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                // Validate settings
                validateAuctionSettings(settings)

                val auctionObject =
                    ParseObject(AUCTION_CLASS).apply {
                        put("sellerId", settings.sellerId)
                        put("fowlId", settings.fowlId)
                        put("startingPrice", settings.startingPrice)
                        put("reservePrice", settings.reservePrice ?: 0.0)
                        put("customDurationHours", settings.customDurationHours)
                        put("minimumBidPrice", settings.minimumBidPrice)
                        put("requiresBidderDeposit", settings.requiresBidderDeposit)
                        put("bidderDepositPercentage", settings.bidderDepositPercentage)
                        put("allowsProxyBidding", settings.allowsProxyBidding)
                        put("sellerBidMonitoring", settings.sellerBidMonitoring.name)
                        put("autoExtendOnLastMinuteBid", settings.autoExtendOnLastMinuteBid)
                        put("extensionMinutes", settings.extensionMinutes)
                        put("buyNowPrice", settings.buyNowPrice ?: 0.0)
                        put("startTime", settings.startTime)
                        put(
                            "endTime",
                            calculateEndTime(settings.startTime, settings.customDurationHours),
                        )
                        put("status", AuctionStatus.PENDING.name)
                        put("currentBid", settings.startingPrice)
                        put("bidCount", 0)
                        put("watchers", 0)
                        put("allowedBidderTypes", settings.allowedBidderTypes.map { it.name })
                        put("minimumIncrement", calculateMinimumIncrement(settings.minimumBidPrice))
                        put(
                            "isReserveSet",
                            settings.reservePrice != null && settings.reservePrice > 0,
                        )
                    }

                auctionObject.save()

                FirebaseCrashlytics.getInstance()
                    .log("Enhanced auction created: ${auctionObject.objectId}")
                Result.success(auctionObject.objectId)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(e)
            }
        }

    /**
     * Places a bid with deposit handling and validation
     */
    suspend fun placeBid(
        auctionId: String,
        bidAmount: Double,
        bidderMessage: String? = null,
        proxyMaxAmount: Double? = null,
    ): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val auction =
                    fetchAuctionById(auctionId)
                        ?: return@withContext Result.failure(Exception("Auction not found"))

                val currentUser =
                    ParseUser.getCurrentUser()
                        ?: return@withContext Result.failure(Exception("User not logged in"))

                // Validate bid
                validateBid(auction, bidAmount, currentUser.objectId)

                // Handle deposit if required
                val depositAmount =
                    if (auction.requiresBidderDeposit) {
                        bidAmount * (auction.bidderDepositPercentage / 100.0)
                    } else {
                        null
                    }

                val depositStatus =
                    if (depositAmount != null) {
                        // In real implementation, integrate with payment gateway
                        handleBidderDeposit(depositAmount, currentUser.objectId)
                    } else {
                        DepositStatus.NOT_REQUIRED
                    }

                // Create bid record
                val bidObject =
                    ParseObject(BID_CLASS).apply {
                        put("auctionId", auctionId)
                        put("bidderId", currentUser.objectId)
                        put("bidderName", currentUser.username ?: "Unknown")
                        put("bidAmount", bidAmount)
                        put("bidTime", Date())
                        put("isWinning", true) // Will be updated as new bids come in
                        put("isProxyBid", proxyMaxAmount != null)
                        put("proxyMaxAmount", proxyMaxAmount ?: 0.0)
                        put("depositAmount", depositAmount ?: 0.0)
                        put("depositStatus", depositStatus.name)
                        put("bidStatus", BidStatus.ACTIVE.name)
                        put("bidMessage", bidderMessage ?: "")
                        put("bidderRating", getBidderRating(currentUser.objectId))
                        put("previousBidCount", getPreviousBidCount(auctionId, currentUser.objectId))
                    }

                bidObject.save()

                // Update auction with new highest bid
                updateAuctionWithNewBid(auctionId, bidAmount, bidObject.objectId)

                // Handle auto-extension if needed
                handleAutoExtension(auction, bidObject)

                // Notify seller based on monitoring preferences
                notifySellerOfBid(auction, bidObject)

                FirebaseCrashlytics.getInstance().log("Bid placed: $bidAmount on $auctionId")
                Result.success(bidObject.objectId)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(e)
            }
        }

    /**
     * Handles auction end and initiates payment cascade
     */
    suspend fun endAuction(auctionId: String): Result<AuctionWinner?> =
        withContext(Dispatchers.IO) {
            try {
                val auction =
                    fetchAuctionById(auctionId)
                        ?: return@withContext Result.failure(Exception("Auction not found"))

                val winningBid =
                    getWinningBid(auctionId)
                        ?: return@withContext Result.success(null) // No bids

                // Create auction winner record
                val winner =
                    AuctionWinner(
                        auctionId = auctionId,
                        winnerId = winningBid.bidderId,
                        winnerName = winningBid.bidderName,
                        winningBid = winningBid.bidAmount,
                        paymentDeadline = Date(System.currentTimeMillis() + 10 * 60 * 1000), // 10 minutes
                        paymentStatus = AuctionPaymentStatus.PENDING,
                        backupBidders = prepareBackupBidders(auctionId, winningBid.bidderId),
                    )

                // Save winner record
                val winnerObject =
                    ParseObject("AuctionWinner").apply {
                        put("auctionId", auctionId)
                        put("winnerId", winner.winnerId)
                        put("winnerName", winner.winnerName)
                        put("winningBid", winner.winningBid)
                        put("paymentDeadline", winner.paymentDeadline)
                        put("paymentStatus", AuctionPaymentStatus.PENDING.name)
                    }
                winnerObject.save()

                // Update auction status
                updateAuctionStatus(auctionId, AuctionStatus.ENDED)

                // Start payment timer
                startPaymentTimer(auctionId, winner)

                // Notify winner and prepare backup cascade
                notifyWinner(winner)

                Result.success(winner)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(e)
            }
        }

    /**
     * Handles payment completion or failure
     */
    suspend fun handlePayment(
        auctionId: String,
        isSuccessful: Boolean,
        paymentReference: String? = null,
    ): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val winner =
                    getAuctionWinner(auctionId)
                        ?: return@withContext Result.failure(Exception("Winner not found"))

                if (isSuccessful) {
                    // Payment successful
                    updatePaymentStatus(auctionId, AuctionPaymentStatus.COMPLETED)
                    updateAuctionStatus(auctionId, AuctionStatus.SETTLED)
                    transferOwnership(auctionId, winner)
                    refundLosingBidders(auctionId)

                    FirebaseCrashlytics.getInstance().log("Auction payment completed: $auctionId")
                    Result.success(true)
                } else {
                    // Payment failed - start backup cascade
                    updatePaymentStatus(auctionId, AuctionPaymentStatus.FAILED)
                    forfeitWinnerDeposit(winner)
                    startBackupCascade(auctionId, winner)

                    FirebaseCrashlytics.getInstance()
                        .log("Auction payment failed, starting cascade: $auctionId")
                    Result.success(false)
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(e)
            }
        }

    /**
     * Buy Now functionality for immediate purchase
     */
    suspend fun buyNow(auctionId: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val auction =
                    fetchAuctionById(auctionId)
                        ?: return@withContext Result.failure(Exception("Auction not found"))

                val buyNowPrice =
                    auction.buyNowPrice
                        ?: return@withContext Result.failure(Exception("Buy Now not available"))

                val currentUser =
                    ParseUser.getCurrentUser()
                        ?: return@withContext Result.failure(Exception("User not logged in"))

                // Create immediate winner
                val winner =
                    AuctionWinner(
                        auctionId = auctionId,
                        winnerId = currentUser.objectId,
                        winnerName = currentUser.username ?: "Unknown",
                        winningBid = buyNowPrice,
                        paymentDeadline = Date(System.currentTimeMillis() + 10 * 60 * 1000),
                        paymentStatus = AuctionPaymentStatus.PENDING,
                        backupBidders = emptyList(),
                    )

                // End auction immediately
                updateAuctionStatus(auctionId, AuctionStatus.ENDED)

                // Refund all bidders
                refundAllBidders(auctionId)

                // Start payment process
                startPaymentTimer(auctionId, winner)
                notifyWinner(winner)

                Result.success(true)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(e)
            }
        }

    /**
     * Create an enhanced auction with seller controls
     */
    suspend fun createEnhancedAuction(settings: AuctionSettings): Result<String> {
        return try {
            val auction =
                ParseObject(AUCTION_CLASS).apply {
                    // Basic auction info
                    put("fowlId", settings.fowlId)
                    put("startingPrice", settings.startingPrice)
                    put("minimumBidPrice", settings.minimumBidPrice)
                    put("reservePrice", settings.reservePrice ?: 0.0)
                    put("seller", ParseUser.getCurrentUser())

                    // Enhanced seller controls
                    put("customDurationHours", settings.customDurationHours)
                    put("requiresBidderDeposit", settings.requiresBidderDeposit)
                    put("bidderDepositPercentage", settings.bidderDepositPercentage)
                    put("allowsProxyBidding", settings.allowsProxyBidding)
                    put("sellerBidMonitoring", settings.sellerBidMonitoring.name)
                    put("autoExtendOnLastMinuteBid", settings.autoExtendOnLastMinuteBid)
                    put("extensionMinutes", settings.extensionMinutes)
                    put("buyNowPrice", settings.buyNowPrice ?: 0.0)

                    // Timing
                    put("startTime", settings.startTime)
                    val endTime =
                        Calendar.getInstance().apply {
                            time = settings.startTime
                            add(Calendar.HOUR_OF_DAY, settings.customDurationHours)
                        }.time
                    put("endTime", endTime)

                    // Status and counts
                    put("status", AuctionStatus.PENDING.name)
                    put("bidCount", 0)
                    put("watchers", 0)
                    put("currentBid", settings.startingPrice)

                    // Bidder restrictions
                    put("allowedBidderTypes", settings.allowedBidderTypes.map { it.name })

                    // Auto-calculated fields
                    put("minimumIncrement", calculateMinimumIncrement(settings.minimumBidPrice))
                    put("isReserveSet", settings.reservePrice != null && settings.reservePrice > 0)
                }

            auction.save()
            Result.success(auction.objectId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Place a bid with deposit validation and categorization
     */
    suspend fun placeBid(
        auctionId: String,
        bidAmount: Double,
        isProxyBid: Boolean = false,
        proxyMaxAmount: Double? = null,
        bidMessage: String? = null,
    ): Result<BidResult> {
        return try {
            val auction =
                getAuctionById(auctionId) ?: return Result.failure(Exception("Auction not found"))
            val currentUser =
                ParseUser.getCurrentUser() ?: return Result.failure(Exception("User not logged in"))

            // Validate bid amount
            val validation = validateBid(auction, bidAmount)
            if (!validation.isValid) {
                return Result.failure(Exception(validation.reason))
            }

            // Handle deposit requirement
            val depositResult = handleBidDeposit(auction, currentUser, bidAmount)
            if (!depositResult.isSuccessful) {
                return Result.failure(Exception(depositResult.message))
            }

            // Create bid record
            val bid =
                ParseObject(BID_CLASS).apply {
                    put("auctionId", auctionId)
                    put("bidder", currentUser)
                    put("bidAmount", bidAmount)
                    put("bidTime", Date())
                    put("isProxyBid", isProxyBid)
                    put("proxyMaxAmount", proxyMaxAmount ?: 0.0)
                    put("depositAmount", depositResult.depositAmount)
                    put("depositStatus", depositResult.depositStatus.name)
                    put("bidStatus", BidStatus.ACTIVE.name)
                    put("bidMessage", bidMessage ?: "")
                    put("bidderRating", getUserRating(currentUser))
                    put("previousBidCount", getUserBidCountForAuction(currentUser.objectId, auctionId))
                }

            bid.save()

            // Update auction with new bid
            updateAuctionWithNewBid(auction, bidAmount, bid.objectId)

            // Categorize bid for seller
            val category = categorizeBid(auction, bidAmount)

            // Send notifications
            sendBidNotifications(auction, bid, category)

            Result.success(
                BidResult(
                    bidId = bid.objectId,
                    category = category,
                    isWinning = true,
                    depositRequired = depositResult.depositAmount > 0,
                    depositAmount = depositResult.depositAmount,
                ),
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get real-time auction updates
     */
    fun getAuctionUpdates(auctionId: String): Flow<AuctionUpdate> =
        flow {
            try {
                val auction = getAuctionById(auctionId)
                if (auction != null) {
                    val update =
                        AuctionUpdate(
                            auctionId = auctionId,
                            currentBid = auction.getDouble("currentBid"),
                            bidCount = auction.getInt("bidCount"),
                            timeRemaining = calculateTimeRemaining(auction),
                            status = AuctionStatus.valueOf(auction.getString("status") ?: "ACTIVE"),
                            watchers = auction.getInt("watchers"),
                            lastBidTime = getLastBidTime(auctionId),
                        )
                    emit(update)
                }
            } catch (e: Exception) {
                // Handle error silently or emit error state
            }
        }

    /**
     * Get categorized bid list for seller monitoring
     */
    suspend fun getBidsForSeller(
        auctionId: String,
        monitoringType: BidMonitoringCategory,
    ): Result<List<CategorizedBid>> {
        return try {
            val query = ParseQuery.getQuery<ParseObject>(BID_CLASS)
            query.whereEqualTo("auctionId", auctionId)
            query.include("bidder")
            query.orderByDescending("bidAmount")

            val bids = query.find()
            val auction =
                getAuctionById(auctionId) ?: return Result.failure(Exception("Auction not found"))
            val minimumBidPrice = auction.getDouble("minimumBidPrice")

            val categorizedBids =
                bids.map { bid ->
                    val bidAmount = bid.getDouble("bidAmount")
                    val category =
                        if (bidAmount >= minimumBidPrice) {
                            BidCategory.ABOVE_MINIMUM
                        } else {
                            BidCategory.BELOW_MINIMUM
                        }

                    CategorizedBid(
                        bidId = bid.objectId,
                        bidderName = getBidderName(bid, monitoringType),
                        bidAmount = bidAmount,
                        bidTime = bid.getDate("bidTime") ?: Date(),
                        category = category,
                        isWinning = bid.getBoolean("isWinning"),
                        depositPaid = bid.getString("depositStatus") == DepositStatus.PAID.name,
                    )
                }

            // Filter based on monitoring preferences
            val filteredBids =
                when (monitoringType) {
                    BidMonitoringCategory.WINNING_BIDS_ONLY ->
                        categorizedBids.filter { it.isWinning }

                    BidMonitoringCategory.PRIVATE_BIDDING ->
                        categorizedBids.take(1) // Only show highest bid
                    else -> categorizedBids
                }

            Result.success(filteredBids)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Private helper methods

    private fun calculateMinimumIncrement(minimumBidPrice: Double): Double {
        return when {
            minimumBidPrice < 1000 -> 50.0
            minimumBidPrice < 5000 -> 100.0
            minimumBidPrice < 10000 -> 250.0
            else -> 500.0
        }
    }

    private fun getAuctionById(auctionId: String): ParseObject? {
        return try {
            val query = ParseQuery.getQuery<ParseObject>(AUCTION_CLASS)
            query.get(auctionId)
        } catch (e: Exception) {
            null
        }
    }

    private fun validateBid(
        auction: ParseObject,
        bidAmount: Double,
    ): BidValidation {
        val currentBid = auction.getDouble("currentBid")
        val minimumBidPrice = auction.getDouble("minimumBidPrice")
        val minimumIncrement = auction.getDouble("minimumIncrement")
        val status = auction.getString("status")

        return when {
            status != AuctionStatus.ACTIVE.name ->
                BidValidation(false, "Auction is not active")

            bidAmount < minimumBidPrice ->
                BidValidation(false, "Bid below minimum price of ₹$minimumBidPrice")

            bidAmount <= currentBid ->
                BidValidation(false, "Bid must be higher than current bid of ₹$currentBid")

            bidAmount < currentBid + minimumIncrement ->
                BidValidation(false, "Minimum increment is ₹$minimumIncrement")

            else -> BidValidation(true, "Valid bid")
        }
    }

    private suspend fun handleBidDeposit(
        auction: ParseObject,
        user: ParseUser,
        bidAmount: Double,
    ): DepositResult {
        val requiresDeposit = auction.getBoolean("requiresBidderDeposit")

        if (!requiresDeposit) {
            return DepositResult(true, 0.0, DepositStatus.NOT_REQUIRED, "No deposit required")
        }

        val depositPercentage = auction.getDouble("bidderDepositPercentage")
        val depositAmount = bidAmount * (depositPercentage / 100)

        // In a real implementation, this would integrate with payment gateway
        // For now, we'll simulate deposit payment
        val depositPaid = simulateDepositPayment(user, depositAmount)

        return if (depositPaid) {
            DepositResult(true, depositAmount, DepositStatus.PAID, "Deposit paid successfully")
        } else {
            DepositResult(false, depositAmount, DepositStatus.PENDING, "Deposit payment failed")
        }
    }

    private fun simulateDepositPayment(
        user: ParseUser,
        amount: Double,
    ): Boolean {
        // Mock payment - in production, integrate with Razorpay/UPI
        return true // Assume payment succeeds for development
    }

    private fun categorizeBid(
        auction: ParseObject,
        bidAmount: Double,
    ): BidCategory {
        val minimumBidPrice = auction.getDouble("minimumBidPrice")
        return if (bidAmount >= minimumBidPrice) {
            BidCategory.ABOVE_MINIMUM
        } else {
            BidCategory.BELOW_MINIMUM
        }
    }

    private fun updateAuctionWithNewBid(
        auction: ParseObject,
        bidAmount: Double,
        bidId: String,
    ) {
        auction.put("currentBid", bidAmount)
        auction.increment("bidCount")
        auction.put("lastBidId", bidId)
        auction.put("lastBidTime", Date())
        auction.saveInBackground()
    }

    private fun sendBidNotifications(
        auction: ParseObject,
        bid: ParseObject,
        category: BidCategory,
    ) {
        // Implementation for push notifications to seller and other bidders
        // This would integrate with FCM in a real implementation
    }

    private fun calculateTimeRemaining(auction: ParseObject): Long {
        val endTime = auction.getDate("endTime") ?: return 0L
        return max(0L, endTime.time - System.currentTimeMillis())
    }

    private fun getLastBidTime(auctionId: String): Date? {
        return try {
            val query = ParseQuery.getQuery<ParseObject>(BID_CLASS)
            query.whereEqualTo("auctionId", auctionId)
            query.orderByDescending("bidTime")
            query.limit = 1
            val lastBid = query.first
            lastBid.getDate("bidTime")
        } catch (e: Exception) {
            null
        }
    }

    private fun getUserRating(user: ParseUser): Double {
        return user.getDouble("rating").takeIf { it > 0 } ?: 4.0
    }

    private fun getBidderName(
        bid: ParseObject,
        monitoringType: BidMonitoringCategory,
    ): String {
        return when (monitoringType) {
            BidMonitoringCategory.PRIVATE_BIDDING -> "Anonymous Bidder"
            else -> {
                val bidder = bid.getParseUser("bidder")
                bidder?.getString("username") ?: "Unknown Bidder"
            }
        }
    }

    private fun getUserBidCountForAuction(
        userId: String,
        auctionId: String,
    ): Int {
        return try {
            val query = ParseQuery.getQuery<ParseObject>(BID_CLASS)
            query.whereEqualTo("auctionId", auctionId)
            query.whereEqualTo("bidder", ParseUser.createWithoutData(ParseUser::class.java, userId))
            query.count()
        } catch (e: Exception) {
            0
        }
    }

    private fun validateAuctionSettings(settings: AuctionSettings) {
        require(settings.customDurationHours in 1..168) { "Duration must be 1-168 hours" }
        require(settings.minimumBidPrice > 0) { "Minimum bid must be positive" }
        require(settings.bidderDepositPercentage in 5.0..25.0) { "Deposit percentage must be 5-25%" }
        require(settings.startingPrice >= settings.minimumBidPrice) { "Starting price must be >= minimum bid" }
    }

    private fun calculateEndTime(
        startTime: Date,
        durationHours: Int,
    ): Date {
        return Date(startTime.time + durationHours * 60 * 60 * 1000L)
    }

    private suspend fun fetchAuctionById(auctionId: String): AuctionListing? =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>(AUCTION_CLASS)
                val obj = query.get(auctionId)

                AuctionListing(
                    auctionId = obj.objectId,
                    title = obj.getString("title") ?: "",
                    description = obj.getString("description") ?: "",
                    startingPrice = obj.getNumber("startingPrice")?.toDouble() ?: 0.0,
                    currentBid = obj.getNumber("currentBid")?.toDouble() ?: 0.0,
                    minimumIncrement = obj.getNumber("minimumIncrement")?.toDouble() ?: 1.0,
                    startTime = obj.getDate("startTime") ?: Date(),
                    endTime = obj.getDate("endTime") ?: Date(),
                    sellerId = obj.getString("sellerId") ?: "",
                    sellerName = obj.getString("sellerName") ?: "",
                    fowlId = obj.getString("fowlId") ?: "",
                    bidCount = obj.getNumber("bidCount")?.toInt() ?: 0,
                    isReserveSet = obj.getBoolean("isReserveSet"),
                    reservePrice = obj.getNumber("reservePrice")?.toDouble() ?: 0.0,
                    imageUrls = obj.getList<String>("imageUrls") ?: emptyList(),
                    status = AuctionStatus.valueOf(obj.getString("status") ?: "PENDING"),
                    location = obj.getString("location") ?: "",
                    category = obj.getString("category") ?: "",
                    customDurationHours = obj.getNumber("customDurationHours")?.toInt() ?: 24,
                    minimumBidPrice = obj.getNumber("minimumBidPrice")?.toDouble() ?: 0.0,
                    requiresBidderDeposit = obj.getBoolean("requiresBidderDeposit"),
                    bidderDepositPercentage =
                        obj.getNumber("bidderDepositPercentage")?.toDouble()
                            ?: 0.0,
                    allowsProxyBidding = obj.getBoolean("allowsProxyBidding"),
                    sellerBidMonitoring =
                        BidMonitoringCategory.valueOf(
                            obj.getString("sellerBidMonitoring") ?: "ALL_BIDS",
                        ),
                    autoExtendOnLastMinuteBid = obj.getBoolean("autoExtendOnLastMinuteBid"),
                    extensionMinutes = obj.getNumber("extensionMinutes")?.toInt() ?: 5,
                    buyNowPrice = obj.getNumber("buyNowPrice")?.toDouble(),
                    watchers = obj.getNumber("watchers")?.toInt() ?: 0,
                )
            } catch (e: Exception) {
                null
            }
        }

    private fun validateBid(
        auction: AuctionListing,
        bidAmount: Double,
        bidderId: String,
    ) {
        require(auction.status == AuctionStatus.ACTIVE) { "Auction is not active" }
        require(bidAmount >= auction.minimumBidPrice) { "Bid below minimum" }
        require(bidAmount > auction.currentBid) { "Bid must be higher than current bid" }
        require(auction.sellerId != bidderId) { "Seller cannot bid on own auction" }
        require(Date().before(auction.endTime)) { "Auction has ended" }
    }

    private suspend fun handleBidderDeposit(
        amount: Double,
        bidderId: String,
    ): DepositStatus {
        // In real implementation, integrate with payment gateway
        // For now, simulate deposit processing
        return if (Random.nextBoolean()) {
            DepositStatus.PAID
        } else {
            DepositStatus.PENDING
        }
    }

    private suspend fun getBidderRating(bidderId: String): Double =
        withContext(Dispatchers.IO) {
            // Fetch bidder's platform rating
            return@withContext 4.2 // Mock rating
        }

    private suspend fun getPreviousBidCount(
        auctionId: String,
        bidderId: String,
    ): Int =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val query = ParseQuery.getQuery<ParseObject>(BID_CLASS)
                query.whereEqualTo("auctionId", auctionId)
                query.whereEqualTo("bidderId", bidderId)
                query.count()
            } catch (e: Exception) {
                0
            }
        }

    private suspend fun updateAuctionWithNewBid(
        auctionId: String,
        bidAmount: Double,
        bidId: String,
    ) {
        try {
            val auctionQuery = ParseQuery.getQuery<ParseObject>(AUCTION_CLASS)
            val auction = auctionQuery.get(auctionId)

            auction.put("currentBid", bidAmount)
            auction.increment("bidCount")
            auction.put("lastBidId", bidId)
            auction.put("lastBidTime", Date())
            auction.save()

            // Update previous winning bids
            updatePreviousWinningBids(auctionId, bidId)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private suspend fun updatePreviousWinningBids(
        auctionId: String,
        newWinningBidId: String,
    ) {
        try {
            val query = ParseQuery.getQuery<ParseObject>(BID_CLASS)
            query.whereEqualTo("auctionId", auctionId)
            query.whereEqualTo("isWinning", true)
            query.whereNotEqualTo("objectId", newWinningBidId)

            val previousWinningBids = query.find()
            previousWinningBids.forEach { bid ->
                bid.put("isWinning", false)
                bid.put("bidStatus", BidStatus.OUTBID.name)
                bid.save()
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private suspend fun handleAutoExtension(
        auction: AuctionListing,
        bid: ParseObject,
    ) {
        if (!auction.autoExtendOnLastMinuteBid) return

        val timeToEnd = auction.endTime.time - System.currentTimeMillis()
        val oneMinute = 60 * 1000L

        if (timeToEnd <= oneMinute) {
            // Extend auction
            val newEndTime = Date(auction.endTime.time + auction.extensionMinutes * 60 * 1000L)

            try {
                val auctionQuery = ParseQuery.getQuery<ParseObject>(AUCTION_CLASS)
                val auctionObj = auctionQuery.get(auction.auctionId)
                auctionObj.put("endTime", newEndTime)
                auctionObj.put("status", AuctionStatus.EXTENDED.name)
                auctionObj.save()

                notifyWatchers(
                    auction.auctionId,
                    "Auction extended by ${auction.extensionMinutes} minutes",
                )
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    private suspend fun notifySellerOfBid(
        auction: AuctionListing,
        bid: ParseObject,
    ) {
        when (auction.sellerBidMonitoring) {
            BidMonitoringCategory.ALL_BIDS -> {
                // Notify seller of every bid
                sendNotification(
                    auction.sellerId,
                    "New bid received: ₹${bid.getNumber("bidAmount")}",
                )
            }

            BidMonitoringCategory.WINNING_BIDS_ONLY -> {
                if (bid.getBoolean("isWinning")) {
                    sendNotification(
                        auction.sellerId,
                        "New winning bid: ₹${bid.getNumber("bidAmount")}",
                    )
                }
            }

            BidMonitoringCategory.PRIVATE_BIDDING -> {
                // Only notify of bid count, not amounts
                sendNotification(
                    auction.sellerId,
                    "New bid received. Total bids: ${auction.bidCount + 1}",
                )
            }

            BidMonitoringCategory.SELLER_NOTIFICATIONS_ONLY -> {
                // Minimal notifications
                if (auction.bidCount % 5 == 0) { // Every 5th bid
                    sendNotification(auction.sellerId, "Auction activity update")
                }
            }
        }
    }

    private suspend fun getWinningBid(auctionId: String): EnhancedAuctionBid? {
        return try {
            val query = ParseQuery.getQuery<ParseObject>(BID_CLASS)
            query.whereEqualTo("auctionId", auctionId)
            query.whereEqualTo("isWinning", true)
            query.orderByDescending("bidAmount")

            val bidObj = query.find().firstOrNull() ?: return null

            EnhancedAuctionBid(
                bidId = bidObj.objectId,
                auctionId = auctionId,
                bidderId = bidObj.getString("bidderId") ?: "",
                bidderName = bidObj.getString("bidderName") ?: "",
                bidAmount = bidObj.getNumber("bidAmount")?.toDouble() ?: 0.0,
                bidTime = bidObj.getDate("bidTime") ?: Date(),
                isWinning = true,
                isProxyBid = bidObj.getBoolean("isProxyBid"),
                proxyMaxAmount = bidObj.getNumber("proxyMaxAmount")?.toDouble(),
                depositAmount = bidObj.getNumber("depositAmount")?.toDouble(),
                depositStatus =
                    DepositStatus.valueOf(
                        bidObj.getString("depositStatus") ?: "NOT_REQUIRED",
                    ),
                bidStatus = BidStatus.WINNING,
                bidMessage = bidObj.getString("bidMessage"),
                bidderRating = bidObj.getNumber("bidderRating")?.toDouble() ?: 0.0,
                previousBidCount = bidObj.getNumber("previousBidCount")?.toInt() ?: 0,
            )
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun prepareBackupBidders(
        auctionId: String,
        winningBidderId: String,
    ): List<BackupBidder> {
        return try {
            val query = ParseQuery.getQuery<ParseObject>(BID_CLASS)
            query.whereEqualTo("auctionId", auctionId)
            query.whereNotEqualTo("bidderId", winningBidderId)
            query.orderByDescending("bidAmount")
            query.limit = 5 // Top 5 backup bidders

            val bids = query.find()
            bids.map { bid ->
                BackupBidder(
                    bidderId = bid.getString("bidderId") ?: "",
                    bidderName = bid.getString("bidderName") ?: "",
                    bidAmount = bid.getNumber("bidAmount")?.toDouble() ?: 0.0,
                    offerSentTime = null,
                    offerResponse = null,
                    responseDeadline = null,
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun startPaymentTimer(
        auctionId: String,
        winner: AuctionWinner,
    ) {
        // In real implementation, use WorkManager or similar for background tasks
        // For now, just log the payment deadline
        FirebaseCrashlytics.getInstance().log(
            "Payment timer started for auction $auctionId, deadline: ${winner.paymentDeadline}",
        )
    }

    private suspend fun startBackupCascade(
        auctionId: String,
        failedWinner: AuctionWinner,
    ) {
        for (backupBidder in failedWinner.backupBidders) {
            try {
                // Offer to backup bidder
                sendNotification(
                    backupBidder.bidderId,
                    "Opportunity available! Original winner payment failed. Accept your bid of ₹${backupBidder.bidAmount}?",
                )

                // Set response deadline (10 minutes)
                val responseDeadline = Date(System.currentTimeMillis() + 10 * 60 * 1000)

                // Create offer record
                val offerObject =
                    ParseObject("BackupBidderOffer").apply {
                        put("auctionId", auctionId)
                        put("bidderId", backupBidder.bidderId)
                        put("bidAmount", backupBidder.bidAmount)
                        put("offerSentTime", Date())
                        put("responseDeadline", responseDeadline)
                        put("offerResponse", OfferResponse.PENDING.name)
                    }
                offerObject.save()

                // Wait for response before offering to next bidder
                break // Offer to one at a time
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                continue // Try next backup bidder
            }
        }
    }

    // Utility methods for payment, notifications, etc.
    private suspend fun updatePaymentStatus(
        auctionId: String,
        status: AuctionPaymentStatus,
    ) {
        try {
            val query = ParseQuery.getQuery<ParseObject>("AuctionWinner")
            query.whereEqualTo("auctionId", auctionId)
            val winner = query.find().firstOrNull()
            winner?.put("paymentStatus", status.name)
            winner?.save()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private suspend fun updateAuctionStatus(
        auctionId: String,
        status: AuctionStatus,
    ) {
        try {
            val query = ParseQuery.getQuery<ParseObject>(AUCTION_CLASS)
            val auction = query.get(auctionId)
            auction.put("status", status.name)
            auction.save()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private suspend fun getAuctionWinner(auctionId: String): AuctionWinner? {
        return try {
            val query = ParseQuery.getQuery<ParseObject>("AuctionWinner")
            query.whereEqualTo("auctionId", auctionId)
            val obj = query.find().firstOrNull() ?: return null

            AuctionWinner(
                auctionId = auctionId,
                winnerId = obj.getString("winnerId") ?: "",
                winnerName = obj.getString("winnerName") ?: "",
                winningBid = obj.getNumber("winningBid")?.toDouble() ?: 0.0,
                paymentDeadline = obj.getDate("paymentDeadline") ?: Date(),
                paymentStatus =
                    AuctionPaymentStatus.valueOf(
                        obj.getString("paymentStatus") ?: "PENDING",
                    ),
                backupBidders = emptyList(), // Fetch separately if needed
            )
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun transferOwnership(
        auctionId: String,
        winner: AuctionWinner,
    ) {
        // Implementation for transferring fowl ownership
        FirebaseCrashlytics.getInstance()
            .log("Ownership transferred for auction: $auctionId to ${winner.winnerId}")
    }

    private suspend fun refundLosingBidders(auctionId: String) {
        // Refund deposits of all losing bidders
        FirebaseCrashlytics.getInstance().log("Refunding losing bidders for auction: $auctionId")
    }

    private suspend fun refundAllBidders(auctionId: String) {
        // Refund all bidders (for Buy Now scenario)
        FirebaseCrashlytics.getInstance().log("Refunding all bidders for auction: $auctionId")
    }

    private suspend fun forfeitWinnerDeposit(winner: AuctionWinner) {
        // Forfeit winner's deposit for payment failure
        FirebaseCrashlytics.getInstance()
            .log("Forfeiting deposit for failed payment: ${winner.winnerId}")
    }

    private suspend fun notifyWinner(winner: AuctionWinner) {
        sendNotification(
            winner.winnerId,
            "Congratulations! You won the auction with ₹${winner.winningBid}. Please complete payment by ${winner.paymentDeadline}",
        )
    }

    private suspend fun notifyWatchers(
        auctionId: String,
        message: String,
    ) {
        // Notify all users watching the auction
        FirebaseCrashlytics.getInstance().log("Notifying watchers for auction $auctionId: $message")
    }

    private suspend fun sendNotification(
        userId: String,
        message: String,
    ) {
        // Implementation for sending notifications (FCM, in-app, etc.)
        FirebaseCrashlytics.getInstance().log("Notification sent to $userId: $message")
    }
}

// Supporting data classes

data class BidResult(
    val bidId: String,
    val category: BidCategory,
    val isWinning: Boolean,
    val depositRequired: Boolean,
    val depositAmount: Double,
)

data class BidValidation(
    val isValid: Boolean,
    val reason: String,
)

data class DepositResult(
    val isSuccessful: Boolean,
    val depositAmount: Double,
    val depositStatus: DepositStatus,
    val message: String,
)

data class AuctionUpdate(
    val auctionId: String,
    val currentBid: Double,
    val bidCount: Int,
    val timeRemaining: Long,
    val status: AuctionStatus,
    val watchers: Int,
    val lastBidTime: Date?,
)

data class CategorizedBid(
    val bidId: String,
    val bidderName: String,
    val bidAmount: Double,
    val bidTime: Date,
    val category: BidCategory,
    val isWinning: Boolean,
    val depositPaid: Boolean,
)

enum class BidCategory {
    ABOVE_MINIMUM,
    BELOW_MINIMUM,
}
