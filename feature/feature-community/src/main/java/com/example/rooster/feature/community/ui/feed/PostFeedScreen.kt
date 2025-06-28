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
    var showFeedTypeMenu by remember { mutableStateOf(false) }

    Scaffold(
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
                        // TODO: Add options for USER_SPECIFIC (needs input), TAG_SPECIFIC (needs input), FOLLOWING
                        // Example for USER_SPECIFIC (would need a dialog to get userId)
                        // DropdownMenuItem(
                        //     text = { Text("User's Posts") },
                        //     onClick = {
                        //         // TODO: Show dialog to get userId, then:
                        //         // viewModel.setFeedType(FeedType.USER_SPECIFIC, "some_user_id")
                        //         showFeedTypeMenu = false
                        //     }
                        // )
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
            when (val state = uiState) {
                is PostFeedUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is PostFeedUiState.Success -> {
                    if (state.posts.isEmpty()) {
                        Text(
                            text = "No posts yet. Be the first to share!",
                            modifier = Modifier.align(Alignment.Center).padding(16.dp),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp),
                           // verticalArrangement = Arrangement.spacedBy(8.dp) // Spacing handled by PostItem padding
                        ) {
                            items(state.posts, key = { it.postId }) { post ->
                                PostItem(
                                    post = post,
                                    onPostClick = { onNavigateToPostDetail(post.postId) },
                                    onAuthorClick = { onNavigateToUserProfile(post.authorUserId) },
                                    onLikeClick = { /* TODO: viewModel.likePost(post.postId) */ },
                                    onCommentClick = { onNavigateToPostDetail(post.postId) /* Or specific comment section */ },
                                    onShareClick = { /* TODO: Implement share functionality */ }
                                )
                                Divider(thickness = 0.5.dp, modifier = Modifier.padding(top=4.dp, bottom = 4.dp))
                            }
                            // TODO: Add item for loading more / pagination
                        }
                    }
                }
                is PostFeedUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchPosts(forceRefresh = true) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

// No specific Preview for PostFeedScreen here as it's complex with ViewModel.
// Individual PostItem previews are more useful, or UI tests for the screen.
