package com.example.rooster.feature.auctions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Imports for AuctionListing, AuctionWinner, EnhancedAuctionBid, fetchers, TokenService, etc.,
// are assumed to be correct if these entities are in com.example.rooster.* (app module or core-common)
// or will be adjusted if they also move/are in core modules.
import com.example.rooster.AuctionListing // May need path update
import com.example.rooster.AuctionWinner // May need path update
import com.example.rooster.EnhancedAuctionBid // May need path update
import com.example.rooster.fetchActiveAuctions // May need path update
import com.example.rooster.fetchAuctionWinner // May need path update
import com.example.rooster.fetchEnhancedAuctionBids // May need path update
import com.example.rooster.services.TokenService // This service will likely need to be refactored or made available via DI
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseObject
import com.parse.ParseQuery
import dagger.hilt.android.lifecycle.HiltViewModel // Added for Hilt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject // Added for Hilt

// Data classes ValidationResult and BidStatistics might be better placed in a domain or common model package
// if they are used by more than just this ViewModel, or kept here if specific to its UI state.
// For now, they move with the ViewModel.

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

@HiltViewModel
class AuctionViewModel @Inject constructor(
    // TODO: Inject repositories or use cases here instead of direct Parse/TokenService calls
    // For example:
    // private val auctionRepository: AuctionRepository,
    // private val tokenRepository: TokenRepository
    private val tokenService: TokenService // Assuming TokenService can be injected or wrapped
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
            // TODO: Replace with repository call
            tokenService.loadTokenBalance { balance -> // Assuming TokenService is injectable or accessible
                _tokenBalance.value = balance
            }
        }

    fun deductToken(onResult: (Boolean) -> Unit) =
        viewModelScope.launch {
            // TODO: Replace with repository call
            tokenService.deductToken { success -> // Assuming TokenService is injectable or accessible
                if (success) {
                    _tokenBalance.value = _tokenBalance.value.dec().coerceAtLeast(0)
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

    fun loadAuctions() =
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            // TODO: Replace with repository call
            fetchActiveAuctions(
                onResult = { list -> _auctions.value = list; _error.value = null },
                onError = { errorMsg -> _error.value = errorMsg }, // TODO: Localize
                setLoading = { isLoading -> _loading.value = isLoading }
            )
            // If fetchActiveAuctions is suspend and returns Result:
            // when (val result = auctionRepository.getActiveAuctions()) {
            //     is Result.Success -> _auctions.value = result.data
            //     is Result.Error -> _error.value = result.exception.message
            //     is Result.Loading -> _loading.value = true // Handled by initial _loading.value = true
            // }
            // _loading.value = false
        }

    fun loadBids(auctionId: String) =
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            // TODO: Replace with repository call
            fetchEnhancedAuctionBids(
                auctionId = auctionId,
                onResult = { bidsList -> _bids.value = bidsList; categorizeBids(); _error.value = null },
                onError = { errorMsg -> _error.value = errorMsg }, // TODO: Localize
                setLoading = { isLoading -> _loading.value = isLoading }
            )
        }

    fun loadWinner(auctionId: String) =
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            // TODO: Replace with repository call
            fetchAuctionWinner(
                auctionId = auctionId,
                onResult = { winnerResult -> _winner.value = winnerResult; _error.value = null },
                onError = { errorMsg -> _error.value = errorMsg }, // TODO: Localize
                setLoading = { isLoading -> _loading.value = isLoading }
            )
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
}
