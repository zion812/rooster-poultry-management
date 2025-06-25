package com.example.rooster.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Traceability Timeline Item Component
 *
 * Features:
 * - Rural-optimized design with clear visual hierarchy
 * - Telugu language support for cultural farming community
 * - Network-adaptive image loading for milestone photos
 * - Visual timeline with verification indicators
 * - Material 3 design with RoosterTheme integration
 * - Accessibility-first design with large touch targets
 */

data class TraceabilityEvent(
    val id: String,
    val type: TraceabilityEventType,
    val title: String,
    val description: String,
    val timestamp: Long,
    val location: String = "",
    val imageUrl: String = "",
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING,
    val performedBy: String = "",
    val details: Map<String, String> = emptyMap(),
)

enum class TraceabilityEventType {
    BIRTH,
    VACCINATION,
    HEALTH_CHECK,
    FEEDING_CHANGE,
    TRANSFER,
    BREEDING,
    MEDICATION,
    MILESTONE,
    SALE,
    DEATH,
}

enum class VerificationStatus {
    VERIFIED,
    PENDING,
    REJECTED,
    NOT_REQUIRED,
}

@Composable
fun TraceabilityTimelineItem(
    event: TraceabilityEvent,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    onEventClick: () -> Unit,
    onImageClick: (() -> Unit)? = null,
    onVerificationClick: (() -> Unit)? = null,
    isTeluguMode: Boolean = false,
    showFullDetails: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    // val networkQuality = remember { assessNetworkQualitySafely(context) }

    // Animation state
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(event.id) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(),
        modifier = modifier,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onEventClick() }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            // Timeline indicator
            TimelineIndicator(
                eventType = event.type,
                verificationStatus = event.verificationStatus,
                isFirst = isFirst,
                isLast = isLast,
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Event content
            EventContent(
                event = event,
                onImageClick = onImageClick,
                onVerificationClick = onVerificationClick,
                // networkQuality = networkQuality,
                isTeluguMode = isTeluguMode,
                showFullDetails = showFullDetails,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TimelineIndicator(
    eventType: TraceabilityEventType,
    verificationStatus: VerificationStatus,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier,
) {
    val (icon, iconColor, backgroundColor) = getEventTypeStyle(eventType, verificationStatus)

    Column(
        modifier = modifier.width(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top line
        if (!isFirst) {
            Box(
                modifier =
                    Modifier
                        .width(2.dp)
                        .height(16.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            )
        }

        // Event indicator circle
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = backgroundColor,
            border =
                androidx.compose.foundation.BorderStroke(
                    2.dp,
                    when (verificationStatus) {
                        VerificationStatus.VERIFIED -> MaterialTheme.colorScheme.primary
                        VerificationStatus.PENDING -> getWarningColor()
                        VerificationStatus.REJECTED -> MaterialTheme.colorScheme.error
                        VerificationStatus.NOT_REQUIRED -> MaterialTheme.colorScheme.outline
                    },
                ),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // Bottom line
        if (!isLast) {
            Box(
                modifier =
                    Modifier
                        .width(2.dp)
                        .height(16.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            )
        }
    }
}

@Composable
private fun EventContent(
    event: TraceabilityEvent,
    onImageClick: (() -> Unit)?,
    onVerificationClick: (() -> Unit)?,
    // networkQuality: com.example.rooster.NetworkQualityLevel,
    isTeluguMode: Boolean,
    showFullDetails: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Event header
            EventHeader(
                event = event,
                onVerificationClick = onVerificationClick,
                isTeluguMode = isTeluguMode,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Event description
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = if (showFullDetails) Int.MAX_VALUE else 2,
                overflow = if (showFullDetails) TextOverflow.Visible else TextOverflow.Ellipsis,
            )

            // Event image
            if (event.imageUrl.isNotEmpty() && onImageClick != null) {
                Spacer(modifier = Modifier.height(12.dp))
                NetworkAdaptiveEventImage(
                    imageUrl = event.imageUrl,
                    // networkQuality = networkQuality,
                    contentDescription = "${event.title} image",
                    onClick = onImageClick,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .clip(MaterialTheme.shapes.small),
                )
            }

            // Event details
            if (showFullDetails && event.details.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                EventDetails(
                    details = event.details,
                    isTeluguMode = isTeluguMode,
                )
            }

            // Event metadata
            Spacer(modifier = Modifier.height(12.dp))
            EventMetadata(
                event = event,
                isTeluguMode = isTeluguMode,
            )
        }
    }
}

@Composable
private fun EventHeader(
    event: TraceabilityEvent,
    onVerificationClick: (() -> Unit)?,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = getEventTypeTitle(event.type, isTeluguMode),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = formatTimestamp(event.timestamp, isTeluguMode),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Verification status
        onVerificationClick?.let { verificationCallback ->
            VerificationBadge(
                status = event.verificationStatus,
                onClick = verificationCallback,
                isTeluguMode = isTeluguMode,
            )
        }
    }
}

@Composable
private fun VerificationBadge(
    status: VerificationStatus,
    onClick: () -> Unit,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    val (text, color, icon) =
        when (status) {
            VerificationStatus.VERIFIED ->
                Triple(
                    if (isTeluguMode) "ధృవీకరించబడింది" else "Verified",
                    MaterialTheme.colorScheme.primary,
                    Icons.Default.Verified,
                )

            VerificationStatus.PENDING ->
                Triple(
                    if (isTeluguMode) "పెండింగ్" else "Pending",
                    getWarningColor(),
                    Icons.Default.Schedule,
                )

            VerificationStatus.REJECTED ->
                Triple(
                    if (isTeluguMode) "తిరస్కరించబడింది" else "Rejected",
                    MaterialTheme.colorScheme.error,
                    Icons.Default.Error,
                )

            VerificationStatus.NOT_REQUIRED ->
                Triple(
                    if (isTeluguMode) "అవసరం లేదు" else "N/A",
                    MaterialTheme.colorScheme.outline,
                    Icons.Default.Info,
                )
        }

    val chipColors =
        AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.1f),
            labelColor = color,
        )

    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = color,
            )
        },
        colors = chipColors,
        modifier = modifier,
    )
}

