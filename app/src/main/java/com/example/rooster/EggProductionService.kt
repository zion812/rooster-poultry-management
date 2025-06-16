package com.example.rooster

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * Service for Egg Production Analytics and Leaderboards.
 * Manages data operations for recording egg counts, calculating summaries,
 * and generating leaderboard data. Includes offline support.
 */
class EggProductionService(private val context: Context) {
    private val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())

    /**
     * Adds a new egg production record for the current user.
     */
    suspend fun addEggProductionRecord(record: EggProductionRecord): Result<EggProductionRecord> {
        return withContext(Dispatchers.IO) {
            try {
                val parseObject = record.toParseObject()
                // Ensure createdBy and breeder are set to the current user if not already.
                if (parseObject.getParseUser("createdBy") == null) {
                    parseObject.put("createdBy", ParseUser.getCurrentUser())
                }
                if (parseObject.getParseUser("breeder") == null) {
                    parseObject.put("breeder", ParseUser.getCurrentUser())
                }
                parseObject.save()
                val savedRecord = EggProductionRecord.fromParseObject(parseObject)
                FirebaseCrashlytics.getInstance()
                    .log("Egg production record added for ${dateFormat.format(savedRecord.date)}: ${savedRecord.eggCount} eggs.")
                Result.success(savedRecord)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(Exception("Failed to add egg production record: ${e.message}", e))
            }
        }
    }

    /**
     * Retrieves egg production records for the current user within a date range.
     */
    suspend fun getEggProductionRecords(
        startDate: Date,
        endDate: Date,
    ): Result<List<EggProductionRecord>> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("EggProduction")
                query.whereEqualTo("breeder", ParseUser.getCurrentUser())
                query.whereGreaterThanOrEqualTo("date", startDate)
                query.whereLessThanOrEqualTo("date", endDate)
                query.orderByAscending("date")

                applyNetworkCachePolicy(query, "egg_records_cache")

                val results = query.find()
                val records = results.map { EggProductionRecord.fromParseObject(it) }
                ParseObject.pinAllInBackground(
                    "egg_records_cache_range_${startDate.time}_${endDate.time}",
                    results,
                )
                Result.success(records)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                // Attempt to load from cache on failure
                try {
                    val cachedQuery =
                        ParseQuery.getQuery<ParseObject>("EggProduction")
                            .fromLocalDatastore()
                            .whereEqualTo("breeder", ParseUser.getCurrentUser())
                            .whereGreaterThanOrEqualTo("date", startDate)
                            .whereLessThanOrEqualTo("date", endDate)
                            .orderByAscending("date")
                    val cachedResults = cachedQuery.find()
                    Result.success(cachedResults.map { EggProductionRecord.fromParseObject(it) })
                } catch (cacheError: Exception) {
                    Result.failure(
                        Exception(
                            "Failed to get egg production records: ${e.message}",
                            e,
                        ),
                    )
                }
            }
        }
    }

    /**
     * Calculates monthly egg production summaries for the current user.
     */
    suspend fun getMonthlySummaries(year: Int): Result<List<EggProductionMonthlySummary>> {
        return withContext(Dispatchers.IO) {
            try {
                val calendar = Calendar.getInstance()
                calendar.set(year, 0, 1) // Start of the year
                val startDate = calendar.time
                calendar.set(year, 11, 31) // End of the year
                val endDate = calendar.time

                val recordsResult = getEggProductionRecords(startDate, endDate)
                if (recordsResult.isFailure) {
                    return@withContext Result.failure(recordsResult.exceptionOrNull()!!)
                }
                val records = recordsResult.getOrNull() ?: emptyList()

                val monthlyData =
                    records.groupBy { dateFormat.format(it.date) }
                        .map { (monthYear, dailyRecords) ->
                            val totalEggs = dailyRecords.sumOf { it.eggCount }
                            val daysInMonthWithRecords =
                                dailyRecords.map {
                                    val cal = Calendar.getInstance()
                                    cal.time = it.date
                                    cal.get(Calendar.DAY_OF_MONTH)
                                }.distinct().size

                            val averageDaily =
                                if (daysInMonthWithRecords > 0) (totalEggs.toDouble() / daysInMonthWithRecords) else 0.0
                            EggProductionMonthlySummary(
                                monthYear,
                                totalEggs,
                                (averageDaily * 100).roundToInt() / 100.0,
                            ) // Rounded to 2 decimal places
                        }
                Result.success(monthlyData.sortedBy { it.monthYear })
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(Exception("Failed to calculate monthly summaries: ${e.message}", e))
            }
        }
    }

    /**
     * Generates an egg production leaderboard.
     */
    suspend fun getEggProductionLeaderboard(
        period: LeaderboardPeriod,
        limit: Int = 10,
    ): Result<List<EggProductionLeaderboardEntry>> {
        return withContext(Dispatchers.IO) {
            try {
                val (startDate, endDate) = getDatesForPeriod(period)

                val query = ParseQuery.getQuery<ParseObject>("EggProduction")
                query.whereGreaterThanOrEqualTo("date", startDate)
                query.whereLessThanOrEqualTo("date", endDate)
                query.include("breeder") // Include user data
                // Aggregation on Parse Server would be more efficient if possible.
                // For client-side aggregation:
                val allRecords = query.find()

                val groupedByBreeder = allRecords.groupBy { it.getParseUser("breeder") }

                val leaderboardEntries =
                    groupedByBreeder.mapNotNull { (breederUser, records) ->
                        breederUser?.let {
                            val totalEggs = records.sumOf { it.getInt("eggCount") }
                            EggProductionLeaderboardEntry(
                                rank = 0, // Rank will be assigned after sorting
                                farmerName =
                                    it.getString("fullName") ?: it.username
                                        ?: "Unknown Farmer",
                                farmerId = it.objectId,
                                region = it.getString("region"),
                                totalEggs = totalEggs,
                                profileImageUrl = it.getParseFile("profileImage")?.url,
                            )
                        }
                    }
                        .sortedByDescending { it.totalEggs }
                        .take(limit)
                        .mapIndexed { index, entry -> entry.copy(rank = index + 1) }

                Result.success(leaderboardEntries)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(Exception("Failed to generate leaderboard: ${e.message}", e))
            }
        }
    }

    /**
     * Calculates breeding success rate for the current user.
     * This requires Hatching data, assuming HatchingService is available or similar logic here.
     */
    suspend fun getBreedingSuccessRate(
        breederId: String,
        period: LeaderboardPeriod,
    ): Result<BreedingSuccessRate> {
        return withContext(Dispatchers.IO) {
            // This is a simplified version. A full implementation would involve:
            // 1. Fetching EggProduction records to get totalEggsSet (or a dedicated "EggsSetForHatching" table).
            // 2. Fetching HatchingRecords to get totalChicksHatched for those eggs.
            // This example assumes these values are readily available or fetched through another service.
            try {
                // Placeholder: Fetch relevant data for total eggs set and chicks hatched.
                // This would typically involve querying "Hatching" records linked to the breeder.
                val hatchingQuery = ParseQuery.getQuery<ParseObject>("Hatching")
                hatchingQuery.whereEqualTo(
                    "createdBy",
                    ParseObject.createWithoutData("_User", breederId),
                )
                val (startDate, endDate) = getDatesForPeriod(period)
                hatchingQuery.whereGreaterThanOrEqualTo("startDate", startDate)
                hatchingQuery.whereLessThanOrEqualTo(
                    "startDate",
                    endDate,
                ) // Assuming startDate of hatching is relevant

                val hatchingResults = hatchingQuery.find()
                val totalEggsSet = hatchingResults.sumOf { it.getInt("numberOfEggs") }
                val totalChicksHatched = hatchingResults.sumOf { it.getInt("hatchedCount") }

                val successRate =
                    if (totalEggsSet > 0) (totalChicksHatched.toDouble() / totalEggsSet) * 100 else 0.0

                Result.success(
                    BreedingSuccessRate(
                        breederId = breederId,
                        period = period.displayName,
                        totalEggsSet = totalEggsSet,
                        totalChicksHatched = totalChicksHatched,
                        successRate = (successRate * 100).roundToInt() / 100.0, // Rounded
                    ),
                )
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(
                    Exception(
                        "Failed to calculate breeding success rate: ${e.message}",
                        e,
                    ),
                )
            }
        }
    }

    private fun getDatesForPeriod(period: LeaderboardPeriod): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time // Today or end of current period

        when (period) {
            LeaderboardPeriod.LAST_30_DAYS -> calendar.add(Calendar.DAY_OF_YEAR, -30)
            LeaderboardPeriod.LAST_90_DAYS -> calendar.add(Calendar.DAY_OF_YEAR, -90)
            LeaderboardPeriod.CURRENT_MONTH -> calendar.set(Calendar.DAY_OF_MONTH, 1)
            LeaderboardPeriod.PREVIOUS_MONTH -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val tempEndDate = Calendar.getInstance()
                tempEndDate.time = calendar.time
                tempEndDate.set(
                    Calendar.DAY_OF_MONTH,
                    tempEndDate.getActualMaximum(Calendar.DAY_OF_MONTH),
                )
                return Pair(calendar.time, tempEndDate.time)
            }

            LeaderboardPeriod.CURRENT_YEAR -> calendar.set(Calendar.DAY_OF_YEAR, 1)
            LeaderboardPeriod.ALL_TIME -> calendar.timeInMillis = 0 // A very early date
        }
        val startDate = calendar.time
        return Pair(startDate, endDate)
    }

    private fun applyNetworkCachePolicy(
        query: ParseQuery<ParseObject>,
        cacheKey: String,
    ) {
        val networkQuality = assessNetworkQualitySafely(context)
        when (networkQuality) {
            NetworkQualityLevel.POOR, NetworkQualityLevel.OFFLINE -> {
                query.cachePolicy = ParseQuery.CachePolicy.CACHE_ELSE_NETWORK
                query.maxCacheAge = 20 * 60 * 1000L // 20 minutes for egg data
            }

            NetworkQualityLevel.FAIR -> {
                query.cachePolicy = ParseQuery.CachePolicy.NETWORK_ELSE_CACHE
                query.maxCacheAge = 10 * 60 * 1000L // 10 minutes
            }

            else -> {
                query.cachePolicy = ParseQuery.CachePolicy.NETWORK_ONLY
            }
        }
    }
}
