package com.example.rooster.data

import com.example.rooster.models.BroadcastEvent
import com.example.rooster.models.BroadcastEventParse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseException
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Repository for managing live broadcast events, integrated with Parse backend.
 */
object BroadcastRepository {
    // No longer needed: private val liveBroadcasts = ConcurrentHashMap<String, BroadcastEvent>()

    /** Initiates a new live broadcast. */
    suspend fun initiateBroadcast(
        userId: String,
        type: String,
    ): BroadcastEvent {
        // STUB: Replace with actual implementation
        return BroadcastEvent(id = "stub_id", userId = userId, type = type, startTime = System.currentTimeMillis(), isLive = true)
    }

    /** Lists active broadcasts. */
    suspend fun listActiveBroadcasts(): List<BroadcastEvent> {
        // STUB: Replace with actual implementation
        return emptyList()
    }

    /** Starts a new live broadcast. */
    suspend fun startBroadcast(event: BroadcastEvent) {
        withContext(Dispatchers.IO) {
            try {
                val parseEvent = BroadcastEventParse()
                parseEvent.userId = event.userId
                parseEvent.type = event.type
                parseEvent.startTime = Date(event.startTime)
                parseEvent.isLive = event.isLive
                parseEvent.save()
                FirebaseCrashlytics.getInstance().log("Broadcast started by ${event.userId}: ${parseEvent.objectId}")
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                throw e
            }
        }
    }

    /** Stops an ongoing live broadcast. */
    suspend fun stopBroadcast(broadcastId: String) {
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery(BroadcastEventParse::class.java)
                val parseEvent = query.get(broadcastId)
                parseEvent.isLive = false
                parseEvent.save()
                FirebaseCrashlytics.getInstance().log("Broadcast stopped: $broadcastId")
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                throw e
            }
        }
    }

    /** Fetches the current live broadcast for a user, if any. */
    suspend fun getLiveBroadcast(userId: String): BroadcastEvent? {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery(BroadcastEventParse::class.java)
                query.whereEqualTo("userId", userId)
                query.whereEqualTo("isLive", true)
                val parseEvent = query.first()

                parseEvent?.let {
                    BroadcastEvent(
                        id = it.objectId ?: "",
                        userId = it.userId ?: "",
                        type = it.type ?: "",
                        startTime = it.startTime?.time ?: 0L,
                        isLive = it.isLive,
                    )
                }.also { event ->
                    if (event == null) {
                        FirebaseCrashlytics.getInstance().log("No live broadcast found for $userId")
                    } else {
                        FirebaseCrashlytics.getInstance().log("Live broadcast found for $userId: ${event.id}")
                    }
                }
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                null // Return null on error
            }
        }
    }
}
