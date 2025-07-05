package com.example.rooster.ui.auction

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
import com.example.rooster.AuctionWinner
import com.example.rooster.EnhancedAuctionBid
import com.example.rooster.viewmodel.AuctionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AuctionDetailScreen(
    auctionId: String,
    navController: NavController, // Keep for potential back navigation or other actions
    viewModel: AuctionViewModel = viewModel(),
) {
    val bids by viewModel.bids.collectAsState()
    val winner by viewModel.winner.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(auctionId) {
        viewModel.loadBids(auctionId)
        viewModel.loadWinner(auctionId) // Load winner details as well
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
                    text = error ?: "Unknown error loading details",
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
            Text("Auction Winner!", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Winner: ${winner.winnerName}", style = MaterialTheme.typography.bodyLarge)
            Text("Winning Bid: ₹${winner.winningBid}", style = MaterialTheme.typography.bodyMedium)
            Text(
                "Payment Due: ${formatDate(winner.paymentDeadline)}",
                style = MaterialTheme.typography.bodySmall,
            )
            Text("Status: ${winner.paymentStatus}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun BidsList(bids: List<EnhancedAuctionBid>) {
    if (bids.isEmpty()) {
        Text("No bids placed yet.", modifier = Modifier.padding(top = 8.dp))
        return
    }
    Text(
        "Bids:",
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
                    "${bid.bidderName} bid ₹${bid.bidAmount}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text("Time: ${formatDate(bid.bidTime)}", style = MaterialTheme.typography.bodySmall)
                bid.bidMessage?.let {
                    Text(
                        "Message: $it",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(bid.bidStatus.name, style = MaterialTheme.typography.labelSmall)
                if (bid.isWinning) {
                    Text(
                        "Winning",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

private fun formatDate(date: Date?): String {
    if (date == null) return "N/A"
    val format = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
    return format.format(date)
}
