package com.example.rooster.feature.auctions.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rooster.core.common.models.auction.AuctionWinner // Updated import
import com.example.rooster.core.common.models.auction.EnhancedAuctionBid // Updated import
import com.example.rooster.feature.auctions.viewmodel.AuctionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AuctionDetailScreen(
    auctionId: String,
    navController: NavController,
    viewModel: AuctionViewModel = hiltViewModel(), // Use hiltViewModel()
) {
    val bids by viewModel.bids.collectAsState()
    val winner by viewModel.winner.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(auctionId) {
        viewModel.loadBids(auctionId)
        viewModel.loadWinner(auctionId)
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        when {
            loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            error != null -> {
                Text(
                    text = error ?: "Unknown error loading details", // TODO: Localize
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    winner?.let {
                        WinnerSection(winner = it)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    BidsList(bids = bids)
                }
            }
        }
    }
}

@Composable
private fun WinnerSection(winner: AuctionWinner) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Text("Auction Winner!", style = MaterialTheme.typography.titleLarge) // TODO: Localize
            Spacer(modifier = Modifier.height(8.dp))
            Text("Winner: ${winner.winnerName}", style = MaterialTheme.typography.bodyLarge) // TODO: Localize
            Text("Winning Bid: ₹${winner.winningBid}", style = MaterialTheme.typography.bodyMedium) // TODO: Localize
            Text(
                "Payment Due: ${formatDate(winner.paymentDeadline)}", // TODO: Localize
                style = MaterialTheme.typography.bodySmall,
            )
            Text("Status: ${winner.paymentStatus}", style = MaterialTheme.typography.bodySmall) // TODO: Localize
        }
    }
}

@Composable
private fun BidsList(bids: List<EnhancedAuctionBid>) {
    if (bids.isEmpty()) {
        Text("No bids placed yet.", modifier = Modifier.padding(top = 8.dp)) // TODO: Localize
        return
    }
    Text(
        "Bids:", // TODO: Localize
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp),
    )
    LazyColumn {
        items(bids) { bid ->
            BidItem(bid = bid)
        }
    }
}

@Composable
private fun BidItem(bid: EnhancedAuctionBid) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    "${bid.bidderName} bid ₹${bid.bidAmount}", // TODO: Localize
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text("Time: ${formatDate(bid.bidTime)}", style = MaterialTheme.typography.bodySmall) // TODO: Localize
                bid.bidMessage?.let {
                    Text(
                        "Message: $it", // TODO: Localize
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(bid.bidStatus.name, style = MaterialTheme.typography.labelSmall) // This might need localization if status names are user-facing
                if (bid.isWinning) {
                    Text(
                        "Winning", // TODO: Localize
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

private fun formatDate(date: Date?): String {
    if (date == null) return "N/A" // TODO: Localize
    val format = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()) // Consider locale for date format
    return format.format(date)
}
