package com.example.rooster

import com.parse.ParseObject
import com.parse.ParseUser
import java.util.Date

/**
 * Data Models for Egg Production Analytics and Leaderboards
 * Moderate-level feature for tracking and visualizing egg production data.
 */

// EggProduction Record Data Model
// Represents a daily or periodic record of egg production for a specific breeder/farm.
data class EggProductionRecord(
    val id: String = "",
    val breederId: String, // ID of the ParseUser (Farmer)
    val date: Date, // Date of the record
    val eggCount: Int, // Number of eggs collected on this date
    val breed: String? = null, // Optional: Specific breed if tracking per breed
    val notes: String? = null, // Optional notes
    val region: String? = null, // Farmer's region for regional analytics
    val createdBy: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
) {
    companion object {
        fun fromParseObject(parseObject: ParseObject): EggProductionRecord {
            return EggProductionRecord(
                id = parseObject.objectId ?: "",
                breederId = parseObject.getParseUser("breeder")?.objectId ?: "",
                date = parseObject.getDate("date") ?: Date(),
                eggCount = parseObject.getInt("eggCount"),
                breed = parseObject.getString("breed"),
                notes = parseObject.getString("notes"),
                region = parseObject.getString("region"),
                createdBy = parseObject.getParseUser("createdBy")?.objectId,
                createdAt = parseObject.createdAt ?: Date(),
                updatedAt = parseObject.updatedAt ?: Date(),
            )
        }
    }

    fun toParseObject(): ParseObject {
        val parseObject = ParseObject("EggProduction")
        // If id is present, it means we are updating an existing object
        if (id.isNotBlank()) {
            parseObject.objectId = id
        }
        ParseUser.getCurrentUser()
            ?.let { parseObject.put("breeder", it) } // Link to current user as breeder
        parseObject.put("date", date)
        parseObject.put("eggCount", eggCount)
        breed?.let { parseObject.put("breed", it) }
        notes?.let { parseObject.put("notes", it) }
        ParseUser.getCurrentUser()?.getString("region")
            ?.let { parseObject.put("region", it) } // Get region from user
        ParseUser.getCurrentUser()?.let { parseObject.put("createdBy", it) } // Link to current user
        return parseObject
    }
}

// Data model for displaying aggregated analytics (e.g., monthly production)
data class EggProductionMonthlySummary(
    val monthYear: String, // e.g., "2025-01"
    val totalEggs: Int,
    val averageDailyEggs: Double,
)

// Data model for leaderboard entries
data class EggProductionLeaderboardEntry(
    val rank: Int,
    val farmerName: String,
    val farmerId: String,
    val region: String?,
    val totalEggs: Int, // For a specific period (e.g., last 30 days, current season)
    val profileImageUrl: String? = null, // Optional
)

// Data model for breeding success rate
data class BreedingSuccessRate(
    val breederId: String,
    val period: String, // e.g., "Last 3 Months", "Sankranti Season 2025"
    val totalEggsSet: Int,
    val totalChicksHatched: Int,
    val successRate: Double, // (totalChicksHatched / totalEggsSet) * 100
    val rank: Int? = null, // Optional rank in breeding success leaderboard
)

// Enum for leaderboard periods
enum class LeaderboardPeriod(val displayName: String) {
    LAST_30_DAYS("Last 30 Days"),
    LAST_90_DAYS("Last 90 Days"),
    CURRENT_MONTH("Current Month"),
    PREVIOUS_MONTH("Previous Month"),
    CURRENT_YEAR("Current Year"),
    ALL_TIME("All Time"),
}
