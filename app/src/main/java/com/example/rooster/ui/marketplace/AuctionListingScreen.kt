// Add at top
@file:SuppressLint("NewApi")

package com.example.rooster.ui.marketplace

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rooster.SafeListing
import com.example.rooster.data.Bid
import com.example.rooster.data.MarketplaceRepository
import com.example.rooster.viewmodels.MarketplaceViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuctionListingScreen(
    listingId: String,
    navController: NavController,
    isTeluguMode: Boolean,
    viewModel: MarketplaceViewModel = viewModel(),
) {
    val context = LocalContext.current
    val repository = remember { MarketplaceRepository() }
    val scope = rememberCoroutineScope()

    // State
    var listing by remember { mutableStateOf<SafeListing?>(null) }
    var bids by remember { mutableStateOf<List<Bid>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoadingBids by remember { mutableStateOf(false) }
    var showBidDialog by remember { mutableStateOf(false) }
    var bidAmount by remember { mutableStateOf("") }
    var bidMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPlacingBid by remember { mutableStateOf(false) }

    // Load listing and bids
    LaunchedEffect(listingId) {
        scope.launch {
            try {
                isLoading = true
                listing = repository.fetchListingById(listingId)

                isLoadingBids = true
                bids = repository.fetchBidsForListing(listingId)
                isLoadingBids = false

                FirebaseCrashlytics.getInstance().log("Auction listing loaded: $listingId")
            } catch (e: Exception) {
                errorMessage = "Failed to load listing: ${e.message}"
                FirebaseCrashlytics.getInstance().recordException(e)
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isTeluguMode) "వేలం లిస్టింగ్" else "Auction Listing")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            if (listing != null && !isLoading) {
                FloatingActionButton(
                    onClick = { showBidDialog = true },
                ) {
                    Icon(Icons.Default.Gavel, contentDescription = "Place Bid")
                }
            }
        },
    ) { paddingValues ->

        if (isLoading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (listing == null) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = if (isTeluguMode) "లిస్టింగ్ దొరకలేదు" else "Listing not found",
                        style = MaterialTheme.typography.headlineSmall,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { navController.navigateUp() }) {
                        Text(if (isTeluguMode) "వెనుకకు వెళ్ళు" else "Go Back")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                // Listing Details Card
                item {
                    ListingDetailsCard(
                        listing = listing!!,
                        isTeluguMode = isTeluguMode,
                    )
                }

                // Current Highest Bid
                item {
                    HighestBidCard(
                        bids = bids,
                        isTeluguMode = isTeluguMode,
                    )
                }

                // Bidding History
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = if (isTeluguMode) "వేలం చరిత్ర" else "Bidding History",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                )

                                if (isLoadingBids) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (bids.isEmpty()) {
                                Text(
                                    text = if (isTeluguMode) "ఇంకా వేలం వేయలేదు" else "No bids yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                // Bid Items
                items(bids) { bid ->
                    BidCard(
                        bid = bid,
                        isTeluguMode = isTeluguMode,
                    )
                }

                // Error Message
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

    // Bid Dialog
    if (showBidDialog) {
        PlaceBidDialog(
            currentHighestBid =
                bids.maxByOrNull { it.bidAmount }?.bidAmount ?: listing?.price
                    ?: 0.0,
            isTeluguMode = isTeluguMode,
            bidAmount = bidAmount,
            bidMessage = bidMessage,
            isPlacingBid = isPlacingBid,
            onBidAmountChange = { bidAmount = it },
            onBidMessageChange = { bidMessage = it },
            onDismiss = {
                showBidDialog = false
                bidAmount = ""
                bidMessage = ""
            },
            onConfirm = {
                val amount = bidAmount.toDoubleOrNull()
                if (amount != null && amount > 0) {
                    scope.launch {
                        try {
                            isPlacingBid = true

                            repository.placeBid(
                                listingId = listingId,
                                bidAmount = amount,
                                bidderMessage = bidMessage,
                            )

                            // Refresh bids
                            bids = repository.fetchBidsForListing(listingId)

                            showBidDialog = false
                            bidAmount = ""
                            bidMessage = ""

                            FirebaseCrashlytics.getInstance()
                                .log("Bid placed: $amount on $listingId")
                        } catch (e: Exception) {
                            errorMessage = "Failed to place bid: ${e.message}"
                            FirebaseCrashlytics.getInstance().recordException(e)
                        } finally {
                            isPlacingBid = false
                        }
                    }
                } else {
                    errorMessage =
                        if (isTeluguMode) "సరైన వేలం మొత్తాన్ని నమోదు చేయండి" else "Please enter a valid bid amount"
                }
            },
        )
    }
}

@Composable
private fun ListingDetailsCard(
    listing: SafeListing,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = listing.breed,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = if (isTeluguMode) "వయస్సు: ${listing.age} వారాలు" else "Age: ${listing.age} weeks",
                    style = MaterialTheme.typography.bodyLarge,
                )

                Text(
                    text = "₹${listing.price}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isTeluguMode) "విక్రేత: ${listing.owner}" else "Seller: ${listing.owner}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (listing.fatherId != "N/A" || listing.motherId != "N/A") {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isTeluguMode) "వంశావళి: ${listing.fatherId} x ${listing.motherId}" else "Lineage: ${listing.fatherId} x ${listing.motherId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun HighestBidCard(
    bids: List<Bid>,
    isTeluguMode: Boolean,
) {
    val highestBid = bids.maxByOrNull { it.bidAmount }

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
        ) {
            Text(
                text = if (isTeluguMode) "అత్యధిక వేలం" else "Highest Bid",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (highestBid != null) {
                Text(
                    text = "₹${highestBid.bidAmount}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )

                Text(
                    text = if (isTeluguMode) "ద్వారా ${highestBid.bidder}" else "by ${highestBid.bidder}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            } else {
                Text(
                    text = if (isTeluguMode) "ఇంకా వేలం లేదు" else "No bids yet",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun BidCard(
    bid: Bid,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = bid.bidder,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )

                    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                    Text(
                        text = dateFormat.format(bid.bidDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    if (bid.message.isNotBlank()) {
                        Text(
                            text = bid.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Text(
                text = "₹${bid.bidAmount}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun PlaceBidDialog(
    currentHighestBid: Double,
    isTeluguMode: Boolean,
    bidAmount: String,
    bidMessage: String,
    isPlacingBid: Boolean,
    onBidAmountChange: (String) -> Unit,
    onBidMessageChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isTeluguMode) "వేలం వేయండి" else "Place Bid")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text =
                        if (isTeluguMode) {
                            "ప్రస్తుత అత్యధిక వేలం: ₹$currentHighestBid"
                        } else {
                            "Current highest bid: ₹$currentHighestBid"
                        },
                    style = MaterialTheme.typography.bodyMedium,
                )

                OutlinedTextField(
                    value = bidAmount,
                    onValueChange = onBidAmountChange,
                    label = { Text(if (isTeluguMode) "మీ వేలం (₹)" else "Your bid (₹)") },
                    keyboardOptions =
                        androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                        ),
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = bidMessage,
                    onValueChange = onBidMessageChange,
                    label = { Text(if (isTeluguMode) "సందేశం (ఐచ్ఛికం)" else "Message (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isPlacingBid && bidAmount.isNotBlank(),
            ) {
                if (isPlacingBid) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(if (isTeluguMode) "వేలం వేయండి" else "Place Bid")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isTeluguMode) "రద్దు" else "Cancel")
            }
        },
    )
}
