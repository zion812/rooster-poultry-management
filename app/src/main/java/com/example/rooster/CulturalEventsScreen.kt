package com.example.rooster

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import java.util.*

// Instagram-style data models for cultural events
data class FestivalStory(
    val id: String,
    val name: String,
    val nameTelugu: String,
    val imageUrl: String,
    val backgroundUrl: String,
    val significance: String,
    val significanceTelugu: String,
    val date: Date,
    val discount: Int = 0,
    val isViewed: Boolean = false,
    val stickers: List<String> = emptyList(),
)

data class CulturalPost(
    val id: String,
    val title: String,
    val titleTelugu: String,
    val content: String,
    val contentTelugu: String,
    val imageUrl: String,
    val userName: String,
    val userNameTelugu: String,
    val userImageUrl: String,
    val likes: Int,
    val comments: Int,
    val timeAgo: String,
    val culturalTag: String,
    val isLiked: Boolean = false,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CulturalEventsScreen(
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var selectedStoryIndex by remember { mutableStateOf(-1) }

    // Instagram-style data states
    var festivalStories by remember { mutableStateOf<List<FestivalStory>>(emptyList()) }
    var culturalPosts by remember { mutableStateOf<List<CulturalPost>>(emptyList()) }
    var groupOrders by remember { mutableStateOf<List<GroupOrder>>(emptyList()) }

    // Load data
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoading = true
            try {
                festivalStories = loadFestivalStories()
                culturalPosts = loadCulturalPosts()
                groupOrders = loadGroupOrders()
            } catch (e: Exception) {
                // Handle error gracefully
            } finally {
                isLoading = false
            }
        }
    }

    // Instagram-style full-screen story viewer
    if (selectedStoryIndex >= 0) {
        InstagramStoryViewer(
            stories = festivalStories,
            currentIndex = selectedStoryIndex,
            isTeluguMode = isTeluguMode,
            onDismiss = { selectedStoryIndex = -1 },
            onNext = {
                if (selectedStoryIndex < festivalStories.size - 1) {
                    selectedStoryIndex++
                } else {
                    selectedStoryIndex = -1
                }
            },
            onPrevious = {
                if (selectedStoryIndex > 0) {
                    selectedStoryIndex--
                }
            },
        )
    } else {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.White), // Instagram white background
        ) {
            // Instagram-style header
            CulturalEventsHeader(
                isTeluguMode = isTeluguMode,
                onLanguageToggle = onLanguageToggle,
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFFF5722),
                        modifier = Modifier.size(32.dp),
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    // Festival Stories Section (Instagram Stories style)
                    item {
                        Column {
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text =
                                        stringResource(
                                            id = if (isTeluguMode) R.string.festival_stories_title else R.string.festival_stories_title,
                                        ),
                                    style =
                                        MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                        ),
                                )
                                Text(
                                    text = stringResource(id = if (isTeluguMode) R.string.view_all_action else R.string.view_all_action),
                                    style =
                                        MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFF007AFF),
                                            fontWeight = FontWeight.Medium,
                                        ),
                                    modifier = Modifier.clickable { /* View all stories */ },
                                )
                            }

                            FestivalStoriesRow(
                                stories = festivalStories,
                                isTeluguMode = isTeluguMode,
                                onStoryClick = { index -> selectedStoryIndex = index },
                            )
                        }
                    }

                    // Cultural Posts Feed (Instagram feed style)
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = if (isTeluguMode) R.string.cultural_posts_title else R.string.cultural_posts_title),
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }

                    items(culturalPosts) { post ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(),
                        ) {
                            CulturalPostCard(
                                post = post,
                                isTeluguMode = isTeluguMode,
                                onLike = { postId ->
                                    coroutineScope.launch {
                                        StabilityManager.safeExecute(
                                            operation = { likeCulturalPost(postId) },
                                            fallback = Unit,
                                        )
                                    }
                                },
                                onComment = { /* Navigate to comments */ },
                                onShare = { /* Handle share */ },
                            )
                        }
                    }

                    // Group Orders Section
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = if (isTeluguMode) R.string.group_orders_title else R.string.group_orders_title),
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }

                    items(groupOrders.take(3)) { order -> // Show only first 3
                        GroupOrderCard(order = order, isTeluguMode = isTeluguMode)
                    }

                    // Competition Management Section
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isTeluguMode) "పోటీలు" else "Competitions",
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }

                    item {
                        CompetitionManagementSection(isTeluguMode = isTeluguMode)
                    }

                    // Cultural Showcases Section
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isTeluguMode) "సాంస్కృతిక ప్రదర్శనలు" else "Cultural Showcases",
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }

                    item {
                        CulturalShowcasesSection(isTeluguMode = isTeluguMode)
                    }

                    // Leaderboards Section (Enhanced)
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.leaderboard_title), // New string resource
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                    item {
                        LeaderboardsSection(isTeluguMode = isTeluguMode) // Call the new composable
                    }
                }
            }
        }
    }
}

