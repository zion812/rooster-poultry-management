package com.example.rooster.feature.auctions.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rooster.core.common.models.auction.AuctionListing // Updated import
import com.example.rooster.feature.auctions.viewmodel.AuctionViewModel

@Composable
fun AuctionListScreen(
    navController: NavController,
    viewModel: AuctionViewModel = hiltViewModel(), // Use hiltViewModel() for Hilt injection
) {
    val auctions by viewModel.auctions.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAuctions()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            error != null -> {
                Text(
                    text = error ?: "Unknown error", // TODO: Localize
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            else -> {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                ) {
                    items(auctions) { auction ->
                        AuctionItem(auction = auction) {
                            // TODO: Update navigation route if necessary, e.g. use a route from AuctionNavigation.kt
                            navController.navigate("auction/${auction.auctionId}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuctionItem(
    auction: AuctionListing,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = auction.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Current Bid: ₹${auction.currentBid}", // TODO: Localize
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = timeRemainingText(auction.endTime.time),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

private fun timeRemainingText(endMillis: Long): String {
    val now = System.currentTimeMillis()
    val remMillis = endMillis - now
    val hours = remMillis / (1000 * 60 * 60)
    val minutes = (remMillis / (1000 * 60)) % 60
    return if (remMillis > 0) {
        "Ends in ${hours}h ${minutes}m" // TODO: Localize
    } else {
        "Ended" // TODO: Localize
    }
}
