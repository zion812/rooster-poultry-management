package com.example.rooster.services.backend

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.rooster.services.SmartCacheManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

// Message types for different contexts
enum class MessageType(val type: String) {
    TEXT("text"),
    IMAGE("image"),
    AUDIO("audio"),
    VIDEO("video"),
    DOCUMENT("document"),
    LOCATION("location"),
    FOWL_SHARE("fowl_share"),
    LISTING_SHARE("listing_share"),
    TYPING_INDICATOR("typing"),
    READ_RECEIPT("read_receipt"),
    SYSTEM("system")
}

// Message data model optimized for rural connectivity
data class ChatMessage(
    val messageId: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val messageType: MessageType,
    val timestamp: Long,
    val isDelivered: Boolean = false,
    val isRead: Boolean = false,
    val mediaUrl: String? = null,
    val mediaSize: Long? = null,
    val thumbnailUrl: String? = null,
    val replyToMessageId: String? = null,
    val metadata: Map<String, Any> = emptyMap(),
    val offlineId: String? = null, // For offline message queuing
    val isOptimistic: Boolean = false, // For optimistic UI updates
    val retryCount: Int = 0
)

// Group chat information
data class GroupChat(
    val chatId: String,
    val name: String,
    val nameTelugu: String,
    val description: String?,
    val descriptionTelugu: String?,
    val participants: List<String>,
    val adminIds: List<String>,
    val isPublic: Boolean = false,
    val maxParticipants: Int = 50,
    val createdAt: Long,
    val updatedAt: Long,
    val avatarUrl: String? = null,
    val lastMessage: ChatMessage? = null,
    val unreadCount: Int = 0
)

// Real-time events for messaging
sealed class MessagingEvent {
    data class MessageReceived(val message: ChatMessage) : MessagingEvent()
    data class MessageSent(val message: ChatMessage) : MessagingEvent()
    data class MessageDelivered(val messageId: String, val chatId: String) : MessagingEvent()
    data class MessageRead(val messageId: String, val chatId: String, val readBy: String) :
        MessagingEvent()

    data class TypingIndicator(val chatId: String, val userId: String, val isTyping: Boolean) :
        MessagingEvent()

    data class UserJoinedChat(val chatId: String, val userId: String, val userName: String) :
        MessagingEvent()

    data class UserLeftChat(val chatId: String, val userId: String) : MessagingEvent()
    data class ChatCreated(val chat: GroupChat) : MessagingEvent()
    data class ChatUpdated(val chat: GroupChat) : MessagingEvent()
    data class ConnectionStatusChanged(val isConnected: Boolean, val error: String? = null) :
        MessagingEvent()

    data class MessageError(
        val messageId: String,
        val error: String,
        val isRetryable: Boolean = true
    ) : MessagingEvent()
}

