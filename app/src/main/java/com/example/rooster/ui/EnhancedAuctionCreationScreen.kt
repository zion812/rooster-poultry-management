package com.example.rooster.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rooster.*
import com.example.rooster.NavigationRoute
import com.example.rooster.services.EnhancedAuctionService
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedAuctionCreationScreen(
    fowlId: String,
    navController: NavController,
    isTeluguMode: Boolean,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auctionService = remember { EnhancedAuctionService() }

    // State for auction settings
    var startingPrice by remember { mutableStateOf("") }
    var reservePrice by remember { mutableStateOf("") }
    var customDurationHours by remember { mutableIntStateOf(24) }
    var minimumBidPrice by remember { mutableStateOf("") }
    var requiresBidderDeposit by remember { mutableStateOf(false) }
    var bidderDepositPercentage by remember { mutableDoubleStateOf(10.0) }
    var allowsProxyBidding by remember { mutableStateOf(true) }
    var sellerBidMonitoring by remember { mutableStateOf(BidMonitoringCategory.ALL_BIDS) }
    var autoExtendOnLastMinuteBid by remember { mutableStateOf(true) }
    var extensionMinutes by remember { mutableIntStateOf(5) }
    var buyNowPrice by remember { mutableStateOf("") }
    var allowedBidderTypes by remember { mutableStateOf(setOf(BidderType.ALL_USERS)) }

    // UI state
    var isCreating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isTeluguMode) "వేలం సృష్టించండి" else "Create Enhanced Auction")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                try {
                                    isCreating = true
                                    errorMessage = null

                                    val settings =
                                        createAuctionSettings(
                                            fowlId = fowlId,
                                            startingPrice = startingPrice.toDoubleOrNull() ?: 0.0,
                                            reservePrice = reservePrice.toDoubleOrNull(),
                                            customDurationHours = customDurationHours,
                                            minimumBidPrice = minimumBidPrice.toDoubleOrNull() ?: 0.0,
                                            requiresBidderDeposit = requiresBidderDeposit,
                                            bidderDepositPercentage = bidderDepositPercentage,
                                            allowsProxyBidding = allowsProxyBidding,
                                            sellerBidMonitoring = sellerBidMonitoring,
                                            autoExtendOnLastMinuteBid = autoExtendOnLastMinuteBid,
                                            extensionMinutes = extensionMinutes,
                                            buyNowPrice = buyNowPrice.toDoubleOrNull(),
                                            allowedBidderTypes = allowedBidderTypes.toList(),
                                        )

                                    val result = auctionService.createAuction(settings)
                                    result.fold(
                                        onSuccess = { auctionId ->
                                            successMessage =
                                                if (isTeluguMode) {
                                                    "వేలం విజయవంతంగా సృష్టించబడింది!"
                                                } else {
                                                    "Auction created successfully!"
                                                }
                                            FirebaseCrashlytics.getInstance()
                                                .log("Enhanced auction created: $auctionId")
                                            // Show confirmation dialog
                                            showSuccessDialog = true
                                        },
                                        onFailure = { exception ->
                                            errorMessage =
                                                if (isTeluguMode) {
                                                    "వేలం సృష్టించడంలో లోపం: ${exception.message}"
                                                } else {
                                                    "Error creating auction: ${exception.message}"
                                                }
                                            FirebaseCrashlytics.getInstance()
                                                .recordException(exception)
                                        },
                                    )
                                } catch (e: Exception) {
                                    errorMessage =
                                        if (isTeluguMode) {
                                            "ఊహించని లోపం: ${e.message}"
                                        } else {
                                            "Unexpected error: ${e.message}"
                                        }
                                    FirebaseCrashlytics.getInstance().recordException(e)
                                } finally {
                                    isCreating = false
                                }
                            }
                        },
                        enabled = !isCreating && startingPrice.isNotBlank() && minimumBidPrice.isNotBlank(),
                    ) {
                        if (isCreating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = "Create")
                        }
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
            // Basic Pricing Section
            item {
                BasicPricingCard(
                    startingPrice = startingPrice,
                    reservePrice = reservePrice,
                    minimumBidPrice = minimumBidPrice,
                    buyNowPrice = buyNowPrice,
                    isTeluguMode = isTeluguMode,
                    onStartingPriceChange = { startingPrice = it },
                    onReservePriceChange = { reservePrice = it },
                    onMinimumBidPriceChange = { minimumBidPrice = it },
                    onBuyNowPriceChange = { buyNowPrice = it },
                )
            }

            // Duration and Timing Section
            item {
                DurationTimingCard(
                    customDurationHours = customDurationHours,
                    autoExtendOnLastMinuteBid = autoExtendOnLastMinuteBid,
                    extensionMinutes = extensionMinutes,
                    isTeluguMode = isTeluguMode,
                    onDurationChange = { customDurationHours = it },
                    onAutoExtendChange = { autoExtendOnLastMinuteBid = it },
                    onExtensionMinutesChange = { extensionMinutes = it },
                )
            }

            // Bidder Requirements Section
            item {
                BidderRequirementsCard(
                    requiresBidderDeposit = requiresBidderDeposit,
                    bidderDepositPercentage = bidderDepositPercentage,
                    allowsProxyBidding = allowsProxyBidding,
                    allowedBidderTypes = allowedBidderTypes,
                    isTeluguMode = isTeluguMode,
                    onDepositRequiredChange = { requiresBidderDeposit = it },
                    onDepositPercentageChange = { bidderDepositPercentage = it },
                    onProxyBiddingChange = { allowsProxyBidding = it },
                    onBidderTypesChange = { allowedBidderTypes = it },
                )
            }

            // Seller Monitoring Section
            item {
                SellerMonitoringCard(
                    sellerBidMonitoring = sellerBidMonitoring,
                    isTeluguMode = isTeluguMode,
                    onMonitoringChange = { sellerBidMonitoring = it },
                )
            }

            // Error/Success Messages
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

            successMessage?.let { success ->
                item {
                    Card(
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                    ) {
                        Text(
                            text = success,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                // Navigate to marketplace to show newly listed product
                navController.navigate(NavigationRoute.Marketplace.route) {
                    popUpTo(NavigationRoute.Marketplace.route) {
                        inclusive = false
                    }
                }
            },
            title = {
                Text(text = if (isTeluguMode) "వేలం విజయవంతంగా సృష్టించబడింది!" else "Auction Created Successfully")
            },
            text = {
                Text(text = if (isTeluguMode) "మార్కెట్‌ప్లేస్‌కు వెళ్లండి మరియు వేలం చూడండి" else "Go to Marketplace to view your auction")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        navController.navigate(NavigationRoute.Marketplace.route) {
                            popUpTo(NavigationRoute.Marketplace.route) {
                                inclusive = false
                            }
                        }
                    },
                ) {
                    Text(text = if (isTeluguMode) "మార్కెట్‌ప్లేస్‌కు వెళ్లండి" else "Go to Marketplace")
                }
            },
        )
    }
}

