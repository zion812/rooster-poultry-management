package com.example.rooster.feature.marketplace.ui.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.domain.model.ProductListing
import com.example.rooster.feature.marketplace.domain.model.ProductCategory
import com.example.rooster.feature.marketplace.domain.repository.ProductListingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import android.content.Context
import javax.inject.Inject

sealed interface ProductListUiState {
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
}

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productListingRepository: ProductListingRepository
) : ViewModel() {

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

    fun refreshListings(context: Context) {
        _uiState.value = ProductListUiState.InitialLoading
        viewModelScope.launch {
            try {
                loadProductListings(isRefresh = true)
            } catch (e: Exception) {
                val msg = e.localizedMessage ?: "Unknown error"
                _uiState.value = ProductListUiState.Error(msg)
            }
        }
    }

    fun onSearchQueryChanged(query: String, context: Context) {
        currentSearchTermFilter = query.takeIf { it.isNotBlank() }
        viewModelScope.launch {
            try {
                loadProductListings(isRefresh = true)
            } catch (e: Exception) {
                val msg = e.localizedMessage ?: "Unknown error"
                _uiState.value = ProductListUiState.Error(msg)
            }
        }
    }

    fun onCategorySelected(category: ProductCategory?, context: Context) {
        currentCategoryFilter = category
        viewModelScope.launch {
            try {
                loadProductListings(isRefresh = true)
            } catch (e: Exception) {
                val msg = e.localizedMessage ?: "Unknown error"
                _uiState.value = ProductListUiState.Error(msg)
            }
        }
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
                        val msg = result.exception.localizedMessage ?: "Unknown error"
                        _uiState.value = ProductListUiState.Error(
                            message = msg,
                            currentListings = currentSuccessState?.listings ?: emptyList()
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}
