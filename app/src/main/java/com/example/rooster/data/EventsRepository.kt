package com.example.rooster.data

import com.example.rooster.models.EventItem
import kotlinx.coroutines.delay
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Stub repository for community events and elections.
 */
object EventsRepository {
    private val items = CopyOnWriteArrayList<EventItem>()

    init {
        // sample events
        items.add(
            EventItem(
                title = "Poultry Showdown",
                description = "Annual rooster showcase",
                date = System.currentTimeMillis(),
                type = "CULTURAL",
            ),
        )
        items.add(
            EventItem(
                title = "Village Elections",
                description = "Local coop board elections",
                date = System.currentTimeMillis() + 86400000,
                type = "ELECT",
            ),
        )
    }

    /** Fetches all events and elections. */
    suspend fun fetchEventsAndElects(): List<EventItem> {
        delay(400)
        return items.toList()
    }
}
