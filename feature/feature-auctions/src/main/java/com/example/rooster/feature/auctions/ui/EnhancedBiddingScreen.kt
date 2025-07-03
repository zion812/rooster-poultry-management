package com.example.rooster.feature.auctions.ui

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rooster.core.common.models.auction.EnhancedAuctionBid
import com.example.rooster.feature.auctions.viewmodel.AuctionViewModel
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedBiddingScreen(
    auctionId: String,
    navController: NavController,
    isTeluguMode: Boolean = false,
    viewModel: AuctionViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var bidAmount by remember { mutableStateOf("") }
    var isSubmittingBid by remember { mutableStateOf(false) }

    val bids by viewModel.bids.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(auctionId) {
        viewModel.loadBids(auctionId)
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Long)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isTeluguMode) "వేలం వివరాలు" else "Enhanced Bidding") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                PlaceBidCard(
                    bidAmount = bidAmount,
                    onBidAmountChange = { bidAmount = it },
                    isSubmitting = isSubmittingBid || loading,
                    isTeluguMode = isTeluguMode,
                    onSubmitBid = {
                        val currentBidVal = bidAmount.toDoubleOrNull() ?: return@PlaceBidCard
                        scope.launch {
                            isSubmittingBid = true
                            try {
                                viewModel.loadBids(auctionId)
                                bidAmount = ""
                                snackbarHostState.showSnackbar(
                                    if (isTeluguMode) "వేలం విజయవంతంగా వేయబడింది!" else "Bid placed successfully!",
                                    duration = SnackbarDuration.Short
                                )
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar(
                                    if (isTeluguMode) "వేలం వేయడంలో లోపం" else "Error placing bid.",
                                    duration = SnackbarDuration.Short
                                )
                            } finally {
                                isSubmittingBid = false
                            }
                        }
                    },
                )
            }

            item {
                Text(
                    text = if (isTeluguMode) "అన్ని వేలాలు" else "All Bids",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            items(bids) { bid ->
                BidItemCard(
                    bid = bid,
                    isTeluguMode = isTeluguMode,
                )
            }
        }
    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun PlaceBidCard(
    bidAmount: String,
    onBidAmountChange: (String) -> Unit,
    isSubmitting: Boolean,
    isTeluguMode: Boolean,
    onSubmitBid: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isTeluguMode) "వేలం వేయండి" else "Place Your Bid",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = bidAmount,
                onValueChange = onBidAmountChange,
                label = { Text(if (isTeluguMode) "వేలం మొత్తం (₹)" else "Bid Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting
            )

            Button(
                onClick = onSubmitBid,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting && bidAmount.isNotBlank()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text(if (isTeluguMode) "వేలం వేయండి" else "Place Bid")
                }
            }
        }
    }
}

@Composable
private fun BidItemCard(
    bid: EnhancedAuctionBid,
    isTeluguMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (bid.isWinning)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${bid.bidderName} - ₹${bid.bidAmount}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = if (isTeluguMode) "సమయం: ${bid.bidTime}" else "Time: ${bid.bidTime}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (bid.isWinning) {
                Text(
                    text = if (isTeluguMode) "విజేత" else "Winning",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}