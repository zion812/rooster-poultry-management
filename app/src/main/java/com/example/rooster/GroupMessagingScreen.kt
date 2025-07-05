package com.example.rooster

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Group Messaging Screen for Rooster Project
 * Provides personal, group, and community messaging for rural farmers
 * Optimized for low-end devices and 2G networks
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupMessagingScreen() {
    val context = LocalContext.current
    val messagingManager = remember { MessagingManager.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Personal", "Groups", "Community")

    // Collect message streams
    val personalMessages by messagingManager.personalMessages.collectAsStateWithLifecycle()
    val groupMessages by messagingManager.groupMessages.collectAsStateWithLifecycle()
    val communityMessages by messagingManager.communityMessages.collectAsStateWithLifecycle()

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Message composition states
    var messageText by remember { mutableStateOf("") }
    var selectedGroupId by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Breeding") }
    var communityTitle by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White),
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    "Farmer Messages",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                ),
        )

        // Tab Navigation
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = Color.Black,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFFFF5722),
                )
            },
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            title,
                            color = if (selectedTab == index) Color(0xFFFF5722) else Color.Gray,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                )
            }
        }

        // Error Display
        errorMessage?.let { error ->
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        error,
                        color = Color.Red,
                        fontSize = 14.sp,
                    )
                }
            }
        }

        // Content based on selected tab
        when (selectedTab) {
            0 ->
                PersonalMessagesTab(
                    messages = personalMessages,
                    messageText = messageText,
                    onMessageChange = { messageText = it },
                    onSendMessage = { receiverId ->
                        if (messageText.isNotBlank()) {
                            coroutineScope.launch {
                                isLoading = true
                                val result =
                                    messagingManager.sendPersonalMessage(receiverId, messageText)
                                if (result.isSuccess) {
                                    messageText = ""
                                    errorMessage = null
                                } else {
                                    errorMessage =
                                        "Failed to send message: ${result.exceptionOrNull()?.message}"
                                }
                                isLoading = false
                            }
                        }
                    },
                    isLoading = isLoading,
                )

            1 ->
                GroupMessagesTab(
                    messages = groupMessages,
                    messageText = messageText,
                    selectedGroupId = selectedGroupId,
                    onMessageChange = { messageText = it },
                    onGroupIdChange = { selectedGroupId = it },
                    onSendMessage = {
                        if (messageText.isNotBlank() && selectedGroupId.isNotBlank()) {
                            coroutineScope.launch {
                                isLoading = true
                                val result =
                                    messagingManager.sendGroupMessage(
                                        selectedGroupId,
                                        messageText,
                                        MessagingManager.GroupMessageType.TEXT,
                                    )
                                if (result.isSuccess) {
                                    messageText = ""
                                    errorMessage = null
                                } else {
                                    errorMessage =
                                        "Failed to send group message: ${result.exceptionOrNull()?.message}"
                                }
                                isLoading = false
                            }
                        }
                    },
                    isLoading = isLoading,
                )

            2 ->
                CommunityMessagesTab(
                    messages = communityMessages,
                    messageText = messageText,
                    title = communityTitle,
                    selectedCategory = selectedCategory,
                    onMessageChange = { messageText = it },
                    onTitleChange = { communityTitle = it },
                    onCategoryChange = { selectedCategory = it },
                    onSendMessage = {
                        if (messageText.isNotBlank() && communityTitle.isNotBlank()) {
                            coroutineScope.launch {
                                isLoading = true
                                val result =
                                    messagingManager.sendCommunityMessage(
                                        selectedCategory,
                                        communityTitle,
                                        messageText,
                                    )
                                if (result.isSuccess) {
                                    messageText = ""
                                    communityTitle = ""
                                    errorMessage = null
                                } else {
                                    errorMessage =
                                        "Failed to send community message: ${result.exceptionOrNull()?.message}"
                                }
                                isLoading = false
                            }
                        }
                    },
                    isLoading = isLoading,
                )
        }
    }
}