@Composable
private fun EventDetails(
    details: Map<String, String>,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = if (isTeluguMode) "వివరాలు:" else "Details:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(8.dp))

        details.forEach { (key, value) ->
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = key,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun EventMetadata(
    event: TraceabilityEvent,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Location
        if (event.location.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Performed by
        if (event.performedBy.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = if (isTeluguMode) "ద్వారా: ${event.performedBy}" else "By: ${event.performedBy}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun NetworkAdaptiveEventImage(
    imageUrl: String,
    // networkQuality: com.example.rooster.NetworkQualityLevel,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // val imageSize =
    //     when (networkQuality) {
    //         com.example.rooster.NetworkQualityLevel.EXCELLENT -> 800
    //         com.example.rooster.NetworkQualityLevel.GOOD -> 600
    //         com.example.rooster.NetworkQualityLevel.FAIR -> 400
    //         com.example.rooster.NetworkQualityLevel.POOR -> 300
    //         com.example.rooster.NetworkQualityLevel.OFFLINE -> 300
    //     }

    Surface(
        modifier = modifier.clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small,
    ) {
        AsyncImage(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    // .size(imageSize)
                    .crossfade(true)
                    .build(),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun getEventTypeStyle(
    eventType: TraceabilityEventType,
    verificationStatus: VerificationStatus,
): Triple<ImageVector, androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> {
    return when (eventType) {
        TraceabilityEventType.BIRTH ->
            Triple(
                Icons.Default.ChildCare,
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primaryContainer,
            )

        TraceabilityEventType.VACCINATION ->
            Triple(
                Icons.Default.MedicalServices,
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.secondaryContainer,
            )

        TraceabilityEventType.HEALTH_CHECK ->
            Triple(
                Icons.Default.HealthAndSafety,
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.tertiaryContainer,
            )

        TraceabilityEventType.FEEDING_CHANGE ->
            Triple(
                Icons.Default.Restaurant,
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.secondaryContainer,
            )

        TraceabilityEventType.TRANSFER ->
            Triple(
                Icons.Default.SwapHoriz,
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primaryContainer,
            )

        TraceabilityEventType.BREEDING ->
            Triple(
                Icons.Default.Favorite,
                MaterialTheme.colorScheme.error,
                MaterialTheme.colorScheme.errorContainer,
            )

        TraceabilityEventType.MEDICATION ->
            Triple(
                Icons.Default.LocalPharmacy,
                getWarningColor(),
                getWarningContainerColor(),
            )

        TraceabilityEventType.MILESTONE ->
            Triple(
                Icons.Default.EmojiEvents,
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.tertiaryContainer,
            )

        TraceabilityEventType.SALE ->
            Triple(
                Icons.Default.Sell,
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primaryContainer,
            )

        TraceabilityEventType.DEATH ->
            Triple(
                Icons.Default.Remove,
                MaterialTheme.colorScheme.error,
                MaterialTheme.colorScheme.errorContainer,
            )
    }
}

private fun getEventTypeTitle(
    eventType: TraceabilityEventType,
    isTeluguMode: Boolean,
): String {
    return when (eventType) {
        TraceabilityEventType.BIRTH -> if (isTeluguMode) "జననం" else "Birth"
        TraceabilityEventType.VACCINATION -> if (isTeluguMode) "టీకా" else "Vaccination"
        TraceabilityEventType.HEALTH_CHECK -> if (isTeluguMode) "ఆరోగ్య పరీక్ష" else "Health Check"
        TraceabilityEventType.FEEDING_CHANGE -> if (isTeluguMode) "ఆహార మార్పు" else "Feeding Change"
        TraceabilityEventType.TRANSFER -> if (isTeluguMode) "బదిలీ" else "Transfer"
        TraceabilityEventType.BREEDING -> if (isTeluguMode) "సంతానోత్పత్తి" else "Breeding"
        TraceabilityEventType.MEDICATION -> if (isTeluguMode) "మందు" else "Medication"
        TraceabilityEventType.MILESTONE -> if (isTeluguMode) "మైలురాయి" else "Milestone"
        TraceabilityEventType.SALE -> if (isTeluguMode) "విక్రయం" else "Sale"
        TraceabilityEventType.DEATH -> if (isTeluguMode) "మరణం" else "Death"
    }
}

private fun formatTimestamp(
    timestamp: Long,
    isTeluguMode: Boolean,
): String {
    val formatter =
        SimpleDateFormat(
            if (isTeluguMode) "dd MMM yyyy, HH:mm" else "MMM dd, yyyy at HH:mm",
            if (isTeluguMode) Locale("te") else Locale.getDefault(),
        )
    return formatter.format(Date(timestamp))
}

/**
 * Compact version for overview lists
 */
@Composable
fun CompactTraceabilityTimelineItem(
    event: TraceabilityEvent,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    onEventClick: () -> Unit,
    isTeluguMode: Boolean = false,
    modifier: Modifier = Modifier,
) {
    TraceabilityTimelineItem(
        event = event,
        isFirst = isFirst,
        isLast = isLast,
        onEventClick = onEventClick,
        isTeluguMode = isTeluguMode,
        showFullDetails = false,
        modifier = modifier,
    )
}

@Composable
private fun getWarningColor(): androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFFFF9800)

@Composable
private fun getWarningContainerColor(): androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFFFFF3E0)
