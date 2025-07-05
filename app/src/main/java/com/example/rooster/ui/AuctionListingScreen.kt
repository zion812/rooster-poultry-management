package com.example.rooster.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.viewmodel.FarmerDashboardViewModel

@Composable
fun AuctionListingScreen(
    onViewBids: (String) -> Unit,
    vm: FarmerDashboardViewModel = viewModel(),
) {
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        vm.loadAuctions()
        isLoading = false
    }
    val auctions by vm.activeAuctions.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Auctions") }) },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .padding(16.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (auctions.isEmpty()) {
                Text("No active auctions.")
            } else {
                auctions.forEach { productId ->
                    ListItem(
                        headlineContent = { Text("Product: $productId") },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable { onViewBids(productId) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
