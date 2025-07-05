package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.AuctionListing
import com.example.rooster.AuctionWinner
import com.example.rooster.EnhancedAuctionBid
import com.example.rooster.fetchActiveAuctions
import com.example.rooster.fetchAuctionWinner
import com.example.rooster.fetchEnhancedAuctionBids
import com.example.rooster.services.TokenService
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Data class for bid validation results
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String?,
)

/**
 * Data class for bid statistics
 */
data class BidStatistics(
    val totalBids: Int,
    val bidsAboveMinimum: Int,
    val bidsBelowMinimum: Int,
    val highestBid: Double,
    val averageBid: Double,
    val uniqueBidders: Int,
)

class AuctionViewModel : ViewModel() {
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

    // New bidding mechanics state
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

    // Token balance state
    private val _tokenBalance = MutableStateFlow(0)
    val tokenBalance: StateFlow<Int> = _tokenBalance

    // Store current Razorpay Order ID for verification after payment
    val currentRazorpayOrderId = MutableStateFlow<String?>(null)

    /**
     * Load the current user's token balance.
     */
    override fun onCleared() {
        super.onCleared()
        // Clear state flows to prevent memory leaks
        _loading.value = false
        _tokenBalance.value = /* Reset to initial state */
    }

    fun loadTokenBalance() =
        viewModelScope.launch {
            TokenService.loadTokenBalance { balance ->
                _tokenBalance.value = balance
            }
        }

    /**
     * Deduct one token for placing a bid. Updates local flow on success.
     */
    fun deductToken(onResult: (Boolean) -> Unit) =
        viewModelScope.launch {
            TokenService.deductToken { success ->
                if (success) {
                    _tokenBalance.value = _tokenBalance.value.dec().coerceAtLeast(0)
                }
                onResult(success)
            }
        }

    /**
     * Fetch minimum bid price for a specific auction
     */
    private suspend fun fetchMinBidPrice(auctionId: String): Double? =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("EnhancedAuction")
                val auction = query.get(auctionId)
                auction.getNumber("minimumBidPrice")?.toDouble()
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }

    /**
     * Fetch available auction durations (in hours)
     * Standard options: 12h, 24h, 48h, 72h, 96h, 120h, 168h (7 days)
     */
    private suspend fun fetchAuctionDurations(): List<Int> =
        withContext(Dispatchers.IO) {
            try {
                // These are the confirmed duration options from TODO.md
                listOf(12, 24, 48, 72, 96, 120, 168)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                // Return default durations on error
                listOf(12, 24, 48, 72, 96, 120, 168)
            }
        }

    /**
     * Fetch deposit requirement for a specific auction
     * Returns deposit percentage if required, null if not required
     */
    private suspend fun fetchDepositRequirement(auctionId: String): Double? =
        withContext(Dispatchers.IO) {
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

    /**
     * Load bidding mechanics settings and categorize bids
     */
    fun loadBiddingSettings(auctionId: String) =
        viewModelScope.launch {
            try {
                _loading.value = true

                // Fetch all bidding settings
                val minPrice = fetchMinBidPrice(auctionId)
                val durations = fetchAuctionDurations()
                val depositReq = fetchDepositRequirement(auctionId)

                _minBidPrice.value = minPrice
                _auctionDurations.value = durations
                _depositRequirement.value = depositReq

                // Categorize existing bids based on minimum price
                categorizeBids()

                _error.value = null
                FirebaseCrashlytics.getInstance().log("Bidding settings loaded for auction: $auctionId")
            } catch (e: Exception) {
                _error.value = "Failed to load bidding settings: ${e.message}"
                FirebaseCrashlytics.getInstance().recordException(e)
            } finally {
                _loading.value = false
            }
        }

    /**
     * Categorize bids as above or below minimum price
     */
    private fun categorizeBids() {
        val allBids = _bids.value
        val minPrice = _minBidPrice.value

        if (minPrice != null) {
            _bidsAboveMin.value = allBids.filter { bid -> bid.bidAmount >= minPrice }
            _bidsBelowMin.value = allBids.filter { bid -> bid.bidAmount < minPrice }
        } else {
            // If no minimum price set, all bids are considered valid
            _bidsAboveMin.value = allBids
            _bidsBelowMin.value = emptyList()
        }

        FirebaseCrashlytics.getInstance().log(
            "Bids categorized: ${_bidsAboveMin.value.size} above min, ${_bidsBelowMin.value.size} below min",
        )
    }

    /**
     * Validate if a bid amount meets the minimum requirements
     */
    fun validateBidAmount(bidAmount: Double): ValidationResult {
        val minPrice = _minBidPrice.value
        val currentBids = _bids.value
        val currentHighest = currentBids.maxByOrNull { it.bidAmount }?.bidAmount ?: 0.0

        return when {
            minPrice != null && bidAmount < minPrice -> {
                ValidationResult(
                    isValid = false,
                    errorMessage = "Bid amount ₹$bidAmount is below minimum bid price ₹$minPrice",
                )
            }
            bidAmount <= currentHighest -> {
                ValidationResult(
                    isValid = false,
                    errorMessage = "Bid amount ₹$bidAmount must be higher than current highest bid ₹$currentHighest",
                )
            }
            else -> {
                ValidationResult(
                    isValid = true,
                    errorMessage = null,
                )
            }
        }
    }

    /**
     * Calculate required deposit amount for a bid
     */
    fun calculateDepositAmount(bidAmount: Double): Double? {
        val depositPercentage = _depositRequirement.value
        return depositPercentage?.let {
            bidAmount * (it / 100.0)
        }
    }

    fun loadAuctions() =
        viewModelScope.launch {
            _loading.value = true
            fetchActiveAuctions(
                onResult = { list ->
                    _loading.value = false
                    _auctions.value = list
                    _error.value = null
                },
                onError = { errorMsg ->
                    _loading.value = false
                    _error.value = errorMsg
                },
                setLoading = { loading -> _loading.value = loading },
            )
        }

    fun loadBids(auctionId: String) =
        viewModelScope.launch {
            _loading.value = true
            fetchEnhancedAuctionBids(
                auctionId = auctionId,
                onResult = { bidsList ->
                    _loading.value = false
                    _bids.value = bidsList
                    _error.value = null
                    // Recategorize bids after loading
                    categorizeBids()
                },
                onError = { errorMsg ->
                    _loading.value = false
                    _error.value = errorMsg
                },
                setLoading = { loading -> _loading.value = loading },
            )
        }

    fun loadWinner(auctionId: String) =
        viewModelScope.launch {
            _loading.value = true
            fetchAuctionWinner(
                auctionId = auctionId,
                onResult = { winnerResult ->
                    _loading.value = false
                    _winner.value = winnerResult
                    _error.value = null
                },
                onError = { errorMsg ->
                    _loading.value = false
                    _error.value = errorMsg
                },
                setLoading = { loading -> _loading.value = loading },
            )
        }

    /**
     * Clear error messages
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Get bid statistics for seller dashboard
     */
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
}
