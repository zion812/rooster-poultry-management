package com.example.rooster

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.parse.FindCallback
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import com.parse.SaveCallback
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import java.util.concurrent.CountDownLatch

/**
 * Comprehensive Messaging Manager for Rooster Project
 * Handles personal, group, and community messaging for rural farmers
 * Optimized for low-end devices and 2G networks
 */
class MessagingManager private constructor(private val context: Context) {
    companion object {
        private const val TAG = "RoosterMessaging"
        private const val CHANNEL_ID = "rooster_messages"
        private const val CHANNEL_NAME = "Rooster Messages"
        private const val CHANNEL_DESCRIPTION = "Personal and group messages for farmers"

        @Volatile
        private var INSTANCE: MessagingManager? = null

        fun getInstance(context: Context): MessagingManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MessagingManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Message streams for real-time updates
    private val _personalMessages = MutableStateFlow<List<PersonalMessage>>(emptyList())
    val personalMessages: StateFlow<List<PersonalMessage>> = _personalMessages

    private val _groupMessages = MutableStateFlow<List<GroupMessage>>(emptyList())
    val groupMessages: StateFlow<List<GroupMessage>> = _groupMessages

    private val _communityMessages = MutableStateFlow<List<CommunityMessage>>(emptyList())
    val communityMessages: StateFlow<List<CommunityMessage>> = _communityMessages

    init {
        createNotificationChannel()
        startMessagePolling()
    }

    /**
     * Data Classes for Different Message Types
     */
    data class PersonalMessage(
        val id: String = "",
        val senderId: String = "",
        val receiverId: String = "",
        val senderName: String = "",
        val content: String = "",
        val timestamp: Date = Date(),
        val isRead: Boolean = false,
    )

    data class GroupMessage(
        val id: String = "",
        val groupId: String = "",
        val groupName: String = "",
        val senderId: String = "",
        val senderName: String = "",
        val content: String = "",
        val timestamp: Date = Date(),
        val messageType: GroupMessageType = GroupMessageType.TEXT,
    )

    data class CommunityMessage(
        val id: String = "",
        val category: String = "",
        val title: String = "",
        val content: String = "",
        val authorId: String = "",
        val authorName: String = "",
        val timestamp: Date = Date(),
        val likes: Int = 0,
        val replies: Int = 0,
    )

    enum class GroupMessageType {
        TEXT,
        HEALTH_TIP,
        MARKET_UPDATE,
        CULTURAL_EVENT,
        BREEDING_ADVICE,
    }

    /**
     * Personal Messaging Functions
     */
    suspend fun sendPersonalMessage(
        receiverId: String,
        content: String,
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = ParseUser.getCurrentUser()
                if (currentUser == null || content.length > 500) {
                    return@withContext Result.failure(Exception("Invalid user or message too long"))
                }

                val message = ParseObject("PersonalMessage")
                message.put("senderId", currentUser.objectId)
                message.put("receiverId", receiverId)
                message.put("senderName", currentUser.getString("username") ?: "Unknown")
                message.put("content", content.trim())
                message.put("isRead", false)
                message.put("timestamp", Date())

                var success = false
                val latch = CountDownLatch(1)

                message.saveInBackground(
                    SaveCallback { e ->
                        success = e == null
                        if (e == null) {
                            Log.d(TAG, "Personal message sent successfully")
                            // Send notification to receiver
                            notifyPersonalMessage(
                                receiverId,
                                currentUser.username ?: "Someone",
                                content,
                            )
                        } else {
                            Log.e(TAG, "Failed to send personal message", e)
                        }
                        latch.countDown()
                    },
                )

                latch.await()
                Result.success(success)
            } catch (e: Exception) {
                Log.e(TAG, "Error sending personal message", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Group Messaging Functions
     */
    suspend fun sendGroupMessage(
        groupId: String,
        content: String,
        messageType: GroupMessageType = GroupMessageType.TEXT,
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = ParseUser.getCurrentUser()
                if (currentUser == null || content.length > 500) {
                    return@withContext Result.failure(Exception("Invalid user or message too long"))
                }

                val message = ParseObject("GroupMessage")
                message.put("groupId", groupId)
                message.put("senderId", currentUser.objectId)
                message.put("senderName", currentUser.getString("username") ?: "Unknown")
                message.put("content", content.trim())
                message.put("messageType", messageType.name)
                message.put("timestamp", Date())

                var success = false
                val latch = CountDownLatch(1)

                message.saveInBackground(
                    SaveCallback { e ->
                        success = e == null
                        if (e == null) {
                            Log.d(TAG, "Group message sent successfully")
                            // Notify group members
                            notifyGroupMessage(groupId, currentUser.username ?: "Member", content)
                        } else {
                            Log.e(TAG, "Failed to send group message", e)
                        }
                        latch.countDown()
                    },
                )

                latch.await()
                Result.success(success)
            } catch (e: Exception) {
                Log.e(TAG, "Error sending group message", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Community Messaging Functions
     */
    suspend fun sendCommunityMessage(
        category: String,
        title: String,
        content: String,
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = ParseUser.getCurrentUser()
                if (currentUser == null || content.length > 1000) {
                    return@withContext Result.failure(Exception("Invalid user or content too long"))
                }

                val message = ParseObject("CommunityMessage")
                message.put("category", category)
                message.put("title", title.trim())
                message.put("content", content.trim())
                message.put("authorId", currentUser.objectId)
                message.put("authorName", currentUser.getString("username") ?: "Unknown")
                message.put("timestamp", Date())
                message.put("likes", 0)
                message.put("replies", 0)

                var success = false
                val latch = CountDownLatch(1)

                message.saveInBackground(
                    SaveCallback { e ->
                        success = e == null
                        if (e == null) {
                            Log.d(TAG, "Community message sent successfully")
                            // Notify community members
                            notifyCommunityMessage(category, title, content)
                        } else {
                            Log.e(TAG, "Failed to send community message", e)
                        }
                        latch.countDown()
                    },
                )

                latch.await()
                Result.success(success)
            } catch (e: Exception) {
                Log.e(TAG, "Error sending community message", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Message Fetching Functions
     */
    private fun startMessagePolling() {
        coroutineScope.launch {
            while (true) {
                try {
                    fetchPersonalMessages()
                    fetchGroupMessages()
                    fetchCommunityMessages()

                    // Rural network optimization: longer delays for poor networks
                    val networkQuality = assessNetworkQuality()
                    val delay =
                        when (networkQuality) {
                            "Excellent", "Good" -> 5000L // 5 seconds
                            "Fair" -> 10000L // 10 seconds
                            "Poor" -> 30000L // 30 seconds
                            else -> 60000L // 1 minute for offline
                        }
                    delay(delay)
                } catch (e: Exception) {
                    Log.e(TAG, "Error in message polling", e)
                    delay(30000L) // Wait 30 seconds on error
                }
            }
        }
    }

    private suspend fun fetchPersonalMessages() {
        withContext(Dispatchers.IO) {
            try {
                val currentUser = ParseUser.getCurrentUser() ?: return@withContext

                val query = ParseQuery.getQuery<ParseObject>("PersonalMessage")
                query.whereEqualTo("receiverId", currentUser.objectId)
                query.orderByDescending("timestamp")
                query.limit = 50 // Limit for rural networks

                query.findInBackground(
                    FindCallback<ParseObject> { messages, e ->
                        if (e == null && messages != null) {
                            val personalMessages =
                                messages.map { parseObject ->
                                    PersonalMessage(
                                        id = parseObject.objectId ?: "",
                                        senderId = parseObject.getString("senderId") ?: "",
                                        receiverId = parseObject.getString("receiverId") ?: "",
                                        senderName = parseObject.getString("senderName") ?: "Unknown",
                                        content = parseObject.getString("content") ?: "",
                                        timestamp = parseObject.getDate("timestamp") ?: Date(),
                                        isRead = parseObject.getBoolean("isRead"),
                                    )
                                }
                            _personalMessages.value = personalMessages
                        } else {
                            Log.e(TAG, "Failed to fetch personal messages", e)
                        }
                    },
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching personal messages", e)
            }
        }
    }

    private suspend fun fetchGroupMessages() {
        withContext(Dispatchers.IO) {
            try {
                // Get user's groups first, then fetch messages
                val query = ParseQuery.getQuery<ParseObject>("GroupMessage")
                query.orderByDescending("timestamp")
                query.limit = 100 // Adjust based on network quality

                query.findInBackground(
                    FindCallback<ParseObject> { messages, e ->
                        if (e == null && messages != null) {
                            val groupMessages =
                                messages.map { parseObject ->
                                    GroupMessage(
                                        id = parseObject.objectId ?: "",
                                        groupId = parseObject.getString("groupId") ?: "",
                                        groupName = parseObject.getString("groupName") ?: "Group",
                                        senderId = parseObject.getString("senderId") ?: "",
                                        senderName = parseObject.getString("senderName") ?: "Unknown",
                                        content = parseObject.getString("content") ?: "",
                                        timestamp = parseObject.getDate("timestamp") ?: Date(),
                                        messageType =
                                            try {
                                                GroupMessageType.valueOf(
                                                    parseObject.getString("messageType") ?: "TEXT",
                                                )
                                            } catch (e: Exception) {
                                                GroupMessageType.TEXT
                                            },
                                    )
                                }
                            _groupMessages.value = groupMessages
                        } else {
                            Log.e(TAG, "Failed to fetch group messages", e)
                        }
                    },
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching group messages", e)
            }
        }
    }

    private suspend fun fetchCommunityMessages() {
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("CommunityMessage")
                query.orderByDescending("timestamp")
                query.limit = 50 // Adjust based on network quality

                query.findInBackground(
                    FindCallback<ParseObject> { messages, e ->
                        if (e == null && messages != null) {
                            val communityMessages =
                                messages.map { parseObject ->
                                    CommunityMessage(
                                        id = parseObject.objectId ?: "",
                                        category = parseObject.getString("category") ?: "",
                                        title = parseObject.getString("title") ?: "",
                                        content = parseObject.getString("content") ?: "",
                                        authorId = parseObject.getString("authorId") ?: "",
                                        authorName = parseObject.getString("authorName") ?: "Unknown",
                                        timestamp = parseObject.getDate("timestamp") ?: Date(),
                                        likes = parseObject.getInt("likes"),
                                        replies = parseObject.getInt("replies"),
                                    )
                                }
                            _communityMessages.value = communityMessages
                        } else {
                            Log.e(TAG, "Failed to fetch community messages", e)
                        }
                    },
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching community messages", e)
            }
        }
    }

    /**
     * Notification Functions
     */
    private fun notifyPersonalMessage(
        receiverId: String,
        senderName: String,
        content: String,
    ) {
        val intent =
            Intent(context, Class.forName("com.example.rooster.MainActivity")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("navigate_to", "chat")
                putExtra("senderId", receiverId)
            }

        showNotification(
            title = "Message from $senderName",
            body = content,
            intent = intent,
            priority = NotificationCompat.PRIORITY_HIGH,
        )
    }

    private fun notifyGroupMessage(
        groupId: String,
        senderName: String,
        content: String,
    ) {
        val intent =
            Intent(context, Class.forName("com.example.rooster.MainActivity")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("navigate_to", "community")
                putExtra("groupId", groupId)
            }

        showNotification(
            title = "Group message from $senderName",
            body = content,
            intent = intent,
            priority = NotificationCompat.PRIORITY_DEFAULT,
        )
    }

    private fun notifyCommunityMessage(
        category: String,
        title: String,
        content: String,
    ) {
        val intent =
            Intent(context, Class.forName("com.example.rooster.MainActivity")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("navigate_to", "community")
            }

        showNotification(
            title = "$category: $title",
            body = content,
            intent = intent,
            priority = NotificationCompat.PRIORITY_DEFAULT,
        )
    }

    private fun showNotification(
        title: String,
        body: String,
        intent: Intent,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
    ) {
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(priority)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = CHANNEL_DESCRIPTION
                    enableLights(true)
                    enableVibration(true)
                }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun assessNetworkQuality(): String {
        // Simplified network assessment - can be enhanced with PerformanceOptimization.kt
        return "Good" // Default for now
    }

    /**
     * Mark message as read
     */
    suspend fun markMessageAsRead(messageId: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("PersonalMessage")
                query.whereEqualTo("objectId", messageId)

                var success = false
                val latch = CountDownLatch(1)

                query.findInBackground(
                    FindCallback<ParseObject> { messages, e ->
                        if (e == null && messages?.isNotEmpty() == true) {
                            val message = messages[0]
                            message.put("isRead", true)
                            message.saveInBackground(
                                SaveCallback { saveError ->
                                    success = saveError == null
                                    latch.countDown()
                                },
                            )
                        } else {
                            latch.countDown()
                        }
                    },
                )

                latch.await()
                Result.success(success)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun cleanup() {
        coroutineScope.cancel()
    }
}
