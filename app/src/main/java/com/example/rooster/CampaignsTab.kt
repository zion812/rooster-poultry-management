package com.example.rooster

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignsTab(
    modifier: Modifier = Modifier,
    showTelugu: Boolean = false,
    userRole: String = "general",
) {
    var campaigns by remember { mutableStateOf<List<Campaign>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var networkQuality by remember { mutableStateOf(NetworkQualityLevel.FAIR) }

    val scope = rememberCoroutineScope()
    val campaignsService = remember { CampaignsService() }

    // Load campaigns on tab open
    LaunchedEffect(userRole) {
        isLoading = true
        errorMessage = null

        try {
            networkQuality = NetworkQualityLevel.FAIR // Simplified assessment

            // Load featured campaigns for the user role
            campaigns = campaignsService.getFeaturedCampaigns(networkQuality)

            // If no featured campaigns, load general campaigns
            if (campaigns.isEmpty()) {
                campaigns =
                    campaignsService.fetchCampaigns(
                        userRole = userRole,
                        region = "all",
                        networkQuality = networkQuality,
                    )
            }
        } catch (e: Exception) {
            errorMessage =
                if (showTelugu) {
                    "కాంపైన్లను లోడ్ చేయడంలో దోషం"
                } else {
                    "Error loading campaigns"
                }
            // Use sample campaigns as fallback
            campaigns = getSampleCampaigns().take(3)
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        // Header
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (showTelugu) "అవగాహన కాంపైన్లు" else "Awareness Campaigns",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            // Quick network status
            CampaignNetworkIndicator(networkQuality)
        }

        if (isLoading) {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (showTelugu) "లోడ్ చేస్తున్నాం..." else "Loading...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else if (errorMessage != null) {
            // Error state with retry
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE65100),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                try {
                                    campaigns =
                                        campaignsService.getFeaturedCampaigns(networkQuality)
                                } catch (e: Exception) {
                                    campaigns = getSampleCampaigns().take(3)
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                    ) {
                        Text(if (showTelugu) "మళ్ళీ ప్రయత్నించండి" else "Retry")
                    }
                }
            }
        } else {
            // Content
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (campaigns.isEmpty()) {
                    item {
                        EmptyCampaignsCard(showTelugu = showTelugu)
                    }
                } else {
                    items(campaigns.take(5)) { campaign -> // Limit to 5 for tab view
                        CompactCampaignCard(
                            campaign = campaign,
                            showTelugu = showTelugu,
                            onInteraction = { interactionType ->
                                scope.launch {
                                    campaignsService.trackCampaignInteraction(
                                        campaignId = campaign.id,
                                        interactionType = interactionType,
                                    )
                                }
                            },
                        )
                    }

                    // "View More" button
                    item {
                        OutlinedButton(
                            onClick = { /* Navigate to full CampaignsScreen */ },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (showTelugu) "అన్ని కాంపైన్లు చూడండి" else "View All Campaigns",
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCampaignsCard(showTelugu: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (showTelugu) "ప్రస్తుతం కాంపైన్లు లేవు" else "No campaigns available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (showTelugu) "త్వరలో కొత్త కాంపైన్లు వస్తాయి" else "New campaigns coming soon",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun CompactCampaignCard(
    campaign: Campaign,
    showTelugu: Boolean,
    onInteraction: (InteractionType) -> Unit,
) {
    LaunchedEffect(Unit) {
        onInteraction(InteractionType.VIEW)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = campaign.category.color.copy(alpha = 0.05f),
            ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            // Header with category icon and priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = campaign.category.icon,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showTelugu) campaign.category.displayNameTelugu else campaign.category.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = campaign.category.color,
                        fontWeight = FontWeight.Medium,
                    )
                }

                // Priority indicator
                if (campaign.priority == CampaignPriority.HIGH || campaign.priority == CampaignPriority.URGENT) {
                    Card(
                        colors =
                            CardDefaults.cardColors(
                                containerColor = campaign.priority.color.copy(alpha = 0.2f),
                            ),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = if (campaign.priority == CampaignPriority.URGENT) "🚨" else "⚡",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = if (showTelugu) campaign.titleInTelugu else campaign.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Description (shortened)
            Text(
                text = if (showTelugu) campaign.descriptionInTelugu else campaign.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Action button (compact)
                campaign.actionButtonText?.let { actionText ->
                    TextButton(
                        onClick = { onInteraction(InteractionType.ACTION_BUTTON_CLICK) },
                        colors =
                            ButtonDefaults.textButtonColors(
                                contentColor = campaign.category.color,
                            ),
                    ) {
                        Text(
                            text =
                                if (showTelugu) {
                                    campaign.actionButtonTextTelugu
                                        ?: actionText
                                } else {
                                    actionText
                                },
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                        )
                    }
                }

                // Compact engagement stats
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${campaign.engagement.views}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${campaign.engagement.likes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
