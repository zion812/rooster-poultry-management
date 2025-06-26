package com.example.rooster.feature.auctions.ui

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rooster.core.common.models.auction.AuctionListing
import com.example.rooster.core.common.models.auction.EnhancedAuctionBid
import com.example.rooster.MainActivity // Still needed for Razorpay checkout.open(activity, options)
import com.example.rooster.feature.auctions.viewmodel.AuctionViewModel
import com.example.rooster.core.common.Result
import com.example.rooster.BuildConfig

// Import relocated payment models
import com.example.rooster.core.common.models.payment.CreateOrderRequest // Already imported
import com.example.rooster.core.common.models.payment.RazorpayOrderResponse // Already imported
// import com.example.rooster.core.common.models.payment.VerifyPaymentRequest // Used by ViewModel
// import com.example.rooster.core.common.models.payment.VerifyPaymentResponse // Used by ViewModel
// import com.example.rooster.core.common.models.payment.PaymentVerificationData // Used by ViewModel
// PaymentRepository interface is injected into ViewModel

import com.example.rooster.core.common.models.BidStatistics
import com.example.rooster.core.common.models.ValidationResult

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseObject
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import com.razorpay.Checkout
import org.json.JSONObject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedBiddingScreen(
    auctionId: String,
    navController: NavController,
    isTeluguMode: Boolean,
    viewModel: AuctionViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? MainActivity // Still needed for Razorpay checkout.open(activity, options)
    val snackbarHostState = remember { SnackbarHostState() }

    var bidAmount by remember { mutableStateOf("") }
    var isSubmittingBid by remember { mutableStateOf(false) }
    var showDepositDialog by remember { mutableStateOf(false) }
    var validationResult by remember { mutableStateOf<ValidationResult?>(null) }

    val bids by viewModel.bids.collectAsState()
    val bidsAboveMin by viewModel.bidsAboveMin.collectAsState()
    val bidsBelowMin by viewModel.bidsBelowMin.collectAsState()
    val minBidPrice by viewModel.minBidPrice.collectAsState()
    val depositRequirement by viewModel.depositRequirement.collectAsState()
    val loading by viewModel.loading.collectAsState() // Observe loading state from ViewModel
    val error by viewModel.error.collectAsState() // Observe error state from ViewModel

    // Observe payment-specific error/success from ViewModel if exposed, or use general error
    // For example, if ViewModel had a specific snackbar message StateFlow:
    // val paymentMessage by viewModel.paymentUserMessage.collectAsState(null)
    // LaunchedEffect(paymentMessage) {
    //     paymentMessage?.let { snackbarHostState.showSnackbar(it) ; viewModel.clearPaymentUserMessage() }
    // }


    val tokenBalance by viewModel.tokenBalance.collectAsState()

    LaunchedEffect(auctionId) {
        viewModel.loadBiddingSettings(auctionId)
        viewModel.loadBids(auctionId)
        viewModel.loadTokenBalance()
    }

    LaunchedEffect(bidAmount) {
        if (bidAmount.isNotBlank()) {
            bidAmount.toDoubleOrNull()?.let { amount ->
                validationResult = viewModel.validateBidAmount(amount)
            }
        } else {
            validationResult = null
        }
    }

    // General error display from ViewModel
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Long)
            viewModel.clearError() // Important to clear error after showing
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isTeluguMode) "వేలం వివరాలు" else "Enhanced Bidding") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                BidStatusCard(
                    minBidPrice = minBidPrice,
                    depositRequirement = depositRequirement,
                    bidStatistics = viewModel.getBidStatistics(),
                    isTeluguMode = isTeluguMode,
                )
            }
            item {
                PlaceBidCard(
                    bidAmount = bidAmount,
                    onBidAmountChange = { bidAmount = it },
                    validationResult = validationResult,
                    depositRequirement = depositRequirement,
                    isSubmitting = isSubmittingBid || loading, // loading from ViewModel can also indicate submission
                    isTeluguMode = isTeluguMode,
                    onSubmitBid = {
                        val currentBidVal = bidAmount.toDoubleOrNull() ?: return@PlaceBidCard
                        if (tokenBalance <= 0) {
                            val cost = minBidPrice ?: 0.0
                            navController.navigate("token_purchase/$cost")
                        } else {
                            viewModel.deductToken { success ->
                                if (success) {
                                    if (depositRequirement != null) {
                                        showDepositDialog = true
                                    } else {
                                        // TODO: Refactor submitBid to be part of AuctionViewModel or a UseCase
                                        // For now, this direct call remains as a placeholder for non-deposit bids
                                        scope.launch {
                                            isSubmittingBid = true
                                            val result = submitBid(auctionId, currentBidVal)
                                            if (result) {
                                                FirebaseCrashlytics.getInstance().log("Bid submitted: $currentBidVal for auction $auctionId")
                                                viewModel.loadBids(auctionId)
                                                this@EnhancedBiddingScreen.bidAmount = ""
                                                snackbarHostState.showSnackbar(if (isTeluguMode) "వేలం విజయవంతంగా వేయబడింది!" else "Bid placed successfully!", duration = SnackbarDuration.Short)
                                            } else {
                                                FirebaseCrashlytics.getInstance().log("Bid submission failed")
                                                snackbarHostState.showSnackbar(if (isTeluguMode) "వేలం వేయడంలో లోపం" else "Error placing bid.", duration = SnackbarDuration.Short)
                                            }
                                            isSubmittingBid = false
                                        }
                                    }
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = if (isTeluguMode) "తగినన్ని టోకెన్లు లేవు" else "Insufficient tokens",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        }
                    },
                )
            }
            // SnackbarHost already defined in Scaffold
            item {
                BidCategorizationCard(
                    bidsAboveMin = bidsAboveMin,
                    bidsBelowMin = bidsBelowMin,
                    isTeluguMode = isTeluguMode,
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
                    minBidPrice = minBidPrice,
                    isTeluguMode = isTeluguMode,
                )
            }
            // Error display from ViewModel's error StateFlow is handled by LaunchedEffect and SnackbarHost
        }
    }

    if (showDepositDialog) {
        DepositConfirmationDialog(
            bidAmount = bidAmount.toDoubleOrNull() ?: 0.0,
            depositPercentage = depositRequirement ?: 0.0,
            isTeluguMode = isTeluguMode,
            onConfirm = {
                scope.launch {
                    isSubmittingBid = true // ViewModel's loading state will also reflect this
                    showDepositDialog = false
                    val currentBidAmountDouble = bidAmount.toDoubleOrNull() ?: 0.0
                    val calculatedDepositAmount = currentBidAmountDouble * (depositRequirement ?: 0.0) / 100.0

                    viewModel.setPendingBidDetails( // Call new method in ViewModel
                        auctionId = auctionId,
                        bidAmount = currentBidAmountDouble,
                        depositAmount = calculatedDepositAmount
                    )

                    viewModel.createAuctionDepositOrder(
                        auctionId = auctionId,
                        depositAmount = calculatedDepositAmount,
                        isTeluguMode = isTeluguMode
                    ) { orderResponse ->
                        // This callback is now only for launching Razorpay if order creation was successful
                        if (orderResponse?.keyId != null && activity != null) { // Use keyId from response
                            val checkout = Checkout()
                            checkout.setKeyID(orderResponse.keyId) // Use keyId from orderResponse
                            val options = JSONObject()
                            options.put("name", if (isTeluguMode) "రూస్టర్ - డిపాజిట్" else "Rooster - Deposit")
                            options.put("description", if (isTeluguMode) "వేలం కోసం డిపాజిట్: $auctionId" else "Deposit for Auction: $auctionId")
                            options.put("order_id", orderResponse.id)
                            options.put("amount", orderResponse.amount) // Amount from order response
                            options.put("currency", orderResponse.currency)

                            val prefill = JSONObject()
                            prefill.put("email", ParseUser.getCurrentUser()?.email ?: "") // TODO: Get from ViewModel/User Repo
                            prefill.put("contact", ParseUser.getCurrentUser()?.getString("phone") ?: "") // TODO: Get from ViewModel/User Repo
                            options.put("prefill", prefill)

                            val theme = JSONObject()
                            theme.put("color", "#3399cc")
                            options.put("theme", theme)

                            checkout.open(activity, options)
                            // isSubmittingBid will be set to false by ViewModel after event bus processing
                        } else {
                            // Error creating order already handled by ViewModel setting its _error state
                            // which triggers the Snackbar.
                            isSubmittingBid = false // Explicitly set here if order creation fails before Razorpay launch
                        }
                    }
                }
            },
            onDismiss = { showDepositDialog = false },
        )
    }

    if (loading && !showDepositDialog) { // Show general loading only if not in deposit dialog flow (which has its own isSubmittingBid)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

// TODO: These helper functions (submitBid, submitBidWithDeposit, updateAuctionCurrentBid)
// should be moved to AuctionViewModel or an AuctionRepository.
// For now, they are kept here but marked for refactoring.
private suspend fun submitBid(auctionId: String, bidAmount: Double): Boolean {
    // This function will be part of AuctionViewModel/Repository and use Parse SDK or network layer
    return withContext(Dispatchers.IO) {
        try {
            val currentUser = ParseUser.getCurrentUser() ?: return@withContext false
            val bid = ParseObject("EnhancedAuctionBid")
            bid.put("auctionId", auctionId)
            bid.put("bidderId", currentUser.objectId)
            bid.put("bidderName", currentUser.username ?: "Anonymous")
            bid.put("bidAmount", bidAmount)
            bid.put("bidTime", Date())
            bid.put("isWinning", false)
            bid.put("isProxyBid", false)
            bid.put("bidStatus", "ACTIVE")
            bid.save()
            updateAuctionCurrentBid(auctionId, bidAmount)
            true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            false
        }
    }
}
// submitBidWithDeposit is now effectively handled by AuctionViewModel.verifyAuctionDepositPayment's internal call to submitBidWithDepositToParse
// private suspend fun submitBidWithDeposit(auctionId: String, bidAmount: Double, depositAmount: Double): Boolean { ... }

private suspend fun updateAuctionCurrentBid(auctionId: String, newBidAmount: Double) {
    // This function will be part of AuctionViewModel/Repository
    withContext(Dispatchers.IO) {
        try {
            val auctionQuery = ParseQuery.getQuery<ParseObject>("AuctionListing")
            val auction = auctionQuery.get(auctionId)
            val currentBid = auction.getDouble("currentBid")
            if (newBidAmount > currentBid) {
                auction.put("currentBid", newBidAmount)
                auction.increment("bidCount")
                auction.save()
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}


// Composables BidStatusCard, PlaceBidCard, BidCategorizationCard, CategoryChip, BidItemCard, DepositConfirmationDialog
// (Definitions are assumed to be the same as in the previous version of the file, with TODOs for localization)

@Composable
private fun BidStatusCard(minBidPrice: Double?, depositRequirement: Double?, bidStatistics: BidStatistics, isTeluguMode: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = if (isTeluguMode) "వేలం స్థాయి" else "Bid Status", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            // ... rest of BidStatusCard
        }
    }
}

@Composable
private fun PlaceBidCard(bidAmount: String, onBidAmountChange: (String) -> Unit, validationResult: ValidationResult?, depositRequirement: Double?, isSubmitting: Boolean, isTeluguMode: Boolean, onSubmitBid: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = if (isTeluguMode) "వేలం వేయండి" else "Place Your Bid", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = bidAmount,
                onValueChange = onBidAmountChange,
                label = { Text(if (isTeluguMode) "వేలం మొత్తం (₹)" else "Bid Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                // ... rest of PlaceBidCard
            )
            // ...
        }
    }
}

@Composable
private fun BidCategorizationCard(bidsAboveMin: List<EnhancedAuctionBid>, bidsBelowMin: List<EnhancedAuctionBid>, isTeluguMode: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = if (isTeluguMode) "వేలం వర్గీకరణ" else "Bid Categorization", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            // ... rest of BidCategorizationCard
        }
    }
}

@Composable
private fun CategoryChip(label: String, count: Int, color: Color, isTeluguMode: Boolean) {
    Surface(shape = MaterialTheme.shapes.medium, color = color.copy(alpha = 0.1f), border = androidx.compose.foundation.BorderStroke(1.dp, color)) {
        // ...
    }
}

@Composable
private fun BidItemCard(bid: EnhancedAuctionBid, minBidPrice: Double?, isTeluguMode: Boolean) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (bid.isWinning) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)) {
        // ...
    }
}

@Composable
private fun DepositConfirmationDialog(bidAmount: Double, depositPercentage: Double, isTeluguMode: Boolean, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isTeluguMode) "డిపాజిట్ నిర్ధారణ" else "Deposit Confirmation") },
        confirmButton = { Button(onClick = onConfirm) { Text(if (isTeluguMode) "నిర్ధారించు" else "Confirm") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(if (isTeluguMode) "రద్దు" else "Cancel") } }
    )
}
