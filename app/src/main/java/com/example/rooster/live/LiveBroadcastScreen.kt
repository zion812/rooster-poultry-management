package com.example.rooster.live

import android.view.SurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseUser
import kotlinx.coroutines.launch

/**
 * Main live broadcast screen for showcasing roosters
 * Integrates video streaming, gift animations, and viewer engagement
 */
@Composable
fun LiveBroadcastScreen(
    birdId: String,
    birdName: String = "Premium Rooster",
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val broadcastManager = remember { AgoraBroadcastManager(context) }
    val coroutineScope = rememberCoroutineScope()

    // Collect broadcast state
    val broadcastState by broadcastManager.broadcastState.collectAsState()
    val viewerCount by broadcastManager.viewerCount.collectAsState()
    val connectionQuality by broadcastManager.connectionQuality.collectAsState()

    var isLive by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var isCameraEnabled by remember { mutableStateOf(true) }

    // Create surface view for video
    val surfaceView = remember { SurfaceView(context) }

    // Clean up on dispose
    DisposableEffect(Unit) {
        onDispose {
            broadcastManager.cleanup()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black),
        ) {
            // Top bar with back button and stream info
            LiveBroadcastTopBar(
                birdName = birdName,
                isLive = isLive,
                viewerCount = viewerCount,
                connectionQuality = connectionQuality,
                onBack = onBack,
            )

            // Video container
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
            ) {
                // Video surface
                AndroidView(
                    factory = { surfaceView },
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                ) { view ->
                    // Configure surface view if needed
                }

                // Gift animation overlay
                GiftAnimationOverlay(
                    birdId = birdId,
                    modifier = Modifier.fillMaxSize(),
                )

                // Connection quality indicator
                ConnectionQualityIndicator(
                    quality = connectionQuality,
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                )
            }

            // Control buttons
            LiveBroadcastControls(
                isLive = isLive,
                isMuted = isMuted,
                isCameraEnabled = isCameraEnabled,
                broadcastState = broadcastState,
                onStartStop = {
                    if (isLive) {
                        broadcastManager.stopBroadcast()
                        isLive = false
                    } else {
                        val success = broadcastManager.startBroadcast("bird_$birdId", surfaceView)
                        if (success) isLive = true
                    }
                },
                onMuteToggle = {
                    isMuted = broadcastManager.toggleMicrophone()
                },
                onCameraToggle = {
                    isCameraEnabled = broadcastManager.toggleCamera()
                },
                onSwitchCamera = {
                    broadcastManager.switchCamera()
                },
            )

            // Gift sending panel
            GiftSendingPanel(
                birdId = birdId,
                onSendGift = { birdId, giftType ->
                    // Use coroutine scope to handle the suspend function
                    coroutineScope.launch {
                        sendGift(birdId, giftType)
                    }
                },
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

/**
 * Top bar with stream information and navigation
 */
@Composable
private fun LiveBroadcastTopBar(
    birdName: String,
    isLive: Boolean,
    viewerCount: Int,
    connectionQuality: ConnectionQuality,
    onBack: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Back button
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
            )
        }

        // Stream info
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = birdName,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isLive) {
                    Box(
                        modifier =
                            Modifier
                                .size(8.dp)
                                .background(Color.Red, CircleShape),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "LIVE",
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(8.dp))
                }

                Text(
                    text = "👁 $viewerCount",
                    color = Color.White,
                    fontSize = 12.sp,
                )
            }
        }

        // Share button
        IconButton(onClick = { /* TODO: Implement sharing */ }) {
            Icon(
                Icons.Default.Share,
                contentDescription = "Share",
                tint = Color.White,
            )
        }
    }
}

/**
 * Connection quality indicator
 */
