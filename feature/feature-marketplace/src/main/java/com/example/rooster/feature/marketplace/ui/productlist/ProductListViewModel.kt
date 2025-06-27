package com.example.rooster.feature.marketplace.ui.productlist

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
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProductListUiState {
    data object Loading : ProductListUiState
    data class Success(val listings: List<ProductListing>) : ProductListUiState
    data class Error(val message: String) : ProductListUiState
}

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productListingRepository: ProductListingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    // TODO: Add parameters for category, sellerId, searchTerm from UI interactions
    private var currentCategory: String? = null
    private var currentSellerId: String? = null
    private var currentSearchTerm: String? = null

    init {
        fetchProductListings()
    }

    fun fetchProductListings(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            productListingRepository.getProductListings(
                category = null, // TODO: map currentCategory (String) to ProductCategory enum if needed by repo
                sellerId = currentSellerId,
                searchTerm = currentSearchTerm,
                forceRefresh = forceRefresh
            ).onEach { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> ProductListUiState.Loading
                    is Result.Success -> ProductListUiState.Success(result.data)
                    is Result.Error -> ProductListUiState.Error(result.exception.message ?: "Unknown error")
                }
            }.launchIn(viewModelScope)
        }
    }

    fun onSearchQueryChanged(query: String) {
        currentSearchTerm = query
        // Could add debounce here if desired
        fetchProductListings()
    }

    fun onCategorySelected(category: String?) { // Assuming category comes as String from UI
        currentCategory = category
        fetchProductListings()
    }

    // TODO: Add methods for pagination (loadMore) if implementing infinite scroll
}
