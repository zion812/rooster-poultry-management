package com.example.rooster.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.models.AuctionBid
import com.example.rooster.viewmodel.FarmerDashboardViewModel

@Composable
fun BidHistoryDialog(
    productId: String,
    onDismiss: () -> Unit,
    vm: FarmerDashboardViewModel = viewModel(),
) {
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(productId) {
        vm.viewBids(productId)
        isLoading = false
    }
    val bids by vm.bids.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bid History") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (bids.isEmpty()) {
                    Text("No bids placed yet.")
                } else {
                    bids.forEach { bid: AuctionBid ->
                        Text("Bidder: ${bid.bidderId} - ₹${bid.amount}")
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
    )
}
