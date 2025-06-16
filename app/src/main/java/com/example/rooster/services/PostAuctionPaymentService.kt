package com.example.rooster.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.rooster.AuctionPaymentStatus
import com.example.rooster.AuctionWinner
import com.example.rooster.BackupBidder
import com.example.rooster.OfferResponse
import com.example.rooster.PaymentMethod
import com.example.rooster.PaymentTransaction
import com.example.rooster.TransactionStatus
import com.example.rooster.util.CrashPrevention
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class PostAuctionPaymentService
    @Inject
    constructor() : Service(), CoroutineScope {
        private val job = SupervisorJob()
        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO + job

        private val crashlytics = FirebaseCrashlytics.getInstance()

        companion object {
            private const val PAYMENT_WINDOW_MINUTES = 10L
            private const val BACKUP_OFFER_WINDOW_MINUTES = 5L
            private const val MAX_CASCADE_DEPTH = 5
            private const val PAYMENT_CLASS = "PaymentRecord"
            private const val AUCTION_WINNER_CLASS = "AuctionWinner"
            private const val PAYMENT_TIMEOUT_MS = 10 * 60 * 1000L // 10 minutes
            private const val STATUS_CHECK_INTERVAL_MS = 30_000L // 30 seconds
        }

        override fun onBind(intent: Intent?): IBinder? = null

        override fun onStartCommand(
            intent: Intent?,
            flags: Int,
            startId: Int,
        ): Int {
            val auctionId = intent?.getStringExtra("auctionId")
            if (auctionId.isNullOrEmpty()) {
                crashlytics.log("PostAuctionPaymentService started without auctionId")
                stopSelf()
                return START_NOT_STICKY
            }

            launch {
                CrashPrevention.safeAsync("ProcessPayment") {
                    processAuctionPayment(auctionId)
                }
                stopSelf()
            }

            return START_STICKY
        }

        private suspend fun processAuctionPayment(auctionId: String) {
            crashlytics.log("Processing payment for auction: $auctionId")

            val auction =
                fetchAuction(auctionId) ?: run {
                    crashlytics.log("Auction not found: $auctionId")
                    return
                }

            val winnerId =
                auction.getString("winnerId") ?: run {
                    crashlytics.log("No winner found for auction: $auctionId")
                    return
                }

            val bidAmount = auction.getDouble("currentBid")
            if (bidAmount <= 0) {
                crashlytics.log("Invalid bid amount for auction: $auctionId")
                return
            }

            // Initialize payment process
            val result =
                initializePaymentProcess(
                    auctionId = auctionId,
                    winnerId = winnerId,
                    winningBid = bidAmount,
                )

            if (result.isSuccess) {
                val winner = result.getOrNull() ?: return
                // Start payment flow with timeout
                withTimeoutOrNull(PAYMENT_TIMEOUT_MS) {
                    monitorPaymentStatus(auctionId, winner.winnerId)
                } ?: handlePaymentTimeout(auctionId, winner.winnerId)
            } else {
                crashlytics.log("Failed to initialize payment process for auction: $auctionId")
                handlePaymentTimeout(auctionId, winnerId)
            }
        }

        private fun fetchAuction(auctionId: String): ParseObject? {
            return CrashPrevention.safeDatabase("FetchAuction") {
                val query = ParseQuery.getQuery<ParseObject>("Auction")
                query.whereEqualTo("objectId", auctionId)
                query.include("winner")
                query.include("seller")
                query.find().firstOrNull()
            }
        }

        private suspend fun monitorPaymentStatus(
            auctionId: String,
            winnerId: String,
        ) {
            while (true) {
                val winner = getAuctionWinner(auctionId, winnerId)
                val status = winner?.paymentStatus

                when (status) {
                    null,
                    AuctionPaymentStatus.PENDING,
                    AuctionPaymentStatus.PROCESSING,
                    -> {
                        // Not completed yet, poll again
                        delay(STATUS_CHECK_INTERVAL_MS)
                    }
                    AuctionPaymentStatus.COMPLETED -> {
                        handlePaymentSuccess(auctionId)
                        break
                    }
                    AuctionPaymentStatus.FAILED -> {
                        handlePaymentFailure(auctionId, winnerId)
                        break
                    }
                    AuctionPaymentStatus.CANCELLED -> {
                        handlePaymentCancellation(auctionId, winnerId)
                        break
                    }
                    AuctionPaymentStatus.EXPIRED -> {
                        handlePaymentTimeout(auctionId, winnerId)
                        break
                    }
                    else -> {
                        // Fallback in case of new status values
                        delay(STATUS_CHECK_INTERVAL_MS)
                    }
                }
            }
        }

        private suspend fun initializePaymentProcess(
            auctionId: String,
            winnerId: String,
            winningBid: Double,
        ): Result<AuctionWinner> {
            return try {
                val winner =
                    AuctionWinner(
                        auctionId = auctionId,
                        winnerId = winnerId,
                        winnerName = getUserName(winnerId),
                        winningBid = winningBid,
                        paymentDeadline = Date(System.currentTimeMillis() + PAYMENT_WINDOW_MINUTES * 60 * 1000),
                        paymentStatus = AuctionPaymentStatus.PENDING,
                        backupBidders = getBackupBidders(auctionId, winnerId),
                    )

                // Save winner record
                saveAuctionWinner(winner)

                Result.success(winner)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        private suspend fun processPayment(
            auctionId: String,
            winnerId: String,
            paymentReference: String,
            paymentMethod: PaymentMethod = PaymentMethod.UPI,
        ): Result<PaymentResult> {
            return try {
                // Validate payment (mock implementation)
                val paymentValid = validatePayment(paymentReference, winnerId)

                if (paymentValid) {
                    // Update winner status
                    updateWinnerPaymentStatus(auctionId, winnerId, AuctionPaymentStatus.COMPLETED)

                    // Create payment record
                    val paymentRecord =
                        createPaymentRecord(
                            auctionId = auctionId,
                            winnerId = winnerId,
                            paymentReference = paymentReference,
                            paymentMethod = paymentMethod,
                        )

                    // Initiate transfer process
                    initiateOwnershipTransfer(auctionId, winnerId)

                    Result.success(PaymentResult.Success(paymentRecord.transactionId))
                } else {
                    Result.success(PaymentResult.Failed("Payment validation failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        private suspend fun handlePaymentTimeout(
            auctionId: String,
            winnerId: String,
        ) {
            val result = handlePaymentTimeoutInternal(auctionId, winnerId)
            if (result.isSuccess) {
                val cascadeResult = result.getOrNull()
                when (cascadeResult) {
                    is CascadeResult.CascadeStarted -> {
                        // Continue with backup cascade
                        val backupBidders = getBackupBidders(auctionId, winnerId)
                        processBackupCascade(auctionId, backupBidders)
                    }

                    CascadeResult.NoBackupBidders -> {
                        // No backup bidders - auction failed
                        markAuctionAsFailed(auctionId)
                    }
                    else -> {
                        // No action needed for null or unknown result
                    }
                }
            }
        }

        private suspend fun handlePaymentTimeoutInternal(
            auctionId: String,
            winnerId: String,
        ): Result<CascadeResult> {
            return try {
                // Mark winner payment as expired
                updateWinnerPaymentStatus(auctionId, winnerId, AuctionPaymentStatus.EXPIRED)

                // Forfeit winner's deposit
                forfeitDeposit(auctionId, winnerId)

                // Get backup bidders
                val backupBidders = getBackupBidders(auctionId, winnerId)

                if (backupBidders.isNotEmpty()) {
                    // Start cascade process
                    val cascadeResult = startBackupCascade(auctionId, backupBidders)
                    Result.success(cascadeResult)
                } else {
                    // No backup bidders - auction failed
                    markAuctionAsFailed(auctionId)
                    Result.success(CascadeResult.NoBackupBidders)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        private suspend fun processBackupCascade(
            auctionId: String,
            backupBidders: List<BackupBidder>,
        ) {
            // Process backup cascade
            for ((index, bidder) in backupBidders.withIndex()) {
                // Skip if we've reached max cascade depth
                if (index >= MAX_CASCADE_DEPTH) break

                // Send offer to backup bidder
                val offerSent = sendOfferToBackupBidder(bidder, auctionId)

                if (offerSent) {
                    // Set response deadline
                    val responseDeadline =
                        Date(System.currentTimeMillis() + BACKUP_OFFER_WINDOW_MINUTES * 60 * 1000)
                    updateBackupBidderResponseDeadline(auctionId, bidder.bidderId, responseDeadline)

                    // Wait for response or timeout
                    val response = waitForBidderResponse(bidder.bidderId, auctionId)

                    when (response) {
                        OfferResponse.ACCEPTED -> {
                            // Bidder accepted - initialize payment process
                            initializePaymentProcess(auctionId, bidder.bidderId, bidder.bidAmount)
                            return
                        }

                        OfferResponse.DECLINED, OfferResponse.EXPIRED -> {
                            // Continue to next bidder
                            continue
                        }

                        else -> {
                            // Continue to next bidder
                            continue
                        }
                    }
                }
            }

            // If we reach here, no backup bidders accepted
            markAuctionAsFailed(auctionId)
        }

        private suspend fun handlePaymentSuccess(auctionId: String) {
            CrashPrevention.safeAsync("HandlePaymentSuccess") {
                crashlytics.log("Payment successful for auction: $auctionId")
                updateAuctionStatus(auctionId, "completed")

                // Release deposit
                releaseDeposit(auctionId)

                // Send notifications
                sendPaymentSuccessNotification(auctionId)
            }
        }

        private suspend fun handlePaymentFailure(
            auctionId: String,
            winnerId: String,
        ) {
            CrashPrevention.safeAsync("HandlePaymentFailure") {
                crashlytics.log("Payment failed for auction: $auctionId, winner: $winnerId")

                // Forfeit deposit
                forfeitDeposit(auctionId, winnerId)

                // Try backup bidder
                initiateBackupBidder(auctionId)
            }
        }

        private suspend fun handlePaymentCancellation(
            auctionId: String,
            winnerId: String,
        ) {
            CrashPrevention.safeAsync("HandlePaymentCancellation") {
                crashlytics.log("Payment cancelled for auction: $auctionId, winner: $winnerId")

                // Forfeit deposit
                forfeitDeposit(auctionId, winnerId)

                // Try backup bidder
                initiateBackupBidder(auctionId)
            }
        }

        private suspend fun updateAuctionStatus(
            auctionId: String,
            status: String,
        ) {
            CrashPrevention.safeDatabase("UpdateAuctionStatus") {
                val auction = fetchAuction(auctionId)
                auction?.let {
                    it.put("status", status)
                    it.put("completedAt", System.currentTimeMillis())
                    it.save()
                }
            }
        }

        private suspend fun forfeitDeposit(
            auctionId: String,
            winnerId: String,
        ) {
            CrashPrevention.safeDatabase("ForfeitDeposit") {
                // Find and forfeit deposit
                val depositQuery = ParseQuery.getQuery<ParseObject>("Deposit")
                depositQuery.whereEqualTo("auctionId", auctionId)
                depositQuery.whereEqualTo("bidderId", winnerId)

                val deposit = depositQuery.find().firstOrNull()
                deposit?.let {
                    it.put("status", "forfeited")
                    it.put("forfeitedAt", System.currentTimeMillis())
                    it.save()

                    crashlytics.log("Deposit forfeited for auction: $auctionId, bidder: $winnerId")
                }
            }
        }

        private suspend fun releaseDeposit(auctionId: String) {
            CrashPrevention.safeDatabase("ReleaseDeposit") {
                // Release all other deposits for this auction
                val depositsQuery = ParseQuery.getQuery<ParseObject>("Deposit")
                depositsQuery.whereEqualTo("auctionId", auctionId)
                depositsQuery.whereNotEqualTo("status", "forfeited")

                val deposits = depositsQuery.find()
                deposits.forEach { deposit ->
                    deposit.put("status", "released")
                    deposit.put("releasedAt", System.currentTimeMillis())
                    deposit.save()
                }

                crashlytics.log("Deposits released for auction: $auctionId")
            }
        }

        private suspend fun initiateBackupBidder(auctionId: String) {
            CrashPrevention.safeAsync("InitiateBackupBidder") {
                val auction = fetchAuction(auctionId)
                val backupBidders = auction?.getList<String>("backupBidders") ?: emptyList()

                if (backupBidders.isNotEmpty()) {
                    val nextBidder = backupBidders.first()

                    // Update auction with new winner
                    auction?.put("winnerId", nextBidder)
                    auction?.put("status", "payment_pending")

                    // Remove used backup bidder
                    val remainingBackups = backupBidders.drop(1)
                    auction?.put("backupBidders", remainingBackups)
                    auction?.save()

                    crashlytics.log("Backup bidder initiated for auction: $auctionId, bidder: $nextBidder")

                    // Start new payment process
                    val newPaymentRecord =
                        createPaymentRecord(
                            auctionId = auctionId,
                            winnerId = nextBidder,
                            paymentReference = "manual_cascade_${System.currentTimeMillis()}",
                            paymentMethod = PaymentMethod.UPI,
                        )

                    // Restart monitoring for new bidder
                    withTimeoutOrNull(PAYMENT_TIMEOUT_MS) {
                        monitorPaymentStatus(auctionId, nextBidder)
                    } ?: handlePaymentTimeout(auctionId, nextBidder)
                } else {
                    // No backup bidders, auction failed
                    auction?.put("status", "failed_no_payment")
                    auction?.save()

                    crashlytics.log("No backup bidders available for auction: $auctionId")
                    sendAuctionFailedNotification(auctionId)
                }
            }
        }

        private suspend fun markAuctionAsFailed(auctionId: String) {
            // Mark auction as failed in database
            CrashPrevention.safeDatabase("MarkAuctionAsFailed") {
                val auction = fetchAuction(auctionId)
                auction?.put("status", "failed_no_payment")
                auction?.save()
            }
        }

        private suspend fun initiateOwnershipTransfer(
            auctionId: String,
            winnerId: String,
        ) {
            // Start the ownership transfer process
        }

        private suspend fun getUserName(userId: String): String {
            return try {
                val user = ParseUser.createWithoutData(ParseUser::class.java, userId)
                user.getString("username") ?: "Unknown User"
            } catch (e: Exception) {
                "Unknown User"
            }
        }

        private suspend fun getAuctionWinner(
            auctionId: String,
            winnerId: String,
        ): AuctionWinner? {
            return try {
                val query = ParseQuery.getQuery<ParseObject>(AUCTION_WINNER_CLASS)
                query.whereEqualTo("auctionId", auctionId)
                query.whereEqualTo("winnerId", winnerId)

                val result = query.first
                AuctionWinner(
                    auctionId = result.getString("auctionId") ?: "",
                    winnerId = result.getString("winnerId") ?: "",
                    winnerName = result.getString("winnerName") ?: "",
                    winningBid = result.getDouble("winningBid"),
                    paymentDeadline = result.getDate("paymentDeadline") ?: Date(),
                    paymentStatus =
                        AuctionPaymentStatus.valueOf(
                            result.getString("paymentStatus") ?: "PENDING",
                        ),
                    backupBidders = emptyList(), // Would load from separate query
                )
            } catch (e: Exception) {
                null
            }
        }

        private suspend fun getBackupBidders(
            auctionId: String,
            excludeWinnerId: String,
        ): List<BackupBidder> {
            return try {
                // Mock implementation - in real app, query Parse for backup bidders
                listOf(
                    BackupBidder(
                        bidderId = "backup1",
                        bidderName = "Backup Bidder 1",
                        bidAmount = 2400.0,
                        offerSentTime = null,
                        offerResponse = null,
                        responseDeadline = null,
                    ),
                    BackupBidder(
                        bidderId = "backup2",
                        bidderName = "Backup Bidder 2",
                        bidAmount = 2300.0,
                        offerSentTime = null,
                        offerResponse = null,
                        responseDeadline = null,
                    ),
                    BackupBidder(
                        bidderId = "backup3",
                        bidderName = "Backup Bidder 3",
                        bidAmount = 2200.0,
                        offerSentTime = null,
                        offerResponse = null,
                        responseDeadline = null,
                    ),
                )
            } catch (e: Exception) {
                emptyList()
            }
        }

        private suspend fun saveAuctionWinner(winner: AuctionWinner) {
            val winnerObject =
                ParseObject(AUCTION_WINNER_CLASS).apply {
                    put("auctionId", winner.auctionId)
                    put("winnerId", winner.winnerId)
                    put("winnerName", winner.winnerName)
                    put("winningBid", winner.winningBid)
                    put("paymentDeadline", winner.paymentDeadline)
                    put("paymentStatus", winner.paymentStatus.name)
                }
            winnerObject.save()
        }

        private suspend fun updateWinnerPaymentStatus(
            auctionId: String,
            winnerId: String,
            status: AuctionPaymentStatus,
        ) {
            val query = ParseQuery.getQuery<ParseObject>(AUCTION_WINNER_CLASS)
            query.whereEqualTo("auctionId", auctionId)
            query.whereEqualTo("winnerId", winnerId)

            val winner = query.first
            winner.put("paymentStatus", status.name)
            winner.save()
        }

        private suspend fun createPaymentRecord(
            auctionId: String,
            winnerId: String,
            paymentReference: String,
            paymentMethod: PaymentMethod,
        ): PaymentTransaction {
            val transaction =
                PaymentTransaction(
                    transactionId = "TXN_${System.currentTimeMillis()}",
                    razorpayPaymentId = paymentReference,
                    orderId = auctionId,
                    amount = 0.0, // Will be set from auction data
                    currency = "INR",
                    status = TransactionStatus.SUCCESS,
                    method = paymentMethod,
                    description = "Auction payment for $auctionId",
                    listingId = auctionId,
                    sellerId = null,
                    buyerId = winnerId,
                    createdAt = Date(),
                    completedAt = Date(),
                    failureReason = null,
                )

            // Save to Parse
            val paymentObject =
                ParseObject(PAYMENT_CLASS).apply {
                    put("transactionId", transaction.transactionId)
                    put("auctionId", auctionId)
                    put("winnerId", winnerId)
                    put("paymentReference", paymentReference)
                    put("paymentMethod", paymentMethod.name)
                    put("status", TransactionStatus.SUCCESS.name)
                    put("createdAt", Date())
                }
            paymentObject.save()

            return transaction
        }

        private suspend fun validatePayment(
            paymentReference: String,
            winnerId: String,
        ): Boolean {
            // Mock payment validation - in real app, verify with payment gateway
            return paymentReference.isNotEmpty() && winnerId.isNotEmpty()
        }

        private suspend fun startPaymentTimer(
            auctionId: String,
            winnerId: String,
        ) {
            // In real implementation, this would set up a background job
            // For now, we rely on the real-time updates flow
        }

        // Duplicate overload removed to resolve ambiguity

        private suspend fun startBackupCascade(
            auctionId: String,
            backupBidders: List<BackupBidder>,
        ): CascadeResult {
            return CascadeResult.CascadeStarted(backupBidders.size)
        }

        private suspend fun sendOfferToBackupBidder(
            bidder: BackupBidder,
            auctionId: String,
        ): Boolean {
            // Send push notification and email to backup bidder
            return true // Mock implementation
        }

        private suspend fun waitForBidderResponse(
            bidderId: String,
            auctionId: String,
        ): OfferResponse {
            // Wait for bidder response or timeout
            delay(BACKUP_OFFER_WINDOW_MINUTES * 60 * 1000) // 5 minutes
            return OfferResponse.EXPIRED // Mock - timeout
        }

        private suspend fun updateBackupBidderResponseDeadline(
            auctionId: String,
            bidderId: String,
            deadline: Date,
        ) {
            // Update the response deadline for this backup bidder
        }

        private suspend fun sendAuctionFailedNotification(auctionId: String) {
            // Implementation for sending failure notifications
            // This would integrate with your notification system
            crashlytics.log("Auction failed notification sent for auction: $auctionId")
        }

        /**
         * Stub for sending payment success notification
         */
        private fun sendPaymentSuccessNotification(auctionId: String) {
            CrashPrevention.safeExecute("SendPaymentSuccess") {
                crashlytics.log("Payment success notification sent for auction: $auctionId")
            }
        }

        override fun onDestroy() {
            job.cancel()
            super.onDestroy()
        }
    }

// Supporting data classes

data class PaymentStatusUpdate(
    val auctionId: String,
    val winnerId: String,
    val paymentStatus: AuctionPaymentStatus,
    val timeRemaining: Long,
    val winningBid: Double,
    val backupBiddersCount: Int,
    val error: String? = null,
) {
    companion object {
        fun error(
            auctionId: String,
            winnerId: String,
            error: String,
        ) = PaymentStatusUpdate(
            auctionId = auctionId,
            winnerId = winnerId,
            paymentStatus = AuctionPaymentStatus.FAILED,
            timeRemaining = 0,
            winningBid = 0.0,
            backupBiddersCount = 0,
            error = error,
        )
    }
}

sealed class BackupCascadeUpdate {
    data class OfferSent(val bidderId: String, val bidAmount: Double) : BackupCascadeUpdate()

    data class OfferAccepted(val bidderId: String, val bidAmount: Double) : BackupCascadeUpdate()

    data class OfferDeclined(val bidderId: String) : BackupCascadeUpdate()

    data class CascadeCompleted(val successful: Boolean) : BackupCascadeUpdate()

    data class Error(val message: String) : BackupCascadeUpdate()
}

sealed class PaymentResult {
    data class Success(val transactionId: String) : PaymentResult()

    data class Failed(val reason: String) : PaymentResult()
}

sealed class CascadeResult {
    data class CascadeStarted(val backupBiddersCount: Int) : CascadeResult()

    object NoBackupBidders : CascadeResult()
}

sealed class PaymentRetryResult {
    data class RetryAllowed(val newDeadline: Date) : PaymentRetryResult()

    data class RetryNotAllowed(val reason: String) : PaymentRetryResult()
}