@Composable
private fun ConnectionQualityIndicator(
    quality: ConnectionQuality,
    modifier: Modifier = Modifier,
) {
    val (color, text) =
        when (quality) {
            ConnectionQuality.GOOD -> Color.Green to "Good"
            ConnectionQuality.POOR -> Color(0xFFFFA500) to "Poor"
            ConnectionQuality.BAD -> Color.Red to "Bad"
            ConnectionQuality.UNKNOWN -> Color.Gray to "Unknown"
        }

    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.7f),
            ),
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(8.dp)
                        .background(color, CircleShape),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 10.sp,
            )
        }
    }
}

/**
 * Broadcast control buttons
 */
@Composable
private fun LiveBroadcastControls(
    isLive: Boolean,
    isMuted: Boolean,
    isCameraEnabled: Boolean,
    broadcastState: BroadcastState,
    onStartStop: () -> Unit,
    onMuteToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    onSwitchCamera: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        // Microphone toggle
        IconButton(
            onClick = onMuteToggle,
            modifier =
                Modifier
                    .size(48.dp)
                    .background(
                        if (isMuted) Color.Red else Color.Gray,
                        CircleShape,
                    ),
        ) {
            Icon(
                if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                contentDescription = if (isMuted) "Unmute" else "Mute",
                tint = Color.White,
            )
        }

        // Camera toggle
        IconButton(
            onClick = onCameraToggle,
            modifier =
                Modifier
                    .size(48.dp)
                    .background(
                        if (!isCameraEnabled) Color.Red else Color.Gray,
                        CircleShape,
                    ),
        ) {
            Icon(
                if (isCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                contentDescription = if (isCameraEnabled) "Disable Camera" else "Enable Camera",
                tint = Color.White,
            )
        }

        // Start/Stop broadcast button
        Button(
            onClick = onStartStop,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = if (isLive) Color.Red else Color.Green,
                ),
            enabled = broadcastState != BroadcastState.CONNECTING,
        ) {
            if (broadcastState == BroadcastState.CONNECTING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(8.dp))
                Text("Connecting...")
            } else {
                Text(
                    if (isLive) "STOP LIVE" else "GO LIVE (3 coins)",
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Switch camera
        IconButton(
            onClick = onSwitchCamera,
            modifier =
                Modifier
                    .size(48.dp)
                    .background(Color.Gray, CircleShape),
        ) {
            Icon(
                Icons.Default.Cameraswitch,
                contentDescription = "Switch Camera",
                tint = Color.White,
            )
        }
    }
}

/**
 * Send gift function with coin deduction
 * Integrates with CoinManager for proper coin transactions
 */
private suspend fun sendGift(
    birdId: String,
    giftType: String,
) {
    val cost =
        when (giftType) {
            "🌹" -> 1 // Rose - ₹5
            "🎀" -> 2 // Bow - ₹10
            "🏆" -> 5 // Trophy - ₹25
            "💎" -> 10 // Diamond - ₹50
            else -> 0
        }

    if (cost > 0) {
        val success = CoinManager.deductCoins(cost, "Gift: $giftType")

        if (success) {
            // Publish gift event for immediate animation
            GiftEventsStore.publish(
                Gift(
                    birdId = birdId,
                    type = giftType,
                    icon = giftType,
                    senderId = ParseUser.getCurrentUser()?.objectId,
                    senderName = ParseUser.getCurrentUser()?.username ?: "Anonymous",
                ),
            )

            // Update broadcast statistics
            updateBroadcastStats(birdId, giftType)
        } else {
            // Log insufficient coins
            FirebaseCrashlytics.getInstance().log("Insufficient coins for gift: $giftType")
        }
    }
}

/**
 * Update broadcast statistics in Firestore
 */
private suspend fun updateBroadcastStats(
    birdId: String,
    giftType: String,
) {
    try {
        // TODO: Implement Firestore update for broadcast statistics
        // This would update gift counts, viewer engagement metrics, etc.
        FirebaseCrashlytics.getInstance().log("Gift sent: $giftType for bird: $birdId")
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
    }
}
