package com.example.rooster.services.backend

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

// Real-time auction events
sealed class AuctionEvent {
    data class BidPlaced(
        val auctionId: String,
        val bidderId: String,
        val bidAmount: Double,
        val timestamp: Long,
        val bidderName: String,
    ) : AuctionEvent()

    data class AuctionStarted(
        val auctionId: String,
        val startTime: Long,
        val duration: Long,
    ) : AuctionEvent()

    data class AuctionEnded(
        val auctionId: String,
        val winnerId: String?,
        val winningBid: Double?,
        val endTime: Long,
    ) : AuctionEvent()

    data class ParticipantJoined(
        val auctionId: String,
        val participantId: String,
        val participantName: String,
    ) : AuctionEvent()

    data class ParticipantLeft(
        val auctionId: String,
        val participantId: String,
    ) : AuctionEvent()

    data class AuctionTimeUpdate(
        val auctionId: String,
        val remainingTime: Long,
    ) : AuctionEvent()

    data class ErrorEvent(
        val message: String,
        val code: Int? = null,
    ) : AuctionEvent()

    object ConnectionEstablished : AuctionEvent()

    object ConnectionLost : AuctionEvent()
}

// Connection states for UI feedback
sealed class ConnectionState {
    object Connecting : ConnectionState()

    object Connected : ConnectionState()

    object Disconnected : ConnectionState()

    data class Error(val message: String) : ConnectionState()
}

