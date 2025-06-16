package com.example.rooster.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rooster.*
import com.example.rooster.viewmodel.AuctionViewModel
import com.example.rooster.viewmodel.BidStatistics
import com.example.rooster.viewmodel.ValidationResult
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseObject
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedBiddingScreen(
    auctionId: String,
    navController: NavController,
    isTeluguMode: Boolean,
    viewModel: AuctionViewModel = viewModel(),
) {
    val scope = rememberCoroutineScope()

    // UI State
    var bidAmount by remember { mutableStateOf("") }
    var isSubmittingBid by remember { mutableStateOf(false) }
    var showDepositDialog by remember { mutableStateOf(false) }
    var validationResult by remember { mutableStateOf<ValidationResult?>(null) }

    // Collect ViewModel state
    val bids by viewModel.bids.collectAsState()
    val bidsAboveMin by viewModel.bidsAboveMin.collectAsState()
    val bidsBelowMin by viewModel.bidsBelowMin.collectAsState()
    val minBidPrice by viewModel.minBidPrice.collectAsState()
    val depositRequirement by viewModel.depositRequirement.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val tokenBalance by viewModel.tokenBalance.collectAsState()

    // Load bidding settings on first composition
    LaunchedEffect(auctionId) {
        viewModel.loadBiddingSettings(auctionId)
        viewModel.loadBids(auctionId)
        viewModel.loadTokenBalance()
    }

    // Validate bid amount in real-time
    LaunchedEffect(bidAmount) {
        if (bidAmount.isNotBlank()) {
            bidAmount.toDoubleOrNull()?.let { amount ->
                validationResult = viewModel.validateBidAmount(amount)
            }
        } else {
            validationResult = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isTeluguMode) "వేలం వివరాలు" else "Enhanced Bidding")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Current Bid Status Card
            item {
                BidStatusCard(
                    minBidPrice = minBidPrice,
                    depositRequirement = depositRequirement,
                    bidStatistics = viewModel.getBidStatistics(),
                    isTeluguMode = isTeluguMode,
                )
            }

            // Place Bid Card
            item {
                PlaceBidCard(
                    bidAmount = bidAmount,
                    onBidAmountChange = { bidAmount = it },
                    validationResult = validationResult,
                    depositRequirement = depositRequirement,
                    isSubmitting = isSubmittingBid,
                    isTeluguMode = isTeluguMode,
                    onSubmitBid = {
                        val amount = bidAmount.toDoubleOrNull() ?: return@PlaceBidCard
                        // Token guard
                        if (tokenBalance <= 0) {
                            // Navigate to purchase tokens, passing base bid price as product cost
                            val cost = minBidPrice ?: 0.0
                            navController.navigate("token_purchase/$cost")
                        } else {
                            // Deduct one token then proceed
                            viewModel.deductToken { success ->
                                if (success) {
                                    if (depositRequirement != null) {
                                        showDepositDialog = true
                                    } else {
                                        scope.launch {
                                            isSubmittingBid = true
                                            try {
                                                val result = submitBid(auctionId, amount)
                                                if (result) {
                                                    FirebaseCrashlytics.getInstance()
                                                        .log("Bid submitted successfully: $amount for auction $auctionId")

                                                    // Refresh bids list
                                                    viewModel.loadBids(auctionId)

                                                    // Clear bid amount
                                                    bidAmount = ""
                                                } else {
                                                    FirebaseCrashlytics.getInstance()
                                                        .log("Bid submission failed")
                                                }
                                            } catch (e: Exception) {
                                                FirebaseCrashlytics.getInstance().recordException(e)
                                            } finally {
                                                isSubmittingBid = false
                                            }
                                        }
                                    }
                                } else {
                                    // insufficient tokens
                                    // TODO: show snackbar "Insufficient tokens"
                                }
                            }
                        }
                    },
                )
            }

            // Bid Categorization Section
            item {
                BidCategorizationCard(
                    bidsAboveMin = bidsAboveMin,
                    bidsBelowMin = bidsBelowMin,
                    isTeluguMode = isTeluguMode,
                )
            }

            // All Bids List
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
                    minBidPrice = minBidPrice,
                    isTeluguMode = isTeluguMode,
                )
            }

            // Error Display
            error?.let { errorMsg ->
                item {
                    Card(
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                            ),
                    ) {
                        Text(
                            text = errorMsg,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }
        }
    }

    // Deposit Confirmation Dialog
    if (showDepositDialog) {
        DepositConfirmationDialog(
            bidAmount = bidAmount.toDoubleOrNull() ?: 0.0,
            depositPercentage = depositRequirement ?: 0.0,
            isTeluguMode = isTeluguMode,
            onConfirm = {
                scope.launch {
                    isSubmittingBid = true
                    showDepositDialog = false
                    try {
                        // Process deposit payment
                        val depositAmount =
                            bidAmount.toDoubleOrNull() ?: 0.0 * (depositRequirement ?: 0.0) / 100.0
                        val depositResult = processDepositPayment(depositAmount, auctionId)

                        if (depositResult) {
                            // Submit bid with deposit
                            val result =
                                submitBidWithDeposit(
                                    auctionId,
                                    bidAmount.toDoubleOrNull() ?: 0.0,
                                    depositAmount,
                                )
                            if (result) {
                                // Bid submitted successfully
                                FirebaseCrashlytics.getInstance()
                                    .log("Bid with deposit submitted successfully: $bidAmount for auction $auctionId")

                                // Refresh bids list
                                viewModel.loadBids(auctionId)

                                // Clear bid amount
                                bidAmount = ""

                                // Show success message (you can add a snackbar here)
                            } else {
                                // Handle bid submission error
                                FirebaseCrashlytics.getInstance()
                                    .log("Bid submission failed after deposit payment")
                            }
                        } else {
                            // Deposit payment failed
                            FirebaseCrashlytics.getInstance()
                                .log("Deposit payment failed for auction $auctionId")
                        }
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance()
                            .recordException(e)
                    } finally {
                        isSubmittingBid = false
                    }
                }
            },
            onDismiss = {
                showDepositDialog = false
            },
        )
    }

    // Loading overlay
    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}

