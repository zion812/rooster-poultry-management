package com.example.rooster.feature.community.ui.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rooster.feature.community.domain.model.Post
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPagerApi::class) // For HorizontalPager
@Composable
fun PostItem(
    post: Post,
    currentUserId: String?, // Added to determine if post is liked by current user
    onPostClick: (postId: String) -> Unit,
    onAuthorClick: (authorId: String) -> Unit,
    onLikeClick: (postId: String) -> Unit, // This will now be onLikeUnlikeClick
    onUnlikeClick: (postId: String) -> Unit, // Added
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
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.authorProfilePictureUrl)
                        // .placeholder(R.drawable.default_avatar) // TODO
                        // .error(R.drawable.default_avatar)
                        .crossfade(true)
                        .build(),
                    contentDescription = "${post.authorDisplayName} profile picture",
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(post.authorDisplayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        // Convert timestamp to a readable date/time string
                        text = SimpleDateFormat("MMM dd, yyyy 'at' hh:mma", Locale.getDefault()).format(Date(post.createdTimestamp)),
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
                Text(it, style = MaterialTheme.typography.bodyMedium, maxLines = 5, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Image/Video Content (using Accompanist Pager for multiple images)
            val imagesToShow = post.imageUrls ?: emptyList()
            if (imagesToShow.isNotEmpty()) {
                val pagerState = rememberPagerState()
                HorizontalPager(
                    count = imagesToShow.size,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f) // Common aspect ratio for images/videos
                        .clip(MaterialTheme.shapes.medium)
                ) { page ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imagesToShow[page])
                            // .placeholder(R.drawable.image_placeholder) // TODO
                            .crossfade(true)
                            .build(),
                        contentDescription = "Post image ${page + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // TODO: Add PagerIndicator if imagesToShow.size > 1
                Spacer(modifier = Modifier.height(8.dp))
            } else if (post.videoUrl != null) {
                // TODO: Placeholder for Video Player
                Box(modifier = Modifier.fillMaxWidth().aspectRatio(16/9f).background(Color.Gray)) {
                    Text("Video Placeholder: ${post.videoUrl}", modifier = Modifier.align(Alignment.Center))
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Tags
            post.tags?.let { tags ->
                if (tags.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        tags.take(3).forEach { tag -> // Show a few tags
                            Chip(onClick = { /* TODO: Navigate to tag search */ }, label = { Text("#$tag", style = MaterialTheme.typography.labelSmall) })
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }


            // Action Bar (Like, Comment, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton(
                    icon = {
                        if (isLikedByCurrentUser) {
                            Icon(Icons.Filled.Favorite, contentDescription = "Unlike", tint = MaterialTheme.colorScheme.primary)
                        } else {
                            Icon(Icons.Filled.FavoriteBorder, contentDescription = "Like")
                        }
                    },
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
                    icon = Icons.Filled.Comment,
                    text = post.commentCount.toString(),
                    onClick = { onCommentClick(post.postId) }
                )
                ActionButton(
                    icon = Icons.Filled.Share,
                    text = post.shareCount.toString(), // Or just "Share"
                    onClick = { onShareClick(post.postId) }
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: @Composable () -> Unit,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(onClick = onClick, modifier = modifier) {
        icon()
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
        contentText = "Just harvested some fresh organic tomatoes! 🍅 They look amazing this year. #organic #farming #fresh #tomatoes. Come and get them at the local market this weekend, or message me for direct sales. We also have some excellent Nattu Kodi roosters available.",
        imageUrls = listOf("https://via.placeholder.com/400x225.png?text=Tomato+Harvest"),
        videoUrl = null,
        createdTimestamp = System.currentTimeMillis() - (2 * 60 * 60 * 1000), // 2 hours ago
        updatedTimestamp = null,
        likeCount = 15,
        commentCount = 3,
        shareCount = 2,
        tags = listOf("organic", "tomatoes", "harvest", "nattukodi"),
        location = "Krishna District"
    )
    MaterialTheme {
        PostItem(
            post = samplePost,
            onPostClick = {},
            onAuthorClick = {},
            onLikeClick = {},
            onCommentClick = {},
            onShareClick = {}
        )
    }
}
