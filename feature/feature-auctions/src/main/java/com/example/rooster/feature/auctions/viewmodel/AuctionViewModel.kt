package com.example.rooster.feature.auctions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Imports for AuctionListing, AuctionWinner, EnhancedAuctionBid, fetchers, TokenService, etc.
import com.example.rooster.core.common.models.auction.AuctionListing // Updated import
import com.example.rooster.core.common.models.auction.AuctionWinner // Updated import
import com.example.rooster.core.common.models.auction.EnhancedAuctionBid // Updated import
import com.example.rooster.core.common.models.ValidationResult // Import from new location
import com.example.rooster.core.common.models.BidStatistics // Import from new location
import com.example.rooster.core.common.models.payment.CreateOrderRequest
import com.example.rooster.core.common.models.payment.RazorpayOrderResponse
import com.example.rooster.core.common.models.payment.VerifyPaymentRequest
import com.example.rooster.core.common.Result
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import com.example.rooster.feature.auctions.domain.repository.AuctionRepository
import com.example.rooster.core.common.domain.repository.TokenRepository
import com.example.rooster.core.common.domain.repository.PaymentRepository
import com.example.rooster.core.common.event.AppEventBus
import com.example.rooster.core.common.event.PaymentEvent
import com.example.rooster.core.common.toUserFriendlyMessage
import android.content.Context