@Composable
private fun PersonalMessagesTab(
    messages: List<MessagingManager.PersonalMessage>,
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    isLoading: Boolean,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        // Messages List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(messages) { message ->
                PersonalMessageCard(message)
            }

            if (messages.isEmpty()) {
                item {
                    MessagingEmptyState(
                        icon = Icons.AutoMirrored.Filled.Chat,
                        title = "No Personal Messages",
                        subtitle = "Start a conversation with a farmer or buyer",
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Message Composition
        PersonalMessageComposer(
            messageText = messageText,
            onMessageChange = onMessageChange,
            onSendMessage = onSendMessage,
            isLoading = isLoading,
        )
    }
}

@Composable
private fun GroupMessagesTab(
    messages: List<MessagingManager.GroupMessage>,
    messageText: String,
    selectedGroupId: String,
    onMessageChange: (String) -> Unit,
    onGroupIdChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isLoading: Boolean,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        // Group Selection
        SimpleGroupSelector(
            selectedGroupId = selectedGroupId,
            onGroupIdChange = onGroupIdChange,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Messages List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(messages.filter { it.groupId == selectedGroupId || selectedGroupId.isEmpty() }) { message ->
                GroupMessageCard(message)
            }

            if (messages.isEmpty()) {
                item {
                    MessagingEmptyState(
                        icon = Icons.Default.Group,
                        title = "No Group Messages",
                        subtitle = "Join a farming group to start discussions",
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Message Composition
        GroupMessageComposer(
            messageText = messageText,
            onMessageChange = onMessageChange,
            onSendMessage = onSendMessage,
            isLoading = isLoading,
        )
    }
}

@Composable
private fun CommunityMessagesTab(
    messages: List<MessagingManager.CommunityMessage>,
    messageText: String,
    title: String,
    selectedCategory: String,
    onMessageChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isLoading: Boolean,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        // Category Selection
        SimpleCategorySelector(
            selectedCategory = selectedCategory,
            onCategoryChange = onCategoryChange,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Messages List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(messages.filter { it.category == selectedCategory || selectedCategory.isEmpty() }) { message ->
                CommunityMessageCard(message)
            }

            if (messages.isEmpty()) {
                item {
                    MessagingEmptyState(
                        icon = Icons.Default.Forum,
                        title = "No Community Posts",
                        subtitle = "Share your farming knowledge with the community",
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Message Composition
        CommunityMessageComposer(
            title = title,
            messageText = messageText,
            onTitleChange = onTitleChange,
            onMessageChange = onMessageChange,
            onSendMessage = onSendMessage,
            isLoading = isLoading,
        )
    }
}

@Composable
private fun PersonalMessageCard(message: MessagingManager.PersonalMessage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = if (message.isRead) Color.White else Color(0xFFF3E5F5),
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    message.senderName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                )
                Text(
                    SimpleDateFormat(
                        "MMM dd, HH:mm",
                        Locale.getDefault(),
                    ).format(message.timestamp),
                    fontSize = 12.sp,
                    color = Color.Gray,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                message.content,
                fontSize = 14.sp,
                color = Color.Black,
            )
        }
    }
}

@Composable
private fun GroupMessageCard(message: MessagingManager.GroupMessage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        message.groupName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFFFF5722),
                    )
                    Text(
                        message.senderName,
                        fontSize = 12.sp,
                        color = Color.Gray,
                    )
                }
                Text(
                    SimpleDateFormat(
                        "MMM dd, HH:mm",
                        Locale.getDefault(),
                    ).format(message.timestamp),
                    fontSize = 12.sp,
                    color = Color.Gray,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                message.content,
                fontSize = 14.sp,
                color = Color.Black,
            )

            if (message.messageType != MessagingManager.GroupMessageType.TEXT) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    message.messageType.name.replace("_", " "),
                    fontSize = 10.sp,
                    color = Color(0xFFFF5722),
                    modifier =
                        Modifier
                            .background(
                                Color(0xFFFFEBEE),
                                RoundedCornerShape(4.dp),
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun CommunityMessageCard(message: MessagingManager.CommunityMessage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    message.category,
                    fontSize = 12.sp,
                    color = Color(0xFFFF5722),
                    modifier =
                        Modifier
                            .background(
                                Color(0xFFFFEBEE),
                                RoundedCornerShape(4.dp),
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                )
                Text(
                    SimpleDateFormat(
                        "MMM dd, HH:mm",
                        Locale.getDefault(),
                    ).format(message.timestamp),
                    fontSize = 12.sp,
                    color = Color.Gray,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                message.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                message.content,
                fontSize = 14.sp,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${message.likes}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.Comment,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${message.replies}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                    )
                }

                Text(
                    "by ${message.authorName}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                )
            }
        }
    }
}

