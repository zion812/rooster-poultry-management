package com.example.rooster

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

data class SimpleChatMessage(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val senderName: String = "",
    val content: String = "",
    val timestamp: Date = Date(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleChatScreen(
    receiverFirebaseUid: String,
    navController: NavController,
) {
    var messages by remember { mutableStateOf(listOf<SimpleChatMessage>()) }
    var newMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var receiverName by remember { mutableStateOf("Loading...") }
    var sendingMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val currentUser = ParseUser.getCurrentUser()
    val currentUserFirebaseUid = currentUser?.getString("firebaseUid") ?: ""

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Create chat room ID
    val chatRoomId =
        remember {
            if (currentUserFirebaseUid.isNotEmpty()) {
                createChatRoomId(currentUserFirebaseUid, receiverFirebaseUid)
            } else {
                ""
            }
        }

    // Fetch receiver info and messages
    LaunchedEffect(receiverFirebaseUid) {
        try {
            // Get receiver info
            val userQuery = ParseQuery.getQuery<ParseUser>("_User")
            userQuery.whereEqualTo("firebaseUid", receiverFirebaseUid)
            val receiver = userQuery.first
            receiverName =
                try {
                    receiver.username ?: "Unknown User"
                } catch (e: IllegalStateException) {
                    "Unknown User"
                }

            // Fetch initial messages
            fetchMessages(chatRoomId) { messageList ->
                messages = messageList
                isLoading = false

                // Scroll to bottom
                scope.launch {
                    if (messageList.isNotEmpty()) {
                        listState.animateScrollToItem(messageList.size - 1)
                    }
                }
            }
        } catch (e: Exception) {
            receiverName = "Unknown User"
            isLoading = false
            errorMessage = "Failed to load chat: ${e.localizedMessage}"
        }
    }

    // Auto-refresh messages every 5 seconds
    LaunchedEffect(chatRoomId) {
        while (true) {
            delay(5000)
            if (chatRoomId.isNotEmpty() && !isLoading) {
                fetchMessages(chatRoomId) { messageList ->
                    val oldSize = messages.size
                    messages = messageList

                    // Scroll to bottom if new messages
                    if (messageList.size > oldSize) {
                        scope.launch {
                            listState.animateScrollToItem(messageList.size - 1)
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = receiverName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFFF5722),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                    ),
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = newMessage,
                        onValueChange = {
                            if (it.length <= 500) {
                                newMessage = it
                            }
                        },
                        placeholder = {
                            Text(
                                "Type a message... (${newMessage.length}/500)",
                                fontSize = 14.sp,
                            )
                        },
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                        maxLines = 3,
                        enabled = !sendingMessage,
                    )

                    IconButton(
                        onClick = {
                            if (newMessage.isNotBlank() && !sendingMessage) {
                                scope.launch {
                                    sendingMessage = true
                                    try {
                                        sendMessage(
                                            chatRoomId = chatRoomId,
                                            content = newMessage,
                                            receiverId = receiverFirebaseUid,
                                        )

                                        newMessage = ""
                                        errorMessage = ""

                                        // Refresh messages
                                        fetchMessages(chatRoomId) { messageList ->
                                            messages = messageList
                                            scope.launch {
                                                if (messageList.isNotEmpty()) {
                                                    listState.animateScrollToItem(messageList.size - 1)
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        errorMessage =
                                            "Failed to send message: ${e.localizedMessage}"
                                    } finally {
                                        sendingMessage = false
                                    }
                                }
                            }
                        },
                        enabled = newMessage.isNotBlank() && !sendingMessage,
                    ) {
                        if (sendingMessage) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (newMessage.isNotBlank()) Color(0xFFFF5722) else Color.Gray,
                            )
                        }
                    }
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            // Error message display
            if (errorMessage.isNotEmpty()) {
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFFD32F2F),
                        fontSize = 14.sp,
                    )
                }
            }

            // Messages list
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFFFF5722))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading messages...", fontSize = 14.sp)
                    }
                }
            } else if (messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "No messages yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Start a conversation with $receiverName",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(messages) { message ->
                        ChatMessageBubble(
                            message = message,
                            isFromCurrentUser = message.senderId == currentUserFirebaseUid,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: SimpleChatMessage,
    isFromCurrentUser: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start,
    ) {
        Card(
            modifier =
                Modifier
                    .widthIn(max = 280.dp)
                    .wrapContentWidth(),
            shape =
                RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isFromCurrentUser) 16.dp else 4.dp,
                    bottomEnd = if (isFromCurrentUser) 4.dp else 16.dp,
                ),
            colors =
                CardDefaults.cardColors(
                    containerColor = if (isFromCurrentUser) Color(0xFFFF5722) else Color(0xFFF5F5F5),
                ),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
            ) {
                if (!isFromCurrentUser) {
                    Text(
                        text = message.senderName,
                        color = Color(0xFFFF5722),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Text(
                    text = message.content,
                    color = if (isFromCurrentUser) Color.White else Color.Black,
                    fontSize = 14.sp,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatMessageTimestamp(message.timestamp),
                    color = if (isFromCurrentUser) Color.White.copy(alpha = 0.8f) else Color.Gray,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

// Helper functions for Parse Backend
private suspend fun fetchMessages(
    chatRoomId: String,
    onResult: (List<SimpleChatMessage>) -> Unit,
) {
    try {
        val query = ParseQuery.getQuery<ParseObject>("ChatMessage")
        query.whereEqualTo("chatRoomId", chatRoomId)
        query.orderByAscending("createdAt")
        query.include("sender")

        val results = query.find()
        val messages =
            results.map { parseMessage ->
                SimpleChatMessage(
                    id = parseMessage.objectId ?: "",
                    senderId = parseMessage.getString("senderId") ?: "",
                    receiverId = parseMessage.getString("receiverId") ?: "",
                    senderName =
                        try {
                            parseMessage.getParseUser("sender")?.username ?: "Unknown"
                        } catch (e: IllegalStateException) {
                            "Unknown Sender"
                        },
                    content = parseMessage.getString("content") ?: "",
                    timestamp = parseMessage.createdAt ?: Date(),
                )
            }

        onResult(messages)
    } catch (e: Exception) {
        onResult(emptyList())
    }
}

private suspend fun sendMessage(
    chatRoomId: String,
    content: String,
    receiverId: String,
) {
    val currentUser = ParseUser.getCurrentUser()
    val currentUserFirebaseUid = currentUser?.getString("firebaseUid") ?: ""

    if (currentUser == null || content.isBlank() || chatRoomId.isEmpty()) {
        throw IllegalArgumentException("Invalid message data")
    }

    val message = ParseObject("ChatMessage")
    message.put("chatRoomId", chatRoomId)
    message.put("senderId", currentUserFirebaseUid)
    message.put("receiverId", receiverId)
    message.put("content", content)
    message.put("sender", currentUser)

    message.save()
}

private fun formatMessageTimestamp(timestamp: Date): String {
    val now = Date()
    val diff = now.time - timestamp.time

    return when {
        diff < 60000 -> "Now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        else -> "${diff / 86400000}d ago"
    }
}

fun createChatRoomId(
    userId1: String,
    userId2: String,
): String {
    return if (userId1 < userId2) "$userId1-$userId2" else "$userId2-$userId1"
}