@HiltViewModel
class AuctionViewModel @Inject constructor(
    // TODO: Inject repositories or use cases here instead of direct Parse/TokenService calls
    // For example:
    private val auctionRepository: AuctionRepository, // Injected AuctionRepository
    private val tokenRepository: TokenRepository,
    private val paymentRepository: PaymentRepository,
    private val eventBus: AppEventBus
) : ViewModel() {
    private val _auctions = MutableStateFlow<List<AuctionListing>>(emptyList())
    val auctions: StateFlow<List<AuctionListing>> = _auctions

    private val _bids = MutableStateFlow<List<EnhancedAuctionBid>>(emptyList())
    val bids: StateFlow<List<EnhancedAuctionBid>> = _bids

    private val _winner = MutableStateFlow<AuctionWinner?>(null)
    val winner: StateFlow<AuctionWinner?> = _winner

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _minBidPrice = MutableStateFlow<Double?>(null)
    val minBidPrice: StateFlow<Double?> = _minBidPrice

    private val _auctionDurations = MutableStateFlow<List<Int>>(emptyList())
    val auctionDurations: StateFlow<List<Int>> = _auctionDurations

    private val _depositRequirement = MutableStateFlow<Double?>(null)
    val depositRequirement: StateFlow<Double?> = _depositRequirement

    private val _bidsAboveMin = MutableStateFlow<List<EnhancedAuctionBid>>(emptyList())
    val bidsAboveMin: StateFlow<List<EnhancedAuctionBid>> = _bidsAboveMin

    private val _bidsBelowMin = MutableStateFlow<List<EnhancedAuctionBid>>(emptyList())
    val bidsBelowMin: StateFlow<List<EnhancedAuctionBid>> = _bidsBelowMin

    private val _tokenBalance = MutableStateFlow(0)
    val tokenBalance: StateFlow<Int> = _tokenBalance

    val currentRazorpayOrderId = MutableStateFlow<String?>(null)

    fun loadTokenBalance() =
        viewModelScope.launch {
            tokenRepository.loadTokenBalance { balance ->
                _tokenBalance.value = balance
            }
        }

    fun deductToken(onResult: (Boolean) -> Unit) =
        viewModelScope.launch {
            tokenRepository.deductTokens { success -> // Use deductTokens
                if (success) {
                    // Optionally, re-fetch balance or optimistically update
                    // For now, just ensure the callback is passed
                    loadTokenBalance() // Re-fetch balance after deduction
                }
                onResult(success)
            }
        }

    private suspend fun fetchMinBidPrice(auctionId: String): Double? =
        withContext(Dispatchers.IO) {
            // TODO: Replace with repository call
            try {
                val query = ParseQuery.getQuery<ParseObject>("EnhancedAuction")
                val auction = query.get(auctionId)
                auction.getNumber("minimumBidPrice")?.toDouble()
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }

    private suspend fun fetchAuctionDurations(): List<Int> =
        withContext(Dispatchers.IO) {
            // TODO: This could be from a config or repository
            try {
                listOf(12, 24, 48, 72, 96, 120, 168)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                listOf(12, 24, 48, 72, 96, 120, 168)
            }
        }

    private suspend fun fetchDepositRequirement(auctionId: String): Double? =
        withContext(Dispatchers.IO) {
            // TODO: Replace with repository call
            try {
                val query = ParseQuery.getQuery<ParseObject>("EnhancedAuction")
                val auction = query.get(auctionId)
                val requiresDeposit = auction.getBoolean("requiresBidderDeposit")
                if (requiresDeposit) {
                    auction.getNumber("bidderDepositPercentage")?.toDouble()
                } else {
                    null
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }

    fun loadBiddingSettings(auctionId: String) =
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val minPrice = fetchMinBidPrice(auctionId)
                val durations = fetchAuctionDurations()
                val depositReq = fetchDepositRequirement(auctionId)

                _minBidPrice.value = minPrice
                _auctionDurations.value = durations
                _depositRequirement.value = depositReq
                categorizeBids() // Uses _bids.value, so ensure bids are loaded or this is called after bids load
                FirebaseCrashlytics.getInstance().log("Bidding settings loaded for auction: $auctionId")
            } catch (e: Exception) {
                _error.value = "Failed to load bidding settings: ${e.message}" // TODO: Localize
                FirebaseCrashlytics.getInstance().recordException(e)
            } finally {
                _loading.value = false
            }
        }

    private fun categorizeBids() {
        val allBids = _bids.value
        val minPrice = _minBidPrice.value
        if (minPrice != null) {
            _bidsAboveMin.value = allBids.filter { it.bidAmount >= minPrice }
            _bidsBelowMin.value = allBids.filter { it.bidAmount < minPrice }
        } else {
            _bidsAboveMin.value = allBids
            _bidsBelowMin.value = emptyList()
        }
        FirebaseCrashlytics.getInstance().log(
            "Bids categorized: ${_bidsAboveMin.value.size} above min, ${_bidsBelowMin.value.size} below min",
        )
    }

    fun validateBidAmount(bidAmount: Double): ValidationResult {
        val minPrice = _minBidPrice.value
        val currentBids = _bids.value // Assuming bids are already loaded and up-to-date
        val currentHighest = currentBids.maxByOrNull { it.bidAmount }?.bidAmount ?: 0.0

        return when {
            minPrice != null && bidAmount < minPrice -> ValidationResult(false, "Bid amount ₹$bidAmount is below minimum bid price ₹$minPrice") // TODO: Localize
            bidAmount <= currentHighest -> ValidationResult(false, "Bid amount ₹$bidAmount must be higher than current highest bid ₹$currentHighest") // TODO: Localize
            else -> ValidationResult(true, null)
        }
    }

    fun calculateDepositAmount(bidAmount: Double): Double? {
        val depositPercentage = _depositRequirement.value
        return depositPercentage?.let { bidAmount * (it / 100.0) }
    }

    fun loadAuctions(context: Context? = null) =
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = auctionRepository.getActiveAuctions().collect { result ->
                    when (result) {
                        is com.example.rooster.core.common.Result.Success -> {
                            _auctions.value = result.data
                            _error.value = null
                            _loading.value = false
                        }
                        is com.example.rooster.core.common.Result.Error -> {
                            val msg = context?.let { toUserFriendlyMessage(result.exception, it) } ?: result.exception.localizedMessage ?: "Unknown error"
                            _error.value = msg
                            _loading.value = false
                        }
                        is com.example.rooster.core.common.Result.Loading -> {
                            _loading.value = true
                            _error.value = null
                        }
                    }
                }
            } catch (e: Exception) {
                val msg = context?.let { toUserFriendlyMessage(e, it) } ?: e.localizedMessage ?: "Unknown error"
                _error.value = msg
            } finally {
                _loading.value = false
            }
        }

    fun loadBids(auctionId: String) =
        viewModelScope.launch {
            auctionRepository.getEnhancedAuctionBids(auctionId).collect { result ->
                when (result) {
                    is com.example.rooster.core.common.Result.Success -> {
                        _bids.value = result.data
                        categorizeBids()
                        _error.value = null
                        _loading.value = false
                    }
                    is com.example.rooster.core.common.Result.Error -> {
                        _error.value = result.exception.message ?: "Failed to load bids" // TODO: Localize
                        _loading.value = false
                    }
                    is com.example.rooster.core.common.Result.Loading -> {
                        _loading.value = true
                        _error.value = null
                    }
                }
            }
        }

    fun loadWinner(auctionId: String) =
        viewModelScope.launch {
            auctionRepository.getAuctionWinner(auctionId).collect { result ->
                when (result) {
                    is com.example.rooster.core.common.Result.Success -> {
                        _winner.value = result.data
                        _error.value = null
                        _loading.value = false
                    }
                    is com.example.rooster.core.common.Result.Error -> {
                        _error.value = result.exception.message ?: "Failed to load winner" // TODO: Localize
                        _loading.value = false
                    }
                    is com.example.rooster.core.common.Result.Loading -> {
                        _loading.value = true
                        _error.value = null
                    }
                }
            }
        }

    fun clearError() {
        _error.value = null
    }

    fun getBidStatistics(): BidStatistics {
        val allBids = _bids.value
        val aboveMin = _bidsAboveMin.value
        val belowMin = _bidsBelowMin.value
        return BidStatistics(
            totalBids = allBids.size,
            bidsAboveMinimum = aboveMin.size,
            bidsBelowMinimum = belowMin.size,
            highestBid = allBids.maxByOrNull { it.bidAmount }?.bidAmount ?: 0.0,
            averageBid = if (allBids.isNotEmpty()) allBids.map { it.bidAmount }.average() else 0.0,
            uniqueBidders = allBids.map { it.bidderId }.distinct().size,
        )
    }

    // --- Payment Orchestration Methods ---

    fun createAuctionDepositOrder(
        auctionId: String,
        depositAmount: Double,
        isTeluguMode: Boolean, // Used for description, ideally from user profile/settings
        onResult: (RazorpayOrderResponse?) -> Unit
    ) = viewModelScope.launch {
        _loading.value = true
        _error.value = null
        val orderRequest = CreateOrderRequest(
            amount = (depositAmount * 100).toInt(), // Amount in paise
            currency = "INR",
            receiptId = "receipt_auction_${auctionId}_${System.currentTimeMillis()}",
            notes = mapOf(
                "auctionId" to auctionId,
                "userId" to (ParseUser.getCurrentUser()?.objectId ?: "unknown"), // TODO: Get user ID properly
                "type" to "auction_deposit"
            )
        )
        when (val result = paymentRepository.createRazorpayOrder(orderRequest)) {
            is com.example.rooster.core.common.Result.Success -> {
                currentRazorpayOrderId.value = result.data.id // Store Razorpay order_id
                onResult(result.data)
            }
            is com.example.rooster.core.common.Result.Error -> {
                _error.value = "Error creating payment order: ${result.exception.message}" // TODO: Localize
                FirebaseCrashlytics.getInstance().recordException(result.exception)
                onResult(null)
            }
            is com.example.rooster.core.common.Result.Loading -> {
                // Handled by _loading.value at start of method
            }
        }
        _loading.value = false
    }

    fun verifyAuctionDepositPayment(
        razorpayOrderId: String,
        razorpayPaymentId: String,
        razorpaySignature: String,
        auctionId: String,
        bidAmount: Double = 0.0,
        depositAmount: Double = 0.0,
        onResult: ((isSuccess: Boolean, message: String?) -> Unit)? = null
    ) = viewModelScope.launch {
        _loading.value = true
        // Use pending bid details if parameters are not provided
        val pendingBid = _pendingBidDetails.value
        val actualBidAmount = if (bidAmount > 0.0) bidAmount else (pendingBid?.bidAmount ?: 0.0)
        val actualDepositAmount =
            if (depositAmount > 0.0) depositAmount else (pendingBid?.depositAmount ?: 0.0)

        val verifyRequest = VerifyPaymentRequest(
            razorpayOrderId = razorpayOrderId,
            razorpayPaymentId = razorpayPaymentId,
            razorpaySignature = razorpaySignature,
            auctionId = auctionId
        )
        when (val result = paymentRepository.verifyRazorpayPayment(verifyRequest)) {
            is com.example.rooster.core.common.Result.Success -> {
                if (result.data.success) {
                    val bidSubmissionSuccess = submitBidWithDepositToParse(
                        auctionId,
                        actualBidAmount,
                        actualDepositAmount,
                        razorpayPaymentId
                    )
                    if (bidSubmissionSuccess) {
                        loadBids(auctionId)
                        _error.value = null // Explicitly ensure error is null on full success
                        onResult?.invoke(true, "Bid placed successfully!") // TODO: Localize
                    } else {
                        _error.value = "Failed to record bid after payment." // TODO: Localize
                        FirebaseCrashlytics.getInstance().log("CRITICAL: Payment ${razorpayPaymentId} verified but bid submission failed for auction ${auctionId}.")
                        onResult?.invoke(false, _error.value)
                    }
                } else {
                    _error.value = result.data.message // Message from backend
                    FirebaseCrashlytics.getInstance().log("Payment verification failed by backend: ${result.data.message} for order ${razorpayOrderId}")
                    onResult?.invoke(false, _error.value)
                }
            }
            is com.example.rooster.core.common.Result.Error -> {
                _error.value = "Error verifying payment: ${result.exception.message}" // TODO: Localize
                FirebaseCrashlytics.getInstance().recordException(result.exception)
                onResult?.invoke(false, _error.value)
            }
            is com.example.rooster.core.common.Result.Loading -> {
                // Handled by _loading.value at start of method
            }
        }
        _loading.value = false
        // Clear pending bid details after processing
        _pendingBidDetails.value = null
        currentRazorpayOrderId.value = null
    }

    // TODO: Move this Parse interaction to an AuctionRepository
    private suspend fun submitBidWithDepositToParse(auctionId: String, bidAmount: Double, depositAmount: Double, paymentId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = ParseUser.getCurrentUser() ?: return@withContext false
                val bid = ParseObject("EnhancedAuctionBid") // Use constants for class names
                bid.put("auctionId", auctionId)
                bid.put("bidderId", currentUser.objectId)
                bid.put("bidderName", currentUser.username ?: "Anonymous")
                bid.put("bidAmount", bidAmount)
                bid.put("bidTime", Date())
                bid.put("isWinning", false) // Server should determine this
                bid.put("isProxyBid", false) // Assuming not a proxy bid for this flow
                bid.put("bidStatus", "ACTIVE") // Use enum/constant
                bid.put("depositAmount", depositAmount)
                bid.put("depositStatus", "PAID") // Use enum/constant
                bid.put("paymentId", paymentId) // Store payment ID for reference
                bid.save() // Use saveEventually for offline resilience if needed

                // TODO: Call a cloud function to update auction's current highest bid atomically
                // For now, direct update is placeholder from old code, less ideal.
                updateAuctionCurrentBidInParse(auctionId, bidAmount)
                true
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                false
            }
        }
    }

    // TODO: Move this to an AuctionRepository / Cloud Function
    private suspend fun updateAuctionCurrentBidInParse(auctionId: String, newBidAmount: Double) {
        withContext(Dispatchers.IO) {
            try {
                val auctionQuery = ParseQuery.getQuery<ParseObject>("AuctionListing")
                val auction = auctionQuery.get(auctionId)
                val currentBid = auction.getDouble("currentBid")
                if (newBidAmount > currentBid) {
                    auction.put("currentBid", newBidAmount)
                    auction.increment("bidCount")
                    auction.save()
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    // Collect payment events
    init {
        viewModelScope.launch {
            eventBus.paymentEvents.collect { event ->
                // Only process if this ViewModel initiated the payment (check currentRazorpayOrderId)
                val initiatedOrderId = currentRazorpayOrderId.value ?: return@collect

                when (event) {
                    is PaymentEvent.Success -> {
                        if (event.orderId == initiatedOrderId || event.orderId == null) { // Razorpay sometimes returns null orderId on success for some flows
                            // Payment successful via Razorpay, now verify with backend
                            // This assumes verifyAuctionDepositPayment is the method that needs this event
                            // It might need parameters like bidAmount, depositAmount if they were stored in VM
                            // For simplicity, let's assume we have stored the pending bid details.
                            val pendingBid = _pendingBidDetails.value
                            if (pendingBid != null && (event.orderId == initiatedOrderId || initiatedOrderId.isNotBlank())) {
                                verifyAuctionDepositPayment(
                                    razorpayOrderId = initiatedOrderId,
                                    razorpayPaymentId = event.paymentId,
                                    razorpaySignature = event.signature,
                                    auctionId = pendingBid.auctionId,
                                    bidAmount = pendingBid.bidAmount,
                                    depositAmount = pendingBid.depositAmount
                                )
                            }
                        }
                    }
                    is PaymentEvent.Failure -> {
                        _error.value =
                            "Razorpay payment failed: ${event.description}" // TODO: Localize
                        FirebaseCrashlytics.getInstance()
                            .log("Razorpay payment failed event: ${event.description} for order ${initiatedOrderId}")
                        // Potentially trigger UI update to show failure more explicitly if not covered by _error
                        _pendingBidDetails.value = null // Clear pending bid
                        currentRazorpayOrderId.value = null // Clear order ID
                    }
                }
            }
        }
    }

    // Store details of the bid that is currently undergoing payment
    private val _pendingBidDetails = MutableStateFlow<PendingBidInfo?>(null)
    // Public getter if needed, though typically not for this pattern
    // val pendingBidDetails: StateFlow<PendingBidInfo?> = _pendingBidDetails
    data class PendingBidInfo(
        val auctionId: String,
        val bidAmount: Double,
        val depositAmount: Double
    )

    fun setPendingBidDetails(auctionId: String, bidAmount: Double, depositAmount: Double) {
        _pendingBidDetails.value = PendingBidInfo(auctionId, bidAmount, depositAmount)
    }
}
