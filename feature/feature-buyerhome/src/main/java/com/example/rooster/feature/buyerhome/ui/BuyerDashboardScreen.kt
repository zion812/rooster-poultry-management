package com.example.rooster.feature.buyerhome.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rooster.core.common.R // Assuming R class from core-common
import com.example.rooster.core.data.model.FeaturedProductTeaser
import com.example.rooster.core.data.model.OrderTeaser
import com.example.rooster.core.data.model.ProductCategoryTeaser
import com.example.rooster.ui.theme.RoosterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerDashboardScreen(
    viewModel: BuyerHomeViewModel = hiltViewModel(),
    // Add navigation lambdas as needed, e.g.:
    // onNavigateToProductDetails: (productId: String) -> Unit,
    // onNavigateToOrderDetails: (orderId: String) -> Unit,
    // onNavigateToCategory: (categoryId: String) -> Unit,
    // onNavigateToMarketplace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.buyer_dashboard_title)) } // New String
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(
                    text = uiState.errorMessage ?: stringResource(id = R.string.error_generic_loading_failed),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            BuyerDashboardContent(
                uiState = uiState,
                // Pass action lambdas here
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun BuyerDashboardContent(
    uiState: BuyerHomeUiState,
    modifier: Modifier = Modifier
    // Add action lambdas
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Welcome Message & Personalized Message
        Text(
            text = stringResource(id = R.string.buyer_dashboard_welcome, uiState.userName), // New String
            style = MaterialTheme.typography.headlineSmall
        )
        uiState.personalizedMessage?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }

        // Featured Products Section
        if (uiState.featuredProducts.isNotEmpty()) {
            DashboardSection(title = stringResource(id = R.string.buyer_dashboard_featured_products)) { // New String
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(uiState.featuredProducts) { product ->
                        ProductTeaserCard(product = product, onClick = { /* TODO: Navigate to product details */ })
                    }
                }
            }
        }

        // Browse Categories Section
        if (uiState.browseCategories.isNotEmpty()) {
            DashboardSection(title = stringResource(id = R.string.buyer_dashboard_browse_categories)) { // New String
                 LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(uiState.browseCategories) { category ->
                        CategoryTeaserChip(category = category, onClick = { /* TODO: Navigate to category/marketplace with filter */ })
                    }
                }
            }
        }

        // Recent Orders Section
        if (uiState.recentOrders.isNotEmpty()) {
            DashboardSection(title = stringResource(id = R.string.buyer_dashboard_recent_orders)) { // New String
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.recentOrders.take(3).forEach { order -> // Show a few recent orders
                        OrderTeaserItem(order = order, onClick = { /* TODO: Navigate to order details */ })
                        if (uiState.recentOrders.indexOf(order) < 2 && uiState.recentOrders.size > 1) { // Don't add divider after last item shown
                           HorizontalDivider()
                        }
                    }
                    if (uiState.recentOrders.size > 3) {
                        TextButton(onClick = { /* TODO: Navigate to full order history */ }, modifier = Modifier.align(Alignment.End)) {
                            Text(stringResource(id = R.string.buyer_dashboard_view_all_orders)) // New String
                        }
                    }
                }
            }
        }

        // Call to Action to visit marketplace
        OutlinedButton(onClick = { /* TODO: Navigate to Marketplace list */ }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Storefront, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text(stringResource(id = R.string.buyer_dashboard_goto_marketplace)) // New String
        }
    }
}

@Composable
fun DashboardSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductTeaserCard(product: FeaturedProductTeaser, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(180.dp) // Fixed width for horizontal scroll
    ) {
        Column {
            // Placeholder for an image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Image, contentDescription = product.name, tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Column(Modifier.padding(8.dp)) {
                Text(text = product.name, style = MaterialTheme.typography.titleSmall, maxLines = 2)
                Text(text = product.price, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryTeaserChip(category: ProductCategoryTeaser, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(category.name) },
        leadingIcon = {
            // Placeholder for category icon
            Icon(Icons.Filled.Category, contentDescription = category.name)
        }
    )
}

@Composable
fun OrderTeaserItem(order: OrderTeaser, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = stringResource(id = R.string.order_id_prefix, order.id), style = MaterialTheme.typography.labelLarge) // New String "Order #%1$s"
            Text(text = stringResource(id = R.string.order_date_prefix, order.orderDate), style = MaterialTheme.typography.bodySmall) // New String "Date: %1$s"
        }
        Text(text = order.status, style = MaterialTheme.typography.bodyMedium)
        Text(text = order.totalAmount, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 16.dp))
    }
}


@Preview(showBackground = true, name = "Buyer Dashboard - Populated")
@Composable
fun BuyerDashboardScreenPreviewPopulated() {
    RoosterTheme {
        BuyerDashboardContent(
            uiState = BuyerDashboardSummary(
                userName = "Priya S.",
                featuredProducts = listOf(
                    FeaturedProductTeaser("p1", "Premium Rooster Feed", null, "₹1200"),
                    FeaturedProductTeaser("p2", "Day-old Chicks (Aseel)", null, "₹150/each")
                ),
                recentOrders = listOf(
                    OrderTeaser("o1", "Dec 20, 2023", "Delivered", "₹1500"),
                    OrderTeaser("o2", "Dec 18, 2023", "Shipped", "₹800")
                ),
                browseCategories = listOf(
                    ProductCategoryTeaser("c1", "Live Birds", null),
                    ProductCategoryTeaser("c2", "Feed", null)
                ),
                personalizedMessage = "Special offer: 10% off on all feed!"
            ).let { summary -> // Convert BuyerDashboardSummary to BuyerHomeUiState for preview
                 BuyerHomeUiState(
                    isLoading = false,
                    userName = summary.userName,
                    featuredProducts = summary.featuredProducts,
                    recentOrders = summary.recentOrders,
                    browseCategories = summary.browseCategories,
                    personalizedMessage = summary.personalizedMessage
                )
            }
        )
    }
}

// New Strings:
// R.string.buyer_dashboard_title ("Buyer Dashboard")
// R.string.buyer_dashboard_welcome ("Hello, %1$s")
// R.string.buyer_dashboard_featured_products ("Featured Products")
// R.string.buyer_dashboard_browse_categories ("Browse Categories")
// R.string.buyer_dashboard_recent_orders ("Recent Orders")
// R.string.buyer_dashboard_view_all_orders ("View All Orders")
// R.string.buyer_dashboard_goto_marketplace ("Explore Marketplace")
// R.string.order_id_prefix ("Order #%1$s")
// R.string.order_date_prefix ("Date: %1$s")
```

**Notes on `BuyerDashboardScreen.kt`:**
*   Uses `Scaffold` with a `TopAppBar`.
*   Displays loading and error states from the `BuyerHomeUiState`.
*   `BuyerDashboardContent` composable lays out the sections: Welcome message, Personalized message, Featured Products (horizontal scroll), Browse Categories (chips in horizontal scroll), Recent Orders (vertical list), and a button to go to the full marketplace.
*   Helper composables `DashboardSection`, `ProductTeaserCard`, `CategoryTeaserChip`, `OrderTeaserItem` are created for modularity.
*   Placeholder TODOs for navigation actions.
*   A preview `BuyerDashboardScreenPreviewPopulated` is included.
*   Identified new string resources needed.

Next, I will set up navigation for this screen in `RoosterNavHost.kt` and add string resources. Then, the unit test for `BuyerHomeViewModel`.
