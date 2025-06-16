package com.example.rooster.data

import com.example.rooster.models.SuggestionItem
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap

/**
 * Stub repository for action suggestions based on bird age and conditions.
 */
object SuggestionRepository {
    private val suggestions = ConcurrentHashMap<String, MutableList<SuggestionItem>>()

    /** Generates suggestions for a given bird (stubbed). */
    suspend fun generateSuggestions(birdId: String): List<SuggestionItem> {
        delay(300)
        val item =
            SuggestionItem(
                id = birdId + "-suggestion",
                birdId = birdId,
                message = "Vaccinate $birdId this week",
                date = System.currentTimeMillis(),
            )
        suggestions.computeIfAbsent(birdId) { mutableListOf() }.add(item)
        return suggestions[birdId]?.toList() ?: emptyList()
    }

    /** Fetches existing suggestions for a bird. */
    suspend fun fetchSuggestions(birdId: String): List<SuggestionItem> {
        delay(200)
        return suggestions[birdId]?.toList() ?: emptyList()
    }
}
