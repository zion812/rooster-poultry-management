package com.example.rooster.feature.community.ui.createpost

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: CreatePostViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onPostCreatedSuccessfully: (postId: String) -> Unit // Pass new post ID back
) {
    val formState by viewModel.formState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.formState.collectLatest { state ->
            if (state.submissionSuccess && state.createdPostId != null) {
                snackbarHostState.showSnackbar("Post created successfully!")
                onPostCreatedSuccessfully(state.createdPostId)
                viewModel.resetSubmissionStatus() // Reset after handling
            } else if (state.submissionError != null) {
                snackbarHostState.showSnackbar("Error: ${state.submissionError}")
                viewModel.resetSubmissionStatus() // Reset after handling
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Post") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.submitPost() }, enabled = !formState.isSubmitting) {
                        if (formState.isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Filled.Send, contentDescription = "Submit Post")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = formState.contentText,
                onValueChange = viewModel::onContentTextChange,
                label = { Text("What's on your mind?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Takes available space
                placeholder = { Text("Share your thoughts, questions, or updates with the community...") },
                isError = formState.submissionError?.contains("content", ignoreCase = true) == true
            )

            // TODO: Add UI for image/video selection, tags, location later
            // For now, just a text post.

            Spacer(modifier = Modifier.height(16.dp))
            // Submit button is in TopAppBar actions for this design
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreatePostScreen_Initial() {
    MaterialTheme {
        CreatePostScreen(onNavigateBack = {}, onPostCreatedSuccessfully = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreatePostScreen_Submitting() {
    // To preview submitting state, you'd ideally pass a mock ViewModel
    // or directly set the formState for a preview Composable.
    val submittingState = CreatePostFormState(contentText = "Some text...", isSubmitting = true)
    MaterialTheme {
        // Simplified preview, actual screen uses Hilt ViewModel
         Scaffold(
            topBar = { TopAppBar(title = { Text("Create Post") }, actions = { CircularProgressIndicator(modifier = Modifier.size(24.dp))})}
        ) { padding ->
            Column(Modifier.padding(padding).padding(16.dp)) {
                 OutlinedTextField(
                    value = submittingState.contentText,
                    onValueChange = {},
                    label = { Text("What's on your mind?") },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    readOnly = true
                )
            }
         }
    }
}
