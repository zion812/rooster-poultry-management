package com.example.rooster.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rooster.NetworkQualityLevel
// import com.example.rooster.assessNetworkQualitySafely

/**
 * Farmer Profile Card Component
 *
 * Features:
 * - Rural-optimized design with large touch targets
 * - Telugu language support for cultural farming community
 * - Network-adaptive image loading for profile photos
 * - Verification status indicators
 * - Farmer-specific metrics (experience, livestock count, etc.)
 * - Material 3 design with RoosterTheme integration
 * - Accessibility-first design
 */
@Composable
fun FarmerProfileCard(
    farmerId: String,
    farmerName: String,
    profileImageUrl: String = "",
    location: String,
    experienceYears: Int,
    livestockCount: Int,
    rating: Float = 0f,
    isVerified: Boolean = false,
    isOnline: Boolean = false,
    specialties: List<String> = emptyList(),
    onProfileClick: () -> Unit,
    onMessageClick: (() -> Unit)? = null,
    onCallClick: (() -> Unit)? = null,
    onFollowClick: (() -> Unit)? = null,
    isFollowing: Boolean = false,
    isTeluguMode: Boolean = false,
    showActions: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    // val networkQuality = remember { assessNetworkQualitySafely(context) }
    val networkQuality = NetworkQualityLevel.GOOD

    // Animation state
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(farmerId) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(),
        modifier = modifier,
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onProfileClick() }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                // Profile header
                ProfileHeader(
                    farmerName = farmerName,
                    profileImageUrl = profileImageUrl,
                    location = location,
                    isVerified = isVerified,
                    isOnline = isOnline,
                    rating = rating,
                    networkQuality = networkQuality,
                    isTeluguMode = isTeluguMode,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Farmer stats
                FarmerStats(
                    experienceYears = experienceYears,
                    livestockCount = livestockCount,
                    isTeluguMode = isTeluguMode,
                )

                // Specialties
                if (specialties.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    FarmerSpecialties(
                        specialties = specialties,
                        isTeluguMode = isTeluguMode,
                    )
                }

                // Action buttons
                if (showActions) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileActions(
                        onMessageClick = onMessageClick,
                        onCallClick = onCallClick,
                        onFollowClick = onFollowClick,
                        isFollowing = isFollowing,
                        isTeluguMode = isTeluguMode,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    farmerName: String,
    profileImageUrl: String,
    location: String,
    isVerified: Boolean,
    isOnline: Boolean,
    rating: Float,
    networkQuality: NetworkQualityLevel,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        // Profile picture with online indicator
        Box {
            NetworkAdaptiveProfileImage(
                imageUrl = profileImageUrl,
                networkQuality = networkQuality,
                contentDescription = "$farmerName profile picture",
                modifier = Modifier.size(80.dp),
            )

            // Online status indicator
            if (isOnline) {
                Surface(
                    modifier =
                        Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    border =
                        androidx.compose.foundation.BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.surface,
                        ),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = if (isTeluguMode) "ఆన్‌లైన్" else "Online",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Farmer info
        Column(
            modifier = Modifier.weight(1f),
        ) {
            // Name with verification
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = farmerName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )

                if (isVerified) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = if (isTeluguMode) "ధృవీకరించబడిన రైతు" else "Verified Farmer",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Location
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Rating
            if (rating > 0f) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", rating),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun FarmerStats(
    experienceYears: Int,
    livestockCount: Int,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Experience
        StatItem(
            value = "$experienceYears",
            label = if (isTeluguMode) "సంవత్సరాల అనుభవం" else "Years Experience",
            icon = Icons.Default.AccessTime,
            modifier = Modifier.weight(1f),
        )

        // Livestock count
        StatItem(
            value = "$livestockCount",
            label = if (isTeluguMode) "పశువులు" else "Livestock",
            icon = Icons.Default.Pets,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FarmerSpecialties(
    specialties: List<String>,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = if (isTeluguMode) "ప్రత్యేకతలు:" else "Specialties:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Display specialties as chips
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            specialties.take(3).forEach { specialty ->
                AssistChip(
                    onClick = { /* Handle specialty click */ },
                    label = {
                        Text(
                            text = specialty,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    colors =
                        AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                )
            }

            if (specialties.size > 3) {
                AssistChip(
                    onClick = { /* Handle view more */ },
                    label = {
                        Text(
                            text = if (isTeluguMode) "+${specialties.size - 3} మరిన్ని" else "+${specialties.size - 3} more",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    colors =
                        AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            labelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        ),
                )
            }
        }
    }
}

@Composable
private fun ProfileActions(
    onMessageClick: (() -> Unit)?,
    onCallClick: (() -> Unit)?,
    onFollowClick: (() -> Unit)?,
    isFollowing: Boolean,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Message button
        onMessageClick?.let { messageCallback ->
            OutlinedButton(
                onClick = messageCallback,
                modifier = Modifier.weight(1f),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Message,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTeluguMode) "సందేశం" else "Message",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }

        // Call button
        onCallClick?.let { callCallback ->
            OutlinedButton(
                onClick = callCallback,
                modifier = Modifier.weight(1f),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTeluguMode) "కాల్" else "Call",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }

        // Follow button
        onFollowClick?.let { followCallback ->
            Button(
                onClick = followCallback,
                modifier = Modifier.weight(1f),
                colors =
                    if (isFollowing) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    },
            ) {
                Icon(
                    imageVector = if (isFollowing) Icons.Default.PersonRemove else Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text =
                        if (isFollowing) {
                            if (isTeluguMode) "అనుసరించడం" else "Following"
                        } else {
                            if (isTeluguMode) "అనుసరించండి" else "Follow"
                        },
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun NetworkAdaptiveProfileImage(
    imageUrl: String,
    networkQuality: NetworkQualityLevel,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val imageSize =
        when (networkQuality) {
            NetworkQualityLevel.EXCELLENT -> 400
            NetworkQualityLevel.GOOD -> 300
            NetworkQualityLevel.FAIR -> 200
            NetworkQualityLevel.POOR -> 150
            NetworkQualityLevel.OFFLINE -> 150
        }

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border =
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            ),
    ) {
        if (imageUrl.isNotEmpty()) {
            AsyncImage(
                model =
                    ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .size(imageSize)
                        .crossfade(true)
                        .build(),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            // Default farmer icon
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Agriculture,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp),
                )
            }
        }
    }
}

/**
 * Compact version for lists
 */
@Composable
fun CompactFarmerProfileCard(
    farmerId: String,
    farmerName: String,
    profileImageUrl: String = "",
    location: String,
    rating: Float = 0f,
    isVerified: Boolean = false,
    onProfileClick: () -> Unit,
    isTeluguMode: Boolean = false,
    modifier: Modifier = Modifier,
) {
    FarmerProfileCard(
        farmerId = farmerId,
        farmerName = farmerName,
        profileImageUrl = profileImageUrl,
        location = location,
        experienceYears = 0,
        livestockCount = 0,
        rating = rating,
        isVerified = isVerified,
        onProfileClick = onProfileClick,
        showActions = false,
        isTeluguMode = isTeluguMode,
        modifier = modifier,
    )
}
