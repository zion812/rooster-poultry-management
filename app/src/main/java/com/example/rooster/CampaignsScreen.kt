package com.example.rooster

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignsScreen() {
    var campaigns by remember { mutableStateOf<List<Campaign>>(emptyList()) }
    var featuredCampaigns by remember { mutableStateOf<List<Campaign>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CampaignCategory?>(null) }
    var showTelugu by remember { mutableStateOf(false) }
    var networkQuality by remember { mutableStateOf(NetworkQualityLevel.FAIR) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val campaignsService = remember { CampaignsService() }

    // Assess network quality and load campaigns
    LaunchedEffect(selectedCategory, searchQuery) {
        isLoading = true
        errorMessage = null

        try {
            // Assess network quality (simplified for now)
            networkQuality = NetworkQualityLevel.FAIR

            // Load campaigns based on search and category
            campaigns =
                if (searchQuery.isNotBlank()) {
                    campaignsService.searchCampaigns(
                        searchQuery = searchQuery,
                        category = selectedCategory,
                        networkQuality = networkQuality,
                    )
                } else if (selectedCategory != null) {
                    campaignsService.fetchCampaignsByCategory(
                        category = selectedCategory!!,
                        networkQuality = networkQuality,
                    )
                } else {
                    campaignsService.fetchCampaigns(
                        userRole = "general", // Could be dynamic based on user
                        region = "all",
                        networkQuality = networkQuality,
                    )
                }

            // Load featured campaigns on initial load
            if (searchQuery.isBlank() && selectedCategory == null) {
                featuredCampaigns = campaignsService.getFeaturedCampaigns(networkQuality)
            }
        } catch (e: Exception) {
            errorMessage =
                if (showTelugu) {
                    "కాంపైన్లను లోడ్ చేయడంలో దోషం: ${e.message}"
                } else {
                    "Error loading campaigns: ${e.message}"
                }
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        // Header with language toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (showTelugu) "అవగాహన కాంపైన్లు" else "Awareness Campaigns",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Network quality indicator
                CampaignNetworkIndicator(networkQuality)

                Spacer(modifier = Modifier.width(8.dp))

                Switch(
                    checked = showTelugu,
                    onCheckedChange = { showTelugu = it },
                    thumbContent = {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = {
                Text(if (showTelugu) "కాంపైన్లను వెతకండి" else "Search Campaigns")
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category filter chips
        Text(
            text = if (showTelugu) "వర్గాలు" else "Categories",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // "All" category chip
            item {
                FilterChip(
                    onClick = { selectedCategory = null },
                    label = {
                        Text(if (showTelugu) "అన్నీ" else "All")
                    },
                    selected = selectedCategory == null,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                )
            }

            // Category chips
            items(CampaignCategory.values()) { category ->
                FilterChip(
                    onClick = {
                        selectedCategory = if (selectedCategory == category) null else category
                    },
                    label = {
                        Text(
                            if (showTelugu) category.displayNameTelugu else category.displayName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    selected = selectedCategory == category,
                    leadingIcon = {
                        Text(
                            text = category.icon,
                            fontSize = 16.sp,
                        )
                    },
                    colors =
                        FilterChipDefaults.filterChipColors(
                            selectedContainerColor = category.color.copy(alpha = 0.2f),
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            // Loading state
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
                        text = if (showTelugu) "కాంపైన్లను లోడ్ చేస్తున్నాం..." else "Loading campaigns...",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        } else if (errorMessage != null) {
            // Error state
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        } else {
            // Content
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Featured campaigns section (only when no search/filter)
                if (searchQuery.isBlank() && selectedCategory == null && featuredCampaigns.isNotEmpty()) {
                    item {
                        Text(
                            text = if (showTelugu) "ముఖ్య కాంపైన్లు" else "Featured Campaigns",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    items(featuredCampaigns.take(3)) { campaign ->
                        FeaturedCampaignCard(
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

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (showTelugu) "అన్ని కాంపైన్లు" else "All Campaigns",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                // Regular campaigns
                if (campaigns.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text =
                                        if (showTelugu) {
                                            if (searchQuery.isNotBlank()) "శోధన ఫలితాలు లేవు" else "కాంపైన్లు లేవు"
                                        } else {
                                            if (searchQuery.isNotBlank()) "No search results" else "No campaigns available"
                                        },
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                if (searchQuery.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text =
                                            if (showTelugu) {
                                                "వేరే కీవర్డ్స్ ప్రయత్నించండి"
                                            } else {
                                                "Try different keywords"
                                            },
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(campaigns) { campaign ->
                        CampaignCard(
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
                }
            }
        }
    }
}

@Composable
fun CampaignNetworkIndicator(networkQuality: NetworkQualityLevel) {
    val (icon, color) =
        when (networkQuality) {
            NetworkQualityLevel.EXCELLENT -> Icons.Default.SignalWifi4Bar to Color(0xFF4CAF50)
            NetworkQualityLevel.GOOD -> Icons.Default.SignalWifi4Bar to Color(0xFF8BC34A)
            NetworkQualityLevel.FAIR -> Icons.Default.Wifi to Color(0xFFFF9800)
            NetworkQualityLevel.POOR -> Icons.Default.WifiOff to Color(0xFFFF5722)
            NetworkQualityLevel.OFFLINE -> Icons.Default.SignalWifiOff to Color(0xFF757575)
        }

    Icon(
        imageVector = icon,
        contentDescription = networkQuality.name,
        tint = color,
        modifier = Modifier.size(20.dp),
    )
}

@Composable
fun FeaturedCampaignCard(
    campaign: Campaign,
    showTelugu: Boolean,
    onInteraction: (InteractionType) -> Unit,
) {
    LaunchedEffect(Unit) {
        onInteraction(InteractionType.VIEW)
    }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onInteraction(InteractionType.CLICK) },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = campaign.category.color.copy(alpha = 0.1f),
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Header with category and priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = campaign.category.icon,
                        fontSize = 20.sp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showTelugu) campaign.category.displayNameTelugu else campaign.category.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = campaign.category.color,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Card(
                    colors =
                        CardDefaults.cardColors(
                            containerColor = campaign.priority.color.copy(alpha = 0.2f),
                        ),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                ) {
                    Text(
                        text = "🔥 Featured",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = campaign.priority.color,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title and description
            Text(
                text = if (showTelugu) campaign.titleInTelugu else campaign.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (showTelugu) campaign.descriptionInTelugu else campaign.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action button and engagement
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                campaign.actionButtonText?.let { actionText ->
                    Button(
                        onClick = { onInteraction(InteractionType.ACTION_BUTTON_CLICK) },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = campaign.category.color,
                            ),
                    ) {
                        Text(
                            if (showTelugu) {
                                campaign.actionButtonTextTelugu
                                    ?: actionText
                            } else {
                                actionText
                            },
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${campaign.engagement.views}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
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

@Composable
fun CampaignCard(
    campaign: Campaign,
    showTelugu: Boolean,
    onInteraction: (InteractionType) -> Unit,
) {
    LaunchedEffect(Unit) {
        onInteraction(InteractionType.VIEW)
    }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onInteraction(InteractionType.CLICK) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Header with category and dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = campaign.category.icon,
                        fontSize = 18.sp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showTelugu) campaign.category.displayNameTelugu else campaign.category.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = campaign.category.color,
                        fontWeight = FontWeight.Medium,
                    )
                }

                val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                Text(
                    text = "Until ${dateFormat.format(campaign.endDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = if (showTelugu) campaign.titleInTelugu else campaign.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = if (showTelugu) campaign.descriptionInTelugu else campaign.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tags
            if (campaign.tags.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(campaign.tags.take(3)) { tag ->
                        Card(
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                ),
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                        ) {
                            Text(
                                text = "#$tag",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                campaign.actionButtonText?.let { actionText ->
                    OutlinedButton(
                        onClick = { onInteraction(InteractionType.ACTION_BUTTON_CLICK) },
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                contentColor = campaign.category.color,
                            ),
                        border =
                            ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(campaign.category.color),
                            ),
                    ) {
                        Text(
                            if (showTelugu) {
                                campaign.actionButtonTextTelugu
                                    ?: actionText
                            } else {
                                actionText
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                // Engagement stats
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onInteraction(InteractionType.LIKE) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Like",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = "${campaign.engagement.likes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    IconButton(
                        onClick = { onInteraction(InteractionType.SHARE) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = "${campaign.engagement.shares}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
