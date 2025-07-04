package com.example.rooster.feature.marketplace.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.Result
import com.example.rooster.core.common.model.Product
import com.example.rooster.feature.marketplace.domain.repository.MarketplaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MarketplaceUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String? = null, // Example: using categoryId as String
    val sortOption: SortOption = SortOption.NONE,
    val categories: List<String> = listOf("All", "Eggs", "Broilers", "Feed", "Equipment"), // Mock categories for now
    val isLoadingNextPage: Boolean = false,
    val canLoadNextPage: Boolean = true,
    val page: Int = 1 // Current page for pagination
)

enum class SortOption {
    NONE, PRICE_ASC, PRICE_DESC, RATING_DESC, DISTANCE_ASC, FRESHNESS_DESC // Distance and Freshness might require more data
}

@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    private val marketplaceRepository: MarketplaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketplaceUiState())
    val uiState: StateFlow<MarketplaceUiState> = _uiState.asStateFlow()

    private val pageSize = 20 // Number of items per page

    init {
        loadProducts(isRefresh = true)
    }

    fun loadProducts(isRefresh: Boolean = false) {
        if (_uiState.value.isLoading || _uiState.value.isLoadingNextPage) return

        val currentPage = if (isRefresh) 1 else _uiState.value.page
        if (isRefresh) {
            _uiState.update { it.copy(isLoading = true, error = null, products = emptyList(), page = 1, canLoadNextPage = true) }
        } else {
            _uiState.update { it.copy(isLoadingNextPage = true) }
        }

        viewModelScope.launch {
            // For now, repository's getAllProducts handles pagination internally or not at all.
            // The call below is simplified. A true pagination would pass page/pageSize to repository.
            // The current MarketplaceRepository.getAllProducts has optional page/pageSize.
            // Let's assume for now it fetches all and we filter/sort client side,
            // or the repository handles basic pagination if params are passed.
            // For a more robust pagination, repository methods would need to support it properly.

            // Simplified: using searchProducts for filtering and fetching
            // This is a temporary simplification. Ideally, getAllProducts would support filters & pagination.
            marketplaceRepository.searchProducts(
                query = _uiState.value.searchQuery,
                filters = _uiState.value.selectedCategory?.let { if (it != "All") mapOf("categoryId" to it) else null }
            )
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            val newProducts = result.data ?: emptyList()
                            _uiState.update {
                                val allProducts = if (isRefresh) newProducts else it.products + newProducts
                                it.copy(
                                    products = sortProducts(allProducts, it.sortOption),
                                    isLoading = false,
                                    isLoadingNextPage = false,
                                    error = null,
                                    page = if (newProducts.isNotEmpty() && !isRefresh) it.page + 1 else it.page, // crude page increment
                                    canLoadNextPage = newProducts.isNotEmpty() // crude check
                                )
                            }
                        }
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isLoadingNextPage = false,
                                    error = result.exception?.message ?: "Unknown error"
                                )
                            }
                        }
                        Result.Loading -> {
                            if (isRefresh) _uiState.update { it.copy(isLoading = true) }
                            else _uiState.update { it.copy(isLoadingNextPage = true) }
                        }
                    }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingNextPage = false,
                            error = e.message ?: "Flow collection error"
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadProducts(isRefresh = true) // Reload products on search query change
    }

    fun onCategorySelected(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadProducts(isRefresh = true) // Reload products on category change
    }

    fun onSortOptionSelected(sortOption: SortOption) {
        _uiState.update {
            it.copy(
                sortOption = sortOption,
                products = sortProducts(it.products, sortOption)
            )
        }
    }

    fun refreshProducts() {
        loadProducts(isRefresh = true)
    }

    private fun sortProducts(products: List<Product>, sortOption: SortOption): List<Product> {
        return when (sortOption) {
            SortOption.PRICE_ASC -> products.sortedBy { it.price }
            SortOption.PRICE_DESC -> products.sortedByDescending { it.price }
            SortOption.RATING_DESC -> products.sortedByDescending { it.rating ?: 0.0 }
            // TODO: Implement distance and freshness sorting if data becomes available
            SortOption.DISTANCE_ASC -> products // Placeholder
            SortOption.FRESHNESS_DESC -> products // Placeholder
            SortOption.NONE -> products
        }
    }
}
