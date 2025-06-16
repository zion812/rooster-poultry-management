package com.example.rooster.ui.auction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.BidMonitoringCategory
import com.example.rooster.services.BidCategory
import com.example.rooster.services.CategorizedBid
import com.example.rooster.viewmodel.AuctionMonitoringViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Enhanced bidding interface with real-time updates and deposit handling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedBiddingScreen(
    auctionId: String,
    onNavigateBack: () -> Unit,
    viewModel: AuctionMonitoringViewModel = viewModel(),
) {
    var bidAmount by remember { mutableStateOf("") }
    var isProxyBid by remember { mutableStateOf(false) }
    var proxyMaxAmount by remember { mutableStateOf("") }
    var bidMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDepositDialog by remember { mutableStateOf(false) }

    // Collect real-time auction updates
    val auctionUpdate by viewModel.getAuctionUpdates(auctionId).collectAsState(initial = null)

    LaunchedEffect(auctionId) {
        viewModel.loadAuctionDetails(auctionId)
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Live Auction",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Real-time auction status
        auctionUpdate?.let { update ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(
                                text = "Current Bid",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Text(
                                text = "₹${update.currentBid}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formatTimeRemaining(update.timeRemaining),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                            Text(
                                text = "${update.bidCount} bids • ${update.watchers} watching",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bidding Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Place Your Bid",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bid Amount
                OutlinedTextField(
                    value = bidAmount,
                    onValueChange = { bidAmount = it },
                    label = { Text("Bid Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.AttachMoney, contentDescription = null)
                    },
                    supportingText = {
                        auctionUpdate?.let { update ->
                            Text("Minimum next bid: ₹${update.currentBid + 50}")
                        }
                    },
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Proxy Bidding Option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = isProxyBid,
                        onCheckedChange = { isProxyBid = it },
                    )
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Enable Proxy Bidding")
                        Text(
                            text = "Automatically bid up to your maximum amount",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                if (isProxyBid) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = proxyMaxAmount,
                        onValueChange = { proxyMaxAmount = it },
                        label = { Text("Maximum Proxy Amount (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = { Text("We'll bid on your behalf up to this amount") },
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Optional Bid Message
                OutlinedTextField(
                    value = bidMessage,
                    onValueChange = { bidMessage = it },
                    label = { Text("Message (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    supportingText = { Text("Add a message with your bid") },
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Error Message
                errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                            ),
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Place Bid Button
                Button(
                    onClick = {
                        if (validateBidAmount(bidAmount, auctionUpdate?.currentBid ?: 0.0)) {
                            isLoading = true
                            errorMessage = null

                            viewModel.placeBid(
                                auctionId = auctionId,
                                bidAmount = bidAmount.toDouble(),
                                isProxyBid = isProxyBid,
                                proxyMaxAmount = if (isProxyBid) proxyMaxAmount.toDoubleOrNull() else null,
                                bidMessage = bidMessage.takeIf { it.isNotBlank() },
                                onSuccess = { bidId: String ->
                                    isLoading = false
                                    // Clear form after successful bid
                                    bidAmount = ""
                                    bidMessage = ""
                                },
                                onError = { error: String ->
                                    isLoading = false
                                    errorMessage = error
                                },
                                onDepositRequired = { depositAmount: Double ->
                                    isLoading = false
                                    showDepositDialog = true
                                },
                            )
                        } else {
                            errorMessage = "Please enter a valid bid amount"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && bidAmount.isNotBlank(),
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (isProxyBid) "Set Proxy Bid" else "Place Bid",
                    )
                }
            }
        }

        // Deposit Dialog
        if (showDepositDialog) {
            BidDepositDialog(
                depositAmount = 0.0, // This would be calculated based on bid
                onConfirm = {
                    showDepositDialog = false
                    // Handle deposit payment
                },
                onDismiss = {
                    showDepositDialog = false
                },
            )
        }
    }
}

/**
 * Seller's auction monitoring dashboard
 */
@Composable
fun SellerAuctionMonitoringScreen(
    auctionId: String,
    onNavigateBack: () -> Unit,
    viewModel: AuctionMonitoringViewModel = viewModel(),
) {
    var selectedMonitoringType by remember { mutableStateOf(BidMonitoringCategory.ALL_BIDS) }

    // Collect categorized bids
    val categorizedBids by viewModel.getCategorizedBids(auctionId, selectedMonitoringType)
        .collectAsState(initial = emptyList<CategorizedBid>())

    val auctionUpdate by viewModel.getAuctionUpdates(auctionId).collectAsState(initial = null)

    LaunchedEffect(auctionId) {
        viewModel.loadAuctionDetails(auctionId)
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Auction Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Auction Status Card
        auctionUpdate?.let { update ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Your Auction Performance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text("Current Bid")
                            Text(
                                text = "₹${update.currentBid}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Time Remaining")
                            Text(
                                text = formatTimeRemaining(update.timeRemaining),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("${update.bidCount} total bids")
                        Text("${update.watchers} watchers")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Monitoring Type Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Bid Monitoring View",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = selectedMonitoringType == BidMonitoringCategory.ALL_BIDS,
                        onClick = { selectedMonitoringType = BidMonitoringCategory.ALL_BIDS },
                        label = { Text("All Bids") },
                    )
                    FilterChip(
                        selected = selectedMonitoringType == BidMonitoringCategory.WINNING_BIDS_ONLY,
                        onClick = { selectedMonitoringType = BidMonitoringCategory.WINNING_BIDS_ONLY },
                        label = { Text("Winning Only") },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Categorized Bids List
        Text(
            text = "Bid History",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(categorizedBids) { bid: CategorizedBid ->
                BidCategoryCard(bid = bid)
            }
        }
    }
}

@Composable
private fun BidCategoryCard(bid: CategorizedBid) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    when (bid.category) {
                        BidCategory.ABOVE_MINIMUM -> MaterialTheme.colorScheme.primaryContainer
                        BidCategory.BELOW_MINIMUM -> MaterialTheme.colorScheme.errorContainer
                    },
            ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = bid.bidderName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Text(
                    text = "₹${bid.bidAmount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text =
                        when (bid.category) {
                            BidCategory.ABOVE_MINIMUM -> "✓ Above minimum"
                            BidCategory.BELOW_MINIMUM -> "⚠ Below minimum"
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        when (bid.category) {
                            BidCategory.ABOVE_MINIMUM -> Color(0xFF4CAF50)
                            BidCategory.BELOW_MINIMUM -> Color(0xFFF44336)
                        },
                )
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(bid.bidTime),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            if (bid.depositPaid) {
                Text(
                    text = "💰 Deposit paid",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50),
                )
            }
        }
    }
}

@Composable
private fun BidDepositDialog(
    depositAmount: Double,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Deposit Required")
        },
        text = {
            Text("This auction requires a deposit of ₹$depositAmount to place your bid. The deposit will be refunded if you don't win.")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Pay Deposit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

private fun validateBidAmount(
    bidAmount: String,
    currentBid: Double,
): Boolean {
    val amount = bidAmount.toDoubleOrNull()
    return amount != null && amount > currentBid
}

private fun formatTimeRemaining(millis: Long): String {
    val hours = millis / (1000 * 60 * 60)
    val minutes = (millis % (1000 * 60 * 60)) / (1000 * 60)
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "Ending soon"
    }
}