@Singleton
class RealTimeAuctionService
    @Inject
    constructor() {
        private val tag = "RealTimeAuctionService"

        // WebSocket client with optimized settings for rural connectivity
        private val client =
            OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .pingInterval(20, TimeUnit.SECONDS) // Keep connection alive
                .retryOnConnectionFailure(true)
                .build()

        private var webSocket: WebSocket? = null
        private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        // Event channels
        private val eventChannel = Channel<AuctionEvent>(Channel.UNLIMITED)
        val events = eventChannel.receiveAsFlow()

        private val connectionStateChannel = Channel<ConnectionState>(Channel.CONFLATED)
        val connectionState = connectionStateChannel.receiveAsFlow()

        // Subscription management
        private val subscribedAuctions = mutableSetOf<String>()
        private var reconnectAttempts = 0
        private val maxReconnectAttempts = 10
        private var reconnectJob: Job? = null

        // Message queue for offline scenarios
        private val messageQueue = mutableListOf<String>()

        fun connectToAuctionUpdates(serverUrl: String = "wss://your-server.com/auction-ws") {
            if (webSocket != null) {
                Log.d(tag, "WebSocket already connected")
                return
            }

            coroutineScope.launch {
                connectionStateChannel.send(ConnectionState.Connecting)
            }

            val request =
                Request.Builder()
                    .url(serverUrl)
                    .addHeader("User-Agent", "RoosterApp-Android/1.0")
                    .build()

            webSocket = client.newWebSocket(request, createWebSocketListener())
        }

        private fun createWebSocketListener() =
            object : WebSocketListener() {
                override fun onOpen(
                    webSocket: WebSocket,
                    response: Response,
                ) {
                    Log.d(tag, "WebSocket connection opened")
                    reconnectAttempts = 0

                    coroutineScope.launch {
                        connectionStateChannel.send(ConnectionState.Connected)
                        eventChannel.send(AuctionEvent.ConnectionEstablished)

                        // Send queued messages
                        messageQueue.forEach { message ->
                            webSocket.send(message)
                        }
                        messageQueue.clear()

                        // Re-subscribe to previously subscribed auctions
                        subscribedAuctions.forEach { auctionId ->
                            subscribeToAuction(auctionId)
                        }
                    }
                }

                override fun onMessage(
                    webSocket: WebSocket,
                    text: String,
                ) {
                    Log.d(tag, "Received WebSocket message: $text")
                    handleIncomingMessage(text)
                }

                override fun onMessage(
                    webSocket: WebSocket,
                    bytes: ByteString,
                ) {
                    Log.d(tag, "Received WebSocket binary message")
                    handleIncomingMessage(bytes.utf8())
                }

                override fun onClosing(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String,
                ) {
                    Log.d(tag, "WebSocket closing: $code $reason")
                    webSocket.close(1000, null)
                }

                override fun onClosed(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String,
                ) {
                    Log.d(tag, "WebSocket closed: $code $reason")
                    this@RealTimeAuctionService.webSocket = null

                    coroutineScope.launch {
                        connectionStateChannel.send(ConnectionState.Disconnected)
                        eventChannel.send(AuctionEvent.ConnectionLost)

                        // Attempt reconnection if not intentionally closed
                        if (code != 1000 && reconnectAttempts < maxReconnectAttempts) {
                            scheduleReconnect()
                        }
                    }
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?,
                ) {
                    Log.e(tag, "WebSocket failure", t)
                    this@RealTimeAuctionService.webSocket = null

                    coroutineScope.launch {
                        val errorMessage = t.message ?: "Connection failed"
                        connectionStateChannel.send(ConnectionState.Error(errorMessage))
                        eventChannel.send(AuctionEvent.ErrorEvent(errorMessage))

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

                val event =
                    when (eventType) {
                        "bid_placed" ->
                            AuctionEvent.BidPlaced(
                                auctionId = data.getString("auctionId"),
                                bidderId = data.getString("bidderId"),
                                bidAmount = data.getDouble("bidAmount"),
                                timestamp = data.getLong("timestamp"),
                                bidderName = data.optString("bidderName", "Anonymous"),
                            )

                        "auction_started" ->
                            AuctionEvent.AuctionStarted(
                                auctionId = data.getString("auctionId"),
                                startTime = data.getLong("startTime"),
                                duration = data.getLong("duration"),
                            )

                        "auction_ended" ->
                            AuctionEvent.AuctionEnded(
                                auctionId = data.getString("auctionId"),
                                winnerId = data.optString("winnerId").takeIf { it.isNotEmpty() },
                                winningBid = if (data.has("winningBid")) data.getDouble("winningBid") else null,
                                endTime = data.getLong("endTime"),
                            )

                        "participant_joined" ->
                            AuctionEvent.ParticipantJoined(
                                auctionId = data.getString("auctionId"),
                                participantId = data.getString("participantId"),
                                participantName = data.optString("participantName", "Anonymous"),
                            )

                        "participant_left" ->
                            AuctionEvent.ParticipantLeft(
                                auctionId = data.getString("auctionId"),
                                participantId = data.getString("participantId"),
                            )

                        "time_update" ->
                            AuctionEvent.AuctionTimeUpdate(
                                auctionId = data.getString("auctionId"),
                                remainingTime = data.getLong("remainingTime"),
                            )

                        else -> {
                            Log.w(tag, "Unknown event type: $eventType")
                            null
                        }
                    }

                event?.let { auctionEvent ->
                    coroutineScope.launch {
                        eventChannel.send(auctionEvent)

                        // Cache auction data for offline access
                        if (auctionEvent is AuctionEvent.BidPlaced) {
                            cacheAuctionUpdate(auctionEvent)
                        }
                    }
                }
            } catch (e: JSONException) {
                Log.e(tag, "Failed to parse WebSocket message", e)
                coroutineScope.launch {
                    eventChannel.send(AuctionEvent.ErrorEvent("Failed to parse server message"))
                }
            }
        }

        fun subscribeToAuction(auctionId: String) {
            subscribedAuctions.add(auctionId)

            val subscribeMessage =
                JSONObject().apply {
                    put("action", "subscribe")
                    put("auctionId", auctionId)
                }.toString()

            sendMessage(subscribeMessage)
        }

        fun unsubscribeFromAuction(auctionId: String) {
            subscribedAuctions.remove(auctionId)

            val unsubscribeMessage =
                JSONObject().apply {
                    put("action", "unsubscribe")
                    put("auctionId", auctionId)
                }.toString()

            sendMessage(unsubscribeMessage)
        }

        fun placeBid(
            auctionId: String,
            bidAmount: Double,
            bidderId: String,
            bidderName: String,
        ) {
            val bidMessage =
                JSONObject().apply {
                    put("action", "place_bid")
                    put("auctionId", auctionId)
                    put("bidAmount", bidAmount)
                    put("bidderId", bidderId)
                    put("bidderName", bidderName)
                    put("timestamp", System.currentTimeMillis())
                }.toString()

            sendMessage(bidMessage)
        }

        private fun sendMessage(message: String) {
            val currentWebSocket = webSocket
            if (currentWebSocket != null) {
                val success = currentWebSocket.send(message)
                if (!success) {
                    Log.w(tag, "Failed to send message, queueing for later")
                    messageQueue.add(message)
                }
            } else {
                Log.w(tag, "WebSocket not connected, queueing message")
                messageQueue.add(message)
            }
        }

        private fun scheduleReconnect() {
            reconnectJob?.cancel()
            reconnectJob =
                coroutineScope.launch {
                    val delayMs = (2000 * (reconnectAttempts + 1)).coerceAtMost(30000) // Exponential backoff, max 30s

                    Log.d(tag, "Scheduling reconnect attempt ${reconnectAttempts + 1} in ${delayMs}ms")
                    delay(delayMs.toLong())

                    reconnectAttempts++
                    connectToAuctionUpdates()
                }
        }

        private suspend fun cacheAuctionUpdate(bidEvent: AuctionEvent.BidPlaced) {
            try {
                // Simple in-memory cache for now
                Log.d(tag, "Caching auction update for auction ${bidEvent.auctionId}")
            } catch (e: Exception) {
                Log.e(tag, "Failed to cache auction update", e)
            }
        }

        fun disconnect() {
            Log.d(tag, "Disconnecting WebSocket")
            reconnectJob?.cancel()
            webSocket?.close(1000, "User disconnected")
            webSocket = null
            subscribedAuctions.clear()
            messageQueue.clear()
        }

        fun getConnectionStateFlow(): Flow<ConnectionState> = connectionState

        fun isConnected(): Boolean = webSocket != null

        // Get cached latest bid for offline scenarios
        suspend fun getCachedLatestBid(auctionId: String): AuctionEvent.BidPlaced? {
            return try {
                // Simple implementation for now
                null
            } catch (e: Exception) {
                Log.e(tag, "Failed to get cached bid", e)
                null
            }
        }

        fun cleanup() {
            disconnect()
            coroutineScope.cancel()
        }
    }

// Optimistic UI update helper
class OptimisticBidManager {
    private val optimisticBids = mutableMapOf<String, AuctionEvent.BidPlaced>()

    fun addOptimisticBid(bid: AuctionEvent.BidPlaced) {
        optimisticBids[bid.auctionId] = bid
    }

    fun confirmBid(auctionId: String) {
        optimisticBids.remove(auctionId)
    }

    fun rejectBid(auctionId: String) {
        optimisticBids.remove(auctionId)
    }

    fun getOptimisticBid(auctionId: String): AuctionEvent.BidPlaced? {
        return optimisticBids[auctionId]
    }

    fun hasOptimisticBid(auctionId: String): Boolean {
        return optimisticBids.containsKey(auctionId)
    }
}

// Auction loading states for UI
sealed class AuctionLoadingState {
    object LoadingAuctions : AuctionLoadingState()

    object LoadingBids : AuctionLoadingState()

    object PlacingBid : AuctionLoadingState()

    object ConnectingToLive : AuctionLoadingState()

    data class LoadingAuctionDetails(val auctionId: String) : AuctionLoadingState()

    data class AuctionError(val message: String, val isRetryable: Boolean = true) :
        AuctionLoadingState()

    object Success : AuctionLoadingState()
}
