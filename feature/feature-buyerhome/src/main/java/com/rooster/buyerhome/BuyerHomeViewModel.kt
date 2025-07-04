package com.rooster.buyerhome

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

package com.rooster.buyerhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooster.buyerhome.domain.model.MarketplaceRecommendationItem
import com.rooster.buyerhome.domain.model.OrderItem
import com.rooster.buyerhome.domain.model.PriceComparisonProduct
import com.rooster.buyerhome.domain.model.SupplierRatingInfo
import com.rooster.buyerhome.domain.repository.BuyerMarketplaceRepository
import com.rooster.buyerhome.domain.repository.BuyerOrderRepository
import com.rooster.buyerhome.domain.repository.BuyerSupplierRepository
import com.rooster.buyerhome.domain.repository.PriceComparisonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


// Define UI State for BuyerHomeScreen
data class BuyerHomeUiState(
    val recommendations: List<MarketplaceRecommendationItem> = emptyList(),
    val isLoadingRecommendations: Boolean = false,
    val recommendationsError: String? = null,

    val recentOrders: List<OrderItem> = emptyList(),
    val isLoadingOrders: Boolean = false,
    val ordersError: String? = null,

    val priceComparisons: List<PriceComparisonProduct> = emptyList(),
    val isLoadingPriceComparisons: Boolean = false,
    val priceComparisonsError: String? = null,

    val topSuppliers: List<SupplierRatingInfo> = emptyList(),
    val isLoadingSuppliers: Boolean = false,
    val suppliersError: String? = null
)

@HiltViewModel
class BuyerHomeViewModel @Inject constructor(
    private val marketplaceRepository: BuyerMarketplaceRepository,
    private val orderRepository: BuyerOrderRepository,
    private val priceComparisonRepository: PriceComparisonRepository,
    private val supplierRepository: BuyerSupplierRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuyerHomeUiState())
    val uiState: StateFlow<BuyerHomeUiState> = _uiState.asStateFlow()

    // Assume a buyerId is available, e.g., from user session
    private val currentBuyerId = "buyer789" // Placeholder

    init {
        fetchMarketplaceRecommendations()
        fetchRecentOrders()
        fetchPriceComparisons(listOf("Broilers", "Eggs")) // Example products
        fetchTopSuppliers()
    }

    fun fetchMarketplaceRecommendations() {
        viewModelScope.launch {
            marketplaceRepository.getMarketplaceRecommendations(currentBuyerId)
                .onStart { _uiState.value = _uiState.value.copy(isLoadingRecommendations = true, recommendationsError = null) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoadingRecommendations = false, recommendationsError = e.message) }
                .collect { data -> _uiState.value = _uiState.value.copy(isLoadingRecommendations = false, recommendations = data) }
        }
    }

    fun fetchRecentOrders(count: Int = 5) {
        viewModelScope.launch {
            orderRepository.getRecentOrders(currentBuyerId, count)
                .onStart { _uiState.value = _uiState.value.copy(isLoadingOrders = true, ordersError = null) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoadingOrders = false, ordersError = e.message) }
                .collect { data -> _uiState.value = _uiState.value.copy(isLoadingOrders = false, recentOrders = data) }
        }
    }

    fun fetchPriceComparisons(productNames: List<String>) {
        viewModelScope.launch {
            priceComparisonRepository.getPriceComparisonForProducts(productNames)
                .onStart { _uiState.value = _uiState.value.copy(isLoadingPriceComparisons = true, priceComparisonsError = null) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoadingPriceComparisons = false, priceComparisonsError = e.message) }
                .collect { data -> _uiState.value = _uiState.value.copy(isLoadingPriceComparisons = false, priceComparisons = data) }
        }
    }

    fun fetchTopSuppliers(count: Int = 3) {
        viewModelScope.launch {
            supplierRepository.getTopRatedSuppliers(count)
                .onStart { _uiState.value = _uiState.value.copy(isLoadingSuppliers = true, suppliersError = null) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoadingSuppliers = false, suppliersError = e.message) }
                .collect { data -> _uiState.value = _uiState.value.copy(isLoadingSuppliers = false, topSuppliers = data) }
        }
    }
}
