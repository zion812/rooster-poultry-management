package com.example.rooster.feature.community.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.VerifiedUser // For verified badge
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rooster.core.common.user.UserIdProvider // Assuming this is how current user ID is obtained
import com.example.rooster.feature.community.domain.model.CommunityUserProfile
// TODO: Import Post model and Post item composable when posts are displayed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityProfileScreen(
    viewModel: CommunityProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onEditProfile: (userId: String) -> Unit, // If current user is viewing their own profile
    // TODO: Inject or get currentUserId to determine if it's own profile
    // currentUserIdProvider: UserIdProvider
) {
    val uiState by viewModel.uiState.collectAsState()
    // val currentUserId = currentUserIdProvider.getCurrentUserId() // Example

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState is ProfileUiState.Success) {
                        val profile = (uiState as ProfileUiState.Success).profile
                        // TODO: Check if profile.userId == currentUserId
                        // if (profile.userId == currentUserId) {
                        IconButton(onClick = { onEditProfile(profile.userId) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit Profile")
                        }
                        // }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProfileUiState.Success -> {
                    ProfileContent(profile = state.profile)
                }
                is ProfileUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileContent(profile: CommunityUserProfile, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(profile.profilePictureUrl)
                // .placeholder(R.drawable.default_profile_placeholder) // TODO: Add placeholder
                // .error(R.drawable.default_profile_placeholder)
                .crossfade(true)
                .build(),
            contentDescription = "${profile.displayName} profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = profile.displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            if (profile.isVerifiedFarmer) { // Example of a badge
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Filled.VerifiedUser,
                    contentDescription = "Verified Farmer",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        profile.farmName?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        profile.location?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        profile.bio?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        ProfileStatsRow(profile = profile)

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Text(
            text = "Posts",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        // TODO: Placeholder for user's posts list/grid
        Text("User's posts will be displayed here.")
        // Example:
        // if (posts.isEmpty()) {
        //     Text("No posts yet.")
        // } else {
        //     LazyColumn(...) { items(posts) { post -> PostListItem(post) } }
        // }
    }
}

@Composable
fun ProfileStatsRow(profile: CommunityUserProfile) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        StatItem("Posts", profile.postCount.toString())
        StatItem("Followers", profile.followerCount.toString())
        StatItem("Following", profile.followingCount.toString())
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileContent() {
    val sampleProfile = CommunityUserProfile(
        userId = "user123",
        displayName = "Rooster Raju",
        profilePictureUrl = "https://via.placeholder.com/120",
        bio = "Passionate Nattu Kodi farmer from Krishna district. Specializing in Aseel breed for over 10 years. Always happy to share knowledge and connect with fellow enthusiasts!",
        location = "Krishna District, AP",
        farmName = "Raju Poultry Farm",
        interests = listOf("Aseel Breeding", "Organic Feed", "Sankranti Events"),
        followerCount = 1250,
        followingCount = 300,
        postCount = 75,
        lastActiveTimestamp = System.currentTimeMillis(),
        joinDateTimestamp = System.currentTimeMillis() - (365L * 24 * 60 * 60 * 1000), // Joined 1 year ago
        isVerifiedFarmer = true,
        isEnthusiast = true
    )
    MaterialTheme {
        ProfileContent(profile = sampleProfile)
    }
}
