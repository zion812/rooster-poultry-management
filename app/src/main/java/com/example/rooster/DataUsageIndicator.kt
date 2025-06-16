package com.example.rooster

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

// Data Usage Tracking Models
data class DataUsageInfo(
    val estimated2GCost: String, // "~15MB"
    val estimated2GCostTelugu: String, // "సుమారు 15MB"
    val estimatedTime2G: String, // "~30 seconds"
    val estimatedTime2GTelugu: String, // "సుమారు 30 సెకన్లు"
    val contentType: DataContentType,
    val priority: DataPriority,
    val userFriendlyDescription: String,
    val userFriendlyDescriptionTelugu: String,
)

enum class DataContentType(
    val displayName: String,
    val displayNameTelugu: String,
    val icon: ImageVector,
    val baseDataSize: Float, // in MB
) {
    TEXT_ONLY("Text Only", "కేవలం వచనం", Icons.AutoMirrored.Filled.TextSnippet, 0.1f),
    IMAGES_LOW("Images (Low Quality)", "చిత్రాలు (తక్కువ నాణ్యత)", Icons.Default.Image, 0.5f),
    IMAGES_HIGH("Images (High Quality)", "చిత్రాలు (అధిక నాణ్యత)", Icons.Default.PhotoCamera, 2.0f),
    VIDEOS("Videos", "వీడియోలు", Icons.Default.VideoLibrary, 10.0f),
    MARKETPLACE_LISTING(
        "Marketplace Listing",
        "మార్కెట్ లిస్టింగ్",
        Icons.Default.ShoppingCart,
        1.5f,
    ),
    COMMUNITY_FEED("Community Feed", "కమ్యూనిటీ ఫీడ్", Icons.Default.Group, 3.0f),
    CHAT_MESSAGES("Chat Messages", "చాట్ సందేశాలు", Icons.AutoMirrored.Filled.Chat, 0.3f),
    HEALTH_RECORDS("Health Records", "ఆరోగ్య రికార్డులు", Icons.Default.HealthAndSafety, 0.8f),
    CULTURAL_CONTENT("Cultural Content", "సాంస్కృతిక కంటెంట్", Icons.Default.Festival, 2.5f),
}

enum class DataPriority(
    val displayName: String,
    val displayNameTelugu: String,
    val color: Color,
    val icon: String,
) {
    ESSENTIAL("Essential", "అవసరమైన", Color(0xFF4CAF50), "🟢"),
    IMPORTANT("Important", "ముఖ్యమైన", Color(0xFFFF9800), "🟡"),
    OPTIONAL("Optional", "ఐచ్ఛిక", Color(0xFF2196F3), "🔵"),
    LUXURY("Luxury", "విలాసం", Color(0xFFFF5722), "🔴"),
}

// Calculate data usage based on network quality and content type
fun calculateDataUsage(
    contentType: DataContentType,
    networkQuality: NetworkQualityLevel,
    itemCount: Int = 1,
    includeImages: Boolean = true,
): DataUsageInfo {
    val baseMB = contentType.baseDataSize * itemCount

    // Adjust based on network quality (compression affects data usage)
    val adjustedMB =
        when (networkQuality) {
            NetworkQualityLevel.EXCELLENT -> baseMB * 1.2f // Higher quality, more data
            NetworkQualityLevel.GOOD -> baseMB * 1.0f
            NetworkQualityLevel.FAIR -> baseMB * 0.7f // Compressed content
            NetworkQualityLevel.POOR -> baseMB * 0.4f // Heavily compressed
            NetworkQualityLevel.OFFLINE -> 0f
        }

    val finalMB =
        if (!includeImages && contentType != DataContentType.TEXT_ONLY) {
            adjustedMB * 0.3f // Text-only mode
        } else {
            adjustedMB
        }

    // Calculate download time on 2G (typical speed: 50-100 kbps)
    val downloadTimeSeconds = (finalMB * 8 * 1024) / 75 // 75 kbps average 2G speed

    val priority =
        when (contentType) {
            DataContentType.TEXT_ONLY, DataContentType.CHAT_MESSAGES -> DataPriority.ESSENTIAL
            DataContentType.MARKETPLACE_LISTING, DataContentType.HEALTH_RECORDS -> DataPriority.IMPORTANT
            DataContentType.COMMUNITY_FEED, DataContentType.IMAGES_LOW -> DataPriority.OPTIONAL
            DataContentType.IMAGES_HIGH, DataContentType.VIDEOS, DataContentType.CULTURAL_CONTENT -> DataPriority.LUXURY
        }

    return DataUsageInfo(
        estimated2GCost = formatDataSize(finalMB),
        estimated2GCostTelugu = formatDataSizeTelugu(finalMB),
        estimatedTime2G = formatTime(downloadTimeSeconds),
        estimatedTime2GTelugu = formatTimeTelugu(downloadTimeSeconds),
        contentType = contentType,
        priority = priority,
        userFriendlyDescription = generateDescription(contentType, finalMB, false),
        userFriendlyDescriptionTelugu = generateDescription(contentType, finalMB, true),
    )
}

