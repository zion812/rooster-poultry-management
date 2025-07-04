package com.example.rooster.feature.news.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
// import com.google.accompanist.swiperefresh.SwipeRefresh
// import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

// Using Material's pullRefresh as Accompanist SwipeRefresh might be deprecated
// in favor of foundation/material solutions.

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun NewsAndUpdatesScreen(
    viewModel: NewsAndUpdatesViewModel = hiltViewModel()
    // navController: NavController // Add if navigation from this screen is needed
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { viewModel.onRefresh() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agricultural News & Updates") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            CategoryFilterChips(
                categories = uiState.categories,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { category -> viewModel.onCategorySelected(category) }
            )

            Box(Modifier.pullRefresh(pullRefreshState)) {
                when {
                    uiState.isLoading && uiState.newsItems.isEmpty() && !uiState.isRefreshing -> {
                        CenteredContent {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Loading News...")
                        }
                    }
                    uiState.error != null && uiState.newsItems.isEmpty() -> {
                        CenteredContent {
                            Text(
                                text = uiState.error ?: "An unknown error occurred.",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.onRetry() }) {
                                Text("Retry")
                            }
                        }
                    }
                    else -> {
                        NewsList(
                            newsItems = uiState.newsItems,
                            isLoadingMore = uiState.isLoadingMore,
                            hasMore = uiState.hasMore,
                            onLoadMore = { viewModel.onLoadMore() },
                            onItemClick = { newsItem ->
                                newsItem.link?.let { url ->
                                    try {
                                        uriHandler.openUri(url)
                                    } catch (e: Exception) {
                                        // Fallback or show error if URI opening fails
                                        // For now, just log or Toast
                                        // Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
                                        println("Error opening URI: $url, Exception: $e")
                                    }
                                }
                            }
                        )
                    }
                }
                PullRefreshIndicator(uiState.isRefreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
            }
        }
    }
}

@Composable
fun CategoryFilterChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                label = { Text(category.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }) }
            )
        }
    }
}

@Composable
fun NewsList(
    newsItems: List<NewsItemUiModel>,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    onLoadMore: () -> Unit,
    onItemClick: (NewsItemUiModel) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(newsItems, key = { it.id }) { newsItem ->
            NewsItemCard(newsItem = newsItem, onClick = { onItemClick(newsItem) })
        }

        if (hasMore || isLoadingMore) {
            item {
                LaunchedEffect(listState) { // More reliable way to trigger load more
                    snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                        .collect { visibleItems ->
                            val lastVisibleItem = visibleItems.lastOrNull()
                            if (lastVisibleItem != null && lastVisibleItem.index == newsItems.size - 1 && hasMore && !isLoadingMore) {
                                onLoadMore()
                            }
                        }
                }
                if (isLoadingMore) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        if (!hasMore && newsItems.isNotEmpty() && !isLoadingMore) {
             item {
                Text(
                    text = "You've reached the end!",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        if (newsItems.isEmpty() && !isLoadingMore && !hasMore ) { // Covers empty after filter
            item {
                 CenteredContent(modifier = Modifier.padding(top=60.dp)) {
                    Text("No news or updates found.")
                 }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsItemCard(newsItem: NewsItemUiModel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            newsItem.imageUrl?.let {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(it)
                        .crossfade(true)
                        .build(),
                    contentDescription = newsItem.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = newsItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = newsItem.source,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = " • ${newsItem.formattedDate}", // Use pre-formatted date
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = newsItem.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SuggestionChip(
                        onClick = { /* Non-interactive or could filter by this category */ },
                        label = { Text(newsItem.category) }
                    )
                    if (newsItem.link != null) {
                        Text(
                            text = "Read More",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.clickable(onClick = onClick) // Make sure this also triggers card's onClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CenteredContent(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

// Date formatting utility - can be moved to a common utils package
// For now, already handled in mock data generation in ViewModel by creating 'formattedDate'
// import java.text.SimpleDateFormat
// import java.util.Locale
// fun formatDate(dateString: String, outputFormat: String = "MMM dd, yyyy"): String {
//    return try {
//        val inputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val date = inputSdf.parse(dateString)
//        date?.let { SimpleDateFormat(outputFormat, Locale.getDefault()).format(it) } ?: dateString
//    } catch (e: Exception) {
//        dateString // fallback to original if parsing fails
//    }
// }
