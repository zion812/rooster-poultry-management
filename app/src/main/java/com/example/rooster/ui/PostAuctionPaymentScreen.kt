package com.example.rooster.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rooster.*
import com.example.rooster.services.EnhancedAuctionService
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostAuctionPaymentScreen(
    auctionId: String,
    navController: NavController,
    isTeluguMode: Boolean,
) {
    val scope = rememberCoroutineScope()
    val auctionService = remember { EnhancedAuctionService() }

    // State management
    var winner by remember { mutableStateOf<AuctionWinner?>(null) }
    var timeRemaining by remember {
        mutableStateOf(10 * 60 * 1000L) // 10 minutes in milliseconds
    }
    var paymentStatus by remember { mutableStateOf(AuctionPaymentStatus.PENDING) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var showBackupCascade by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load winner data on first composition
    LaunchedEffect(auctionId) {
        try {
            // Fetch auction winner data
            winner = fetchAuctionWinner(auctionId)
            winner?.let { w ->
                val currentTime = System.currentTimeMillis()
                val deadline = w.paymentDeadline.time
                timeRemaining = (deadline - currentTime).coerceAtLeast(0)
            }
        } catch (e: Exception) {
            errorMessage = "Failed to load auction data: ${e.message}"
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Countdown timer
    LaunchedEffect(timeRemaining) {
        if (timeRemaining > 0 && paymentStatus == AuctionPaymentStatus.PENDING) {
            delay(1000L)
            timeRemaining -= 1000L
        } else if (timeRemaining <= 0 && paymentStatus == AuctionPaymentStatus.PENDING) {
            // Time expired - handle payment failure
            paymentStatus = AuctionPaymentStatus.FAILED
            showBackupCascade = true
            scope.launch {
                auctionService.handlePayment(auctionId, false)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isTeluguMode) "చెల్లింపు పూర్తి చేయండి" else "Complete Payment")
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
            winner?.let { w ->
                // Payment Status Header
                item {
                    PaymentStatusCard(
                        winner = w,
                        timeRemaining = timeRemaining,
                        paymentStatus = paymentStatus,
                        isTeluguMode = isTeluguMode,
                    )
                }

                // Payment Action Card
                if (paymentStatus == AuctionPaymentStatus.PENDING) {
                    item {
                        PaymentActionCard(
                            winner = w,
                            isProcessing = isProcessingPayment,
                            isTeluguMode = isTeluguMode,
                            onCompletePayment = {
                                scope.launch {
                                    isProcessingPayment = true
                                    try {
                                        val result =
                                            auctionService.handlePayment(
                                                auctionId,
                                                true,
                                                "mock_payment_ref",
                                            )
                                        if (result.isSuccess) {
                                            paymentStatus = AuctionPaymentStatus.COMPLETED
                                            FirebaseCrashlytics.getInstance()
                                                .log("Payment completed for auction: $auctionId")
                                        } else {
                                            paymentStatus = AuctionPaymentStatus.FAILED
                                            showBackupCascade = true
                                            errorMessage = "Payment processing failed"
                                        }
                                    } catch (e: Exception) {
                                        paymentStatus = AuctionPaymentStatus.FAILED
                                        errorMessage = "Payment error: ${e.message}"
                                        FirebaseCrashlytics.getInstance().recordException(e)
                                    } finally {
                                        isProcessingPayment = false
                                    }
                                }
                            },
                        )
                    }
                }

                // Backup Cascade Information
                if (showBackupCascade && w.backupBidders.isNotEmpty()) {
                    item {
                        BackupCascadeCard(
                            backupBidders = w.backupBidders,
                            isTeluguMode = isTeluguMode,
                        )
                    }
                }

                // Payment Success Card
                if (paymentStatus == AuctionPaymentStatus.COMPLETED) {
                    item {
                        PaymentSuccessCard(
                            winner = w,
                            isTeluguMode = isTeluguMode,
                        )
                    }
                }
            }

            // Error Display
            errorMessage?.let { error ->
                item {
                    Card(
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
            }
        }
    }
}

@Composable
private fun PaymentStatusCard(
    winner: AuctionWinner,
    timeRemaining: Long,
    paymentStatus: AuctionPaymentStatus,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    when (paymentStatus) {
                        AuctionPaymentStatus.PENDING -> MaterialTheme.colorScheme.primaryContainer
                        AuctionPaymentStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                        AuctionPaymentStatus.FAILED -> MaterialTheme.colorScheme.errorContainer
                        AuctionPaymentStatus.PROCESSING -> MaterialTheme.colorScheme.secondaryContainer
                        AuctionPaymentStatus.EXPIRED -> MaterialTheme.colorScheme.errorContainer
                        AuctionPaymentStatus.CANCELLED -> MaterialTheme.colorScheme.surfaceVariant
                    },
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isTeluguMode) "గెలుపు వివరాలు" else "Winning Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                when (paymentStatus) {
                    AuctionPaymentStatus.PENDING -> {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }

                    AuctionPaymentStatus.COMPLETED -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }

                    AuctionPaymentStatus.FAILED -> {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }

                    AuctionPaymentStatus.PROCESSING -> {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                    }

                    AuctionPaymentStatus.EXPIRED -> {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }

                    AuctionPaymentStatus.CANCELLED -> {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                        )
                    }
                }
            }

            Text(
                text = if (isTeluguMode) "గెలుపు మొత్తం: ₹${winner.winningBid}" else "Winning Amount: ₹${winner.winningBid}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            if (paymentStatus == AuctionPaymentStatus.PENDING && timeRemaining > 0) {
                val minutes = (timeRemaining / 1000 / 60).toInt()
                val seconds = ((timeRemaining / 1000) % 60).toInt()

                Text(
                    text =
                        if (isTeluguMode) {
                            "మిగిలిన సమయం: $minutes:${seconds.toString().padStart(2, '0')}"
                        } else {
                            "Time Remaining: $minutes:${seconds.toString().padStart(2, '0')}"
                        },
                    style = MaterialTheme.typography.bodyLarge,
                    color =
                        if (timeRemaining < 2.minutes.inWholeMilliseconds) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        },
                )

                LinearProgressIndicator(
                    progress = { (timeRemaining.toFloat() / (10 * 60 * 1000f)).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun PaymentActionCard(
    winner: AuctionWinner,
    isProcessing: Boolean,
    isTeluguMode: Boolean,
    onCompletePayment: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = if (isTeluguMode) "చెల్లింపు పూర్తి చేయండి" else "Complete Payment",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text =
                    if (isTeluguMode) {
                        "మీరు వేలం గెలిచారు! దయచేసి 10 నిమిషాలలో చెల్లింపు పూర్తి చేయండి లేకపోతే మీ డిపాజిట్ జప్తు చేయబడుతుంది మరియు తదుపరి అత్యధిక బిడ్డర్‌కు అవకాశం ఇవ్వబడుతుంది."
                    } else {
                        "Congratulations! You won the auction. Please complete payment within 10 minutes or your deposit will be forfeited and the next highest bidder will be offered the item."
                    },
                style = MaterialTheme.typography.bodyMedium,
            )

            Button(
                onClick = onCompletePayment,
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    if (isProcessing) {
                        if (isTeluguMode) "చెల్లిస్తోంది..." else "Processing..."
                    } else {
                        if (isTeluguMode) "₹${winner.winningBid} చెల్లించండి" else "Pay ₹${winner.winningBid}"
                    },
                )
            }
        }
    }
}