@Composable
private fun CulturalEventsHeader(
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color(0xFFFF5722).copy(alpha = 0.1f),
            ),
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text =
                        stringResource(
                            id = if (isTeluguMode) R.string.cultural_events_header_title else R.string.cultural_events_header_title,
                        ),
                    style =
                        MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        ),
                    color = Color(0xFFD84315),
                )
                Text(
                    text = stringResource(id = if (isTeluguMode) R.string.our_traditions_subtitle else R.string.our_traditions_subtitle),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color(0xFFFF5722),
                )
            }

            // Language toggle
            IconButton(onClick = onLanguageToggle) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = stringResource(id = if (isTeluguMode) R.string.switch_to_english else R.string.switch_to_telugu),
                )
            }
        }
    }
}

@Composable
private fun FestivalStoriesRow(
    stories: List<FestivalStory>,
    isTeluguMode: Boolean,
    onStoryClick: (Int) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        items(stories.size) { index ->
            val story = stories[index]
            StoryCircleWithDiscount(
                story = story,
                isTeluguMode = isTeluguMode,
                onClick = { onStoryClick(index) },
            )
        }
    }
}

@Composable
private fun StoryCircleWithDiscount(
    story: FestivalStory,
    isTeluguMode: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .clickable { onClick() }
                .width(80.dp),
    ) {
        Box {
            // Story circle
            Box(
                modifier =
                    Modifier
                        .size(70.dp)
                        .background(
                            if (story.isViewed) {
                                Color.Gray.copy(alpha = 0.3f)
                            } else {
                                Color(0xFFFF5722)
                            },
                            CircleShape,
                        )
                        .padding(2.dp),
            ) {
                AsyncImage(
                    model =
                        ImageRequest.Builder(LocalContext.current)
                            .data(story.imageUrl.ifEmpty { R.drawable.ic_launcher_foreground })
                            .crossfade(true)
                            .size(240) // 2G optimization
                            .build(),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.White),
                    contentScale = ContentScale.Crop,
                )
            }

            // Discount badge
            if (story.discount > 0) {
                Surface(
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp),
                    shape = CircleShape,
                    color = Color(0xFFFF3040),
                ) {
                    Text(
                        text = "${story.discount}%",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(4.dp),
                    )
                }
            }
        }

        Text(
            text = if (isTeluguMode) story.nameTelugu else story.name,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun InstagramStoryViewer(
    stories: List<FestivalStory>,
    currentIndex: Int,
    isTeluguMode: Boolean,
    onDismiss: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    if (currentIndex < 0 || currentIndex >= stories.size) return

    val currentStory = stories[currentIndex]

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { onNext() }, // Tap to go to next story
    ) {
        // Background image (9:16 aspect ratio like Instagram Stories)
        AsyncImage(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(currentStory.backgroundUrl.ifEmpty { currentStory.imageUrl.ifEmpty { R.drawable.ic_launcher_foreground } })
                    .crossfade(true)
                    .size(480) // Rural optimization
                    .build(),
            contentDescription = null,
            modifier =
                Modifier
                    .fillMaxSize()
                    .aspectRatio(9f / 16f),
            contentScale = ContentScale.Crop,
        )

        // Dark overlay
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
        )

        // Progress bars at top (Instagram style)
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            stories.forEachIndexed { index, _ ->
                LinearProgressIndicator(
                    progress =
                        when {
                            index < currentIndex -> 1f
                            index == currentIndex -> 0.8f // Simulated progress
                            else -> 0f
                        },
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(2.dp)
                            .clip(RoundedCornerShape(1.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f),
                )
            }
        }

        // Close button
        IconButton(
            onClick = onDismiss,
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 16.dp),
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = stringResource(id = R.string.close_button_desc),
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }

        // Story content
        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .fillMaxWidth(),
        ) {
            Text(
                text = if (isTeluguMode) currentStory.nameTelugu else currentStory.name,
                style =
                    MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isTeluguMode) currentStory.significanceTelugu else currentStory.significance,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )

            if (currentStory.discount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFF5722),
                ) {
                    Text(
                        text =
                            stringResource(
                                id = R.string.sankranti_offer_dynamic,
                                currentStory.discount,
                            ),
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }

            // Festival stickers (Telugu cultural elements)
            if (currentStory.stickers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(currentStory.stickers) { sticker ->
                        Text(
                            text = sticker,
                            fontSize = 24.sp,
                            modifier =
                                Modifier
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        RoundedCornerShape(8.dp),
                                    )
                                    .padding(8.dp),
                        )
                    }
                }
            }
        }

        // Navigation areas (left/right invisible buttons)
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onPrevious() },
            )
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onNext() },
            )
        }
    }
}

