package com.example.rooster.data.repo

import com.example.rooster.models.EventItem

object EventsRepository {
    suspend fun fetchEventsAndElects(): List<EventItem> {
        return listOf(
            EventItem(
                title = "Cockfight Championship",
                description = "Regional competition",
                date = System.currentTimeMillis() + 86400000, // Tomorrow
                type = "COMPETITION",
            ),
        )
    }
}
