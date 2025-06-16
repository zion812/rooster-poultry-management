package com.example.rooster.data

import com.example.rooster.models.PollResponse
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap

/**
 * Stub repository for community polls.
 */
object PollRepository {
    // pollId -> List of responses
    private val responses = ConcurrentHashMap<String, MutableList<PollResponse>>()

    /** Fetch aggregated results: optionId -> vote count */
    suspend fun fetchResults(pollId: String): Map<String, Int> {
        delay(200)
        val list = responses[pollId] ?: emptyList()
        return list.groupingBy { it.optionId }.eachCount()
    }

    /** Submit a vote response */
    suspend fun submitPollResponse(response: PollResponse) {
        delay(200)
        val list = responses.getOrPut(response.pollId) { mutableListOf() }
        list.add(response)
    }
}
