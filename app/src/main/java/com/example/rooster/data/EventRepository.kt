package com.example.rooster.data

import com.example.rooster.models.ParseEvent
import kotlinx.coroutines.delay
import java.util.*

/**
 * Stub repository for community events and elections.
 */
object EventRepository {
    private val events =
        mutableListOf(
            ParseEvent(
                id = "evt1",
                title = "Local Farm Training",
                description = "Learn best poultry management practices.",
                date = System.currentTimeMillis() + 86400000,
                type = "Training",
            ),
            ParseEvent(
                id = "evt2",
                title = "Breed Election 2025",
                description = "Vote for your favorite breed this month.",
                date = System.currentTimeMillis() + 2 * 86400000,
                type = "Election",
            ),
        )

    /** fetch all events */
    suspend fun fetchEvents(): List<ParseEvent> {
        delay(500)
        return events.toList()
    }

    /** mark user as joined */
    suspend fun joinEvent(eventId: String) {
        delay(200)
        events.find { it.id == eventId }?.let {
            val idx = events.indexOf(it)
            events[idx] = it.copy(isJoined = true, participants = it.participants + "currentUser")
        }
    }

    /** vote in election event */
    suspend fun voteEvent(eventId: String) {
        delay(200)
        // simulate vote processing
    }
}
