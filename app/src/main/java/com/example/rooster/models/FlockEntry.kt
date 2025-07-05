package com.example.rooster.models

import java.util.*

/**
 * Represents a flock entry (batch of birds) with creation date.
 */
data class FlockEntry(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val count: Int,
    val createdAt: Long = System.currentTimeMillis(),
)

/**
 * Represents growth statistic for a flock by age in weeks.
 */
data class GrowthStat(
    val ageWeeks: Int,
    val avgWeight: Double, // in grams
)

/**
 * Enum for age group categories.
 */
enum class AgeGroup {
    WEEKLY,
    MONTHLY,
    QUARTERLY,
}
