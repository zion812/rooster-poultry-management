package com.example.rooster.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rooster.assessNetworkQualitySafely
import com.example.rooster.models.Post
import java.text.SimpleDateFormat
import java.util.*

/**
 * Social Post Card Component
 *
 * Features:
 * - Rural-optimized design with large touch targets
 * - Telugu language support for cultural sensitivity
 * - Network-adaptive image loading
 * - Interactive engagement features (like, comment, share)
 * - Material 3 design with RoosterTheme integration
 * - Accessibility-first design
 */
@Composable
fun SocialPostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onProfileClick: () -> Unit,
    authorName: String = "Unknown User",
    authorImageUrl: String = "",
    isVerified: Boolean = false,
    isLiked: Boolean = false,
    likeCount: Int = 0,
    commentCount: Int = 0,
    isTeluguMode: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val networkQuality = remember { assessNetworkQualitySafely(context) }

    // Animation state
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(post.id) {
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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                // Post header with user info
                PostHeader(
                    authorName = authorName,
                    authorImageUrl = authorImageUrl,
                    timestamp = post.timestamp,
                    isVerified = isVerified,
                    isTeluguMode = isTeluguMode,
                    onProfileClick = onProfileClick,
                    networkQuality = networkQuality,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Post content
                PostContent(
                    content = post.text,
                    imageUrl = post.mediaUrls.firstOrNull(),
                    networkQuality = networkQuality,
                    isTeluguMode = isTeluguMode,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Engagement actions
                PostActions(
                    isLiked = isLiked,
                    likeCount = likeCount,
                    commentCount = commentCount,
                    onLikeClick = onLikeClick,
                    onCommentClick = onCommentClick,
                    onShareClick = onShareClick,
                    isTeluguMode = isTeluguMode,
                )
            }
        }
    }
}

@Composable
private fun PostHeader(
    authorName: String,
    authorImageUrl: String,
    timestamp: Long,
    isVerified: Boolean,
    isTeluguMode: Boolean,
    onProfileClick: () -> Unit,
    networkQuality: com.example.rooster.NetworkQualityLevel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onProfileClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Profile picture
        NetworkAdaptiveProfileImage(
            imageUrl = authorImageUrl,
            networkQuality = networkQuality,
            contentDescription = "$authorName profile picture",
            modifier = Modifier.size(48.dp),
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Author info
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = authorName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = if (isTeluguMode) "ధృవీకరించబడింది" else "Verified",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Text(
                text = formatTimestamp(timestamp, isTeluguMode),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // More options
        IconButton(
            onClick = { /* Handle more options */ },
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = if (isTeluguMode) "మరిన్ని ఎంపికలు" else "More options",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PostContent(
    content: String,
    imageUrl: String?,
    networkQuality: com.example.rooster.NetworkQualityLevel,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Text content
        if (content.isNotEmpty()) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
            )
        }

        // Image content
        if (!imageUrl.isNullOrEmpty()) {
            if (content.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
            }

            NetworkAdaptivePostImage(
                imageUrl = imageUrl,
                networkQuality = networkQuality,
                contentDescription = if (isTeluguMode) "పోస్ట్ చిత్రం" else "Post image",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .clip(MaterialTheme.shapes.medium),
            )
        }
    }
}

@Composable
private fun PostActions(
    isLiked: Boolean,
    likeCount: Int,
    commentCount: Int,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        // Like button
        PostActionButton(
            icon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            text = formatCount(likeCount, if (isTeluguMode) "ఇష్టాలు" else "Likes"),
            onClick = onLikeClick,
            isActive = isLiked,
            activeColor = MaterialTheme.colorScheme.error,
        )

        // Comment button
        PostActionButton(
            icon = Icons.AutoMirrored.Filled.Comment,
            text = formatCount(commentCount, if (isTeluguMode) "కామెంట్లు" else "Comments"),
            onClick = onCommentClick,
            isActive = false,
        )

        // Share button
        PostActionButton(
            icon = Icons.Default.Share,
            text = if (isTeluguMode) "పంచుకోండి" else "Share",
            onClick = onShareClick,
            isActive = false,
        )
    }
}

@Composable
private fun PostActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    isActive: Boolean = false,
    activeColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
) {
    val color = if (isActive) activeColor else MaterialTheme.colorScheme.onSurfaceVariant

    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp), // Rural-friendly touch target
        colors =
            ButtonDefaults.textButtonColors(
                contentColor = color,
            ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun NetworkAdaptiveProfileImage(
    imageUrl: String,
    networkQuality: com.example.rooster.NetworkQualityLevel,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val imageSize =
        when (networkQuality) {
            com.example.rooster.NetworkQualityLevel.EXCELLENT -> 200
            com.example.rooster.NetworkQualityLevel.GOOD -> 150
            com.example.rooster.NetworkQualityLevel.FAIR -> 100
            com.example.rooster.NetworkQualityLevel.POOR -> 80
            com.example.rooster.NetworkQualityLevel.OFFLINE -> 80
        }

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
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
            // Default profile icon
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun NetworkAdaptivePostImage(
    imageUrl: String,
    networkQuality: com.example.rooster.NetworkQualityLevel,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val imageSize =
        when (networkQuality) {
            com.example.rooster.NetworkQualityLevel.EXCELLENT -> 1080
            com.example.rooster.NetworkQualityLevel.GOOD -> 720
            com.example.rooster.NetworkQualityLevel.FAIR -> 480
            com.example.rooster.NetworkQualityLevel.POOR -> 240
            com.example.rooster.NetworkQualityLevel.OFFLINE -> 240
        }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
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
    }
}

private fun formatTimestamp(
    timestamp: Long,
    isTeluguMode: Boolean,
): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> if (isTeluguMode) "ఇప్పుడే" else "Just now"
        diff < 3600_000 -> {
            val minutes = diff / 60_000
            if (isTeluguMode) "$minutes నిమిషాల క్రితం" else "${minutes}m ago"
        }

        diff < 86400_000 -> {
            val hours = diff / 3600_000
            if (isTeluguMode) "$hours గంటల క్రితం" else "${hours}h ago"
        }

        else -> {
            val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
            formatter.format(Date(timestamp))
        }
    }
}

private fun formatCount(
    count: Int,
    label: String,
): String {
    return when {
        count == 0 -> label
        count < 1000 -> "$count $label"
        count < 100000 -> "${count / 1000}K $label"
        else -> "${count / 100000}L $label"
    }
}

/**
 * Compact version for feed lists
 */
@Composable
fun CompactSocialPostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onProfileClick: () -> Unit,
    authorName: String = "Unknown User",
    authorImageUrl: String = "",
    isVerified: Boolean = false,
    isLiked: Boolean = false,
    likeCount: Int = 0,
    commentCount: Int = 0,
    isTeluguMode: Boolean = false,
    modifier: Modifier = Modifier,
) {
    SocialPostCard(
        post = post,
        onLikeClick = onLikeClick,
        onCommentClick = onCommentClick,
        onShareClick = onShareClick,
        onProfileClick = onProfileClick,
        authorName = authorName,
        authorImageUrl = authorImageUrl,
        isVerified = isVerified,
        isLiked = isLiked,
        likeCount = likeCount,
        commentCount = commentCount,
        isTeluguMode = isTeluguMode,
        modifier = modifier,
    )
}
