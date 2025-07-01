package com.example.rooster.data

import com.example.rooster.models.EventItem
import com.example.rooster.models.EventItemParse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for community events and elections, integrated with Parse backend.
 */
object EventsRepository {
    /** Fetches all events and elections. */
    suspend fun fetchEventsAndElects(): List<EventItem> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery(EventItemParse::class.java)
                query.orderByAscending("date")
                val parseEvents = query.find()

                parseEvents.map { parseEvent ->
                    EventItem(
                        eventId = parseEvent.objectId ?: "",
                        title = parseEvent.title ?: "",
                        description = parseEvent.description ?: "",
                        date = parseEvent.date?.time ?: 0L,
                        type = parseEvent.type ?: "",
                    )
                }.also { list ->
                    FirebaseCrashlytics.getInstance().log("Fetched ${list.size} events and elects")
                }
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                emptyList() // Return empty list on error
            }
        }
    }

    /** Joins an event. */
    suspend fun joinEvent(eventId: String) {
        val currentUser = ParseUser.getCurrentUser()
        if (currentUser == null) {
            FirebaseCrashlytics.getInstance().log("Attempted to join event without logged in user.")
            throw IllegalStateException("User not logged in.")
        }

        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery(EventItemParse::class.java)
                val event = query.get(eventId)
                // Assuming 'attendees' is a List<String> on EventItemParse
                val attendees = event.getList<String>("attendees")?.toMutableList() ?: mutableListOf()
                if (!attendees.contains(currentUser.objectId)) {
                    attendees.add(currentUser.objectId)
                    event.put("attendees", attendees)
                    event.save()
                    FirebaseCrashlytics.getInstance().log("User ${currentUser.objectId} joined event $eventId")
                }
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                throw e
            }
        }
    }

    /** Votes for an election. */
    suspend fun voteEvent(eventId: String) {
        val currentUser = ParseUser.getCurrentUser()
        if (currentUser == null) {
            FirebaseCrashlytics.getInstance().log("Attempted to vote without logged in user.")
            throw IllegalStateException("User not logged in.")
        }

        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery(EventItemParse::class.java)
                val event = query.get(eventId)
                // Assuming 'votes' is a Map<String, Int> where key is candidateId/option and value is vote count
                // For simplicity, let's assume a 'votesCount' field for now, or a Cloud Function for specific candidates.
                // For a general 'vote' on an event, we'll increment a 'totalVotes' counter.
                event.increment("totalVotes")
                event.save()
                FirebaseCrashlytics.getInstance().log("User ${currentUser.objectId} voted for event $eventId")
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                throw e
            }
        }
    }
}
