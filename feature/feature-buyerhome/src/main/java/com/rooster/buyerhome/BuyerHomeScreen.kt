package com.rooster.buyerhome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rooster.core.common.util.DataState
import com.rooster.buyerhome.ui.components.MarketplaceRecommendationItemCard
import com.rooster.buyerhome.ui.components.OrderItemCard
import com.rooster.buyerhome.ui.components.PriceComparisonItemCard
import com.rooster.buyerhome.ui.components.SupplierRatingItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerHomeScreen(
    viewModel: BuyerHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.messageId) {
        uiState.transientUserMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearTransientMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { scaffoldPadding ->
        val pullRefreshState = rememberPullRefreshState(
            refreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh
        )

        Box(
            modifier = Modifier
                .padding(scaffoldPadding)
                .pullRefresh(pullRefreshState)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.isOffline) {
                    Text(
                        stringResource(id = com.example.rooster.R.string.offline_banner_message),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                            .padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Text(
                    stringResource(id = com.example.rooster.R.string.buyer_home_title), 
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                MarketplaceRecommendationsSection(
                    recommendationsState = uiState.recommendationsState,
                    onRetry = { viewModel.fetchMarketplaceRecommendations() }
                )
                Spacer(modifier = Modifier.height(16.dp))

                OrderTrackingSection(
                    ordersState = uiState.recentOrdersState,
                    onRetry = { viewModel.fetchRecentOrders() }
                )
                Spacer(modifier = Modifier.height(16.dp))

                PriceComparisonSection(
                    comparisonsState = uiState.priceComparisonsState,
                    onRetry = { viewModel.fetchPriceComparisons(listOf("Broilers", "Eggs")) }
                )
                Spacer(modifier = Modifier.height(16.dp))

                SupplierRatingsSection(
                    suppliersState = uiState.topSuppliersState,
                    onRetry = { viewModel.fetchTopSuppliers() }
                )
            }

            PullRefreshIndicator(
                refreshing = uiState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceRecommendationsSection(
    recommendationsState: DataState<List<com.rooster.buyerhome.domain.model.MarketplaceRecommendationItem>>,
    onRetry: () -> Unit
) {
    val recommendations = recommendationsState.getUnderlyingData() ?: emptyList()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(id = com.example.rooster.R.string.recommendations_title), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        when (recommendationsState) {
            is DataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                if (recommendations.isNotEmpty()) {
                    Text(stringResource(id = com.example.rooster.R.string.updating_cached_data), style = MaterialTheme.typography.labelSmall)
                    recommendations.forEach { item -> MarketplaceRecommendationItemCard(item = item) }
                }
            }
            is DataState.Success -> {
                if (recommendations.isEmpty()) {
                    Text(stringResource(id = com.example.rooster.R.string.recommendations_no_data))
                } else {
                    if (recommendationsState.isFromCache && recommendationsState.isStale) {
                        Badge { Text("!") }
                        Text(stringResource(id = com.example.rooster.R.string.data_possibly_stale), style = MaterialTheme.typography.labelSmall)
                    }
                    recommendations.forEach { item -> MarketplaceRecommendationItemCard(item = item) }
                }
            }
            is DataState.Error -> {
                Text(stringResource(id = com.example.rooster.R.string.error_prefix) + " ${recommendationsState.message ?: recommendationsState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                if (recommendations.isNotEmpty()) {
                     Text(stringResource(id = com.example.rooster.R.string.failed_update_showing_cached), style = MaterialTheme.typography.labelSmall)
                     recommendations.forEach { item -> MarketplaceRecommendationItemCard(item = item) }
                }
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) { 
                    Text(stringResource(id = com.example.rooster.R.string.retry_button)) 
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingSection(
    ordersState: DataState<List<com.rooster.buyerhome.domain.model.OrderItem>>,
    onRetry: () -> Unit
) {
    val orders = ordersState.getUnderlyingData() ?: emptyList()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(id = com.example.rooster.R.string.recent_orders_title), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        when (ordersState) {
            is DataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                if (orders.isNotEmpty()) {
                     Text(stringResource(id = com.example.rooster.R.string.updating_cached_data), style = MaterialTheme.typography.labelSmall)
                    orders.forEach { order -> OrderItemCard(order = order) }
                }
            }
            is DataState.Success -> {
                if (orders.isEmpty()) {
                    Text(stringResource(id = com.example.rooster.R.string.orders_no_recent))
                } else {
                    if (ordersState.isFromCache && ordersState.isStale) {
                        Badge { Text("!") }
                        Text(stringResource(id = com.example.rooster.R.string.data_possibly_stale), style = MaterialTheme.typography.labelSmall)
                    }
                    orders.forEach { order -> OrderItemCard(order = order) }
                }
            }
            is DataState.Error -> {
                Text(stringResource(id = com.example.rooster.R.string.error_prefix) + " ${ordersState.message ?: ordersState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                 if (orders.isNotEmpty()) {
                     Text(stringResource(id = com.example.rooster.R.string.failed_update_showing_cached), style = MaterialTheme.typography.labelSmall)
                     orders.forEach { order -> OrderItemCard(order = order) }
                }
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) { 
                    Text(stringResource(id = com.example.rooster.R.string.retry_button)) 
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceComparisonSection(
    comparisonsState: DataState<List<com.rooster.buyerhome.domain.model.PriceComparisonProduct>>,
    onRetry: () -> Unit
) {
    val comparisons = comparisonsState.getUnderlyingData() ?: emptyList()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(id = com.example.rooster.R.string.price_watch_title), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        when (comparisonsState) {
            is DataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                 if (comparisons.isNotEmpty()) {
                    Text(stringResource(id = com.example.rooster.R.string.updating_cached_data), style = MaterialTheme.typography.labelSmall)
                    comparisons.forEach { item -> PriceComparisonItemCard(item = item) }
                }
            }
            is DataState.Success -> {
                if (comparisons.isEmpty()) {
                    Text(stringResource(id = com.example.rooster.R.string.price_comparison_no_data))
                } else {
                     if (comparisonsState.isFromCache && comparisonsState.isStale) {
                        Badge { Text("!") }
                        Text(stringResource(id = com.example.rooster.R.string.data_possibly_stale), style = MaterialTheme.typography.labelSmall)
                    }
                    comparisons.forEach { item -> PriceComparisonItemCard(item = item) }
                }
            }
            is DataState.Error -> {
                Text(stringResource(id = com.example.rooster.R.string.error_prefix) + " ${comparisonsState.message ?: comparisonsState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                if (comparisons.isNotEmpty()) {
                    Text(stringResource(id = com.example.rooster.R.string.failed_update_showing_cached), style = MaterialTheme.typography.labelSmall)
                    comparisons.forEach { item -> PriceComparisonItemCard(item = item) }
                }
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) { 
                    Text(stringResource(id = com.example.rooster.R.string.retry_button)) 
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierRatingsSection(
    suppliersState: DataState<List<com.rooster.buyerhome.domain.model.SupplierRatingInfo>>,
    onRetry: () -> Unit
) {
    val suppliers = suppliersState.getUnderlyingData() ?: emptyList()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(id = com.example.rooster.R.string.top_suppliers_title), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        when (suppliersState) {
            is DataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                if (suppliers.isNotEmpty()) {
                    Text(stringResource(id = com.example.rooster.R.string.updating_cached_data), style = MaterialTheme.typography.labelSmall)
                    suppliers.forEach { supplier -> SupplierRatingItemCard(supplier = supplier) }
                }
            }
            is DataState.Success -> {
                if (suppliers.isEmpty()) {
                    Text(stringResource(id = com.example.rooster.R.string.suppliers_no_data))
                } else {
                    if (suppliersState.isFromCache && suppliersState.isStale) {
                        Badge { Text("!") }
                        Text(stringResource(id = com.example.rooster.R.string.data_possibly_stale), style = MaterialTheme.typography.labelSmall)
                    }
                    suppliers.forEach { supplier -> SupplierRatingItemCard(supplier = supplier) }
                }
            }
            is DataState.Error -> {
                Text(stringResource(id = com.example.rooster.R.string.error_prefix) + " ${suppliersState.message ?: suppliersState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                 if (suppliers.isNotEmpty()) {
                    Text(stringResource(id = com.example.rooster.R.string.failed_update_showing_cached), style = MaterialTheme.typography.labelSmall)
                    suppliers.forEach { supplier -> SupplierRatingItemCard(supplier = supplier) }
                }
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) { 
                    Text(stringResource(id = com.example.rooster.R.string.retry_button)) 
                }
            }
        }
    }
}

@Composable
fun SupplierRatingItemCard(supplier: com.rooster.buyerhome.domain.model.SupplierRatingInfo) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(supplier.supplierName, style = MaterialTheme.typography.titleSmall)
            Text("Rating: ${String.format("%.1f", supplier.averageRating)}/5.0 (${supplier.numberOfReviews} reviews)", style = MaterialTheme.typography.bodyMedium)
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
            DataState.Success(listOf(com.rooster.buyerhome.domain.model.MarketplaceRecommendationItem("p1", "Preview Chickens", "Seller A", "150/kg", null, "Loc A")))
        )
    },
    orderRepository = object : com.rooster.buyerhome.domain.repository.BuyerOrderRepository {
        override fun getRecentOrders(buyerId: String, count: Int) = kotlinx.coroutines.flow.flowOf(
            DataState.Success(listOf(com.rooster.buyerhome.domain.model.OrderItem("o1", "Preview Eggs", "10dz", "800", java.util.Date(), null, com.rooster.buyerhome.domain.model.OrderStatus.DELIVERED, "Seller B")))
        )
    },
    priceComparisonRepository = object : com.rooster.buyerhome.domain.repository.PriceComparisonRepository {
        override fun getPriceComparisonForProducts(productNames: List<String>) = kotlinx.coroutines.flow.flowOf(
            DataState.Success(listOf(com.rooster.buyerhome.domain.model.PriceComparisonProduct("Preview Broilers", "160/kg", "155/kg", "150/kg (Seller C)")))
        )
    },
    supplierRepository = object : com.rooster.buyerhome.domain.repository.BuyerSupplierRepository {
        override fun getTopRatedSuppliers(count: Int) = kotlinx.coroutines.flow.flowOf(
            DataState.Success(listOf(com.rooster.buyerhome.domain.model.SupplierRatingInfo("s1", "Top Preview Seller", 4.8f, 120, null)))
        )
    },
    connectivityRepository = object : com.example.rooster.core.common.connectivity.ConnectivityRepository {
        override fun observeNetworkStatus() = kotlinx.coroutines.flow.flowOf(com.example.rooster.core.common.connectivity.NetworkStatus.Available)
    }
)