private fun formatDataSize(mb: Float): String {
    return when {
        mb < 0.1f -> "~${(mb * 1024).roundToInt()}KB"
        mb < 1f -> "~${(mb * 10).roundToInt() / 10f}MB"
        else -> "~${mb.roundToInt()}MB"
    }
}

private fun formatDataSizeTelugu(mb: Float): String {
    return when {
        mb < 0.1f -> "సుమారు ${(mb * 1024).roundToInt()}KB"
        mb < 1f -> "సుమారు ${(mb * 10).roundToInt() / 10f}MB"
        else -> "సుమారు ${mb.roundToInt()}MB"
    }
}

private fun formatTime(seconds: Float): String {
    return when {
        seconds < 1f -> "~1 second"
        seconds < 60f -> "~${seconds.roundToInt()} seconds"
        seconds < 3600f -> "~${(seconds / 60).roundToInt()} minutes"
        else -> "~${(seconds / 3600).roundToInt()} hours"
    }
}

private fun formatTimeTelugu(seconds: Float): String {
    return when {
        seconds < 1f -> "సుమారు 1 సెకను"
        seconds < 60f -> "సుమారు ${seconds.roundToInt()} సెకన్లు"
        seconds < 3600f -> "సుమారు ${(seconds / 60).roundToInt()} నిమిషాలు"
        else -> "సుమారు ${(seconds / 3600).roundToInt()} గంటలు"
    }
}

private fun generateDescription(
    contentType: DataContentType,
    mb: Float,
    isTelugu: Boolean,
): String {
    return if (isTelugu) {
        when {
            mb < 0.5f -> "చాలా తక్కువ డేటా వినియోగం - 2G నెట్వర్క్లకు అనుకూలం"
            mb < 2f -> "మధ్యస్థ డేటా వినియోగం - 2G లో కొంచెం ఆలస్యం"
            mb < 5f -> "అధిక డేటా వినియోగం - 2G లో చాలా ఆలస్యం"
            else -> "చాలా అధిక డేటా వినియోగం - WiFi సిఫార్సు"
        }
    } else {
        when {
            mb < 0.5f -> "Very low data usage - suitable for 2G networks"
            mb < 2f -> "Moderate data usage - some delay on 2G"
            mb < 5f -> "High data usage - significant delay on 2G"
            else -> "Very high data usage - WiFi recommended"
        }
    }
}

