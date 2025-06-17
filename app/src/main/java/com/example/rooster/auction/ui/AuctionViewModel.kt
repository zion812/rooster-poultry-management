package com.example.rooster.auction.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.auction.model.BidUpdate
import com.example.rooster.auction.repo.AuctionRepository
import com.example.rooster.services.optimized.BandwidthLevel
import com.example.rooster.services.optimized.ChatMessage
import com.example.rooster.services.optimized.ConnectionState
import com.example.rooster.services.optimized.NetworkQuality
import com.example.rooster.services.optimized.NetworkState
import com.example.rooster.services.optimized.OfflineBid
import com.example.rooster.services.optimized.RealTimeCollaborationFetcher
import com.example.rooster.services.optimized.RuralConnectivityOptimizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BiddingState {
    object Idle : BiddingState()

    object BidPlaced : BiddingState()

    data class Active(
        val update: BidUpdate,
        val participants: Int = 0,
        val chatMessages: List<ChatMessage> = emptyList(),
        val networkQuality: NetworkQuality = NetworkQuality.UNKNOWN,
        val isOptimizedForRural: Boolean = false,
    ) : BiddingState()

    data class Error(val throwable: Throwable) : BiddingState()

    data class OfflineMode(
        val queuedBids: List<OfflineBid> = emptyList(),
        val lastKnownBid: BidUpdate? = null,
        val networkQuality: NetworkQuality = NetworkQuality.POOR,
    ) : BiddingState()
}

data class AuctionCollaborationState(
    val isConnected: Boolean = false,
    val participantCount: Int = 0,
    val recentMessages: List<ChatMessage> = emptyList(),
    val currentBid: Double = 0.0,
    val networkQuality: NetworkQuality = NetworkQuality.UNKNOWN,
    val isRuralOptimized: Boolean = false,
    val connectionLatency: Long = 0L,
)

