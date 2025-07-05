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
import com.example.rooster.core.common.util.DataState


// Define UI State for BuyerHomeScreen
data class BuyerHomeUiState(
    val recommendationsState: DataState<List<MarketplaceRecommendationItem>> = DataState.Loading(null),
    val recentOrdersState: DataState<List<OrderItem>> = DataState.Loading(null),
    val priceComparisonsState: DataState<List<PriceComparisonProduct>> = DataState.Loading(null),
    val topSuppliersState: DataState<List<SupplierRatingInfo>> = DataState.Loading(null),
    val transientUserMessage: String? = null,
    val messageId: java.util.UUID? = null,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false // Added for network status
)

@HiltViewModel
class BuyerHomeViewModel @Inject constructor(
    private val marketplaceRepository: BuyerMarketplaceRepository,
    private val orderRepository: BuyerOrderRepository,
    private val priceComparisonRepository: PriceComparisonRepository,
    private val supplierRepository: BuyerSupplierRepository,
    private val connectivityRepository: com.example.rooster.core.common.connectivity.ConnectivityRepository // Fully qualified
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuyerHomeUiState())
    val uiState: StateFlow<BuyerHomeUiState> = _uiState.asStateFlow()

    // Assume a buyerId is available, e.g., from user session
    private val currentBuyerId = "buyer789" // Placeholder

    init {
        fetchAllData()
        observeNetworkStatus() // Call new function
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            connectivityRepository.observeNetworkStatus().collect { status ->
                _uiState.value = _uiState.value.copy(
                    isOffline = status != com.example.rooster.core.common.connectivity.NetworkStatus.Available
                )
            }
        }
    }

    private fun fetchAllData() {
        fetchMarketplaceRecommendations()
        fetchRecentOrders()
        fetchPriceComparisons(listOf("Broilers", "Eggs")) // Example products
        fetchTopSuppliers()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            fetchAllData()
            // Consider a more robust way to set isRefreshing = false, e.g., when all flows have emitted.
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    fun fetchMarketplaceRecommendations() {
        viewModelScope.launch {
            // Assuming repository now returns Flow<DataState<List<MarketplaceRecommendationItem>>>
            marketplaceRepository.getMarketplaceRecommendations(currentBuyerId)
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(recommendationsState = dataState)
                }
        }
    }

    fun fetchRecentOrders(count: Int = 5) {
        viewModelScope.launch {
            // Assuming repository now returns Flow<DataState<List<OrderItem>>>
            orderRepository.getRecentOrders(currentBuyerId, count)
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(recentOrdersState = dataState)
                }
        }
    }

    fun fetchPriceComparisons(productNames: List<String>) {
        viewModelScope.launch {
            // Assuming repository now returns Flow<DataState<List<PriceComparisonProduct>>>
            priceComparisonRepository.getPriceComparisonForProducts(productNames)
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(priceComparisonsState = dataState)
                }
        }
    }

    fun fetchTopSuppliers(count: Int = 3) {
        viewModelScope.launch {
            // Assuming repository now returns Flow<DataState<List<SupplierRatingInfo>>>
            supplierRepository.getTopRatedSuppliers(count)
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(topSuppliersState = dataState)
                }
        }
    }

    // Add if transient messages are used
    fun clearTransientMessage() {
        _uiState.value = _uiState.value.copy(transientUserMessage = null, messageId = null)
    }
}
