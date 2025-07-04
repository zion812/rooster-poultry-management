package com.rooster.buyerhome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun BuyerHomeScreen(
    viewModel: BuyerHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Make the whole screen scrollable
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Buyer Home Screen", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        MarketplaceRecommendationsSection(
            recommendations = uiState.recommendations,
            isLoading = uiState.isLoadingRecommendations,
            error = uiState.recommendationsError,
            onRetry = { viewModel.fetchMarketplaceRecommendations() }
        )
        Spacer(modifier = Modifier.height(16.dp))

        OrderTrackingSection(
            orders = uiState.recentOrders,
            isLoading = uiState.isLoadingOrders,
            error = uiState.ordersError,
            onRetry = { viewModel.fetchRecentOrders() }
        )
        Spacer(modifier = Modifier.height(16.dp))

        PriceComparisonSection(
            comparisons = uiState.priceComparisons,
            isLoading = uiState.isLoadingPriceComparisons,
            error = uiState.priceComparisonsError,
            onRetry = { viewModel.fetchPriceComparisons(listOf("Broilers", "Eggs")) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        SupplierRatingsSection(
            suppliers = uiState.topSuppliers,
            isLoading = uiState.isLoadingSuppliers,
            error = uiState.suppliersError,
            onRetry = { viewModel.fetchTopSuppliers() }
        )
    }
}


@Composable
fun MarketplaceRecommendationsSection(
    recommendations: List<com.rooster.buyerhome.domain.model.MarketplaceRecommendationItem>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Recommendations For You", style = MaterialTheme.typography.titleMedium)
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else if (recommendations.isEmpty()) {
            Text("No recommendations available right now.")
        } else {
            recommendations.forEach { item ->
                // TODO: Create a proper RecommendationItemCard
                androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(item.productName, style = MaterialTheme.typography.titleSmall)
                        Text("Seller: ${item.sellerName} (${item.location ?: "N/A"})", style = MaterialTheme.typography.bodySmall)
                        Text("Price: ${item.price}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderTrackingSection(
    orders: List<com.rooster.buyerhome.domain.model.OrderItem>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Recent Orders", style = MaterialTheme.typography.titleMedium)
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else if (orders.isEmpty()) {
            Text("No recent orders found.")
        } else {
            orders.forEach { order ->
                // TODO: Create a proper OrderItemCard
                androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("${order.productName} (${order.quantity})", style = MaterialTheme.typography.titleSmall)
                        Text("Seller: ${order.sellerName}", style = MaterialTheme.typography.bodySmall)
                        Text("Status: ${order.status}", style = MaterialTheme.typography.bodyMedium)
                        Text("Total: ${order.totalPrice}", style = MaterialTheme.typography.bodyMedium)
                        Text("Ordered: ${java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(order.orderDate)}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun PriceComparisonSection(
    comparisons: List<com.rooster.buyerhome.domain.model.PriceComparisonProduct>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Price Watch", style = MaterialTheme.typography.titleMedium)
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else if (comparisons.isEmpty()) {
            Text("No price comparisons available.")
        } else {
            comparisons.forEach { item ->
                 androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(item.productName, style = MaterialTheme.typography.titleSmall)
                        Text("Avg. Market Price: ${item.averageMarketPrice}", style = MaterialTheme.typography.bodyMedium)
                        item.yourLastPaidPrice?.let { Text("You Paid: $it", style = MaterialTheme.typography.bodySmall) }
                        item.bestAvailablePrice?.let { Text("Best Offer: $it", style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        }
    }
}

@Composable
fun SupplierRatingsSection(
    suppliers: List<com.rooster.buyerhome.domain.model.SupplierRatingInfo>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Top Suppliers", style = MaterialTheme.typography.titleMedium)
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else if (suppliers.isEmpty()) {
            Text("No supplier ratings available.")
        } else {
            suppliers.forEach { supplier ->
                androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(supplier.supplierName, style = MaterialTheme.typography.titleSmall)
                        Text("Rating: ${String.format("%.1f", supplier.averageRating)}/5.0 (${supplier.numberOfReviews} reviews)", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BuyerHomeScreenPreview() {
    MaterialTheme {
        BuyerHomeScreen(viewModel = PreviewBuyerHomeViewModel())
    }
}

// ViewModel for Preview
class PreviewBuyerHomeViewModel : BuyerHomeViewModel(
    marketplaceRepository = object : com.rooster.buyerhome.domain.repository.BuyerMarketplaceRepository {
        override fun getMarketplaceRecommendations(buyerId: String) = kotlinx.coroutines.flow.flowOf(
            listOf(com.rooster.buyerhome.domain.model.MarketplaceRecommendationItem("p1", "Preview Chickens", "Seller A", "150/kg", null, "Loc A"))
        )
    },
    orderRepository = object : com.rooster.buyerhome.domain.repository.BuyerOrderRepository {
        override fun getRecentOrders(buyerId: String, count: Int) = kotlinx.coroutines.flow.flowOf(
            listOf(com.rooster.buyerhome.domain.model.OrderItem("o1", "Preview Eggs", "10dz", "800", java.util.Date(), null, com.rooster.buyerhome.domain.model.OrderStatus.DELIVERED, "Seller B"))
        )
    },
    priceComparisonRepository = object : com.rooster.buyerhome.domain.repository.PriceComparisonRepository {
        override fun getPriceComparisonForProducts(productNames: List<String>) = kotlinx.coroutines.flow.flowOf(
            listOf(com.rooster.buyerhome.domain.model.PriceComparisonProduct("Preview Broilers", "160/kg", "155/kg", "150/kg (Seller C)"))
        )
    },
    supplierRepository = object : com.rooster.buyerhome.domain.repository.BuyerSupplierRepository {
        override fun getTopRatedSuppliers(count: Int) = kotlinx.coroutines.flow.flowOf(
            listOf(com.rooster.buyerhome.domain.model.SupplierRatingInfo("s1", "Top Preview Seller", 4.8f, 120, null))
        )
    }
) {
    init {
        // Preview ViewModel can choose to call fetch methods or not,
        // as flows are typically collected by Composable state.
        // If using non-Flow State in UiState, then fetching here would be needed.
    }
}
