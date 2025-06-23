package com.example.rooster

import android.content.Context
import com.example.rooster.models.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Service for Hatching and Bruding Processes
 * Manages data operations for egg-to-chick traceability, with offline support.
 */
class HatchingAndBrudingService(private val context: Context) {
    /**
     * Hatching Management
     */
    suspend fun addHatchingRecord(hatchingRecord: HatchingRecord): Result<HatchingRecord> {
        return withContext(Dispatchers.IO) {
            try {
                val parseObject = hatchingRecord.toParseObject()
                parseObject.put("createdBy", ParseUser.getCurrentUser())
                parseObject.save()
                val result = HatchingRecord.fromParseObject(parseObject)
                FirebaseCrashlytics.getInstance()
                    .log("Hatching record added: ${result.batchName} (Eggs: ${result.numberOfEggs})")
                Result.success(result)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(Exception("Failed to add hatching record: ${e.message}", e))
            }
        }
    }

    suspend fun updateHatchingRecord(hatchingRecord: HatchingRecord): Result<HatchingRecord> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Hatching")
                val parseObject = query.get(hatchingRecord.id)

                parseObject.put("actualHatchDate", hatchingRecord.actualHatchDate ?: Date())
                parseObject.put("hatchedCount", hatchingRecord.hatchedCount)
                parseObject.put("fertilityRate", hatchingRecord.fertilityRate)
                parseObject.put("hatchabilityRate", hatchingRecord.hatchabilityRate)
                parseObject.put("status", hatchingRecord.status.name)
                parseObject.put("notes", hatchingRecord.notes)
                parseObject.put("updatedAt", Date())
                parseObject.save()