@Composable
fun DataUsageIndicator(
    dataUsage: DataUsageInfo,
    showTelugu: Boolean = false,
    networkQuality: NetworkQualityLevel = NetworkQualityLevel.FAIR,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = dataUsage.priority.color.copy(alpha = 0.1f),
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Priority indicator
            Text(
                text = dataUsage.priority.icon,
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Content type icon
            Icon(
                imageVector = dataUsage.contentType.icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = dataUsage.priority.color,
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (showTelugu) dataUsage.estimated2GCostTelugu else dataUsage.estimated2GCost,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = dataUsage.priority.color,
                )

                if (networkQuality == NetworkQualityLevel.POOR || networkQuality == NetworkQualityLevel.FAIR) {
                    Text(
                        text = if (showTelugu) dataUsage.estimatedTime2GTelugu else dataUsage.estimatedTime2G,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun DetailedDataUsageCard(
    dataUsage: DataUsageInfo,
    showTelugu: Boolean = false,
    modifier: Modifier = Modifier,
    onOptimizeClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = dataUsage.contentType.icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = dataUsage.priority.color,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showTelugu) "డేటా వినియోగం" else "Data Usage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Text(
                    text = dataUsage.priority.icon,
                    fontSize = 20.sp,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Data usage details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = if (showTelugu) "2G నెట్వర్క్లో:" else "On 2G networks:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = if (showTelugu) dataUsage.estimated2GCostTelugu else dataUsage.estimated2GCost,
                        style = MaterialTheme.typography.bodyLarge,
                        color = dataUsage.priority.color,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (showTelugu) "లోడ్ చేయడానికి సమయం:" else "Load time:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = if (showTelugu) dataUsage.estimatedTime2GTelugu else dataUsage.estimatedTime2G,
                        style = MaterialTheme.typography.bodyLarge,
                        color = dataUsage.priority.color,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = if (showTelugu) dataUsage.userFriendlyDescriptionTelugu else dataUsage.userFriendlyDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // Optimize button for high data usage
            if (dataUsage.priority == DataPriority.LUXURY && onOptimizeClick != null) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onOptimizeClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = dataUsage.priority.color,
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.DataUsage,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (showTelugu) "డేటా ఆదా చేయండి" else "Save Data",
                    )
                }
            }
        }
    }
}

@Composable
fun NetworkAwareWarning(
    networkQuality: NetworkQualityLevel,
    showTelugu: Boolean = false,
    onOptimizeClick: (() -> Unit)? = null,
) {
    if (networkQuality == NetworkQualityLevel.POOR || networkQuality == NetworkQualityLevel.FAIR) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0),
                ),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text =
                            if (showTelugu) {
                                when (networkQuality) {
                                    NetworkQualityLevel.POOR -> "చాలా నెమ్మదైన నెట్వర్క్"
                                    NetworkQualityLevel.FAIR -> "నెమ్మదైన నెట్వర్క్"
                                    else -> "నెట్వర్క్ హెచ్చరిక"
                                }
                            } else {
                                when (networkQuality) {
                                    NetworkQualityLevel.POOR -> "Very slow network"
                                    NetworkQualityLevel.FAIR -> "Slow network"
                                    else -> "Network warning"
                                }
                            },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE65100),
                    )
                    Text(
                        text =
                            if (showTelugu) {
                                "డేటా ఆదా మోడ్ సిఫార్సు చేయబడింది"
                            } else {
                                "Data saving mode recommended"
                            },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE65100),
                    )
                }

                if (onOptimizeClick != null) {
                    TextButton(
                        onClick = onOptimizeClick,
                        colors =
                            ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFFE65100),
                            ),
                    ) {
                        Text(
                            if (showTelugu) "ఆప్టిమైజ్" else "Optimize",
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DataSavingModeToggle(
    isDataSavingEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    showTelugu: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = if (isDataSavingEnabled) Color(0xFFE8F5E8) else MaterialTheme.colorScheme.surface,
            ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (isDataSavingEnabled) Icons.Default.DataSaverOn else Icons.Default.DataSaverOff,
                contentDescription = null,
                tint = if (isDataSavingEnabled) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (showTelugu) "డేటా ఆదా మోడ్" else "Data Saving Mode",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text =
                        if (showTelugu) {
                            if (isDataSavingEnabled) "ఇమేజ్‌లు కంప్రెస్ చేయబడతాయి" else "పూర్తి నాణ్యత లోడ్ చేయబడుతుంది"
                        } else {
                            if (isDataSavingEnabled) "Images will be compressed" else "Full quality will be loaded"
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Switch(
                checked = isDataSavingEnabled,
                onCheckedChange = onToggle,
            )
        }
    }
}