@Singleton
class RealTimeMessagingService @Inject constructor(
    private val context: Context,
    private val cacheManager: SmartCacheManager
) {

    private val tag = "RealTimeMessagingService"

    // WebSocket client optimized for rural connectivity
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .pingInterval(25, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private var webSocket: WebSocket? = null
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Event channels
    private val messagingEventChannel = Channel<MessagingEvent>(Channel.UNLIMITED)
    val messagingEvents = messagingEventChannel.receiveAsFlow()

    // Local message cache and offline queue
    private val messageCache = ConcurrentHashMap<String, ChatMessage>()
    private val offlineQueue = mutableListOf<ChatMessage>()
    private val typingUsers = ConcurrentHashMap<String, Set<String>>()

    // Connection management
    private var isConnected = false
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 15
    private var reconnectJob: Job? = null

    // Active chats
    private val activeChats = mutableSetOf<String>()

    fun connectToMessaging(
        serverUrl: String = "wss://your-server.com/messaging-ws",
        userId: String
    ) {
        if (webSocket != null) {
            Log.d(tag, "WebSocket already connected")
            return
        }

        val request = Request.Builder()
            .url("$serverUrl?userId=$userId")
            .addHeader("User-Agent", "RoosterApp-Android/1.0")
            .build()

        webSocket = client.newWebSocket(request, createMessagingWebSocketListener())
    }

    private fun createMessagingWebSocketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(tag, "Messaging WebSocket connected")
            isConnected = true
            reconnectAttempts = 0

            coroutineScope.launch {
                messagingEventChannel.send(MessagingEvent.ConnectionStatusChanged(true))

                // Send queued offline messages
                processOfflineQueue()

                // Re-join active chats
                activeChats.forEach { chatId ->
                    joinChat(chatId)
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(tag, "Received message: $text")
            handleIncomingMessage(text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            handleIncomingMessage(bytes.utf8())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(tag, "WebSocket closing: $code $reason")
            webSocket.close(1000, null)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(tag, "WebSocket closed: $code $reason")
            isConnected = false
            this@RealTimeMessagingService.webSocket = null

            coroutineScope.launch {
                messagingEventChannel.send(MessagingEvent.ConnectionStatusChanged(false))

                if (code != 1000 && reconnectAttempts < maxReconnectAttempts) {
                    scheduleReconnect()
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(tag, "WebSocket failure", t)
            isConnected = false
            this@RealTimeMessagingService.webSocket = null

            coroutineScope.launch {
                val errorMessage = t.message ?: "Connection failed"
                messagingEventChannel.send(
                    MessagingEvent.ConnectionStatusChanged(
                        false,
                        errorMessage
                    )
                )

                if (reconnectAttempts < maxReconnectAttempts) {
                    scheduleReconnect()
                }
            }
        }
    }

    private fun handleIncomingMessage(message: String) {
        try {
            val json = JSONObject(message)
            val eventType = json.getString("type")
            val data = json.getJSONObject("data")

            when (eventType) {
                "message" -> {
                    val chatMessage = parseChatMessage(data)
                    coroutineScope.launch {
                        // Cache message for offline access
                        cacheMessage(chatMessage)
                        messageCache[chatMessage.messageId] = chatMessage

                        messagingEventChannel.send(MessagingEvent.MessageReceived(chatMessage))

                        // Send delivery confirmation
                        sendDeliveryConfirmation(chatMessage.messageId, chatMessage.chatId)
                    }
                }

                "message_delivered" -> {
                    val messageId = data.getString("messageId")
                    val chatId = data.getString("chatId")

                    coroutineScope.launch {
                        messagingEventChannel.send(
                            MessagingEvent.MessageDelivered(
                                messageId,
                                chatId
                            )
                        )
                    }
                }

                "message_read" -> {
                    val messageId = data.getString("messageId")
                    val chatId = data.getString("chatId")
                    val readBy = data.getString("readBy")

                    coroutineScope.launch {
                        messagingEventChannel.send(
                            MessagingEvent.MessageRead(
                                messageId,
                                chatId,
                                readBy
                            )
                        )
                    }
                }

                "typing" -> {
                    val chatId = data.getString("chatId")
                    val userId = data.getString("userId")
                    val isTyping = data.getBoolean("isTyping")

                    updateTypingIndicator(chatId, userId, isTyping)

                    coroutineScope.launch {
                        messagingEventChannel.send(
                            MessagingEvent.TypingIndicator(
                                chatId,
                                userId,
                                isTyping
                            )
                        )
                    }
                }

                "user_joined" -> {
                    val chatId = data.getString("chatId")
                    val userId = data.getString("userId")
                    val userName = data.getString("userName")

                    coroutineScope.launch {
                        messagingEventChannel.send(
                            MessagingEvent.UserJoinedChat(
                                chatId,
                                userId,
                                userName
                            )
                        )
                    }
                }

                "user_left" -> {
                    val chatId = data.getString("chatId")
                    val userId = data.getString("userId")

                    coroutineScope.launch {
                        messagingEventChannel.send(MessagingEvent.UserLeftChat(chatId, userId))
                    }
                }

                else -> {
                    Log.w(tag, "Unknown message type: $eventType")
                }
            }

        } catch (e: JSONException) {
            Log.e(tag, "Failed to parse incoming message", e)
        }
    }

    private fun parseChatMessage(data: JSONObject): ChatMessage {
        return ChatMessage(
            messageId = data.getString("messageId"),
            chatId = data.getString("chatId"),
            senderId = data.getString("senderId"),
            senderName = data.getString("senderName"),
            content = data.getString("content"),
            messageType = MessageType.valueOf(data.getString("messageType")),
            timestamp = data.getLong("timestamp"),
            isDelivered = data.optBoolean("isDelivered", false),
            isRead = data.optBoolean("isRead", false),
            mediaUrl = data.optString("mediaUrl").takeIf { it.isNotEmpty() },
            mediaSize = if (data.has("mediaSize")) data.getLong("mediaSize") else null,
            thumbnailUrl = data.optString("thumbnailUrl").takeIf { it.isNotEmpty() },
            replyToMessageId = data.optString("replyToMessageId").takeIf { it.isNotEmpty() }
        )
    }

    // Send a message with optimistic UI updates
    suspend fun sendMessage(
        chatId: String,
        content: String,
        messageType: MessageType = MessageType.TEXT,
        mediaUrl: String? = null,
        replyToMessageId: String? = null
    ): ChatMessage {
        val messageId = generateMessageId()
        val currentUserId = getCurrentUserId()
        val currentUserName = getCurrentUserName()

        val message = ChatMessage(
            messageId = messageId,
            chatId = chatId,
            senderId = currentUserId,
            senderName = currentUserName,
            content = content,
            messageType = messageType,
            timestamp = System.currentTimeMillis(),
            mediaUrl = mediaUrl,
            replyToMessageId = replyToMessageId,
            isOptimistic = true // Mark as optimistic for UI
        )

        // Add to local cache immediately for optimistic UI
        messageCache[messageId] = message

        // Emit optimistic event
        messagingEventChannel.send(MessagingEvent.MessageSent(message))

        // Queue for offline if not connected
        if (!isConnected) {
            offlineQueue.add(message)
            cacheMessage(message)
            return message
        }

        // Send via WebSocket
        val messageJson = JSONObject().apply {
            put("type", "send_message")
            put("data", JSONObject().apply {
                put("messageId", messageId)
                put("chatId", chatId)
                put("content", content)
                put("messageType", messageType.type)
                put("timestamp", message.timestamp)
                put("mediaUrl", mediaUrl)
                put("replyToMessageId", replyToMessageId)
            })
        }

        val success = webSocket?.send(messageJson.toString()) ?: false

        if (!success) {
            // Queue for retry
            offlineQueue.add(message)
            scheduleMessageRetry(message)
        }

        return message
    }

    // Join a chat room
    fun joinChat(chatId: String) {
        activeChats.add(chatId)

        if (isConnected) {
            val joinMessage = JSONObject().apply {
                put("type", "join_chat")
                put("data", JSONObject().apply {
                    put("chatId", chatId)
                })
            }

            webSocket?.send(joinMessage.toString())
        }
    }

    // Leave a chat room
    fun leaveChat(chatId: String) {
        activeChats.remove(chatId)

        if (isConnected) {
            val leaveMessage = JSONObject().apply {
                put("type", "leave_chat")
                put("data", JSONObject().apply {
                    put("chatId", chatId)
                })
            }

            webSocket?.send(leaveMessage.toString())
        }
    }

    // Send typing indicator
    fun sendTypingIndicator(chatId: String, isTyping: Boolean) {
        if (!isConnected) return

        val typingMessage = JSONObject().apply {
            put("type", "typing")
            put("data", JSONObject().apply {
                put("chatId", chatId)
                put("isTyping", isTyping)
            })
        }

        webSocket?.send(typingMessage.toString())
    }

    // Mark message as read
    fun markMessageAsRead(messageId: String, chatId: String) {
        if (!isConnected) return

        val readMessage = JSONObject().apply {
            put("type", "mark_read")
            put("data", JSONObject().apply {
                put("messageId", messageId)
                put("chatId", chatId)
            })
        }

        webSocket?.send(readMessage.toString())
    }

    // Get cached messages for a chat (offline support)
    suspend fun getCachedMessages(chatId: String, limit: Int = 50): List<ChatMessage> {
        return try {
            // Return cached messages from in-memory cache
            messageCache.values.filter { it.chatId == chatId }
                .sortedByDescending { it.timestamp }
                .take(limit)
        } catch (e: Exception) {
            Log.e(tag, "Failed to load cached messages", e)
            emptyList()
        }
    }

    // Get typing users for a chat
    fun getTypingUsers(chatId: String): Set<String> {
        return typingUsers[chatId] ?: emptySet()
    }

    // Helper functions
    private suspend fun cacheMessage(message: ChatMessage) {
        try {
            // Simple in-memory caching for now
            messageCache[message.messageId] = message
            Log.d(tag, "Cached message ${message.messageId} for chat ${message.chatId}")
        } catch (e: Exception) {
            Log.e(tag, "Failed to cache message", e)
        }
    }

    private suspend fun processOfflineQueue() {
        val queuedMessages = offlineQueue.toList()
        offlineQueue.clear()

        queuedMessages.forEach { message ->
            try {
                sendMessage(
                    chatId = message.chatId,
                    content = message.content,
                    messageType = message.messageType,
                    mediaUrl = message.mediaUrl,
                    replyToMessageId = message.replyToMessageId
                )
            } catch (e: Exception) {
                Log.e(tag, "Failed to send queued message", e)
                offlineQueue.add(message)
            }
        }
    }

    private fun sendDeliveryConfirmation(messageId: String, chatId: String) {
        if (!isConnected) return

        val confirmationMessage = JSONObject().apply {
            put("type", "delivery_confirmation")
            put("data", JSONObject().apply {
                put("messageId", messageId)
                put("chatId", chatId)
            })
        }

        webSocket?.send(confirmationMessage.toString())
    }

    private fun updateTypingIndicator(chatId: String, userId: String, isTyping: Boolean) {
        val currentTyping = typingUsers[chatId]?.toMutableSet() ?: mutableSetOf()

        if (isTyping) {
            currentTyping.add(userId)
        } else {
            currentTyping.remove(userId)
        }

        typingUsers[chatId] = currentTyping

        // Auto-clear typing indicators after 5 seconds
        if (isTyping) {
            coroutineScope.launch {
                delay(5000)
                updateTypingIndicator(chatId, userId, false)
            }
        }
    }

    private fun scheduleReconnect() {
        reconnectJob?.cancel()
        reconnectJob = coroutineScope.launch {
            val delayMs = (2000 * (reconnectAttempts + 1)).coerceAtMost(60000) // Max 1 minute delay

            Log.d(
                tag,
                "Scheduling messaging reconnect attempt ${reconnectAttempts + 1} in ${delayMs}ms"
            )
            delay(delayMs.toLong())

            reconnectAttempts++
            connectToMessaging(userId = getCurrentUserId())
        }
    }

    private fun scheduleMessageRetry(message: ChatMessage) {
        val retryWorker = OneTimeWorkRequestBuilder<MessageRetryWorker>()
            .setInputData(
                Data.Builder()
                    .putString("messageId", message.messageId)
                    .putString("chatId", message.chatId)
                    .putString("content", message.content)
                    .putString("messageType", message.messageType.type)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "retry_message_${message.messageId}",
                ExistingWorkPolicy.REPLACE,
                retryWorker
            )
    }

    private fun generateMessageId(): String =
        "MSG_${System.currentTimeMillis()}_${(1000..9999).random()}"

    private fun getCurrentUserId(): String {
        // Get from user session or preferences
        return "current_user_id" // Replace with actual implementation
    }

    private fun getCurrentUserName(): String {
        // Get from user session or preferences
        return "Current User" // Replace with actual implementation
    }

    fun disconnect() {
        Log.d(tag, "Disconnecting messaging WebSocket")
        reconnectJob?.cancel()
        webSocket?.close(1000, "User disconnected")
        webSocket = null
        isConnected = false
        activeChats.clear()
        typingUsers.clear()
    }

    fun cleanup() {
        disconnect()
        coroutineScope.cancel()
    }
}

// Worker class for retrying failed messages
class MessageRetryWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val messageId = inputData.getString("messageId") ?: return Result.failure()
        val chatId = inputData.getString("chatId") ?: return Result.failure()
        val content = inputData.getString("content") ?: return Result.failure()
        val messageTypeStr = inputData.getString("messageType") ?: return Result.failure()

        return try {
            val messageType = MessageType.valueOf(messageTypeStr.uppercase())

            // Attempt to resend message
            // This would typically involve calling the messaging service
            Log.d("MessageRetryWorker", "Retrying message: $messageId")

            Result.success()
        } catch (e: Exception) {
            Log.e("MessageRetryWorker", "Failed to retry message", e)
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}