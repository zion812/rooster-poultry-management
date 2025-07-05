package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.BidMonitoringCategory
import com.example.rooster.services.AuctionUpdate
import com.example.rooster.services.CategorizedBid
import com.example.rooster.services.EnhancedAuctionService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * ViewModel for auction monitoring and bidding functionality
 */
class AuctionMonitoringViewModel : ViewModel() {
    private val auctionService = EnhancedAuctionService()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    /**
     * Get real-time auction updates
     */
    override fun onCleared() {
        super.onCleared()
        // Clear state flows to prevent memory leaks
        _isLoading.value = false
    }

    fun getAuctionUpdates(auctionId: String): Flow<AuctionUpdate?> {
        return auctionService.getAuctionUpdates(auctionId)
    }

    /**
     * Get categorized bids for seller monitoring
     */
    fun getCategorizedBids(
        auctionId: String,
        monitoringType: BidMonitoringCategory,
    ): Flow<List<CategorizedBid>> =
        flow {
            try {
                val result = auctionService.getBidsForSeller(auctionId, monitoringType)
                result.fold(
                    onSuccess = { bids -> emit(bids) },
                    onFailure = { emit(emptyList()) },
                )
            } catch (e: Exception) {
                emit(emptyList())
            }
        }

    /**
     * Load auction details
     */
    fun loadAuctionDetails(auctionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load auction details if needed
                // This could fetch additional auction metadata
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Place a bid on an auction
     */
    fun placeBid(
        auctionId: String,
        bidAmount: Double,
        isProxyBid: Boolean = false,
        proxyMaxAmount: Double? = null,
        bidMessage: String? = null,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        onDepositRequired: (Double) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val result =
                    auctionService.placeBid(
                        auctionId = auctionId,
                        bidAmount = bidAmount,
                        isProxyBid = isProxyBid,
                        proxyMaxAmount = proxyMaxAmount,
                        bidMessage = bidMessage,
                    )
                result.fold(
                    onSuccess = { bidResult ->
                        if (bidResult.depositRequired) {
                            onDepositRequired(bidResult.depositAmount)
                        } else {
                            onSuccess(bidResult.bidId)
                        }
                    },
                    onFailure = { error ->
                        onError(error.message ?: "Failed to place bid")
                    },
                )
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error occurred")
            }
        }
    }
}
