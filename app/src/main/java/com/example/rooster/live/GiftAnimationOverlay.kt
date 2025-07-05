package com.example.rooster.live

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlin.random.Random

/**
 * Gift animation overlay that renders floating gift animations over live streams
 * Optimized for rural networks with lightweight animations
 */
@Composable
fun GiftAnimationOverlay(
    birdId: String,
    modifier: Modifier = Modifier,
) {
    val giftQueue = remember { mutableStateListOf<Gift>() }

    // Collect gift events for this specific bird
    LaunchedEffect(birdId) {
        GiftEventsStore.gifts.collect { gift ->
            if (gift.birdId == birdId) {
                giftQueue += gift
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Render each gift with staggered animation timing
        giftQueue.forEachIndexed { index, gift ->
            key("${gift.timestamp}_${gift.type}") {
                GiftFlyingIcon(
                    gift = gift,
                    onAnimationEnd = {
                        giftQueue.removeAll { it.timestamp == gift.timestamp }
                    },
                    delayMs = index * 200, // Stagger animations by 200ms
                )
            }
        }
    }
}

/**
 * Individual gift animation component
 * Creates a floating animation from bottom to top with fade effects
 */
@Composable
private fun GiftFlyingIcon(
    gift: Gift,
    onAnimationEnd: () -> Unit,
    delayMs: Int,
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Animation progress from 0f to 1f
    val progress by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec =
            tween(
                durationMillis = 2500, // 2.5 second animation
                easing = LinearOutSlowInEasing,
            ),
        finishedListener = { onAnimationEnd() },
    )

    // Start animation after delay
    LaunchedEffect(gift.timestamp) {
        delay(delayMs.toLong())
        startAnimation = true
    }

    val density = LocalDensity.current

    // Calculate movement path (bottom to top)
    val startY = with(density) { 120.dp.roundToPx() }
    val endY = with(density) { (-150).dp.roundToPx() }
    val yOffset = (startY + (endY - startY) * progress).toInt()

    // Add horizontal jitter for more natural movement
    val xJitter = remember { Random.nextInt(-80, 80) }
    val xOffset = (xJitter + (sin(progress * kotlin.math.PI * 2) * 20).toInt())

    // Calculate alpha with fade in/out effect
    val alpha =
        when {
            progress < 0.1f -> progress * 10f // Fade in
            progress > 0.8f -> (1f - progress) * 5f // Fade out
            else -> 1f // Full opacity in middle
        }

    // Calculate dynamic font size with slight oscillation
    val baseFontSize = 28
    val sizeVariation = (sin(progress * kotlin.math.PI * 4) * 4).toInt()
    val dynamicFontSize = (baseFontSize + sizeVariation).sp

    Text(
        text = gift.icon,
        fontSize = dynamicFontSize,
        textAlign = TextAlign.Center,
        modifier =
            Modifier
                .offset { IntOffset(xOffset, yOffset) }
                .alpha(alpha),
    )
}

/**
 * Gift sending panel UI component
 * Displays available gifts with pricing and send functionality
 */
@Composable
fun GiftSendingPanel(
    birdId: String,
    onSendGift: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gifts =
        remember {
            listOf(
                "🌹" to 1, // Rose - 1 coin (₹5)
                "🎀" to 2, // Bow - 2 coins (₹10)
                "🏆" to 5, // Trophy - 5 coins (₹25)
                "💎" to 10, // Diamond - 10 coins (₹50)
            )
        }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Send Gifts",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                gifts.forEach { (giftIcon, cost) ->
                    GiftButton(
                        icon = giftIcon,
                        cost = cost,
                        onClick = { onSendGift(birdId, giftIcon) },
                    )
                }
            }
        }
    }
}

/**
 * Individual gift button component
 */
@Composable
private fun GiftButton(
    icon: String,
    cost: Int,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(60.dp),
        contentPadding = PaddingValues(4.dp),
    ) {
        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
            Text(
                text = icon,
                fontSize = 20.sp,
            )
            Text(
                text = "${cost}c",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
            )
        }
    }
}

// Helper function for sine calculation
private fun sin(x: Double): Double = kotlin.math.sin(x)
