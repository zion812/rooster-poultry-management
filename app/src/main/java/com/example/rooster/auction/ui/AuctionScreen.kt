package com.example.rooster.auction.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rooster.R
import com.example.rooster.services.optimized.ChatMessage
import com.example.rooster.services.optimized.NetworkQuality
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuctionScreen(
    auctionId: String,
    modifier: Modifier = Modifier,
    viewModel: AuctionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val biddingState by viewModel.state.collectAsStateWithLifecycle()
    val collaborationState by viewModel.collaborationState.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    var bidAmount by remember { mutableStateOf("") }
    var chatMessage by remember { mutableStateOf("") }
    var showChat by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with participant count
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.auction_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ID: $auctionId",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )

                // Participant count and chat toggle
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { },
                        label = { Text("${collaborationState.participantCount} లైవ్") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )

                    AssistChip(
                        onClick = { showChat = !showChat },
                        label = { Text(if (showChat) "చాట్ దాచు" else "చాట్ చూపు") },
                        leadingIcon = { Icon(Icons.Default.Chat, contentDescription = null) }
                    )
                }
            }
        }

        // Current Bid Display
        when (val state = biddingState) {
            is BiddingState.Active -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.current_bid, state.update.amount),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Bidder: ${state.update.bidderId}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${state.participants} మంది పాల్గొంటున్నారు",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            is BiddingState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = stringResource(
                            R.string.bidding_error,
                            state.throwable.message ?: "Unknown error"
                        ),
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }

            BiddingState.Idle -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = stringResource(R.string.connecting),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            BiddingState.BidPlaced -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(
                        text = stringResource(R.string.bid_success),
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            is BiddingState.OfflineMode -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800).copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ఆఫ్‌లైన్ మోడ్ - కనెక్షన్ లేదు",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800)
                        )

                        state.lastKnownBid?.let { lastBid ->
                            Text(
                                text = "చివరిగా తెలిసిన బిడ్: ₹${lastBid.amount.toInt()}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Chat Section (expandable)
        if (showChat) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "లైవ్ చాట్",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Chat messages
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        state = rememberLazyListState()
                    ) {
                        items(chatMessages) { message ->
                            ChatMessageItem(message = message)
                        }
                    }

                    // Chat input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatMessage,
                            onValueChange = { chatMessage = it },
                            placeholder = { Text("సందేశం రాయండి...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        IconButton(
                            onClick = {
                                if (chatMessage.isNotBlank()) {
                                    viewModel.sendChatMessage(chatMessage)
                                    chatMessage = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send")
                        }
                    }
                }
            }
        }

        // Quick Chat Actions
        if (!showChat && collaborationState.isConnected) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "త్వరిత సందేశాలు",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = { viewModel.sendInterestMessage() },
                            label = { Text("ఆసక్తి ఉంది") }
                        )

                        AssistChip(
                            onClick = {
                                bidAmount.toDoubleOrNull()?.let { amount ->
                                    viewModel.sendQuickBidMessage(amount)
                                }
                            },
                            label = { Text("బిడ్ వేస్తున్నాను") },
                            enabled = bidAmount.isNotBlank()
                        )
                    }
                }
            }
        }

        // Bid Input Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.place_bid),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = bidAmount,
                    onValueChange = { bidAmount = it },
                    label = { Text("Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Button(
                    onClick = {
                        bidAmount.toDoubleOrNull()?.let { amount ->
                            viewModel.placeBid(amount)
                            bidAmount = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = bidAmount.isNotBlank() && biddingState != BiddingState.BidPlaced,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.bid_button),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Connection Status with rural optimization indicators
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    collaborationState.isConnected && collaborationState.networkQuality == NetworkQuality.GOOD -> Color(
                        0xFF4CAF50
                    ).copy(alpha = 0.1f)

                    collaborationState.isConnected && collaborationState.networkQuality == NetworkQuality.FAIR -> Color(
                        0xFFFFC107
                    ).copy(alpha = 0.1f)

                    collaborationState.networkQuality == NetworkQuality.POOR -> Color(0xFFFF9800).copy(
                        alpha = 0.1f
                    )

                    biddingState is BiddingState.Error -> MaterialTheme.colorScheme.errorContainer.copy(
                        alpha = 0.3f
                    )
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Main connection status
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status indicator
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = when {
                                    collaborationState.isConnected && collaborationState.networkQuality == NetworkQuality.GOOD -> Color(
                                        0xFF4CAF50
                                    )

                                    collaborationState.isConnected && collaborationState.networkQuality == NetworkQuality.FAIR -> Color(
                                        0xFFFFC107
                                    )

                                    collaborationState.networkQuality == NetworkQuality.POOR -> Color(
                                        0xFFFF9800
                                    )

                                    biddingState is BiddingState.Error -> MaterialTheme.colorScheme.error
                                    else -> Color.Gray
                                },
                                shape = RoundedCornerShape(50)
                            )
                    )

                    Text(
                        text = when {
                            collaborationState.isConnected && collaborationState.networkQuality == NetworkQuality.GOOD -> "అద్భుతమైన కనెక్షన్ - పూర్తి సేవలు"
                            collaborationState.isConnected && collaborationState.networkQuality == NetworkQuality.FAIR -> "సాధారణ కనెక్షన్ - ఆప్టిమైజ్డ్ మోడ్"
                            collaborationState.networkQuality == NetworkQuality.POOR -> "నెమ్మది కనెక్షన్ - ఆఫ్‌లైన్ మోడ్"
                            biddingState is BiddingState.Error -> stringResource(R.string.disconnected)
                            else -> stringResource(R.string.connecting)
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Rural optimization status
                if (collaborationState.isRuralOptimized) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFF9800)
                        )
                        Text(
                            text = "గ్రామీణ మోడ్ ఆన్ - డేటా సేవింగ్" +
                                    if (collaborationState.connectionLatency > 0) " (${collaborationState.connectionLatency}ms)" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFF9800)
                        )
                    }
                }

                // Network quality specific messages
                when (collaborationState.networkQuality) {
                    NetworkQuality.POOR -> {
                        Text(
                            text = "🔄 బిడ్లు ఆఫ్‌లైన్‌లో నిల్వ చేయబడుతున్నాయి",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFF9800)
                        )
                    }

                    NetworkQuality.FAIR -> {
                        Text(
                            text = "⚡ డేటా కంప్రెషన్ ఆన్ - వేగవంతమైన లోడింగ్",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFC107)
                        )
                    }
                    else -> { /* Normal quality - no additional message */ }
                }
            }
        }

        // Offline Mode Specific UI
        if (biddingState is BiddingState.OfflineMode) {
            val offlineState = biddingState as BiddingState.OfflineMode

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800).copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFFFF9800)
                        )
                        Text(
                            text = "ఆఫ్‌లైన్ బిడ్ క్యూ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800)
                        )
                    }

                    if (offlineState.queuedBids.isNotEmpty()) {
                        Text(
                            text = "${offlineState.queuedBids.size} బిడ్లు క్యూలో వేచి ఉన్నాయి",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Button(
                            onClick = { viewModel.retryOfflineBids() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ఆఫ్‌లైన్ బిడ్లను మళ్లీ ప్రయత్నించండి")
                        }
                    } else {
                        Text(
                            text = "కనెక్షన్ మెరుగుపడినప్పుడు బిడ్లు ఆటోమేటిక్‌గా పంపబడతాయి",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = timeFormat.format(Date(message.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