@Composable
private fun PersonalMessageComposer(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    isLoading: Boolean,
) {
    var receiverId by remember { mutableStateOf("") }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            OutlinedTextField(
                value = receiverId,
                onValueChange = { receiverId = it },
                label = { Text("Recipient ID") },
                placeholder = { Text("Enter farmer or buyer ID") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageChange,
                label = { Text("Message") },
                placeholder = { Text("Type your message (max 500 chars)") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "${messageText.length}/500",
                    fontSize = 12.sp,
                    color = if (messageText.length > 500) Color.Red else Color.Gray,
                )

                Button(
                    onClick = { onSendMessage(receiverId) },
                    enabled = !isLoading && messageText.isNotBlank() && receiverId.isNotBlank() && messageText.length <= 500,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                        )
                    } else {
                        Text("Send")
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupMessageComposer(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isLoading: Boolean,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageChange,
                label = { Text("Group Message") },
                placeholder = { Text("Share with your farming group (max 500 chars)") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "${messageText.length}/500",
                    fontSize = 12.sp,
                    color = if (messageText.length > 500) Color.Red else Color.Gray,
                )

                Button(
                    onClick = onSendMessage,
                    enabled = !isLoading && messageText.isNotBlank() && messageText.length <= 500,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                        )
                    } else {
                        Text("Send to Group")
                    }
                }
            }
        }
    }
}

@Composable
private fun CommunityMessageComposer(
    title: String,
    messageText: String,
    onTitleChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isLoading: Boolean,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Post Title") },
                placeholder = { Text("Enter a descriptive title") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageChange,
                label = { Text("Community Post") },
                placeholder = { Text("Share your knowledge with the community (max 1000 chars)") },
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "${messageText.length}/1000",
                    fontSize = 12.sp,
                    color = if (messageText.length > 1000) Color.Red else Color.Gray,
                )

                Button(
                    onClick = onSendMessage,
                    enabled = !isLoading && messageText.isNotBlank() && title.isNotBlank() && messageText.length <= 1000,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                        )
                    } else {
                        Text("Post to Community")
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleGroupSelector(
    selectedGroupId: String,
    onGroupIdChange: (String) -> Unit,
) {
    val groups =
        listOf(
            "breeding_experts" to "Breeding Experts",
            "health_care" to "Health Care Group",
            "marketplace_sellers" to "Marketplace Sellers",
            "beginners" to "Beginners Group",
        )

    var expanded by remember { mutableStateOf(false) }

    // Simple dropdown without experimental APIs
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            OutlinedTextField(
                value = groups.find { it.first == selectedGroupId }?.second ?: "Select Group",
                onValueChange = { },
                readOnly = true,
                label = { Text("Group") },
                trailingIcon = {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                    )
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
            )

            if (expanded) {
                groups.forEach { (id, name) ->
                    TextButton(
                        onClick = {
                            onGroupIdChange(id)
                            expanded = false
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                    ) {
                        Text(
                            name,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }

            // Toggle button
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (expanded) "Close" else "Select Group")
            }
        }
    }
}

@Composable
private fun SimpleCategorySelector(
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
) {
    val categories =
        listOf(
            "Breeding",
            "Health Care",
            "Marketplace",
            "Cultural Events",
            "Traditional Knowledge",
            "Competitions",
            "Weather",
            "Government Schemes",
        )

    var expanded by remember { mutableStateOf(false) }

    // Simple dropdown without experimental APIs
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = { },
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                    )
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
            )

            if (expanded) {
                categories.forEach { category ->
                    TextButton(
                        onClick = {
                            onCategoryChange(category)
                            expanded = false
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                    ) {
                        Text(
                            category,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }

            // Toggle button
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (expanded) "Close" else "Select Category")
            }
        }
    }
}

@Composable
private fun MessagingEmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.Gray,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                subtitle,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )
        }
    }
}
