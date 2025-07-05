package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.data.models.Auction
import com.example.rooster.data.models.AuctionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SimpleAuctionViewModel
    @Inject
    constructor(
        // Add repository here when available
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(AuctionUiState())
        val uiState: StateFlow<AuctionUiState> = _uiState.asStateFlow()

        private val _auctions = MutableStateFlow<List<Auction>>(emptyList())
        val auctions: StateFlow<List<Auction>> = _auctions.asStateFlow()

        init {
            loadAuctions()
        }

        private fun loadAuctions() {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)

                try {
                    // Mock data for now
                    val mockAuctions =
                        listOf(
                            Auction(
                                id = "1",
                                title = "Premium Broiler Chickens",
                                description = "High quality broiler chickens, well-fed and healthy",
                                breed = "Broiler",
                                age = 6,
                                currentBid = 1500.0,
                                highestBidder = "Farmer John",
                                sellerName = "Ram Reddy",
                                timeRemaining = 3600000L, // 1 hour
                                isEnded = false,
                                quantity = 50,
                                location = "Krishna District",
                            ),
                            Auction(
                                id = "2",
                                title = "Desi Chicken Batch",
                                description = "Country chickens raised naturally",
                                breed = "Desi",
                                age = 8,
                                currentBid = 2000.0,
                                highestBidder = "Laxmi Farmer",
                                sellerName = "Venkat Rao",
                                timeRemaining = 7200000L, // 2 hours
                                isEnded = false,
                                quantity = 30,
                                location = "Guntur District",
                            ),
                        )

                    _auctions.value = mockAuctions
                    _uiState.value = _uiState.value.copy(isLoading = false)
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to load auctions: ${e.message}",
                        )
                }
            }
        }

        fun placeBid(
            auctionId: String,
            bidAmount: Double,
        ) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isPlacingBid = true)

                try {
                    // Mock bid placement
                    val currentAuctions = _auctions.value.toMutableList()
                    val auctionIndex = currentAuctions.indexOfFirst { it.id == auctionId }

                    if (auctionIndex != -1) {
                        val auction = currentAuctions[auctionIndex]
                        val updatedAuction =
                            auction.copy(
                                currentBid = bidAmount,
                                highestBidder = "You", // Mock current user
                            )
                        currentAuctions[auctionIndex] = updatedAuction
                        _auctions.value = currentAuctions

                        _uiState.value =
                            _uiState.value.copy(
                                isPlacingBid = false,
                                successMessage = "Bid placed successfully!",
                                error = null,
                            )
                    } else {
                        _uiState.value =
                            _uiState.value.copy(
                                isPlacingBid = false,
                                error = "Auction not found",
                            )
                    }
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            isPlacingBid = false,
                            error = "Failed to place bid: ${e.message}",
                        )
                }
            }
        }

        fun clearError() {
            _uiState.value = _uiState.value.copy(error = null)
        }

        fun clearSuccessMessage() {
            _uiState.value = _uiState.value.copy(successMessage = null)
        }
    }