@Composable
private fun CulturalPostCard(
    post: CulturalPost,
    isTeluguMode: Boolean,
    onLike: (String) -> Unit,
    onComment: (String) -> Unit,
    onShare: (String) -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp),
    ) {
        Column {
            // Header with user info
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model =
                        ImageRequest.Builder(LocalContext.current)
                            .data(post.userImageUrl.ifEmpty { R.drawable.ic_launcher_foreground })
                            .crossfade(true)
                            .size(120)
                            .build(),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(32.dp)
                            .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isTeluguMode) post.userNameTelugu else post.userName,
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            ),
                    )
                    Text(
                        text = "#${post.culturalTag}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = Color(0xFF007AFF),
                    )
                }

                Text(
                    text = post.timeAgo,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color.Gray,
                )
            }

            // Post image
            if (post.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model =
                        ImageRequest.Builder(LocalContext.current)
                            .data(post.imageUrl)
                            .crossfade(true)
                            .size(480)
                            .build(),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                    contentScale = ContentScale.Crop,
                )
            }

            // Engagement buttons
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLike(post.id) },
                ) {
                    Icon(
                        if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (post.isLiked) Color(0xFFF44336) else Color.Black,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.likes.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onComment(post.id) },
                ) {
                    Icon(
                        Icons.Filled.ChatBubbleOutline,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.comments.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                IconButton(
                    onClick = { onShare(post.id) },
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.Black,
                    )
                }
            }

            // Post content
            Text(
                text = if (isTeluguMode) post.contentTelugu else post.content,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun GroupOrderCard(
    order: GroupOrder,
    isTeluguMode: Boolean,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = order.title,
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
            )

            Text(
                text = "₹${order.discountedPrice} (${((order.unitPrice - order.discountedPrice) * 100 / order.unitPrice).toInt()}% off)",
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                    ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            val progress =
                if (order.targetQuantity > 0) {
                    order.currentQuantity.toFloat() / order.targetQuantity.toFloat()
                } else {
                    0f
                }

            LinearProgressIndicator(
                progress = progress,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFFE0E0E0),
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${order.currentQuantity}/${order.targetQuantity} ${if (isTeluguMode) "చేరారు" else "joined"}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = Color.Gray,
            )
        }
    }
}

@Composable
fun LeaderboardsSection(isTeluguMode: Boolean) {
    val leaderboards =
        listOf(
            "Fowl A" to 100,
            "Fowl B" to 80,
            "Rooster C" to 120,
            "Hen D" to 95,
        ).sortedByDescending { it.second } // Sort by score

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (leaderboards.isEmpty()) {
                Text(if (isTeluguMode) "లీడర్‌బోర్డ్‌లు ఇంకా అందుబాటులో లేవు" else "Leaderboards not available yet.")
            } else {
                leaderboards.forEachIndexed { index, (fowl, score) ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("${index + 1}. $fowl", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "$score ${if (isTeluguMode) "పాయింట్లు" else "points"}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    if (index < leaderboards.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CompetitionManagementSection(isTeluguMode: Boolean) {
    // Implementation for competition management section
}

@Composable
fun CulturalShowcasesSection(isTeluguMode: Boolean) {
    // Implementation for cultural showcases section
}

// Data loading functions
private suspend fun loadFestivalStories(): List<FestivalStory> {
    return StabilityManager.safeExecute(
        operation = {
            // Mock data - replace with actual Parse query
            listOf(
                FestivalStory(
                    id = "1",
                    name = "Sankranti",
                    nameTelugu = "సంక్రాంతి",
                    imageUrl = "",
                    backgroundUrl = "",
                    significance = "Harvest festival celebrating the sun's journey",
                    significanceTelugu = "సూర్యుడి ప్రయాణాన్ని జరుపుకునే పంట పండుగ",
                    date = Date(),
                    discount = 25,
                    stickers = listOf("🪁", "🌾", "🐂", "💰"),
                ),
                FestivalStory(
                    id = "2",
                    name = "Holi",
                    nameTelugu = "హోళీ",
                    imageUrl = "",
                    backgroundUrl = "",
                    significance = "Festival of colors and spring",
                    significanceTelugu = "రంగుల మరియు వసంత ఋతువు పండుగ",
                    date = Date(),
                    discount = 15,
                    stickers = listOf("🎨", "🌈", "🌸", "🎉"),
                ),
            )
        },
        fallback = emptyList(),
    )
}

private suspend fun loadCulturalPosts(): List<CulturalPost> {
    return StabilityManager.safeExecute(
        operation = {
            // Mock data - replace with actual Parse query
            listOf(
                CulturalPost(
                    id = "1",
                    title = "Traditional Rooster Competition",
                    titleTelugu = "సాంప్రదాయ కోడిపోటీ",
                    content = "Celebrating our heritage with traditional rooster competitions",
                    contentTelugu = "సాంప్రదాయ కోడిపోటీలతో మన వారసత్వాన్ని జరుపుకుంటున్నాము",
                    imageUrl = "",
                    userName = "Farmer Ram",
                    userNameTelugu = "రైతు రామ్",
                    userImageUrl = "",
                    likes = 156,
                    comments = 23,
                    timeAgo = "3h",
                    culturalTag = "సంక్రాంతి",
                ),
            )
        },
        fallback = emptyList(),
    )
}

private suspend fun loadGroupOrders(): List<GroupOrder> {
    return emptyList() // Implementation depends on existing GroupOrder model
}

private suspend fun likeCulturalPost(postId: String) {
    // Implementation for liking cultural posts
}
