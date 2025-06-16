package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.AuctionSettings
import com.example.rooster.services.EnhancedAuctionService
import kotlinx.coroutines.launch

/**
 * ViewModel for enhanced auction creation with seller controls
 */
class AuctionCreationViewModel : ViewModel() {
    private val auctionService = EnhancedAuctionService()

    /**
     * Create an auction with enhanced seller controls
     */
    fun createAuction(
        settings: AuctionSettings,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val result = auctionService.createEnhancedAuction(settings)
                result.fold(
                    onSuccess = { auctionId ->
                        onSuccess(auctionId)
                    },
                    onFailure = { error ->
                        onError(error.message ?: "Failed to create auction")
                    },
                )
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error occurred")
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
                        onSuccess(bidResult.bidId)
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
