package com.example.rooster.feature.marketplace.ui.productdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.domain.model.ProductListing
import com.example.rooster.feature.marketplace.domain.repository.ProductListingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

sealed interface ProductDetailUiState {
    data object Loading : ProductDetailUiState
    data class Success(val listing: ProductListing) : ProductDetailUiState
    data class Error(val message: String) : ProductDetailUiState
}

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productListingRepository: ProductListingRepository,
    savedStateHandle: SavedStateHandle // To get listingId from navigation arguments
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    // Assuming "listingId" is passed as a navigation argument
    private val listingId: String = savedStateHandle.get<String>("listingId") ?: ""

    init {
        if (listingId.isNotBlank()) {
            fetchProductDetails(listingId)
        } else {
            _uiState.value = ProductDetailUiState.Error("Listing ID not provided.")
        }
    }

    private fun fetchProductDetails(id: String) {
        productListingRepository.getProductListingDetails(id)
            .onEach { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> ProductDetailUiState.Loading
                    is Result.Success -> {
                        result.data?.let { ProductDetailUiState.Success(it) }
                            ?: ProductDetailUiState.Error("Product not found.")
                    }
                    is Result.Error -> ProductDetailUiState.Error(result.exception.message ?: "Unknown error")
                }
            }.launchIn(viewModelScope)
    }

    // TODO: Add function to handle "Add to Cart" action, likely calling CartRepository via a use case
    // fun onAddToCartClicked(listing: ProductListing) { ... }
}