                val result = HatchingRecord.fromParseObject(parseObject)
                FirebaseCrashlytics.getInstance()
                    .log("Hatching record updated: ${result.batchName}")
                Result.success(result)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(Exception("Failed to update hatching record: ${e.message}", e))
            }
        }
    }

    suspend fun getHatchingHistoryForUser(): Result<List<HatchingRecord>> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Hatching")
                query.whereEqualTo("createdBy", ParseUser.getCurrentUser())
                query.orderByDescending("startDate")
                query.include("createdBy")

                // Network-aware caching
                val networkQuality = assessNetworkQualitySafely(context)
                when (networkQuality) {
                    NetworkQualityLevel.POOR, NetworkQualityLevel.OFFLINE -> {
                        query.cachePolicy = ParseQuery.CachePolicy.CACHE_ELSE_NETWORK
                        query.maxCacheAge = 15 * 60 * 1000L // 15 minutes for hatching data
                    }

                    else -> query.cachePolicy = ParseQuery.CachePolicy.NETWORK_ELSE_CACHE
                }

                val results = query.find()
                val records = results.map { HatchingRecord.fromParseObject(it) }
                ParseObject.pinAllInBackground(
                    "hatching_history_cache",
                    results,
                ) // Cache for offline
                Result.success(records)
            } catch (e: Exception) {
                try {
                    val cachedResults =
                        ParseQuery.getQuery<ParseObject>("Hatching")
                            .fromLocalDatastore()
                            .whereEqualTo("createdBy", ParseUser.getCurrentUser())
                            .orderByDescending("startDate")
                            .find()
                    Result.success(cachedResults.map { HatchingRecord.fromParseObject(it) })
                } catch (cacheError: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Result.failure(Exception("Failed to get hatching history: ${e.message}", e))
                }
            }
        }
    }

    /**
     * Bruding (Brooding) Management
     */
    suspend fun addBrudingRecord(brudingRecord: BrudingRecord): Result<BrudingRecord> {
        return withContext(Dispatchers.IO) {
            try {
                val parseObject = brudingRecord.toParseObject()
                parseObject.put("createdBy", ParseUser.getCurrentUser())
                parseObject.save()
                val result = BrudingRecord.fromParseObject(parseObject)
                FirebaseCrashlytics.getInstance()
                    .log("Bruding record added: ${result.batchName} (Chicks: ${result.numberOfChicks})")
                Result.success(result)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(Exception("Failed to add bruding record: ${e.message}", e))
            }
        }
    }

    suspend fun updateBrudingRecord(brudingRecord: BrudingRecord): Result<BrudingRecord> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Bruding")
                val parseObject = query.get(brudingRecord.id)

                brudingRecord.endDate?.let { parseObject.put("endDate", it) }
                parseObject.put("temperatureSchedule", brudingRecord.temperatureSchedule)
                parseObject.put("feedType", brudingRecord.feedType)
                parseObject.put("waterSource", brudingRecord.waterSource)
                parseObject.put("mortalityCount", brudingRecord.mortalityCount)
                parseObject.put("healthStatus", brudingRecord.healthStatus.name)
                parseObject.put("notes", brudingRecord.notes)
                parseObject.put("updatedAt", Date())
                parseObject.save()

                val result = BrudingRecord.fromParseObject(parseObject)
                FirebaseCrashlytics.getInstance()
                    .log("Bruding record updated: ${result.batchName}")
                Result.success(result)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(Exception("Failed to update bruding record: ${e.message}", e))
            }
        }
    }

    suspend fun getBrudingHistoryForUser(): Result<List<BrudingRecord>> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Bruding")
                query.whereEqualTo("createdBy", ParseUser.getCurrentUser())
                query.orderByDescending("startDate")
                query.include("createdBy")

                // Network-aware caching
                val networkQuality = assessNetworkQualitySafely(context)
                when (networkQuality) {
                    NetworkQualityLevel.POOR, NetworkQualityLevel.OFFLINE -> {
                        query.cachePolicy = ParseQuery.CachePolicy.CACHE_ELSE_NETWORK
                        query.maxCacheAge = 15 * 60 * 1000L
                    }

                    else -> query.cachePolicy = ParseQuery.CachePolicy.NETWORK_ELSE_CACHE
                }

                val results = query.find()
                val records = results.map { BrudingRecord.fromParseObject(it) }
                ParseObject.pinAllInBackground(
                    "bruding_history_cache",
                    results,
                ) // Cache for offline
                Result.success(records)
            } catch (e: Exception) {
                try {
                    val cachedResults =
                        ParseQuery.getQuery<ParseObject>("Bruding")
                            .fromLocalDatastore()
                            .whereEqualTo("createdBy", ParseUser.getCurrentUser())
                            .orderByDescending("startDate")
                            .find()
                    Result.success(cachedResults.map { BrudingRecord.fromParseObject(it) })
                } catch (cacheError: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Result.failure(Exception("Failed to get bruding history: ${e.message}", e))
                }
            }
        }
    }

    /**
     * Egg Batch Management (Optional - can be expanded later)
     */
    suspend fun addEggBatch(eggBatch: EggBatch): Result<EggBatch> {
        // Basic implementation - can be expanded for detailed egg tracking
        return withContext(Dispatchers.IO) {
            try {
                val parseObject = ParseObject("EggBatch")
                parseObject.put("batchName", eggBatch.batchName)
                parseObject.put("breed", eggBatch.breed)
                parseObject.put("collectionDate", eggBatch.collectionDate)
                parseObject.put("numberOfEggs", eggBatch.numberOfEggs)
                eggBatch.sourceHenId?.let { parseObject.put("sourceHenId", it) }
                eggBatch.fertileEggs?.let { parseObject.put("fertileEggs", it) }
                parseObject.put("incubationStatus", eggBatch.incubationStatus.name)
                parseObject.put("notes", eggBatch.notes)
                parseObject.put("region", eggBatch.region)
                parseObject.put("createdBy", ParseUser.getCurrentUser())
                parseObject.save()
                // Create a simple EggBatch from the saved object to return an ID
                Result.success(eggBatch.copy(id = parseObject.objectId))
            } catch (e: Exception) {
                Result.failure(Exception("Failed to add egg batch: ${e.message}", e))
            }
        }
    }
}
