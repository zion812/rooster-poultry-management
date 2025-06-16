package com.example.rooster.ui.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.rooster.models.MediaType
import com.example.rooster.viewmodel.PostViewModel

@Composable
fun CreatePostScreen(
    postViewModel: PostViewModel = viewModel(),
    onPostSuccess: (postId: String) -> Unit,
) {
    val context = LocalContext.current
    var postText by remember { mutableStateOf(TextFieldValue("")) }
    var mediaUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var mediaTypes by remember { mutableStateOf<List<MediaType>>(emptyList()) }
    val isPosting by postViewModel.isPosting.collectAsState()

    // Photo picker launcher
    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let {
                val urlString = it.toString()
                mediaUrls = mediaUrls + urlString
                mediaTypes = mediaTypes + MediaType.IMAGE
            }
        }

    // Video picker launcher
    val videoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let {
                val urlString = it.toString()
                mediaUrls = mediaUrls + urlString
                mediaTypes = mediaTypes + MediaType.VIDEO
            }
        }

    // Multiple photos picker launcher (for selecting multiple photos at once)
    val multiplePhotosPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
        ) { uris: List<Uri> ->
            if (uris.isNotEmpty()) {
                val newUrls = uris.map { it.toString() }
                val newTypes = uris.map { MediaType.IMAGE }
                mediaUrls = mediaUrls + newUrls
                mediaTypes = mediaTypes + newTypes
            }
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Create Post", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = postText,
            onValueChange = { postText = it },
            label = { Text("What's on your mind?") },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(120.dp),
        )
        // Media pickers
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { photoPickerLauncher.launch("image/*") }) {
                Text("Add Photo")
            }
            Button(onClick = { videoPickerLauncher.launch("video/*") }) {
                Text("Add Video")
            }
            Button(onClick = { multiplePhotosPickerLauncher.launch("image/*") }) {
                Text("Add Photos")
            }
        }
        // Additional media options row
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                // Audio support can be added later with proper media recorder
                // For now, show a placeholder toast or snackbar
            }) {
                Text("Add Audio")
            }
        }
        // Grid preview
        if (mediaUrls.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(200.dp),
            ) {
                itemsIndexed(mediaUrls) { index, url ->
                    Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.padding(4.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(url),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                        )
                        IconButton(onClick = {
                            mediaUrls = mediaUrls.toMutableList().also { it.removeAt(index) }
                            mediaTypes = mediaTypes.toMutableList().also { it.removeAt(index) }
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove")
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                postViewModel.createPost(
                    text = postText.text,
                    mediaUrls = mediaUrls,
                    mediaTypes = mediaTypes,
                ) { newId -> onPostSuccess(newId) }
            },
            enabled = !isPosting && postText.text.isNotBlank(),
            modifier = Modifier.align(Alignment.End),
        ) {
            if (isPosting) {
                CircularProgressIndicator(Modifier.size(20.dp), color = Color.White)
            } else {
                Text("Post")
            }
        }
    }
}
