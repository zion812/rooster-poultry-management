package com.example.rooster

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.launch

// Update the ProfileScreen imports and logout logic
import com.example.rooster.data.AuthRepository
import android.util.Log

// Instagram-style data models for profile
data class ProfilePost(
    val id: String,
    val imageUrl: String,
    val type: String, // "fowl", "listing", "post"
    val title: String,
    val likes: Int,
    val comments: Int,
    val isVideo: Boolean = false,
)

data class ProfileStats(
    val fowlCount: Int,
    val listingsCount: Int,
    val followersCount: Int,
    val achievementsCount: Int,
)

data class UserBio(
    val name: String,
    val nameTelugu: String,
    val location: String,
    val locationTelugu: String,
    val bio: String,
    val bioTelugu: String,
    val isVerified: Boolean,
    val joinDate: String,
    val specializations: List<String>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
    onLogout: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }

    // Instagram-style profile data
    var userBio by remember { mutableStateOf<UserBio?>(null) }
    var profileStats by remember { mutableStateOf(ProfileStats(0, 0, 0, 0)) }
    var profilePosts by remember { mutableStateOf<List<ProfilePost>>(emptyList()) }
    var achievements by remember { mutableStateOf<List<String>>(emptyList()) }

    // Load profile data
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoading = true
            try {
                userBio = loadUserBio()
                profileStats = loadProfileStats()
                profilePosts = loadProfilePosts()
                achievements = loadAchievements()
            } catch (e: Exception) {
                // Handle error gracefully
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White), // Instagram white background
    ) {
        // Instagram-style header
        ProfileHeader(
            isTeluguMode = isTeluguMode,
            onLanguageToggle = onLanguageToggle,
            onLogout = onLogout,
            navController = navController,
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
            ) {
                // Profile info section (Instagram style)
                item {
                    userBio?.let { bio ->
                        InstagramStyleProfileInfo(
                            userBio = bio,
                            profileStats = profileStats,
                            isTeluguMode = isTeluguMode,
                            achievements = achievements,
                            onLogout = onLogout,
                            navController = navController,
                        )
                    }
                }

                // Tab selector (Instagram style)
                item {
                    ProfileTabRow(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        isTeluguMode = isTeluguMode,
                    )
                }

                // Content grid based on selected tab
                item {
                    when (selectedTab) {
                        0 ->
                            ProfilePostsGrid(
                                posts = profilePosts.filter { it.type == "fowl" },
                                isTeluguMode = isTeluguMode,
                            )

                        1 ->
                            ProfilePostsGrid(
                                posts = profilePosts.filter { it.type == "listing" },
                                isTeluguMode = isTeluguMode,
                            )

                        2 ->
                            ProfilePostsGrid(
                                posts = profilePosts.filter { it.type == "post" },
                                isTeluguMode = isTeluguMode,
                            )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
    onLogout: () -> Unit,
    navController: NavController,
) {
    var showMenu by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = if (isTeluguMode) R.string.profile else R.string.profile),
            style =
                MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                ),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Language toggle
            IconButton(onClick = onLanguageToggle) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = stringResource(id = if (isTeluguMode) R.string.switch_to_english else R.string.switch_to_telugu),
                )
            }

            // Menu button with logout option
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = null, // Decorative
                        tint = Color.Black,
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                ) {
                    // Navigation items added here
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(id = if (isTeluguMode) R.string.consult_vet else R.string.consult_vet))
                        },
                        onClick = {
                            showMenu = false
                            navController.navigate(NavigationRoute.VET_CONSULTATION.route)
                        },
                        leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null) },
                    )
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(id = if (isTeluguMode) R.string.iot_dashboard else R.string.iot_dashboard))
                        },
                        onClick = {
                            showMenu = false
                            navController.navigate(NavigationRoute.IOT_DASHBOARD.route)
                        },
                        leadingIcon = { Icon(Icons.Default.Sensors, contentDescription = null) },
                    )
                    DropdownMenuItem(
                        text = { Text("Diagnostics") },
                        onClick = {
                            showMenu = false
                            navController.navigate("diagnostics")
                        },
                        leadingIcon = { Icon(Icons.Default.BugReport, contentDescription = null) },
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(id = if (isTeluguMode) R.string.settings else R.string.settings))
                        },
                        onClick = {
                            showMenu = false
                            navController.navigate(NavigationRoute.SETTINGS.route)
                        },
                        leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    )
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(id = if (isTeluguMode) R.string.help_support_title else R.string.help_support_title))
                        },
                        onClick = {
                            showMenu = false
                            navController.navigate(NavigationRoute.HELP.route)
                        },
                        leadingIcon = { Icon(Icons.Filled.HelpOutline, contentDescription = null) },
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(id = if (isTeluguMode) R.string.logout else R.string.logout),
                                color = MaterialTheme.colorScheme.error, // Error color for logout
                            )
                        },
                        onClick = {
                            showMenu = false
                            coroutineScope.launch {
                                val authRepository = AuthRepository()
                                val result = authRepository.logout()
                                result.fold(
                                    onSuccess = {
                                        Log.d("ProfileScreen", "Logout successful")
                                        onLogout()
                                    },
                                    onFailure = { exception ->
                                        Log.e("ProfileScreen", "Logout failed", exception)
                                        // Force logout navigation even if Parse logout failed
                                        onLogout()
                                    },
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.ExitToApp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun InstagramStyleProfileInfo(
    userBio: UserBio,
    profileStats: ProfileStats,
    isTeluguMode: Boolean,
    achievements: List<String>,
    onLogout: () -> Unit,
    navController: NavController,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        // Profile picture and stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Profile picture
            Box {
                AsyncImage(
                    model =
                        ImageRequest.Builder(LocalContext.current)
                            .data(R.drawable.ic_launcher_foreground) // Replace with actual profile image
                            .crossfade(true)
                            .size(240) // 2G optimization
                            .build(),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.2f))
                            .border(2.dp, Color(0xFFFF5722), CircleShape),
                    contentScale = ContentScale.Crop,
                )

                // Verified badge
                if (userBio.isVerified) {
                    Surface(
                        modifier =
                            Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-4).dp, y = (-4).dp),
                        shape = CircleShape,
                        color = Color(0xFF007AFF),
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color.White,
                            modifier =
                                Modifier
                                    .size(20.dp)
                                    .padding(4.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Stats (Instagram style)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ProfileStatItem(
                    count = profileStats.fowlCount,
                    label = stringResource(id = if (isTeluguMode) R.string.fowl_tab else R.string.fowl_tab),
                )
                ProfileStatItem(
                    count = profileStats.listingsCount,
                    label = stringResource(id = if (isTeluguMode) R.string.listings else R.string.listings),
                )
                ProfileStatItem(
                    count = profileStats.followersCount,
                    label = stringResource(id = if (isTeluguMode) R.string.followers else R.string.followers),
                )
                ProfileStatItem(
                    count = profileStats.achievementsCount,
                    label = stringResource(id = if (isTeluguMode) R.string.awards else R.string.awards),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Name and location
        Text(
            text = if (isTeluguMode) userBio.nameTelugu else userBio.name,
            style =
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = if (isTeluguMode) userBio.locationTelugu else userBio.location,
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    fontSize = 14.sp,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Bio
        Text(
            text = if (isTeluguMode) userBio.bioTelugu else userBio.bio,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )

        if (userBio.specializations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(userBio.specializations) { specialization ->
                    Surface(
                        color = Color(0xFFFF5722).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = specialization,
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFD84315),
                                    fontWeight = FontWeight.Medium,
                                ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            }
        }

        // Achievements highlights
        if (achievements.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                achievements.take(4).forEach { achievement ->
                    Text(
                        text = achievement,
                        fontSize = 16.sp,
                        modifier =
                            Modifier
                                .background(
                                    Color.White,
                                    RoundedCornerShape(8.dp),
                                )
                                .border(
                                    1.dp,
                                    Color.Gray.copy(alpha = 0.3f),
                                    RoundedCornerShape(8.dp),
                                )
                                .padding(6.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons (Instagram style)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = { navController.navigate("profile_edit") },
                modifier = Modifier.weight(1f),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color.Gray.copy(alpha = 0.1f),
                        contentColor = Color.Black,
                    ),
                shape = RoundedCornerShape(4.dp),
            ) {
                Text(
                    text = stringResource(id = if (isTeluguMode) R.string.edit_profile else R.string.edit_profile),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Button(
                onClick = { /* Share profile */ },
                modifier = Modifier.weight(1f),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color.Gray.copy(alpha = 0.1f),
                        contentColor = Color.Black,
                    ),
                shape = RoundedCornerShape(4.dp),
            ) {
                Text(
                    text = stringResource(id = if (isTeluguMode) R.string.share_profile else R.string.share_profile),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        // Removed redundant Logout button from here as it's in the header menu
        // Spacer(modifier = Modifier.height(8.dp))
        // Button(...) { ... }
    }
}

@Composable
private fun ProfileStatItem(
    count: Int,
    label: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = count.toString(),
            style =
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
        )
        Text(
            text = label,
            style =
                MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                ),
            color = Color.Gray,
        )
    }
}

@Composable
private fun ProfileTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    isTeluguMode: Boolean,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        val tabs =
            listOf(
                Triple(Icons.Default.Pets, stringResource(id = if (isTeluguMode) R.string.fowl_tab else R.string.fowl_tab), "fowl"),
                Triple(Icons.Default.Store, stringResource(id = if (isTeluguMode) R.string.listings else R.string.listings), "listing"),
                Triple(
                    Icons.Default.PhotoLibrary,
                    stringResource(id = if (isTeluguMode) R.string.posts_tab else R.string.posts_tab),
                    "post",
                ),
            )

        tabs.forEachIndexed { index, (icon, label, _) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .clickable { onTabSelected(index) }
                        .padding(vertical = 12.dp)
                        .weight(1f),
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (selectedTab == index) Color.Black else Color.Gray,
                    modifier = Modifier.size(24.dp),
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tab indicator line (Instagram style)
                if (selectedTab == index) {
                    Box(
                        modifier =
                            Modifier
                                .height(1.dp)
                                .width(24.dp)
                                .background(Color.Black),
                    )
                }
            }
        }
    }

    HorizontalDivider(
        color = Color.Gray.copy(alpha = 0.3f),
        thickness = 0.5.dp,
    )
}

@Composable
private fun ProfilePostsGrid(
    posts: List<ProfilePost>,
    isTeluguMode: Boolean,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // Instagram 3-column grid
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 400.dp, max = 800.dp),
        // Dynamic height
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        items(posts) { post ->
            ProfilePostItem(
                post = post,
                onClick = { /* Navigate to post details */ },
            )
        }
    }
}

@Composable
private fun ProfilePostItem(
    post: ProfilePost,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .aspectRatio(1f) // Square grid like Instagram
                .clickable { onClick() },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model =
                    ImageRequest.Builder(LocalContext.current)
                        .data(post.imageUrl.ifEmpty { R.drawable.ic_launcher_foreground })
                        .crossfade(true)
                        .size(240) // 2G optimization
                        .build(),
                contentDescription = post.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            // Overlay for video posts
            if (post.isVideo) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(20.dp),
                )
            }

            // Engagement overlay (Instagram style)
            if (post.likes > 0 || post.comments > 0) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.BottomStart)
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                RoundedCornerShape(topEnd = 8.dp),
                            )
                            .padding(4.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        if (post.likes > 0) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp),
                            )
                            Text(
                                text = post.likes.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        if (post.comments > 0) {
                            Icon(
                                Icons.Filled.ChatBubbleOutline,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp),
                            )
                            Text(
                                text = post.comments.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

// Data loading functions with rural optimization
private suspend fun loadUserBio(): UserBio {
    return StabilityManager.safeExecute(
        operation = {
            val currentUser = ParseUser.getCurrentUser()
            UserBio(
                name = currentUser?.username ?: "Unknown User",
                nameTelugu =
                    currentUser?.getString("usernameTelugu") ?: currentUser?.username
                        ?: "అపరిచిత వినియోగదారు",
                location = currentUser?.getString("location") ?: "Unknown Location",
                locationTelugu = currentUser?.getString("locationTelugu") ?: "అపరిచిత ప్రాంతం",
                bio = currentUser?.getString("bio") ?: "Passionate poultry farmer",
                bioTelugu =
                    currentUser?.getString("bioTelugu")
                        ?: "మిక్కిలిపరిచిత కోడిపెంపకందారుడు",
                isVerified = currentUser?.getBoolean("isVerified") == true,
                joinDate = currentUser?.createdAt?.toString()?.substringBefore("T") ?: "",
                specializations =
                    currentUser?.getList<String>("specializations")
                        ?: listOf("కోడి పెంపకం", "సేంద్రీయ వ్యవసాయం"),
            )
        },
        fallback =
            UserBio(
                name = "Unknown User",
                nameTelugu = "అపరిచిత వినియోగదారు",
                location = "Unknown Location",
                locationTelugu = "అపరిచిత ప్రాంతం",
                bio = "Passionate poultry farmer",
                bioTelugu = "మిక్కిలిపరిచిత కోడిపెంపకందారుడు",
                isVerified = false,
                joinDate = "",
                specializations = listOf("కోడి పెంపకం"),
            ),
    )
}

private suspend fun loadProfileStats(): ProfileStats {
    return StabilityManager.safeExecute(
        operation = {
            val currentUser = ParseUser.getCurrentUser()

            // Count fowls
            val fowlQuery = ParseQuery.getQuery<ParseObject>("Fowl")
            fowlQuery.whereEqualTo("owner", currentUser)
            val fowlCount = fowlQuery.count()

            // Count listings
            val listingQuery = ParseQuery.getQuery<ParseObject>("Listing")
            listingQuery.whereEqualTo("seller", currentUser)
            val listingsCount = listingQuery.count()

            // Simulate followers and achievements
            ProfileStats(
                fowlCount = fowlCount,
                listingsCount = listingsCount,
                followersCount = 156, // Mock data
                achievementsCount = 8, // Mock data
            )
        },
        fallback = ProfileStats(0, 0, 0, 0),
    )
}

private suspend fun loadProfilePosts(): List<ProfilePost> {
    return StabilityManager.safeExecute(
        operation = {
            val currentUser = ParseUser.getCurrentUser()
            val posts = mutableListOf<ProfilePost>()

            // Load fowl posts
            val fowlQuery = ParseQuery.getQuery<ParseObject>("Fowl")
            fowlQuery.whereEqualTo("owner", currentUser)
            fowlQuery.limit = 12
            fowlQuery.orderByDescending("createdAt")

            val fowls = fowlQuery.find()
            fowls.forEach { fowl ->
                posts.add(
                    ProfilePost(
                        id = fowl.objectId,
                        imageUrl = fowl.getParseFile("image")?.url ?: "",
                        type = "fowl",
                        title = fowl.getString("name") ?: "Unknown",
                        likes = fowl.getInt("likes"),
                        comments = fowl.getInt("comments"),
                    ),
                )
            }

            // Load listing posts
            val listingQuery = ParseQuery.getQuery<ParseObject>("Listing")
            listingQuery.whereEqualTo("seller", currentUser)
            listingQuery.limit = 12
            listingQuery.orderByDescending("createdAt")

            val listings = listingQuery.find()
            listings.forEach { listing ->
                posts.add(
                    ProfilePost(
                        id = listing.objectId,
                        imageUrl = listing.getParseFile("image")?.url ?: "",
                        type = "listing",
                        title = listing.getString("title") ?: "Unknown",
                        likes = listing.getInt("likes"),
                        comments = listing.getInt("comments"),
                    ),
                )
            }

            // Load social posts
            val postQuery = ParseQuery.getQuery<ParseObject>("Post")
            postQuery.whereEqualTo("user", currentUser)
            postQuery.limit = 12
            postQuery.orderByDescending("createdAt")

            val socialPosts = postQuery.find()
            socialPosts.forEach { post ->
                posts.add(
                    ProfilePost(
                        id = post.objectId,
                        imageUrl = post.getParseFile("image")?.url ?: "",
                        type = "post",
                        title = post.getString("content") ?: "Post",
                        likes = post.getInt("likes"),
                        comments = post.getInt("comments"),
                    ),
                )
            }

            posts
        },
        fallback = emptyList(),
    )
}

private suspend fun loadAchievements(): List<String> {
    return StabilityManager.safeExecute(
        operation = {
            // Mock achievements - replace with actual Parse query
            listOf("🏆", "🥇", "🌟", "🎖️", "🏅", "👑")
        },
        fallback = emptyList(),
    )
}

// Removed EditProfileDialog composable; editing is now in a dedicated screen
