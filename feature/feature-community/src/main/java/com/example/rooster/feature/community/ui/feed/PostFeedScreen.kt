package com.example.rooster.feature.community.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList // For feed type selection
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rooster.feature.community.domain.repository.FeedType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostFeedScreen(
    viewModel: PostFeedViewModel = hiltViewModel(),
    onNavigateToCreatePost: () -> Unit,
    onNavigateToPostDetail: (postId: String) -> Unit,
    onNavigateToUserProfile: (userId: String) -> Unit
    // TODO: Add callbacks for like, comment, share if handled by a parent coordinator/ViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState() // Collect current user ID
    var showFeedTypeMenu by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collect { event ->
            when (event) {
                is PostFeedSingleEvent.LikeUnlikeError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Add SnackbarHost
        topBar = {
            TopAppBar(
                title = { Text("Community Feed") },
                actions = {
                    IconButton(onClick = { showFeedTypeMenu = true }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filter Feed")
                    }
                    DropdownMenu(
                        expanded = showFeedTypeMenu,
                        onDismissRequest = { showFeedTypeMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Global Recent") },
                            onClick = {
                                viewModel.setFeedType(FeedType.GLOBAL_RECENT)
                                showFeedTypeMenu = false
                            }
                        )
                        // Add more feed types as needed
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreatePost) {
                Icon(Icons.Filled.Add, contentDescription = "Create Post")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (uiState) {
                is PostFeedUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is PostFeedUiState.Error -> {
                    val message = (uiState as PostFeedUiState.Error).message
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center).padding(32.dp)
                    )
                }
                is PostFeedUiState.Success -> {
                    val posts = (uiState as PostFeedUiState.Success).posts
                    if (posts.isEmpty()) {
                        Text(
                            text = "No posts available.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp),
                        ) {
                            items(posts, key = { it.postId }) { post ->
                                PostItem(
                                    post = post,
                                    currentUserId = currentUserId,
                                    onPostClick = { onNavigateToPostDetail(post.postId) },
                                    onAuthorClick = { onNavigateToUserProfile(post.authorUserId) },
                                    onLikeClick = { viewModel.onLikeClicked(post.postId) },
                                    onUnlikeClick = { viewModel.onUnlikeClicked(post.postId) },
                                    onCommentClick = { onNavigateToPostDetail(post.postId) /* Or specific comment section */ },
                                    onShareClick = { /* TODO: Implement share functionality */ }
                                )
                                Divider(thickness = 0.5.dp, modifier = Modifier.padding(top=4.dp, bottom = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// No specific Preview for PostFeedScreen here as it's complex with ViewModel.
// Individual PostItem previews are more useful, or UI tests for the screen.
