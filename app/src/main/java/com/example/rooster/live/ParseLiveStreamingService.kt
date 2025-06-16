package com.example.rooster.live

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Parse-integrated live streaming service
 * Handles all backend operations for live streaming using Parse Cloud Functions
 */
class ParseLiveStreamingService {
    // Live session state
    private val _currentSession = MutableStateFlow<BroadcastSession?>(null)
    val currentSession: StateFlow<BroadcastSession?> = _currentSession.asStateFlow()

    private val _activeBroadcasts = MutableStateFlow<List<BroadcastSession>>(emptyList())
    val activeBroadcasts: StateFlow<List<BroadcastSession>> = _activeBroadcasts.asStateFlow()

    // Coin balance state
    private val _coinBalance = MutableStateFlow<Int>(0)
    val coinBalance: StateFlow<Int> = _coinBalance.asStateFlow()

    /**
     * Start a live broadcast session
     */
    suspend fun startBroadcast(
        birdId: String,
        broadcasterName: String,
        birdType: String,
        category: String = "standard",
    ): BroadcastSession? {
        return try {
            val params =
                hashMapOf<String, Any>(
                    "birdId" to birdId,
                    "broadcasterName" to broadcasterName,
                    "birdType" to birdType,
                    "category" to category,
                )

            val result = callCloudFunction<HashMap<String, Any>>("startBroadcast", params)
            val sessionId = result?.get("sessionId") as? String

            if (sessionId != null) {
                val session =
                    BroadcastSession(
                        objectId = sessionId,
                        birdId = birdId,
                        broadcasterName = broadcasterName,
                        birdType = birdType,
                        category = category,
                        isActive = true,
                        startTime = System.currentTimeMillis(),
                    )
                _currentSession.value = session

                // Subscribe to real-time updates
                subscribeToSessionUpdates(sessionId)

                session
            } else {
                null
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    /**
     * Join an existing broadcast as a viewer
     */
    suspend fun joinBroadcast(sessionId: String): BroadcastJoinResult? {
        return try {
            val params = hashMapOf<String, Any>("sessionId" to sessionId)
            val result = callCloudFunction<HashMap<String, Any>>("joinBroadcast", params)

            if (result?.get("success") == true) {
                BroadcastJoinResult(
                    success = true,
                    viewerCount = result["viewerCount"] as? Int ?: 0,
                    broadcasterName = result["broadcasterName"] as? String ?: "",
                    birdType = result["birdType"] as? String ?: "",
                    startTime = (result["startTime"] as? ParseObject)?.createdAt?.time ?: 0L,
                )
            } else {
                null
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    /**
     * Send a gift during live broadcast
     */
    suspend fun sendGift(
        sessionId: String,
        giftType: String,
        message: String = "",
    ): GiftSendResult? {
        return try {
            val params =
                hashMapOf<String, Any>(
                    "sessionId" to sessionId,
                    "giftType" to giftType,
                    "message" to message,
                )

            val result = callCloudFunction<HashMap<String, Any>>("sendGift", params)

            if (result?.get("success") == true) {
                // Update local coin balance
                _coinBalance.value = result["remainingCoins"] as? Int ?: 0

                // Trigger local gift animation
                GiftEventsStore.publish(
                    Gift(
                        birdId = _currentSession.value?.birdId ?: "",
                        type = giftType,
                        icon = giftType,
                        senderId = ParseUser.getCurrentUser()?.objectId,
                        senderName = ParseUser.getCurrentUser()?.username ?: "Anonymous",
                    ),
                )

                GiftSendResult(
                    success = true,
                    remainingCoins = result["remainingCoins"] as? Int ?: 0,
                    broadcasterEarned = result["broadcasterEarned"] as? Int ?: 0,
                    totalGifts = result["totalGifts"] as? Int ?: 0,
                )
            } else {
                null
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    /**
     * Stop the current broadcast
     */
    suspend fun stopBroadcast(sessionId: String): BroadcastStatistics? {
        return try {
            val params = hashMapOf<String, Any>("sessionId" to sessionId)
            val result = callCloudFunction<HashMap<String, Any>>("stopBroadcast", params)

            if (result?.get("success") == true) {
                _currentSession.value = null

                val stats = result["statistics"] as? HashMap<String, Any>
                if (stats != null) {
                    BroadcastStatistics(
                        duration = stats["duration"] as? Int ?: 0,
                        totalViewers = stats["totalViewers"] as? Int ?: 0,
                        totalGifts = stats["totalGifts"] as? Int ?: 0,
                        totalRevenue = stats["totalRevenue"] as? Int ?: 0,
                        avgViewersPerMinute = stats["avgViewersPerMinute"] as? Int ?: 0,
                    )
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    /**
     * Get list of active broadcasts
     */
    suspend fun getActiveBroadcasts(
        region: String? = null,
        category: String? = null,
        limit: Int = 20,
    ): List<BroadcastSession> {
        return try {
            val params =
                hashMapOf<String, Any>(
                    "limit" to limit,
                ).apply {
                    region?.let { put("region", it) }
                    category?.let { put("category", it) }
                }

            val result = callCloudFunction<HashMap<String, Any>>("getActiveBroadcasts", params)
            val broadcasts = result?.get("broadcasts") as? List<HashMap<String, Any>>

            broadcasts?.map { broadcastMap ->
                BroadcastSession(
                    objectId = broadcastMap["sessionId"] as? String ?: "",
                    birdId = broadcastMap["birdId"] as? String ?: "",
                    broadcasterName = broadcastMap["broadcasterName"] as? String ?: "",
                    birdType = broadcastMap["birdType"] as? String ?: "",
                    category = broadcastMap["category"] as? String ?: "standard",
                    viewerCount = broadcastMap["viewerCount"] as? Int ?: 0,
                    totalGifts = broadcastMap["totalGifts"] as? Int ?: 0,
                    isActive = true,
                    startTime = (broadcastMap["startTime"] as? Number)?.toLong() ?: 0L,
                    duration = broadcastMap["duration"] as? Int ?: 0,
                )
            } ?: emptyList()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }

    /**
     * Get broadcast statistics for a user
     */
    suspend fun getBroadcastStats(
        userId: String? = null,
        period: String = "week",
    ): BroadcastUserStats? {
        return try {
            val params =
                hashMapOf<String, Any>(
                    "period" to period,
                ).apply {
                    userId?.let { put("userId", it) }
                }

            val result = callCloudFunction<HashMap<String, Any>>("getBroadcastStats", params)
            val stats = result?.get("stats") as? HashMap<String, Any>

            if (stats != null) {
                BroadcastUserStats(
                    totalBroadcasts = stats["totalBroadcasts"] as? Int ?: 0,
                    totalViewers = stats["totalViewers"] as? Int ?: 0,
                    totalGifts = stats["totalGifts"] as? Int ?: 0,
                    totalRevenue = stats["totalRevenue"] as? Int ?: 0,
                    totalDuration = stats["totalDuration"] as? Int ?: 0,
                    avgViewersPerBroadcast = stats["avgViewersPerBroadcast"] as? Int ?: 0,
                    avgGiftsPerBroadcast = stats["avgGiftsPerBroadcast"] as? Int ?: 0,
                    avgRevenuePerBroadcast = stats["avgRevenuePerBroadcast"] as? Int ?: 0,
                )
            } else {
                null
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    /**
     * Subscribe to real-time broadcast session updates using Parse LiveQuery
     */
    private fun subscribeToSessionUpdates(sessionId: String) {
        try {
            val query = ParseQuery.getQuery<ParseObject>("BroadcastSession")
            query.whereEqualTo("objectId", sessionId)

            // Note: Parse LiveQuery would be used here for real-time updates
            // For now, we'll use periodic polling as fallback
            // TODO: Implement Parse LiveQuery when available
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /**
     * Helper function to call Parse Cloud Functions with proper error handling
     */
    private suspend fun <T> callCloudFunction(
        functionName: String,
        params: HashMap<String, Any>,
    ): T? {
        return suspendCancellableCoroutine { continuation ->
            ParseCloud.callFunctionInBackground<T>(functionName, params) { result, error ->
                if (error == null) {
                    continuation.resume(result)
                } else {
                    continuation.resumeWithException(error)
                }
            }
        }
    }
}

/**
 * Data classes for Parse live streaming
 */
data class BroadcastSession(
    val objectId: String = "",
    val birdId: String = "",
    val broadcasterName: String = "",
    val birdType: String = "",
    val category: String = "standard",
    val viewerCount: Int = 0,
    val totalGifts: Int = 0,
    val giftRevenue: Int = 0,
    val isActive: Boolean = false,
    val startTime: Long = 0L,
    val duration: Int = 0,
    val viewers: List<String> = emptyList(),
    val recentGifts: List<BroadcastGift> = emptyList(),
)

data class BroadcastGift(
    val type: String,
    val senderId: String,
    val senderName: String,
    val message: String,
    val cost: Int,
    val timestamp: Long,
)

data class BroadcastJoinResult(
    val success: Boolean,
    val viewerCount: Int,
    val broadcasterName: String,
    val birdType: String,
    val startTime: Long,
)

data class GiftSendResult(
    val success: Boolean,
    val remainingCoins: Int,
    val broadcasterEarned: Int,
    val totalGifts: Int,
)

data class BroadcastStatistics(
    val duration: Int,
    val totalViewers: Int,
    val totalGifts: Int,
    val totalRevenue: Int,
    val avgViewersPerMinute: Int,
)

data class BroadcastUserStats(
    val totalBroadcasts: Int,
    val totalViewers: Int,
    val totalGifts: Int,
    val totalRevenue: Int,
    val totalDuration: Int,
    val avgViewersPerBroadcast: Int,
    val avgGiftsPerBroadcast: Int,
    val avgRevenuePerBroadcast: Int,
)
