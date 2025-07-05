package com.example.rooster

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.parse.ParseUser
import com.parse.SaveCallback
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import java.util.concurrent.CountDownLatch

/**
 * FCM Token Manager for Rooster Project
 * Manages Firebase Cloud Messaging tokens for rural farmers
 * Integrates with Parse backend for user targeting
 * Optimized for low-end devices and poor network conditions
 */
class FCMTokenManager private constructor(private val context: Context) {
    companion object {
        private const val TAG = "FCMTokenManager"
        private const val PREFS_NAME = "fcm_token_prefs"
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_TOKEN_TIMESTAMP = "token_timestamp"
        private const val KEY_TOKEN_SENT_TO_SERVER = "token_sent_to_server"

        @Volatile
        private var INSTANCE: FCMTokenManager? = null

        fun getInstance(context: Context): FCMTokenManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FCMTokenManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _tokenStatus = MutableStateFlow(TokenStatus.UNKNOWN)
    val tokenStatus: StateFlow<TokenStatus> = _tokenStatus

    private val _currentToken = MutableStateFlow<String?>(null)
    val currentToken: StateFlow<String?> = _currentToken

    enum class TokenStatus {
        UNKNOWN,
        GENERATING,
        GENERATED,
        SENT_TO_SERVER,
        FAILED,
    }

    init {
        // Load existing token from preferences
        val savedToken = prefs.getString(KEY_FCM_TOKEN, null)
        val tokenSentToServer = prefs.getBoolean(KEY_TOKEN_SENT_TO_SERVER, false)

        if (savedToken != null) {
            _currentToken.value = savedToken
            _tokenStatus.value =
                if (tokenSentToServer) TokenStatus.SENT_TO_SERVER else TokenStatus.GENERATED
        }
    }

    /**
     * Store FCM token locally and send to Parse backend
     */
    fun saveToken(token: String) {
        Log.d(TAG, "Saving FCM token: ${token.take(10)}...")

        // Save locally
        prefs.edit()
            .putString(KEY_FCM_TOKEN, token)
            .putLong(KEY_TOKEN_TIMESTAMP, System.currentTimeMillis())
            .putBoolean(KEY_TOKEN_SENT_TO_SERVER, false)
            .apply()

        _currentToken.value = token
        _tokenStatus.value = TokenStatus.GENERATED

        // Send to Parse backend
        sendTokenToServer(token)
    }

    /**
     * Send FCM token to Parse backend for user targeting
     */
    private fun sendTokenToServer(token: String) {
        coroutineScope.launch {
            try {
                _tokenStatus.value = TokenStatus.GENERATING

                withContext(Dispatchers.IO) {
                    val currentUser = ParseUser.getCurrentUser()
                    if (currentUser == null) {
                        Log.w(TAG, "No current user, will retry token upload after login")
                        return@withContext
                    }

                    val deviceInfo = getDeviceInfo()

                    // Update user with FCM token and device info
                    currentUser.put("fcmToken", token)
                    currentUser.put("tokenUpdatedAt", Date())
                    currentUser.put("deviceInfo", deviceInfo)
                    currentUser.put("lastActiveAt", Date())

                    var success = false
                    val latch = CountDownLatch(1)

                    currentUser.saveInBackground(
                        SaveCallback { e ->
                            if (e == null) {
                                success = true
                                Log.d(TAG, "FCM token sent to server successfully")

                                // Mark as sent in local storage
                                prefs.edit()
                                    .putBoolean(KEY_TOKEN_SENT_TO_SERVER, true)
                                    .apply()

                                _tokenStatus.value = TokenStatus.SENT_TO_SERVER
                            } else {
                                Log.e(TAG, "Failed to send FCM token to server", e)
                                _tokenStatus.value = TokenStatus.FAILED
                            }
                            latch.countDown()
                        },
                    )

                    latch.await()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending FCM token to server", e)
                _tokenStatus.value = TokenStatus.FAILED
            }
        }
    }

    /**
     * Retry sending token to server (useful after login)
     */
    fun retryTokenUpload() {
        val token = _currentToken.value
        if (token != null && _tokenStatus.value != TokenStatus.SENT_TO_SERVER) {
            Log.d(TAG, "Retrying FCM token upload")
            sendTokenToServer(token)
        }
    }

    /**
     * Get device information for better targeting
     */
    private fun getDeviceInfo(): Map<String, Any> {
        return mapOf(
            "model" to (android.os.Build.MODEL ?: "Unknown"),
            "manufacturer" to (android.os.Build.MANUFACTURER ?: "Unknown"),
            "osVersion" to android.os.Build.VERSION.RELEASE,
            "appVersion" to getAppVersion(),
            "locale" to Locale.getDefault().toString(),
            "timezone" to TimeZone.getDefault().id,
            "registeredAt" to Date(),
        )
    }

    /**
     * Get app version for device info
     */
    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.versionCode})"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    /**
     * Check if token needs to be refreshed (older than 7 days)
     */
    fun shouldRefreshToken(): Boolean {
        val tokenTimestamp = prefs.getLong(KEY_TOKEN_TIMESTAMP, 0)
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        return tokenTimestamp < sevenDaysAgo
    }