/**
 * Submits a bid to the Parse backend
 */
private suspend fun submitBid(
    auctionId: String,
    bidAmount: Double,
): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val currentUser = ParseUser.getCurrentUser()
            if (currentUser == null) {
                return@withContext false
            } else {
                // Create new bid object
                val bid = ParseObject("EnhancedAuctionBid")
                bid.put("auctionId", auctionId)
                bid.put("bidderId", currentUser.objectId)
                bid.put("bidderName", currentUser.username ?: "Anonymous")
                bid.put("bidAmount", bidAmount)
                bid.put("bidTime", Date())
                bid.put("isWinning", false) // Will be updated by backend logic
                bid.put("isProxyBid", false)
                bid.put("bidStatus", "ACTIVE")

                // Save to Parse
                bid.save()

                // Update auction with new current bid if this is the highest
                updateAuctionCurrentBid(auctionId, bidAmount)

                return@withContext true
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            return@withContext false
        }
    }
}

/**
 * Submits a bid with deposit to the Parse backend
 */
private suspend fun submitBidWithDeposit(
    auctionId: String,
    bidAmount: Double,
    depositAmount: Double,
): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val currentUser = ParseUser.getCurrentUser()
            if (currentUser == null) {
                return@withContext false
            } else {
                // Create new bid object
                val bid = ParseObject("EnhancedAuctionBid")
                bid.put("auctionId", auctionId)
                bid.put("bidderId", currentUser.objectId)
                bid.put("bidderName", currentUser.username ?: "Anonymous")
                bid.put("bidAmount", bidAmount)
                bid.put("bidTime", Date())
                bid.put("isWinning", false) // Will be updated by backend logic
                bid.put("isProxyBid", false)
                bid.put("bidStatus", "ACTIVE")
                bid.put("depositAmount", depositAmount)

                // Save to Parse
                bid.save()

                // Update auction with new current bid if this is the highest
                updateAuctionCurrentBid(auctionId, bidAmount)

                return@withContext true
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            return@withContext false
        }
    }
}

/**
 * Updates the auction's current bid if the new bid is higher
 */
