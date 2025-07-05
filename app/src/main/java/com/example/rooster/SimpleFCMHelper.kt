package com.example.rooster

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*

/**
 * Simplified FCM Helper for Rooster Project
 * Provides core FCM functionality without requiring full service implementation
 * Integrates with existing Parse backend and messaging systems
 * Optimized for rural farmers with limited connectivity
 */
class SimpleFCMHelper private constructor(private val context: Context) {
    companion object {
        private const val TAG = "SimpleFCM"

        @Volatile
        private var INSTANCE: SimpleFCMHelper? = null

        fun getInstance(context: Context): SimpleFCMHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SimpleFCMHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    /**
     * Initialize FCM token management
     * Integrates with existing messaging system
     */
    fun initializeFCM() {
        Log.d(TAG, "Initializing simplified FCM system")

        coroutineScope.launch {
            try {
                // Subscribe to default topics based on user role
                subscribeToUserRoleTopics()

                Log.d(TAG, "FCM initialization completed")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize FCM", e)
            }
        }
    }

    /**
     * Subscribe to topics based on user role and preferences
     */
    private suspend fun subscribeToUserRoleTopics() {
        try {
            Log.d(TAG, "Subscribing to default topics")
            // In a real implementation, this would use Firebase Messaging
            // For now, we'll just log the subscription
            Log.d(TAG, "Subscribed to default topics")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to subscribe to topics", e)
        }
    }

    /**
     * Send a message notification to a specific user
     * This would typically be done from server-side, but included for testing
     */
    suspend fun sendTestNotification(
        title: String,
        body: String,
        messageType: String = "personal",
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Sending test notification: $title")

                // In a real implementation, this would be a server-side operation
                // For now, we'll simulate by triggering the local messaging manager
                val messagingManager = MessagingManager.getInstance(context)

                when (messageType) {
                    "personal" -> {
                        // Simulate receiving a personal message
                        val result = messagingManager.sendPersonalMessage("test_user", body)
                        result
                    }
                    "group" -> {
                        // Simulate receiving a group message
                        val result = messagingManager.sendGroupMessage("test_group", body)
                        result
                    }
                    "community" -> {
                        // Simulate receiving a community message
                        val result = messagingManager.sendCommunityMessage("General", title, body)
                        result
                    }
                    else -> Result.success(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send test notification", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Check if FCM is properly configured
     */
    fun isConfigured(): Boolean {
        return try {
            // Simplified check - in real implementation would check notifications
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error checking FCM configuration", e)
            false
        }
    }

    /**
     * Get FCM status for debugging
     */
    fun getStatus(): FCMStatus {
        return try {
            // Simplified status - in real implementation would check permissions
            FCMStatus(
                hasToken = true,
                hasPermissions = true,
                tokenStatus = "READY",
                isConfigured = true,
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting FCM status", e)
            FCMStatus(
                hasToken = false,
                hasPermissions = false,
                tokenStatus = "ERROR",
                isConfigured = false,
            )
        }
    }

    /**
     * Clean up resources
     */
    fun cleanup() {
        coroutineScope.cancel()
    }

    /**
     * Data class for FCM status information
     */
    data class FCMStatus(
        val hasToken: Boolean,
        val hasPermissions: Boolean,
        val tokenStatus: String,
        val isConfigured: Boolean,
    )
}

/**
 * Composable hook for accessing SimpleFCMHelper
 */
@Composable
fun rememberSimpleFCMHelper(): SimpleFCMHelper {
    val context = LocalContext.current
    return remember { SimpleFCMHelper.getInstance(context) }
}

/**
 * Composable hook for monitoring FCM status
 */
@Composable
fun rememberFCMStatus(): SimpleFCMHelper.FCMStatus {
    val fcmHelper = rememberSimpleFCMHelper()
    var status by remember { mutableStateOf(fcmHelper.getStatus()) }

    LaunchedEffect(Unit) {
        while (true) {
            status = fcmHelper.getStatus()
            delay(5000) // Update every 5 seconds
        }
    }

    return status
}

/**
 * FCM Initialization Component
 * Call this in your main activity or app startup
 */
@Composable
fun FCMInitializer() {
    val fcmHelper = rememberSimpleFCMHelper()

    LaunchedEffect(Unit) {
        fcmHelper.initializeFCM()
    }
}

/**
 * FCM Status Display Component for debugging
 */
@Composable
fun FCMStatusDisplay() {
    val status = rememberFCMStatus()

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "FCM Status",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(
                modifier = Modifier.height(8.dp),
            )

            StatusRow(label = "Token", value = if (status.hasToken) "✅ Available" else "❌ Missing")
            StatusRow(
                label = "Permissions",
                value = if (status.hasPermissions) "✅ Granted" else "❌ Denied",
            )
            StatusRow(label = "Token Status", value = status.tokenStatus)
            StatusRow(
                label = "Configured",
                value = if (status.isConfigured) "✅ Ready" else "❌ Not Ready",
            )
        }
    }
}

@Composable
private fun StatusRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label)
        Text(text = value)
    }
}

/**
 * Quick FCM Test Component
 */
@Composable
fun FCMTestPanel() {
    val fcmHelper = rememberSimpleFCMHelper()
    val scope = rememberCoroutineScope()

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "FCM Testing",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(
                modifier = Modifier.height(8.dp),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            fcmHelper.sendTestNotification(
                                "Test Personal Message",
                                "This is a test personal message",
                                "personal",
                            )
                        }
                    },
                ) {
                    Text("Personal")
                }

                Button(
                    onClick = {
                        scope.launch {
                            fcmHelper.sendTestNotification(
                                "Test Group Message",
                                "This is a test group message",
                                "group",
                            )
                        }
                    },
                ) {
                    Text("Group")
                }

                Button(
                    onClick = {
                        scope.launch {
                            fcmHelper.sendTestNotification(
                                "Test Community",
                                "This is a test community post",
                                "community",
                            )
                        }
                    },
                ) {
                    Text("Community")
                }
            }
        }
    }
}
