package com.example.rooster.ui.auction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.AuctionSettings
import com.example.rooster.BidMonitoringCategory
import com.example.rooster.BidderType
import com.example.rooster.services.EnhancedAuctionService
import com.example.rooster.viewmodel.AuctionCreationViewModel
import java.util.Date

/**
 * Enhanced auction creation screen with seller controls:
 * - Custom duration selection (12h-168h)
 * - Minimum bid price setting
 * - Deposit requirements (5-25%)
 * - Bidder monitoring preferences
 * - Auto-extension settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedAuctionCreationScreen(
    fowlId: String,
    onNavigateBack: () -> Unit,
    onAuctionCreated: (String) -> Unit,
    viewModel: AuctionCreationViewModel = viewModel(),
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Form state
    var startingPrice by remember { mutableStateOf("") }
    var minimumBidPrice by remember { mutableStateOf("") }
    var reservePrice by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableStateOf(24) }
    var requiresDeposit by remember { mutableStateOf(false) }
    var depositPercentage by remember { mutableStateOf(10.0) }
    var allowsProxyBidding by remember { mutableStateOf(true) }
    var autoExtend by remember { mutableStateOf(true) }
    var extensionMinutes by remember { mutableStateOf(5) }
    var buyNowPrice by remember { mutableStateOf("") }
    var monitoringCategory by remember { mutableStateOf(BidMonitoringCategory.ALL_BIDS) }
    var bidderType by remember { mutableStateOf(BidderType.ALL_USERS) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                text = "Create Auction",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Basic Pricing Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Auction Pricing",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Starting Price
                OutlinedTextField(
                    value = startingPrice,
                    onValueChange = { startingPrice = it },
                    label = { Text("Starting Price (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("Minimum amount to start bidding") },
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Minimum Bid Price (New Feature)
                OutlinedTextField(
                    value = minimumBidPrice,
                    onValueChange = { minimumBidPrice = it },
                    label = { Text("Minimum Bid Price (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("Lowest acceptable bid amount") },
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Reserve Price (Optional)
                OutlinedTextField(
                    value = reservePrice,
                    onValueChange = { reservePrice = it },
                    label = { Text("Reserve Price (₹) - Optional") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("Hidden minimum price for sale") },
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Buy Now Price (Optional)
                OutlinedTextField(
                    value = buyNowPrice,
                    onValueChange = { buyNowPrice = it },
                    label = { Text("Buy It Now Price (₹) - Optional") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("Allow immediate purchase at this price") },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Duration Selection (Enhanced Feature)
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Auction Duration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Duration Options
                Column(modifier = Modifier.selectableGroup()) {
                    EnhancedAuctionService.AUCTION_DURATION_PRESETS.forEach { hours ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = selectedDuration == hours,
                                onClick = { selectedDuration = hours },
                            )
                            Text(
                                text =
                                    when {
                                        hours < 24 -> "$hours hours"
                                        hours == 24 -> "1 day"
                                        hours < 168 -> "${hours / 24} days"
                                        else -> "1 week"
                                    },
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Auto-extend Option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = autoExtend,
                        onCheckedChange = { autoExtend = it },
                    )
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Auto-extend on last minute bids")
                        Text(
                            text = "Extend auction by $extensionMinutes minutes if bid placed in final minute",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Deposit Requirements (New Feature)
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Bidder Requirements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Require Deposit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = requiresDeposit,
                        onCheckedChange = { requiresDeposit = it },
                    )
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Require bidder deposit")
                        Text(
                            text = "Prevents frivolous bidding and ensures serious buyers",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                if (requiresDeposit) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Deposit Percentage: ${depositPercentage.toInt()}%")
                    Slider(
                        value = depositPercentage.toFloat(),
                        onValueChange = { depositPercentage = it.toDouble() },
                        valueRange =
                            EnhancedAuctionService.MIN_DEPOSIT_PERCENTAGE.toFloat()..EnhancedAuctionService.MAX_DEPOSIT_PERCENTAGE.toFloat(),
                        steps = 4,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = "Bidders pay ${depositPercentage.toInt()}% of their bid as deposit",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Proxy Bidding
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = allowsProxyBidding,
                        onCheckedChange = { allowsProxyBidding = it },
                    )
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Allow proxy bidding")
                        Text(
                            text = "Bidders can set maximum amount for automatic bidding",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Monitoring Preferences (New Feature)
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Bid Monitoring",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Monitoring Category Selection
                Column(modifier = Modifier.selectableGroup()) {
                    BidMonitoringCategory.values().forEach { category ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = monitoringCategory == category,
                                onClick = { monitoringCategory = category },
                            )
                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(
                                    text =
                                        when (category) {
                                            BidMonitoringCategory.ALL_BIDS -> "See all bids"
                                            BidMonitoringCategory.WINNING_BIDS_ONLY -> "Winning bids only"
                                            BidMonitoringCategory.PRIVATE_BIDDING -> "Private bidding"
                                            BidMonitoringCategory.SELLER_NOTIFICATIONS_ONLY -> "Notifications only"
                                        },
                                )
                                Text(
                                    text =
                                        when (category) {
                                            BidMonitoringCategory.ALL_BIDS -> "View all bidder details and amounts"
                                            BidMonitoringCategory.WINNING_BIDS_ONLY -> "Only see current winning bid"
                                            BidMonitoringCategory.PRIVATE_BIDDING -> "Anonymous bidding for privacy"
                                            BidMonitoringCategory.SELLER_NOTIFICATIONS_ONLY -> "Get notifications without live monitoring"
                                        },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Create Auction Button
        Button(
            onClick = {
                // Validate and create auction
                if (validateForm(startingPrice, minimumBidPrice)) {
                    isLoading = true
                    errorMessage = null

                    val settings =
                        AuctionSettings(
                            sellerId = "", // Will be set by service
                            fowlId = fowlId,
                            startingPrice = startingPrice.toDouble(),
                            reservePrice = reservePrice.toDoubleOrNull(),
                            customDurationHours = selectedDuration,
                            minimumBidPrice = minimumBidPrice.toDouble(),
                            requiresBidderDeposit = requiresDeposit,
                            bidderDepositPercentage = depositPercentage,
                            allowsProxyBidding = allowsProxyBidding,
                            sellerBidMonitoring = monitoringCategory,
                            autoExtendOnLastMinuteBid = autoExtend,
                            extensionMinutes = extensionMinutes,
                            buyNowPrice = buyNowPrice.toDoubleOrNull(),
                            startTime = Date(),
                            allowedBidderTypes = listOf(bidderType),
                        )

                    // Create auction using ViewModel
                    viewModel.createAuction(
                        settings = settings,
                        onSuccess = { auctionId: String ->
                            isLoading = false
                            onAuctionCreated(auctionId)
                        },
                        onError = { error: String ->
                            isLoading = false
                            errorMessage = error
                        },
                    )
                } else {
                    errorMessage = "Please fill in all required fields with valid values"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Create Enhanced Auction")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

private fun validateForm(
    startingPrice: String,
    minimumBidPrice: String,
): Boolean {
    val starting = startingPrice.toDoubleOrNull()
    val minimum = minimumBidPrice.toDoubleOrNull()

    return starting != null && starting > 0 &&
        minimum != null && minimum > 0 &&
        minimum >= starting
}
