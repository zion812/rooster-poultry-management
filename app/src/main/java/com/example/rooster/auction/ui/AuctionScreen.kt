package com.example.rooster.auction.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rooster.data.models.Auction
import com.example.rooster.viewmodel.SimpleAuctionViewModel
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

// Main Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuctionScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
    viewModel: SimpleAuctionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val auctions by viewModel.auctions.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isTeluguMode) "వేలం" else "Auctions",
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    IconButton(
                        onClick = onLanguageToggle,
                    ) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = "Toggle Language",
                            tint = Color(0xFFFF5722),
                        )
                    }
                    IconButton(
                        onClick = { navController.navigateUp() },
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            // Error/Success Messages
            uiState.error?.let { error ->
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }

            uiState.successMessage?.let { message ->
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            // Loading State
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isTeluguMode) "వేలాలు లోడ్ అవుతున్నాయి..." else "Loading auctions...",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            } else if (auctions.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Default.Gavel,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isTeluguMode) "ప్రస్తుతం చురుకుగా ఉన్న వేలాలు లేవు" else "No active auctions found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isTeluguMode) "త్వరలో కొత్త వేలాలు వస్తాయి" else "New auctions coming soon",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            } else {
                // Auction List
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(auctions) { auction ->
                        AuctionCard(
                            auction = auction,
                            isTeluguMode = isTeluguMode,
                            onBidClick = { bidAmount ->
                                viewModel.placeBid(auction.id, bidAmount)
                            },
                            isPlacingBid = uiState.isPlacingBid,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuctionCard(
    auction: Auction,
    isTeluguMode: Boolean,
    onBidClick: (Double) -> Unit,
    isPlacingBid: Boolean,
) {
    var showBidDialog by remember { mutableStateOf(false) }
    val timeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Title and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = auction.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${if (isTeluguMode) "జాతి" else "Breed"}: ${auction.breed}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "${if (isTeluguMode) "వయస్సు" else "Age"}: ${auction.age} ${if (isTeluguMode) "వారాలు" else "weeks"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Time Remaining Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color =
                        if (auction.timeRemaining < 60 * 60 * 1000) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        },
                ) {
                    Text(
                        text = formatTimeRemaining(auction.timeRemaining, isTeluguMode),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color =
                            if (auction.timeRemaining < 60 * 60 * 1000) {
                                MaterialTheme.colorScheme.onErrorContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            },
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = auction.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bid Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = if (isTeluguMode) "ప్రస్తుత బిడ్" else "Current Bid",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "₹${String.format("%.0f", auction.currentBid)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF5722),
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isTeluguMode) "అత్యధిక బిడ్దారు" else "Highest Bidder",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text =
                            auction.highestBidder.ifEmpty {
                                if (isTeluguMode) "ఎవరూ లేరు" else "None"
                            },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seller Information
            Text(
                text = "${if (isTeluguMode) "విక్రేత" else "Seller"}: ${auction.sellerName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = { showBidDialog = true },
                    enabled = !auction.isEnded && !isPlacingBid,
                    modifier = Modifier.weight(1f),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF5722),
                        ),
                ) {
                    if (isPlacingBid) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            Icons.Default.Gavel,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isTeluguMode) "బిడ్ చేయండి" else "Place Bid",
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                OutlinedButton(
                    onClick = { /* TODO: View details */ },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTeluguMode) "వివరాలు" else "Details",
                    )
                }
            }
        }
    }

    // Bid Dialog
    if (showBidDialog) {
        BidDialog(
            auction = auction,
            isTeluguMode = isTeluguMode,
            onBidPlaced = { bidAmount ->
                onBidClick(bidAmount)
                showBidDialog = false
            },
            onDismiss = { showBidDialog = false },
        )
    }
}

@Composable
private fun BidDialog(
    auction: Auction,
    isTeluguMode: Boolean,
    onBidPlaced: (Double) -> Unit,
    onDismiss: () -> Unit,
) {
    var bidAmountText by remember { mutableStateOf("") }
    val minBid = auction.currentBid + 100.0 // Minimum increment

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isTeluguMode) "బిడ్ చేయండి" else "Place Bid",
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column {
                Text(
                    text = auction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${if (isTeluguMode) "ప్రస్తుత బిడ్" else "Current Bid"}: ₹${String.format("%.0f", auction.currentBid)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "${if (isTeluguMode) "కనీస బిడ్" else "Minimum Bid"}: ₹${String.format("%.0f", minBid)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = bidAmountText,
                    onValueChange = { bidAmountText = it },
                    label = {
                        Text(if (isTeluguMode) "మీ బిడ్ మొత్తం (₹)" else "Your Bid Amount (₹)")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val bidAmount = bidAmountText.toDoubleOrNull()
                    if (bidAmount != null && bidAmount >= minBid) {
                        onBidPlaced(bidAmount)
                    }
                },
                enabled = bidAmountText.toDoubleOrNull()?.let { it >= minBid } == true,
            ) {
                Text(if (isTeluguMode) "బిడ్ చేయండి" else "Place Bid")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(if (isTeluguMode) "రద్దు చేయండి" else "Cancel")
            }
        },
    )
}

private fun formatTimeRemaining(
    timeRemaining: Long,
    isTeluguMode: Boolean,
): String {
    if (timeRemaining <= 0) {
        return if (isTeluguMode) "ముగిసింది" else "Ended"
    }

    val hours = timeRemaining / (1000 * 60 * 60)
    val minutes = (timeRemaining % (1000 * 60 * 60)) / (1000 * 60)

    return when {
        hours > 0 -> "${hours}${if (isTeluguMode) "గం" else "h"} ${minutes}${if (isTeluguMode) "ని" else "m"}"
        minutes > 0 -> "${minutes}${if (isTeluguMode) "నిమిషాలు" else " min"}"
        else -> if (isTeluguMode) "ముగుస్తోంది" else "Ending soon"
    }
}