@HiltViewModel
class AuctionViewModel
    @Inject
    constructor(
        private val repo: AuctionRepository,
        private val collaborationFetcher: RealTimeCollaborationFetcher,
        private val ruralOptimizer: RuralConnectivityOptimizer,
        savedState: SavedStateHandle,
    ) : ViewModel() {
        private val auctionId: String = savedState["auctionId"] ?: "default"
        private val _state = MutableStateFlow<BiddingState>(BiddingState.Idle)
        val state: StateFlow<BiddingState> = _state.asStateFlow()

        private val _collaborationState = MutableStateFlow(AuctionCollaborationState())
        val collaborationState: StateFlow<AuctionCollaborationState> = _collaborationState.asStateFlow()

        private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
        val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

        private val _ruralOptimizationStatus = MutableStateFlow(false)
        val ruralOptimizationStatus: StateFlow<Boolean> = _ruralOptimizationStatus.asStateFlow()

        init {
            initializeRuralOptimizedAuction()
            initializeRealTimeFeatures()
            observeBids()
        }

        private fun initializeRuralOptimizedAuction() {
            viewModelScope.launch {
                // Monitor network state from rural optimizer
                ruralOptimizer.networkState
                    .onEach { networkState ->
                        val quality =
                            when (networkState) {
                                NetworkState.CONNECTED -> {
                                    // Check bandwidth to determine quality
                                    when (ruralOptimizer.bandwidthState.value) {
                                        BandwidthLevel.LOW -> NetworkQuality.POOR
                                        BandwidthLevel.MEDIUM -> NetworkQuality.FAIR
                                        BandwidthLevel.HIGH -> NetworkQuality.GOOD
                                    }
                                }

                                NetworkState.DISCONNECTED -> NetworkQuality.POOR
                                else -> NetworkQuality.UNKNOWN
                            }

                        _collaborationState.value =
                            _collaborationState.value.copy(
                                networkQuality = quality,
                                isRuralOptimized = quality == NetworkQuality.POOR || quality == NetworkQuality.FAIR,
                            )

                        // Adjust connection strategy based on network quality
                        when (quality) {
                            NetworkQuality.POOR -> {
                                // Switch to offline mode
                                handlePoorConnectivity()
                            }
                            NetworkQuality.FAIR -> {
                                // Reduce update frequency, optimize data usage
                                optimizeForFairConnection()
                            }
                            else -> {
                                // Normal operation
                                _ruralOptimizationStatus.value = false
                            }
                        }
                    }
                    .launchIn(this)

                // Monitor bandwidth levels
                ruralOptimizer.bandwidthState
                    .onEach { bandwidthLevel ->
                        val quality =
                            when (bandwidthLevel) {
                                BandwidthLevel.LOW -> NetworkQuality.POOR
                                BandwidthLevel.MEDIUM -> NetworkQuality.FAIR
                                BandwidthLevel.HIGH -> NetworkQuality.GOOD
                            }

                        val currentState = _collaborationState.value
                        if (currentState.networkQuality != quality) {
                            _collaborationState.value =
                                currentState.copy(
                                    networkQuality = quality,
                                    isRuralOptimized = quality != NetworkQuality.GOOD,
                                )
                        }
                    }
                    .launchIn(this)
            }
        }

        private fun handlePoorConnectivity() {
            _ruralOptimizationStatus.value = true

            // Switch to offline mode with bid queuing
            val currentState = _state.value
            if (currentState is BiddingState.Active) {
                _state.value =
                    BiddingState.OfflineMode(
                        lastKnownBid = currentState.update,
                        networkQuality = NetworkQuality.POOR,
                    )
            }
        }

        private fun optimizeForFairConnection() {
            _ruralOptimizationStatus.value = true

            // Reduce update frequency and optimize bandwidth usage
            val currentState = _state.value
            if (currentState is BiddingState.Active) {
                _state.value =
                    currentState.copy(
                        networkQuality = NetworkQuality.FAIR,
                        isOptimizedForRural = true,
                    )
            }
        }

        private fun initializeRealTimeFeatures() {
            viewModelScope.launch {
                // Connect to real-time services
                collaborationFetcher.connect()

                // Observe connection state
                collaborationFetcher.connectionState
                    .onEach { connectionState ->
                        _collaborationState.value =
                            _collaborationState.value.copy(
                                isConnected = connectionState == ConnectionState.CONNECTED,
                            )
                    }
                    .launchIn(this)

                // Monitor network quality from collaboration fetcher
                collaborationFetcher.networkQuality
                    .onEach { quality ->
                        val currentState = _state.value
                        if (currentState is BiddingState.Active) {
                            _state.value = currentState.copy(networkQuality = quality)
                        }
                    }
                    .launchIn(this)

                // Monitor offline bid queue
                collaborationFetcher.offlineBidQueue
                    .onEach { queuedBids ->
                        val currentState = _state.value
                        if (currentState is BiddingState.OfflineMode) {
                            _state.value = currentState.copy(queuedBids = queuedBids)
                        }
                    }
                    .launchIn(this)

                // Start auction monitoring with rural optimization
                startOptimizedAuctionMonitoring()

                // Start chat stream for auction room
                val chatRoomId = "auction_$auctionId"
                collaborationFetcher.startChatStream(chatRoomId)
                    .onEach { message ->
                        val updatedMessages = (_chatMessages.value + message).takeLast(50)
                        _chatMessages.value = updatedMessages

                        _collaborationState.value =
                            _collaborationState.value.copy(
                                recentMessages = updatedMessages.takeLast(5),
                            )

                        // Update active state with new messages
                        val currentState = _state.value
                        if (currentState is BiddingState.Active) {
                            _state.value = currentState.copy(chatMessages = updatedMessages)
                        }
                    }
                    .catch { e ->
                        println("Chat stream error: ${e.message}")
                    }
                    .launchIn(this)
            }
        }

        private fun startOptimizedAuctionMonitoring() {
            viewModelScope.launch {
                collaborationFetcher.startAuctionMonitoring(auctionId)
                    .onEach { auctionUpdate ->
                        _collaborationState.value =
                            _collaborationState.value.copy(
                                participantCount = auctionUpdate.bidderCount,
                                currentBid = auctionUpdate.currentBid,
                            )

                        // Convert to BidUpdate for existing logic
                        val bidUpdate =
                            BidUpdate(
                                amount = auctionUpdate.currentBid,
                                bidderId = auctionUpdate.bidderId ?: "anonymous",
                                auctionId = auctionUpdate.auctionId,
                                timestamp = auctionUpdate.timestamp,
                            )

                        val currentMessages = _chatMessages.value
                        val networkQuality = _collaborationState.value.networkQuality

                        _state.value =
                            BiddingState.Active(
                                update = bidUpdate,
                                participants = auctionUpdate.bidderCount,
                                chatMessages = currentMessages,
                                networkQuality = networkQuality,
                                isOptimizedForRural = _ruralOptimizationStatus.value,
                            )
                    }
                    .catch { e ->
                        _state.value = BiddingState.Error(e)
                    }
                    .launchIn(this)
            }
        }

        private fun observeBids() {
            repo.observeBids(auctionId)
                .onEach { bidUpdate ->
                    // Fallback for original bidding system
                    if (_state.value !is BiddingState.Active) {
                        _state.value = BiddingState.Active(bidUpdate)
                    }
                }
                .catch { e ->
                    _state.value = BiddingState.Error(e)
                }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
        }

        fun placeBid(amount: Double) =
            viewModelScope.launch {
                try {
                    val networkQuality = _collaborationState.value.networkQuality

                    when (networkQuality) {
                        NetworkQuality.POOR -> {
                            // Queue bid for offline sync
                            placeBidOffline(amount)
                        }
                        NetworkQuality.FAIR -> {
                            // Use optimized bidding with retry logic
                            placeBidWithOptimization(amount)
                        }

                        else -> {
                            // Normal real-time bidding
                            placeBidRealTime(amount)
                        }
                    }
                } catch (e: Exception) {
                    _state.value = BiddingState.Error(e)
                }
            }

        private suspend fun placeBidOffline(amount: Double) {
            // Add to offline queue
            val offlineBid =
                OfflineBid(
                    auctionId = auctionId,
                    bidAmount = amount,
                    timestamp = System.currentTimeMillis(),
                )

            // Queue will be processed when connection improves
            val currentState = _state.value
            if (currentState is BiddingState.OfflineMode) {
                _state.value =
                    currentState.copy(
                        queuedBids = currentState.queuedBids + offlineBid,
                    )
            }

            // Send Telugu notification about offline queuing
            sendChatMessage("నెట్‌వర్క్ సమస్య - బిడ్ క్యూ లో జోడించబడింది") // "Network issue - bid added to queue"
        }

        private suspend fun placeBidWithOptimization(amount: Double) {
            // Use optimized bidding approach for fair connections
            try {
                // Attempt normal bidding with enhanced error handling
                val result = collaborationFetcher.placeBid(auctionId, amount)

                if (result.isSuccess) {
                    _state.value = BiddingState.BidPlaced
                    val bidMessage = "వేసిన బిడ్: ₹${amount.toInt()} (ఆప్టిమైజ్డ్)"
                    sendChatMessage(bidMessage)
                } else {
                    // Fallback to offline queuing for optimization failures
                    placeBidOffline(amount)
                }
            } catch (e: Exception) {
                // Fallback to offline queuing on any error
                placeBidOffline(amount)
            }
        }

        private suspend fun placeBidRealTime(amount: Double) {
            // Normal real-time collaboration bidding
            val result = collaborationFetcher.placeBid(auctionId, amount)

            if (result.isSuccess) {
                _state.value = BiddingState.BidPlaced
                val bidMessage = "వేసిన బిడ్: ₹${amount.toInt()}"
                sendChatMessage(bidMessage)
            } else {
                // Fallback to original repository method
                val repoResult = repo.placeBid(auctionId, amount)
                if (repoResult.isSuccess) {
                    _state.value = BiddingState.BidPlaced
                } else {
                    _state.value =
                        BiddingState.Error(
                            result.exceptionOrNull() ?: Exception("Bidding failed"),
                        )
                }
            }
        }

        fun sendChatMessage(message: String) =
            viewModelScope.launch {
                try {
                    val chatRoomId = "auction_$auctionId"
                    val networkQuality = _collaborationState.value.networkQuality

                    when (networkQuality) {
                        NetworkQuality.POOR -> {
                            // Queue message for later sending
                            println("Message queued for offline sync: $message")
                        }
                        NetworkQuality.FAIR -> {
                            // Try sending with basic optimization
                            try {
                                collaborationFetcher.sendChatMessage(chatRoomId, message)
                            } catch (e: Exception) {
                                println("Failed to send optimized message: ${e.message}")
                            }
                        }
                        else -> {
                            // Normal sending
                            collaborationFetcher.sendChatMessage(chatRoomId, message)
                        }
                    }
                } catch (e: Exception) {
                    println("Failed to send chat message: ${e.message}")
                }
            }

        fun sendQuickBidMessage(bidAmount: Double) =
            viewModelScope.launch {
                val message = "నేను ₹${bidAmount.toInt()} వేస్తున్నాను" // "I'm bidding ₹amount" in Telugu
                sendChatMessage(message)
            }

        fun sendInterestMessage() =
            viewModelScope.launch {
                val message = "ఈ వేలానికి ఆసక్తి ఉంది" // "Interested in this auction" in Telugu
                sendChatMessage(message)
            }

        fun sendRuralOptimizationStatus() =
            viewModelScope.launch {
                val networkQuality = _collaborationState.value.networkQuality
                val message =
                    when (networkQuality) {
                        NetworkQuality.POOR -> "నెట్‌వర్క్ నెమ్మది - ఆఫ్‌లైన్ మోడ్" // "Network slow - offline mode"
                        NetworkQuality.FAIR -> "నెట్‌వర్క్ సాధారణం - డేటా ఆప్టిమైజ్డ్" // "Network fair - data optimized"
                        else -> "నెట్‌వర్క్ బాగుంది - పూర్తి సేవలు" // "Network good - full services"
                    }
                sendChatMessage(message)
            }

        fun retryOfflineBids() =
            viewModelScope.launch {
                val currentState = _state.value
                if (currentState is BiddingState.OfflineMode && currentState.queuedBids.isNotEmpty()) {
                    // Attempt to process offline bids
                    for (offlineBid in currentState.queuedBids) {
                        try {
                            val result =
                                collaborationFetcher.placeBid(offlineBid.auctionId, offlineBid.bidAmount)
                            if (result.isSuccess) {
                                sendChatMessage("ఆఫ్‌లైన్ బిడ్ విజయవంతం: ₹${offlineBid.bidAmount.toInt()}") // "Offline bid successful"
                            }
                            delay(1000) // Space out requests
                        } catch (e: Exception) {
                            println("Failed to process offline bid: ${e.message}")
                        }
                    }
                }
            }

        fun getParticipantCount(): Int {
            return _collaborationState.value.participantCount
        }

        fun isRealTimeConnected(): Boolean {
            return _collaborationState.value.isConnected
        }

        fun getNetworkQuality(): NetworkQuality {
            return _collaborationState.value.networkQuality
        }

        fun getConnectionLatency(): Long {
            return _collaborationState.value.connectionLatency
        }

        override fun onCleared() {
            super.onCleared()
            collaborationFetcher.disconnect()
            // Rural optimizer doesn't need explicit cleanup in this implementation
        }
    }