private suspend fun updateAuctionCurrentBid(
    auctionId: String,
    newBidAmount: Double,
) {
    withContext(Dispatchers.IO) {
        try {
            // Use ParseQuery to fetch the auction object
            val auctionQuery =
                com.parse.ParseQuery.getQuery<com.parse.ParseObject>("AuctionListing")
                    .get(auctionId)
            // parseQuery.get already fetches the object

            val currentBid = auctionQuery.getDouble("currentBid")
            // Update only if new bid is higher
            if (newBidAmount > currentBid) {
                auctionQuery.put("currentBid", newBidAmount)
                auctionQuery.put("bidCount", auctionQuery.getInt("bidCount") + 1)
                auctionQuery.save()
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}

/**
 * Processes the deposit payment
 */
private suspend fun processDepositPayment(
    depositAmount: Double,
    auctionId: String,
): Boolean {
    // TODO: Implement actual payment processing
    return withContext(Dispatchers.IO) {
        // This is a placeholder - you would integrate with your payment system here
        // Return true for success, false for failure
        true
    }
}

@Composable
private fun BidStatusCard(
    minBidPrice: Double?,
    depositRequirement: Double?,
    bidStatistics: BidStatistics,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (isTeluguMode) "వేలం స్థాయి" else "Bid Status",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = if (isTeluguMode) "కనీస వేలం" else "Minimum Bid",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "₹${minBidPrice ?: 0.0}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Column {
                    Text(
                        text = if (isTeluguMode) "అత్యధిక వేలం" else "Highest Bid",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "₹${bidStatistics.highestBid}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = if (isTeluguMode) "మొత్తం వేలాలు" else "Total Bids",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "${bidStatistics.totalBids}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Column {
                    Text(
                        text = if (isTeluguMode) "వేలం వేసేవారు" else "Unique Bidders",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "${bidStatistics.uniqueBidders}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (depositRequirement != null) {
                HorizontalDivider()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text =
                            if (isTeluguMode) {
                                "డిపాజిట్ అవసరం: $depositRequirement%"
                            } else {
                                "Deposit Required: $depositRequirement%"
                            },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceBidCard(
    bidAmount: String,
    onBidAmountChange: (String) -> Unit,
    validationResult: ValidationResult?,
    depositRequirement: Double?,
    isSubmitting: Boolean,
    isTeluguMode: Boolean,
    onSubmitBid: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (isTeluguMode) "వేలం వేయండి" else "Place Your Bid",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            OutlinedTextField(
                value = bidAmount,
                onValueChange = onBidAmountChange,
                label = { Text(if (isTeluguMode) "వేలం మొత్తం (₹)" else "Bid Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                isError = validationResult?.isValid == false,
                supportingText = {
                    validationResult?.errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
            )

            // Deposit Information
            depositRequirement?.let { deposit ->
                val bidAmountDouble = bidAmount.toDoubleOrNull() ?: 0.0
                val depositAmount = bidAmountDouble * (deposit / 100.0)

                Card(
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text =
                                    if (isTeluguMode) {
                                        "డిపాజిట్ అవసరం: ₹${String.format("%.2f", depositAmount)}"
                                    } else {
                                        "Deposit Required: ₹${String.format("%.2f", depositAmount)}"
                                    },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Text(
                                text =
                                    if (isTeluguMode) {
                                        "$deposit% వేలం మొత్తం"
                                    } else {
                                        "$deposit% of bid amount"
                                    },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onSubmitBid,
                enabled = !isSubmitting && validationResult?.isValid == true,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    if (isSubmitting) {
                        if (isTeluguMode) "వేలం వేస్తోంది..." else "Placing Bid..."
                    } else {
                        if (isTeluguMode) "వేలం వేయండి" else "Place Bid"
                    },
                )
            }
        }
    }
}

@Composable
private fun BidCategorizationCard(
    bidsAboveMin: List<EnhancedAuctionBid>,
    bidsBelowMin: List<EnhancedAuctionBid>,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (isTeluguMode) "వేలం వర్గీకరణ" else "Bid Categorization",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                CategoryChip(
                    label = if (isTeluguMode) "కనీసం కంటే ఎక్కువ" else "Above Minimum",
                    count = bidsAboveMin.size,
                    color = Color.Green,
                    isTeluguMode = isTeluguMode,
                )

                CategoryChip(
                    label = if (isTeluguMode) "కనీసం కంటే తక్కువ" else "Below Minimum",
                    count = bidsBelowMin.size,
                    color = MaterialTheme.colorScheme.tertiary,
                    isTeluguMode = isTeluguMode,
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    count: Int,
    color: Color,
    isTeluguMode: Boolean,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = color,
            )
        }
    }
}

@Composable
private fun BidItemCard(
    bid: EnhancedAuctionBid,
    minBidPrice: Double?,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (bid.isWinning) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
            ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = bid.bidderName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "₹${bid.bidAmount}",
                    style = MaterialTheme.typography.titleMedium,
                    color =
                        if (minBidPrice != null && bid.bidAmount >= minBidPrice) {
                            Color.Green
                        } else {
                            MaterialTheme.colorScheme.tertiary
                        },
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
            ) {
                if (bid.isWinning) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primary,
                    ) {
                        Text(
                            text = if (isTeluguMode) "గెలుస్తున్న" else "Winning",
                            modifier = Modifier.padding(4.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }

                bid.depositAmount?.let { deposit ->
                    Text(
                        text =
                            if (isTeluguMode) {
                                "డిపాజిట్: ₹${String.format("%.2f", deposit)}"
                            } else {
                                "Deposit: ₹${String.format("%.2f", deposit)}"
                            },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }
    }
}

@Composable
private fun DepositConfirmationDialog(
    bidAmount: Double,
    depositPercentage: Double,
    isTeluguMode: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val depositAmount = bidAmount * (depositPercentage / 100.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isTeluguMode) "డిపాజిట్ నిర్ధారణ" else "Deposit Confirmation",
            )
        },
        text = {
            Column {
                Text(
                    text =
                        if (isTeluguMode) {
                            "మీ వేలం: ₹${String.format("%.2f", bidAmount)}"
                        } else {
                            "Your Bid: ₹${String.format("%.2f", bidAmount)}"
                        },
                )
                Text(
                    text =
                        if (isTeluguMode) {
                            "అవసరమైన డిపాజిట్: ₹${
                                String.format(
                                    "%.2f",
                                    depositAmount,
                                )
                            } ($depositPercentage%)"
                        } else {
                            "Required Deposit: ₹${
                                String.format(
                                    "%.2f",
                                    depositAmount,
                                )
                            } ($depositPercentage%)"
                        },
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text =
                        if (isTeluguMode) {
                            "మీరు గెలిస్తే డిపాజిట్ తిరిగి వస్తుంది. చెల్లింపు విఫలమైతే డిపాజిట్ జప్తు చేయబడుతుంది."
                        } else {
                            "Deposit will be refunded if you win. Deposit will be forfeited if payment fails."
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(if (isTeluguMode) "నిర్ధారించు" else "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isTeluguMode) "రద్దు" else "Cancel")
            }
        },
    )
}
