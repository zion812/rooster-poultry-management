package com.example.rooster

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Comprehensive Messaging Screen for Rooster Project
 * Integrates personal, group, and community messaging
 * Optimized for rural farmers using 2G networks and low-end devices
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComprehensiveMessagingScreen(onNavigateBack: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Messaging manager integration
    val messagingManager = remember { MessagingManager.getInstance(context) }

    // Message state flows
    val personalMessages by messagingManager.personalMessages.collectAsState()
    val groupMessages by messagingManager.groupMessages.collectAsState()
    val communityMessages by messagingManager.communityMessages.collectAsState()

    // Tab state
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Personal", "Groups", "Community")

    // Network quality assessment
    var networkQuality by remember { mutableStateOf("Good") }

    LaunchedEffect(Unit) {
        try {
            networkQuality = assessNetworkQuality()
        } catch (e: Exception) {
            networkQuality = "Unknown"
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        // Top App Bar with Network Status
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Messages",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Network: $networkQuality",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        // Navigate to messaging settings
                    },
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            },
        )

        // Network Quality Indicator
        NetworkQualityBanner(networkQuality)

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(title)
                            // Show message count badges
                            when (index) {
                                0 -> {
                                    val unreadCount = personalMessages.count { !it.isRead }
                                    if (unreadCount > 0) {
                                        MessageCountBadge(unreadCount)
                                    }
                                }

                                1 -> {
                                    if (groupMessages.isNotEmpty()) {
                                        MessageCountBadge(groupMessages.size)
                                    }
                                }

                                2 -> {
                                    if (communityMessages.isNotEmpty()) {
                                        MessageCountBadge(communityMessages.size)
                                    }
                                }
                            }
                        }
                    },
                )
            }
        }

        // Content based on selected tab
        when (selectedTab) {
            0 ->
                PersonalMessagesTab(
                    messages = personalMessages,
                    messagingManager = messagingManager,
                    networkQuality = networkQuality,
                )

            1 ->
                GroupMessagesTab(
                    messages = groupMessages,
                    messagingManager = messagingManager,
                    networkQuality = networkQuality,
                )

            2 ->
                CommunityMessagesTab(
                    messages = communityMessages,
                    messagingManager = messagingManager,
                    networkQuality = networkQuality,
                )
        }
    }
}

@Composable
fun NetworkQualityBanner(networkQuality: String) {
    val backgroundColor =
        when (networkQuality) {
            "Excellent", "Good" -> Color(0xFF4CAF50)
            "Fair" -> Color(0xFFFF9800)
            "Poor" -> Color(0xFFF44336)
            else -> Color(0xFF9E9E9E)
        }

    val message =
        when (networkQuality) {
            "Excellent" -> "📶 Excellent connection - All features available"
            "Good" -> "📶 Good connection - Normal messaging"
            "Fair" -> "📱 Fair connection - Reduced data usage"
            "Poor" -> "📱 Poor connection - Text only mode"
            else -> "📱 Checking connection..."
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor.copy(alpha = 0.1f)),
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(12.dp),
            fontSize = 14.sp,
            color = backgroundColor,
        )
    }
}

