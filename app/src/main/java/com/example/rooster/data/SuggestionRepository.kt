package com.example.rooster.data

import com.example.rooster.models.SuggestionItem
import com.example.rooster.models.SuggestionItemParse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseException
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Repository for managing action suggestions, integrated with Parse backend.
 */
object SuggestionRepository {
    // No longer needed: private val suggestions = ConcurrentHashMap<String, MutableList<SuggestionItem>>()

    /** Generates a new suggestion for a bird. */
    suspend fun generateSuggestions(birdId: String): List<SuggestionItem> {
        // STUB: Replace with actual implementation
        return emptyList()
    }

    suspend fun generateSuggestion(
        birdId: String,
        message: String,
    ): SuggestionItem {
        return withContext(Dispatchers.IO) {
            try {
                val parseSuggestion = SuggestionItemParse()
                parseSuggestion.birdId = birdId
                parseSuggestion.message = message
                parseSuggestion.date = Date()
                parseSuggestion.save()
                FirebaseCrashlytics.getInstance().log("Suggestion generated for $birdId: ${parseSuggestion.objectId}")
                SuggestionItem(
                    id = parseSuggestion.objectId ?: "",
                    birdId = parseSuggestion.birdId ?: "",
                    message = parseSuggestion.message ?: "",
                    date = parseSuggestion.date?.time ?: 0L,
                )
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                throw e
            }
        }
    }

    /** Fetches all suggestions for a specific bird. */
    suspend fun fetchSuggestions(birdId: String): List<SuggestionItem> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery(SuggestionItemParse::class.java)
                query.whereEqualTo("birdId", birdId)
                query.orderByDescending("date")
                val parseSuggestions = query.find()

                parseSuggestions.map { parseSuggestion ->
                    SuggestionItem(
                        id = parseSuggestion.objectId ?: "",
                        birdId = parseSuggestion.birdId ?: "",
                        message = parseSuggestion.message ?: "",
                        date = parseSuggestion.date?.time ?: 0L,
                    )
                }.also { list ->
                    FirebaseCrashlytics.getInstance().log("Fetched ${list.size} suggestions for $birdId")
                }
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                emptyList() // Return empty list on error
            }
        }
    }
}
