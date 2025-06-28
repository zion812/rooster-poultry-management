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
 jules/arch-assessment-1
    data object InitialLoading : ProductListUiState // For the very first load
    data class Success(
        val listings: List<ProductListing>,
        val isLoadingMore: Boolean = false,
        val canLoadMore: Boolean = true, // Assume true initially
        val currentCategory: ProductCategory? = null, // To show current filter
        val currentSearchTerm: String? = null // To show current filter
    ) : ProductListUiState
    data class Error(val message: String, val currentListings: List<ProductListing> = emptyList()) : ProductListUiState
    // Error state can also hold current listings to show stale data + error message
=======
    data object Loading : ProductListUiState
    data class Success(val listings: List<ProductListing>) : ProductListUiState
    data class Error(val message: String) : ProductListUiState
 main
}

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productListingRepository: ProductListingRepository
) : ViewModel() {

 jules/arch-assessment-1
    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.InitialLoading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private var currentCategoryFilter: ProductCategory? = null
    private var currentSellerIdFilter: String? = null // Not yet used in UI controls
    private var currentSearchTermFilter: String? = null

    private var lastVisibleListingTimestamp: Long? = null
    private var lastVisibleListingId: String? = null
    private var isCurrentlyLoadingMore = false
    private var allItemsLoaded = false

    companion object {
        private const val PAGE_SIZE = 10 // Should match remote data source or be configurable
    }

    init {
        loadProductListings(isRefresh = false)
    }

    fun refreshListings() {
        loadProductListings(isRefresh = true)
    }

    fun loadMoreProductListings() {
        if (isCurrentlyLoadingMore || allItemsLoaded) return
        loadProductListings(isRefresh = false, loadMore = true)
    }

    private fun loadProductListings(isRefresh: Boolean, loadMore: Boolean = false) {
        if (isCurrentlyLoadingMore && loadMore) return // Prevent multiple simultaneous loadMore calls

        if (isRefresh) {
            lastVisibleListingTimestamp = null
            lastVisibleListingId = null
            allItemsLoaded = false
            _uiState.value = ProductListUiState.InitialLoading // Show full screen loader on refresh
        } else if (loadMore) {
            isCurrentlyLoadingMore = true
            val currentSuccessState = _uiState.value as? ProductListUiState.Success
            _uiState.value = currentSuccessState?.copy(isLoadingMore = true)
                ?: ProductListUiState.InitialLoading // Fallback if state is not Success
        } else { // Initial load
             _uiState.value = ProductListUiState.InitialLoading
        }

        viewModelScope.launch {
            productListingRepository.getProductListings(
                category = currentCategoryFilter,
                sellerId = currentSellerIdFilter,
                searchTerm = currentSearchTermFilter,
                forceRefresh = isRefresh, // Force refresh if explicitly requested
                pageSize = PAGE_SIZE,
                lastVisibleTimestamp = if (loadMore) lastVisibleListingTimestamp else null,
                lastVisibleDocId = if (loadMore) lastVisibleListingId else null
            ).onEach { result ->
                isCurrentlyLoadingMore = false // Reset loading flag once result is received
                val currentSuccessState = _uiState.value as? ProductListUiState.Success

                when (result) {
                    is Result.Loading -> {
                        if (!loadMore && !isRefresh) { // Only show full screen loading on initial load
                           // _uiState.value = ProductListUiState.InitialLoading // Already set or handled by initial state
                        }
                        // For loadMore, isLoadingMore is already true in currentSuccessState
                    }
                    is Result.Success -> {
                        val newListings = result.data
                        allItemsLoaded = newListings.size < PAGE_SIZE

                        val combinedListings = if (loadMore && currentSuccessState != null) {
                            currentSuccessState.listings + newListings
                        } else {
                            newListings
                        }
                        // Deduplicate just in case, though pagination should prevent this
                        val distinctListings = combinedListings.distinctBy { it.id }

                        if (distinctListings.isNotEmpty()) {
                            lastVisibleListingTimestamp = distinctListings.last().postedDateTimestamp
                            lastVisibleListingId = distinctListings.last().id
                        }

                        _uiState.value = ProductListUiState.Success(
                            listings = distinctListings,
                            isLoadingMore = false,
                            canLoadMore = !allItemsLoaded,
                            currentCategory = currentCategoryFilter,
                            currentSearchTerm = currentSearchTermFilter
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = ProductListUiState.Error(
                            message = result.exception.message ?: "Unknown error",
                            currentListings = currentSuccessState?.listings ?: emptyList()
                        )
                    }
=======
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
 main
                }
            }.launchIn(viewModelScope)
        }
    }

    fun onSearchQueryChanged(query: String) {
 jules/arch-assessment-1
        currentSearchTermFilter = query.takeIf { it.isNotBlank() }
        // Add debounce here if desired
        loadProductListings(isRefresh = true)
    }

    fun onCategorySelected(category: ProductCategory?) {
        currentCategoryFilter = category
        loadProductListings(isRefresh = true)
    }
=======
        currentSearchTerm = query
        // Could add debounce here if desired
        fetchProductListings()
    }

    fun onCategorySelected(category: String?) { // Assuming category comes as String from UI
        currentCategory = category
        fetchProductListings()
    }

    // TODO: Add methods for pagination (loadMore) if implementing infinite scroll
 main
}
