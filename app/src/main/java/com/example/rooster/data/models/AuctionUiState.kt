package com.example.rooster.data.models

data class AuctionUiState(
    val isLoading: Boolean = false,
    val isPlacingBid: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val selectedAuctionId: String? = null,
)