@Composable
fun MessageCountBadge(count: Int) {
    if (count > 0) {
        Box(
            modifier =
                Modifier
                    .size(20.dp)
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = CircleShape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                color = MaterialTheme.colorScheme.onError,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun PersonalMessagesTab(
    messages: List<MessagingManager.PersonalMessage>,
    messagingManager: MessagingManager,
    networkQuality: String,
) {
    val scope = rememberCoroutineScope()
    var showNewMessageDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Action Buttons
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ElevatedButton(
                onClick = { showNewMessageDialog = true },
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Message")
            }

            if (networkQuality == "Poor") {
                Text(
                    text = "Text only",
                    modifier =
                        Modifier
                            .background(
                                MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                                RoundedCornerShape(4.dp),
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            if (messages.isEmpty()) {
                item {
                    ComprehensiveEmptyState(
                        icon = Icons.Filled.ChatBubbleOutline,
                        title = "No Personal Messages",
                        subtitle = "Start a conversation with other farmers",
                    )
                }
            } else {
                items(messages) { message ->
                    PersonalMessageCard(
                        message = message,
                        onClick = {
                            scope.launch {
                                messagingManager.markMessageAsRead(message.id)
                            }
                        },
                        networkQuality = networkQuality,
                    )
                }
            }
        }
    }

    // New Message Dialog
    if (showNewMessageDialog) {
        NewPersonalMessageDialog(
            onDismiss = { showNewMessageDialog = false },
            onSend = { receiverId, content ->
                scope.launch {
                    messagingManager.sendPersonalMessage(receiverId, content)
                    showNewMessageDialog = false
                }
            },
            networkQuality = networkQuality,
        )
    }
}

@Composable
fun GroupMessagesTab(
    messages: List<MessagingManager.GroupMessage>,
    messagingManager: MessagingManager,
    networkQuality: String,
) {
    val scope = rememberCoroutineScope()
    var showNewGroupDialog by remember { mutableStateOf(false) }

    // Group messages by group
    val groupedMessages = messages.groupBy { it.groupId }

    Column(modifier = Modifier.fillMaxSize()) {
        // Action Buttons
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ElevatedButton(
                onClick = { showNewGroupDialog = true },
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Default.Group, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Join Group")
            }

            ElevatedButton(
                onClick = { /* Create new group */ },
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Default.GroupAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create")
            }
        }

        // Group Categories
        val farmingGroups =
            listOf(
                GroupCategory("Breeding", Icons.Default.Pets, "Expert breeding advice"),
                GroupCategory(
                    "Health",
                    Icons.Default.LocalHospital,
                    "Health and vaccination tips",
                ),
                GroupCategory("Marketplace", Icons.Default.Store, "Buy and sell fowl"),
                GroupCategory(
                    "Regional",
                    Icons.Default.LocationOn,
                    "Local farming community",
                ),
                GroupCategory(
                    "Cultural",
                    Icons.Default.Festival,
                    "Cultural events and traditions",
                ),
                GroupCategory("Beginners", Icons.Default.School, "New farmer support"),
            )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            // Quick join group categories
            item {
                Text(
                    text = "Farming Groups",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }

            items(farmingGroups) { category ->
                GroupCategoryCard(
                    category = category,
                    onClick = {
                        scope.launch {
                            // Join group based on category
                            messagingManager.sendGroupMessage(
                                groupId = category.name.lowercase(),
                                content = "Hello! I'm interested in ${category.name.lowercase()} topics.",
                                messageType = MessagingManager.GroupMessageType.TEXT,
                            )
                        }
                    },
                )
            }

            // Active group conversations
            if (groupedMessages.isNotEmpty()) {
                item {
                    Text(
                        text = "Active Conversations",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }

                items(groupedMessages.entries.toList()) { (groupId, groupMessages) ->
                    val latestMessage = groupMessages.maxByOrNull { it.timestamp }
                    if (latestMessage != null) {
                        GroupConversationCard(
                            groupId = groupId,
                            groupName = latestMessage.groupName,
                            latestMessage = latestMessage,
                            messageCount = groupMessages.size,
                            onClick = { /* Navigate to group chat */ },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommunityMessagesTab(
    messages: List<MessagingManager.CommunityMessage>,
    messagingManager: MessagingManager,
    networkQuality: String,
) {
    val scope = rememberCoroutineScope()
    var showNewPostDialog by remember { mutableStateOf(false) }

    // Group messages by category
    val categorizedMessages = messages.groupBy { it.category }

    Column(modifier = Modifier.fillMaxSize()) {
        // Action Button
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            ElevatedButton(
                onClick = { showNewPostDialog = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Create, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Community Post")
            }
        }

        // Community Categories
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            if (messages.isEmpty()) {
                item {
                    ComprehensiveEmptyState(
                        icon = Icons.Default.Forum,
                        title = "No Community Posts",
                        subtitle = "Share knowledge with the farming community",
                    )
                }
            } else {
                items(messages.sortedByDescending { it.timestamp }) { message ->
                    CommunityMessageCard(
                        message = message,
                        onClick = { /* Navigate to full post */ },
                        networkQuality = networkQuality,
                    )
                }
            }
        }
    }

    // New Post Dialog
    if (showNewPostDialog) {
        NewCommunityPostDialog(
            onDismiss = { showNewPostDialog = false },
            onPost = { category, title, content ->
                scope.launch {
                    messagingManager.sendCommunityMessage(category, title, content)
                    showNewPostDialog = false
                }
            },
        )
    }
}

@Composable
fun PersonalMessageCard(
    message: MessagingManager.PersonalMessage,
    onClick: () -> Unit,
    networkQuality: String,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (message.isRead) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    },
            ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            // Avatar placeholder
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = message.senderName.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = message.senderName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                    Text(
                        text = formatMessageTime(message.timestamp),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message.content,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (!message.isRead) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(8.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        CircleShape,
                                    ),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Unread",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupCategoryCard(
    category: GroupCategory,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
            ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
                Text(
                    text = category.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun GroupConversationCard(
    groupId: String,
    groupName: String,
    latestMessage: MessagingManager.GroupMessage,
    messageCount: Int,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                            CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = groupName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                    Text(
                        text = formatMessageTime(latestMessage.timestamp),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${latestMessage.senderName}: ${latestMessage.content}",
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (messageCount > 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$messageCount messages",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
fun CommunityMessageCard(
    message: MessagingManager.CommunityMessage,
    onClick: () -> Unit,
    networkQuality: String,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Card(
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                ),
                            modifier = Modifier.height(24.dp),
                        ) {
                            Text(
                                text = message.category,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = message.authorName,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Text(
                    text = formatMessageTime(message.timestamp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = message.content,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = message.likes.toString(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Comment,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = message.replies.toString(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun ComprehensiveEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ),
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// Dialog Components
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPersonalMessageDialog(
    onDismiss: () -> Unit,
    onSend: (String, String) -> Unit,
    networkQuality: String,
) {
    var receiverId by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    val maxLength =
        when (networkQuality) {
            "Poor" -> 100
            "Fair" -> 300
            else -> 500
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Personal Message") },
        text = {
            Column {
                OutlinedTextField(
                    value = receiverId,
                    onValueChange = { receiverId = it },
                    label = { Text("Farmer ID or Username") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        if (it.length <= maxLength) content = it
                    },
                    label = { Text("Message") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    supportingText = {
                        Text("${content.length}/$maxLength characters")
                    },
                )
                if (networkQuality == "Poor") {
                    Text(
                        text = "⚠️ Poor network - keep messages short",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (receiverId.isNotBlank() && content.isNotBlank()) {
                        onSend(receiverId, content)
                    }
                },
                enabled = receiverId.isNotBlank() && content.isNotBlank(),
            ) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCommunityPostDialog(
    onDismiss: () -> Unit,
    onPost: (String, String, String) -> Unit,
) {
    var selectedCategory by remember { mutableStateOf("General") }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    val categories =
        listOf(
            "General",
            "Breeding",
            "Health Care",
            "Marketplace",
            "Cultural Events",
            "Competitions",
            "Tips & Advice",
        )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Community Post") },
        text = {
            Column {
                // Category Dropdown
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier =
                            Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 100) title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("${title.length}/100 characters") },
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { if (it.length <= 1000) content = it },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 6,
                    supportingText = { Text("${content.length}/1000 characters") },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onPost(selectedCategory, title, content)
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
            ) {
                Text("Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

// Helper Data Classes
data class GroupCategory(
    val name: String,
    val icon: ImageVector,
    val description: String,
)

// Helper Functions
private fun formatMessageTime(timestamp: Date): String {
    val now = Date()
    val diff = now.time - timestamp.time
    val hours = diff / (1000 * 60 * 60)

    return when {
        hours < 1 -> "Just now"
        hours < 24 -> "${hours}h ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(timestamp)
    }
}

private fun assessNetworkQuality(): String {
    // Simplified network assessment - integrate with PerformanceOptimization.kt
    return "Good"
}
