package com.example.rooster.feature.community.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rooster.feature.community.domain.model.Post
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PostItem(
    post: Post,
    currentUserId: String?,
    onPostClick: (postId: String) -> Unit,
    onAuthorClick: (authorId: String) -> Unit,
    onLikeClick: (postId: String) -> Unit,
    onUnlikeClick: (postId: String) -> Unit,
    onCommentClick: (postId: String) -> Unit,
    onShareClick: (postId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLikedByCurrentUser = remember(post.likedBy, currentUserId) {
        currentUserId != null && post.likedBy.contains(currentUserId)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onPostClick(post.postId) },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Author Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onAuthorClick(post.authorUserId) }
            ) {
                // Placeholder for profile picture
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.authorDisplayName.firstOrNull()?.toString() ?: "?",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        post.authorDisplayName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy 'at' hh:mma", Locale.getDefault())
                            .format(Date(post.createdTimestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* TODO: Options menu */ }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More options")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Content Text
            post.contentText?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Placeholder for images
            val imagesToShow = post.imageUrls ?: emptyList()
            if (imagesToShow.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${imagesToShow.size} image(s)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Tags
            post.tags?.let { tags ->
                if (tags.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        tags.take(3).forEach { tag ->
                            Text(
                                "#$tag",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.shapes.small
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton(
                    icon = if (isLikedByCurrentUser) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    text = post.likeCount.toString(),
                    onClick = {
                        if (isLikedByCurrentUser) {
                            onUnlikeClick(post.postId)
                        } else {
                            onLikeClick(post.postId)
                        }
                    }
                )
                ActionButton(
                    icon = Icons.Filled.MoreVert, // Using as placeholder for comment icon
                    text = post.commentCount.toString(),
                    onClick = { onCommentClick(post.postId) }
                )
                ActionButton(
                    icon = Icons.Filled.MoreVert, // Using as placeholder for share icon
                    text = post.shareCount.toString(),
                    onClick = { onShareClick(post.postId) }
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(onClick = onClick, modifier = modifier) {
        Icon(icon, contentDescription = "")
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(text, style = MaterialTheme.typography.labelMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostItem() {
    val samplePost = Post(
        postId = "post1",
        authorUserId = "user1",
        authorDisplayName = "Farmer Joe",
        authorProfilePictureUrl = "https://via.placeholder.com/40",
        contentText = "Just harvested some fresh organic tomatoes! ",
        imageUrls = listOf("https://via.placeholder.com/400x225.png?text=Tomato+Harvest"),
        videoUrl = null,
        createdTimestamp = System.currentTimeMillis() - (2 * 60 * 60 * 1000),
        updatedTimestamp = null,
        likeCount = 15,
        commentCount = 3,
        shareCount = 2,
        tags = listOf("organic", "tomatoes", "harvest"),
        location = "Krishna District",
        likedBy = emptyList()
    )
    MaterialTheme {
        PostItem(
            post = samplePost,
            currentUserId = "user1",
            onPostClick = {},
            onAuthorClick = {},
            onLikeClick = {},
            onUnlikeClick = {},
            onCommentClick = {},
            onShareClick = {}
        )
    }
}