@Composable
private fun BackupCascadeCard(
    backupBidders: List<BackupBidder>,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (isTeluguMode) "బ్యాకప్ వేలం వేసేవారు" else "Backup Bidders",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text =
                    if (isTeluguMode) {
                        "చెల్లింపు విఫలమైంది. కింది వేలం వేసేవారికి వారి బిడ్ ధరకు కొనుగోలు అవకాశం అందించబడుతుంది:"
                    } else {
                        "Payment failed. The following bidders will be offered the opportunity to purchase at their bid price:"
                    },
                style = MaterialTheme.typography.bodyMedium,
            )

            backupBidders.take(3).forEachIndexed { index, bidder ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    tonalElevation = 4.dp,
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "${index + 1}. ${bidder.bidderName}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                            )
                            Text(
                                text = "₹${bidder.bidAmount}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }

                        bidder.offerResponse?.let { response ->
                            Text(
                                text =
                                    when (response) {
                                        OfferResponse.PENDING -> if (isTeluguMode) "వేచి ఉంది" else "Pending"
                                        OfferResponse.ACCEPTED -> if (isTeluguMode) "అంగీకరించారు" else "Accepted"
                                        OfferResponse.DECLINED -> if (isTeluguMode) "తిరస్కరించారు" else "Declined"
                                        OfferResponse.EXPIRED -> if (isTeluguMode) "గడువు ముగిసింది" else "Expired"
                                    },
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentSuccessCard(
    winner: AuctionWinner,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp),
            )

            Text(
                text = if (isTeluguMode) "చెల్లింపు విజయవంతం!" else "Payment Successful!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text =
                    if (isTeluguMode) {
                        "మీ చెల్లింపు విజయవంతంగా పూర్తయింది. యాజమాన్యం బదిలీ ప్రక్రియ ప్రారంభించబడింది."
                    } else {
                        "Your payment has been successfully processed. The ownership transfer process has been initiated."
                    },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

// Helper function to fetch auction winner
private suspend fun fetchAuctionWinner(auctionId: String): AuctionWinner? {
    // This would normally fetch from your backend
    // For now, return mock data
    return AuctionWinner(
        auctionId = auctionId,
        winnerId = "user123",
        winnerName = "Winner User",
        winningBid = 2500.0,
        paymentDeadline = Date(System.currentTimeMillis() + 10 * 60 * 1000), // 10 minutes from now
        paymentStatus = AuctionPaymentStatus.PENDING,
        backupBidders =
            listOf(
                BackupBidder(
                    bidderId = "user124",
                    bidderName = "Backup Bidder 1",
                    bidAmount = 2400.0,
                    offerSentTime = null,
                    offerResponse = null,
                    responseDeadline = null,
                ),
                BackupBidder(
                    bidderId = "user125",
                    bidderName = "Backup Bidder 2",
                    bidAmount = 2300.0,
                    offerSentTime = null,
                    offerResponse = null,
                    responseDeadline = null,
                ),
            ),
    )
}