    /**
     * Clear stored token (useful for logout)
     */
    fun clearToken() {
        Log.d(TAG, "Clearing FCM token")
        prefs.edit().clear().apply()
        _currentToken.value = null
        _tokenStatus.value = TokenStatus.UNKNOWN
    }

    /**
     * Subscribe to topic for group messaging
     */
    fun subscribeToTopic(topic: String) {
        Log.d(TAG, "Subscribing to topic: $topic")
        // Firebase Messaging topic subscription would go here
        // For now, we'll store subscriptions in Parse
        coroutineScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val currentUser = ParseUser.getCurrentUser()
                    if (currentUser != null) {
                        val currentTopics =
                            currentUser.getList<String>("subscribedTopics") ?: mutableListOf()
                        if (!currentTopics.contains(topic)) {
                            currentTopics.add(topic)
                            currentUser.put("subscribedTopics", currentTopics)
                            currentUser.saveInBackground()
                            Log.d(TAG, "Subscribed to topic: $topic")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to subscribe to topic: $topic", e)
            }
        }
    }

    /**
     * Unsubscribe from topic
     */
    fun unsubscribeFromTopic(topic: String) {
        Log.d(TAG, "Unsubscribing from topic: $topic")
        coroutineScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val currentUser = ParseUser.getCurrentUser()
                    if (currentUser != null) {
                        val currentTopics =
                            currentUser.getList<String>("subscribedTopics") ?: mutableListOf()
                        if (currentTopics.contains(topic)) {
                            currentTopics.remove(topic)
                            currentUser.put("subscribedTopics", currentTopics)
                            currentUser.saveInBackground()
                            Log.d(TAG, "Unsubscribed from topic: $topic")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to unsubscribe from topic: $topic", e)
            }
        }
    }

    /**
     * Get user's subscribed topics
     */
    suspend fun getSubscribedTopics(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = ParseUser.getCurrentUser()
                currentUser?.getList<String>("subscribedTopics") ?: emptyList()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get subscribed topics", e)
                emptyList()
            }
        }
    }

    /**
     * Update user preferences for notifications
     */
    fun updateNotificationPreferences(preferences: NotificationPreferences) {
        coroutineScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val currentUser = ParseUser.getCurrentUser()
                    if (currentUser != null) {
                        currentUser.put("notificationPreferences", preferences.toMap())
                        currentUser.saveInBackground(
                            SaveCallback { e ->
                                if (e == null) {
                                    Log.d(TAG, "Notification preferences updated")
                                } else {
                                    Log.e(TAG, "Failed to update notification preferences", e)
                                }
                            },
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating notification preferences", e)
            }
        }
    }

    fun cleanup() {
        coroutineScope.cancel()
    }

    /**
     * Data class for notification preferences
     */
    data class NotificationPreferences(
        val enablePersonalMessages: Boolean = true,
        val enableGroupMessages: Boolean = true,
        val enableCommunityUpdates: Boolean = true,
        val enableMarketplaceAlerts: Boolean = true,
        val enableCulturalEvents: Boolean = true,
        val enableTransferAlerts: Boolean = true,
        val enableHealthReminders: Boolean = true,
        val quietHoursStart: String = "22:00", // 10 PM
        val quietHoursEnd: String = "06:00", // 6 AM
    ) {
        fun toMap(): Map<String, Any> {
            return mapOf(
                "enablePersonalMessages" to enablePersonalMessages,
                "enableGroupMessages" to enableGroupMessages,
                "enableCommunityUpdates" to enableCommunityUpdates,
                "enableMarketplaceAlerts" to enableMarketplaceAlerts,
                "enableCulturalEvents" to enableCulturalEvents,
                "enableTransferAlerts" to enableTransferAlerts,
                "enableHealthReminders" to enableHealthReminders,
                "quietHoursStart" to quietHoursStart,
                "quietHoursEnd" to quietHoursEnd,
            )
        }

        companion object {
            fun fromMap(map: Map<String, Any>?): NotificationPreferences {
                if (map == null) return NotificationPreferences()

                return NotificationPreferences(
                    enablePersonalMessages = map["enablePersonalMessages"] as? Boolean ?: true,
                    enableGroupMessages = map["enableGroupMessages"] as? Boolean ?: true,
                    enableCommunityUpdates = map["enableCommunityUpdates"] as? Boolean ?: true,
                    enableMarketplaceAlerts = map["enableMarketplaceAlerts"] as? Boolean ?: true,
                    enableCulturalEvents = map["enableCulturalEvents"] as? Boolean ?: true,
                    enableTransferAlerts = map["enableTransferAlerts"] as? Boolean ?: true,
                    enableHealthReminders = map["enableHealthReminders"] as? Boolean ?: true,
                    quietHoursStart = map["quietHoursStart"] as? String ?: "22:00",
                    quietHoursEnd = map["quietHoursEnd"] as? String ?: "06:00",
                )
            }
        }
    }
}

/**
 * Composable hook for accessing FCM token manager
 */
@Composable
fun rememberFCMTokenManager(): FCMTokenManager {
    val context = LocalContext.current
    return remember { FCMTokenManager.getInstance(context) }
}

/**
 * Composable hook for monitoring token status
 */
@Composable
fun rememberTokenStatus(): FCMTokenManager.TokenStatus {
    val tokenManager = rememberFCMTokenManager()
    val tokenStatus by tokenManager.tokenStatus.collectAsState()
    return tokenStatus
}
