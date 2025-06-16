package com.example.rooster.data

import com.example.rooster.models.BroadcastEvent
import kotlinx.coroutines.delay
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Stub repository for live broadcast events.
 */
object BroadcastRepository { // now BroadcastEvent is resolved
    private val events = CopyOnWriteArrayList<BroadcastEvent>()

    /** Initiates a new broadcast of given type (video/audio). */
    suspend fun initiateBroadcast(
        userId: String,
        type: String,
    ): BroadcastEvent {
        delay(300)
        val evt = BroadcastEvent(userId = userId, type = type)
        events.add(evt)
        return evt
    }

    /** Stops an active broadcast. */
    suspend fun stopBroadcast(eventId: String) {
        delay(200)
        events.find { it.id == eventId }?.let { evt ->
            val idx = events.indexOf(evt)
            events[idx] = evt.copy(isLive = false)
        }
    }

    /** Lists all active broadcasts. */
    suspend fun listActiveBroadcasts(): List<BroadcastEvent> {
        delay(300)
        return events.filter { it.isLive }
    }
}