@Composable
private fun BasicPricingCard(
    startingPrice: String,
    reservePrice: String,
    minimumBidPrice: String,
    buyNowPrice: String,
    isTeluguMode: Boolean,
    onStartingPriceChange: (String) -> Unit,
    onReservePriceChange: (String) -> Unit,
    onMinimumBidPriceChange: (String) -> Unit,
    onBuyNowPriceChange: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (isTeluguMode) "ధర నిర్ణయం" else "Pricing Configuration",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            OutlinedTextField(
                value = startingPrice,
                onValueChange = onStartingPriceChange,
                label = { Text(if (isTeluguMode) "ప్రారంభ ధర (₹)" else "Starting Price (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
            )

            OutlinedTextField(
                value = reservePrice,
                onValueChange = onReservePriceChange,
                label = { Text(if (isTeluguMode) "రిజర్వ్ ధర (ఐచ్ఛికం)" else "Reserve Price (Optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Security, contentDescription = null) },
            )

            OutlinedTextField(
                value = minimumBidPrice,
                onValueChange = onMinimumBidPriceChange,
                label = { Text(if (isTeluguMode) "కనీస వేలం ధర (₹)" else "Minimum Bid Price (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.ArrowUpward, contentDescription = null) },
            )

            OutlinedTextField(
                value = buyNowPrice,
                onValueChange = onBuyNowPriceChange,
                label = { Text(if (isTeluguMode) "తక్షణ కొనుగోలు ధర (ఐచ్ఛికం)" else "Buy Now Price (Optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
            )
        }
    }
}

@Composable
private fun DurationTimingCard(
    customDurationHours: Int,
    autoExtendOnLastMinuteBid: Boolean,
    extensionMinutes: Int,
    isTeluguMode: Boolean,
    onDurationChange: (Int) -> Unit,
    onAutoExtendChange: (Boolean) -> Unit,
    onExtensionMinutesChange: (Int) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (isTeluguMode) "వేలం వ్యవధి" else "Auction Duration",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            // Duration Slider
            Text(
                text =
                    if (isTeluguMode) {
                        "వేలం వ్యవధి: $customDurationHours గంటలు"
                    } else {
                        "Auction Duration: $customDurationHours hours"
                    },
            )
            Slider(
                value = customDurationHours.toFloat(),
                onValueChange = { onDurationChange(it.toInt()) },
                valueRange = 1f..168f, // 1 hour to 7 days
                steps = 167,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isTeluguMode) "చివరి నిమిషంలో పొడిగింపు" else "Auto-extend on last minute bid",
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = autoExtendOnLastMinuteBid,
                    onCheckedChange = onAutoExtendChange,
                )
            }

            if (autoExtendOnLastMinuteBid) {
                Text(
                    text =
                        if (isTeluguMode) {
                            "పొడిగింపు వ్యవధి: $extensionMinutes నిమిషాలు"
                        } else {
                            "Extension Duration: $extensionMinutes minutes"
                        },
                )
                Slider(
                    value = extensionMinutes.toFloat(),
                    onValueChange = { onExtensionMinutesChange(it.toInt()) },
                    valueRange = 1f..30f,
                    steps = 29,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun BidderRequirementsCard(
    requiresBidderDeposit: Boolean,
    bidderDepositPercentage: Double,
    allowsProxyBidding: Boolean,
    allowedBidderTypes: Set<BidderType>,
    isTeluguMode: Boolean,
    onDepositRequiredChange: (Boolean) -> Unit,
    onDepositPercentageChange: (Double) -> Unit,
    onProxyBiddingChange: (Boolean) -> Unit,
    onBidderTypesChange: (Set<BidderType>) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (isTeluguMode) "వేలం వేసేవారి అవసరాలు" else "Bidder Requirements",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isTeluguMode) "వేలం డిపాజిట్ అవసరం" else "Require Bidder Deposit",
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = requiresBidderDeposit,
                    onCheckedChange = onDepositRequiredChange,
                )
            }

            if (requiresBidderDeposit) {
                Text(
                    text =
                        if (isTeluguMode) {
                            "డిపాజిట్ శాతం: ${bidderDepositPercentage.toInt()}%"
                        } else {
                            "Deposit Percentage: ${bidderDepositPercentage.toInt()}%"
                        },
                )
                Slider(
                    value = bidderDepositPercentage.toFloat(),
                    onValueChange = { onDepositPercentageChange(it.toDouble()) },
                    valueRange = 5f..25f,
                    steps = 20,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isTeluguMode) "ప్రాక్సీ వేలం అనుమతి" else "Allow Proxy Bidding",
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = allowsProxyBidding,
                    onCheckedChange = onProxyBiddingChange,
                )
            }

            Text(
                text = if (isTeluguMode) "అనుమతించబడిన వేలం వేసేవారు" else "Allowed Bidder Types",
                style = MaterialTheme.typography.titleMedium,
            )

            BidderType.values().forEach { bidderType ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = bidderType in allowedBidderTypes,
                        onCheckedChange = { checked ->
                            onBidderTypesChange(
                                if (checked) {
                                    allowedBidderTypes + bidderType
                                } else {
                                    allowedBidderTypes - bidderType
                                },
                            )
                        },
                    )
                    Text(
                        text = getBidderTypeDisplayName(bidderType, isTeluguMode),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SellerMonitoringCard(
    sellerBidMonitoring: BidMonitoringCategory,
    isTeluguMode: Boolean,
    onMonitoringChange: (BidMonitoringCategory) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (isTeluguMode) "వేలం పర్యవేక్షణ" else "Bid Monitoring",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            BidMonitoringCategory.values().forEach { category ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = sellerBidMonitoring == category,
                        onClick = { onMonitoringChange(category) },
                    )
                    Text(
                        text = getMonitoringCategoryDisplayName(category, isTeluguMode),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}

// Helper functions
private fun createAuctionSettings(
    fowlId: String,
    startingPrice: Double,
    reservePrice: Double?,
    customDurationHours: Int,
    minimumBidPrice: Double,
    requiresBidderDeposit: Boolean,
    bidderDepositPercentage: Double,
    allowsProxyBidding: Boolean,
    sellerBidMonitoring: BidMonitoringCategory,
    autoExtendOnLastMinuteBid: Boolean,
    extensionMinutes: Int,
    buyNowPrice: Double?,
    allowedBidderTypes: List<BidderType>,
): AuctionSettings {
    // For now, get seller ID from current Parse user or use placeholder
    val sellerId = com.parse.ParseUser.getCurrentUser()?.objectId ?: "currentUser"

    return AuctionSettings(
        sellerId = sellerId,
        fowlId = fowlId,
        startingPrice = startingPrice,
        reservePrice = reservePrice,
        customDurationHours = customDurationHours,
        minimumBidPrice = minimumBidPrice,
        requiresBidderDeposit = requiresBidderDeposit,
        bidderDepositPercentage = bidderDepositPercentage,
        allowsProxyBidding = allowsProxyBidding,
        sellerBidMonitoring = sellerBidMonitoring,
        autoExtendOnLastMinuteBid = autoExtendOnLastMinuteBid,
        extensionMinutes = extensionMinutes,
        buyNowPrice = buyNowPrice,
        startTime = Date(), // Start immediately for now
        allowedBidderTypes = allowedBidderTypes,
    )
}

private fun getBidderTypeDisplayName(
    bidderType: BidderType,
    isTeluguMode: Boolean,
): String {
    return when (bidderType) {
        BidderType.ALL_USERS -> if (isTeluguMode) "అన్ని వినియోగదారులు" else "All Users"
        BidderType.VERIFIED_ONLY -> if (isTeluguMode) "ధృవీకరించబడిన వారు మాత్రమే" else "Verified Only"
        BidderType.PREMIUM_ONLY -> if (isTeluguMode) "ప్రీమియం వారు మాత్రమే" else "Premium Only"
        BidderType.REGIONAL_ONLY -> if (isTeluguMode) "ప్రాంతీయ వారు మాత్రమే" else "Regional Only"
        BidderType.PREVIOUS_BUYERS_ONLY -> if (isTeluguMode) "మునుపటి కొనుగోలుదారులు మాత్రమే" else "Previous Buyers Only"
    }
}

private fun getMonitoringCategoryDisplayName(
    category: BidMonitoringCategory,
    isTeluguMode: Boolean,
): String {
    return when (category) {
        BidMonitoringCategory.ALL_BIDS -> if (isTeluguMode) "అన్ని వేలాలు చూడండి" else "Show All Bids"
        BidMonitoringCategory.WINNING_BIDS_ONLY -> if (isTeluguMode) "గెలుస్తున్న వేలాలు మాత్రమే" else "Winning Bids Only"
        BidMonitoringCategory.PRIVATE_BIDDING -> if (isTeluguMode) "వ్యక్తిగత వేలం" else "Private Bidding"
        BidMonitoringCategory.SELLER_NOTIFICATIONS_ONLY -> if (isTeluguMode) "నోటిఫికేషన్లు మాత్రమే" else "Notifications Only"
    }
}
