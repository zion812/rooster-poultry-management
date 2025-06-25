package com.example.rooster

import com.parse.ParseFile
import com.parse.ParseObject
import java.util.Date

/**
 * Hatching and Bruding Data Models
 * 
 * Moderate-level feature implementation for egg-to-chick traceability
 */

// Hatching Record Data Model
/**
 * Hatching Record Data Model
 */
data class HatchingRecord(
    val id: String = "",
    /**
     * Link to a specific egg if tracked, or a batch ID
     */
    val eggId: String,
    val batchName: String = "",
    val breed: String = "",
    val numberOfEggs: Int = 1,
    val startDate: Date,
    val expectedHatchDate: Date,
    var actualHatchDate: Date? = null,
    var hatchedCount: Int = 0,
    /**
     * (hatchedCount / numberOfEggs) * 100
     */
    var fertilityRate: Double = 0.0,
    /**
     * (hatchedCount / fertileEggs) * 100 - if fertileEggs are known
     */
    var hatchabilityRate: Double = 0.0,
    /**
     * Temp, humidity, turning frequency
     */
    val incubatorSettings: String = "",
    var status: HatchingStatus = HatchingStatus.INCUBATING,
    val notes: String = "",
    val region: String = "",
    val createdBy: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    var photo: ParseFile? = null,
) {
    companion object {
        fun fromParseObject(parseObject: ParseObject): HatchingRecord {
            return try {
                HatchingRecord(
                    id = parseObject.objectId ?: "",
                    eggId = parseObject.getString("eggId") ?: "",
                    batchName =
                        parseObject.getString("batchName")
                            ?: "Batch-${parseObject.objectId?.takeLast(4)}",
                    breed = parseObject.getString("breed") ?: "Unknown",
                    numberOfEggs = parseObject.getInt("numberOfEggs"),
                    startDate = parseObject.getDate("startDate") ?: Date(),
                    expectedHatchDate = parseObject.getDate("expectedHatchDate") ?: Date(),
                    actualHatchDate = parseObject.getDate("actualHatchDate"),
                    hatchedCount = parseObject.getInt("hatchedCount"),
                    fertilityRate = parseObject.getDouble("fertilityRate"),
                    hatchabilityRate = parseObject.getDouble("hatchabilityRate"),
                    incubatorSettings = parseObject.getString("incubatorSettings") ?: "",
                    status =
                        HatchingStatus.valueOf(
                            parseObject.getString("status") ?: "INCUBATING",
                        ),
                    notes = parseObject.getString("notes") ?: "",
                    region = parseObject.getString("region") ?: "",
                    createdBy = parseObject.getParseUser("createdBy")?.objectId ?: "",
                    createdAt = parseObject.createdAt ?: Date(),
                    updatedAt = parseObject.updatedAt ?: Date(),
                    photo = parseObject.getParseFile("photo"),
                )
            } catch (e: Exception) {
                HatchingRecord(
                    eggId = "error",
                    startDate = Date(),
                    expectedHatchDate = Date(),
                    status = HatchingStatus.UNKNOWN_ERROR,
                )
            }
        }
    }

    fun toParseObject(): ParseObject {
        val parseObject = ParseObject("Hatching")
        parseObject.put("eggId", eggId)
        parseObject.put("batchName", batchName)
        parseObject.put("breed", breed)
        parseObject.put("numberOfEggs", numberOfEggs)
        parseObject.put("startDate", startDate)
        parseObject.put("expectedHatchDate", expectedHatchDate)
        actualHatchDate?.let { parseObject.put("actualHatchDate", it) }
        parseObject.put("hatchedCount", hatchedCount)
        parseObject.put("fertilityRate", fertilityRate)
        parseObject.put("hatchabilityRate", hatchabilityRate)
        parseObject.put("incubatorSettings", incubatorSettings)
        parseObject.put("status", status.name)
        parseObject.put("notes", notes)
        parseObject.put("region", region)
        parseObject.put("updatedAt", Date())
        photo?.let { parseObject.put("photo", it) }
        return parseObject
    }
}

// Bruding (Brooding) Record Data Model
/**
 * Bruding (Brooding) Record Data Model
 */
data class BrudingRecord(
    val id: String = "",
    /**
     * Link to individual chick if tracked, or batch ID
     */
    val chickId: String,
    val batchName: String = "",
    /**
     * Link to HatchingRecord
     */
    val hatchingRecordId: String? = null,
    val breed: String = "",
    val numberOfChicks: Int = 1,
    val startDate: Date,
    var endDate: Date? = null,
    /**
     * e.g., Week1: 95F, Week2: 90F
     */
    val temperatureSchedule: String = "",
    val feedType: String = "",
    val waterSource: String = "",
    var mortalityCount: Int = 0,
    var healthStatus: ChickHealthStatus = ChickHealthStatus.GOOD,
    val notes: String = "",
    val region: String = "",
    val createdBy: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
) {
    companion object {
        fun fromParseObject(parseObject: ParseObject): BrudingRecord {
            return try {
                BrudingRecord(
                    id = parseObject.objectId ?: "",
                    chickId = parseObject.getString("chickId") ?: "",
                    batchName =
                        parseObject.getString("batchName")
                            ?: "BrudeBatch-${parseObject.objectId?.takeLast(4)}",
                    hatchingRecordId = parseObject.getString("hatchingRecordId"),
                    breed = parseObject.getString("breed") ?: "Unknown",
                    numberOfChicks = parseObject.getInt("numberOfChicks"),
                    startDate = parseObject.getDate("startDate") ?: Date(),
                    endDate = parseObject.getDate("endDate"),
                    temperatureSchedule = parseObject.getString("temperatureSchedule") ?: "",
                    feedType = parseObject.getString("feedType") ?: "Starter Mash",
                    waterSource = parseObject.getString("waterSource") ?: "Clean Drinkers",
                    mortalityCount = parseObject.getInt("mortalityCount"),
                    healthStatus =
                        ChickHealthStatus.valueOf(
                            parseObject.getString("healthStatus") ?: "GOOD",
                        ),
                    notes = parseObject.getString("notes") ?: "",
                    region = parseObject.getString("region") ?: "",
                    createdBy = parseObject.getParseUser("createdBy")?.objectId ?: "",
                    createdAt = parseObject.createdAt ?: Date(),
                    updatedAt = parseObject.updatedAt ?: Date(),
                )
            } catch (e: Exception) {
                BrudingRecord(
                    chickId = "error",
                    startDate = Date(),
                    healthStatus = ChickHealthStatus.UNKNOWN,
                )
            }
        }
    }

    fun toParseObject(): ParseObject {
        val parseObject = ParseObject("Bruding")
        parseObject.put("chickId", chickId)
        parseObject.put("batchName", batchName)
        hatchingRecordId?.let { parseObject.put("hatchingRecordId", it) }
        parseObject.put("breed", breed)
        parseObject.put("numberOfChicks", numberOfChicks)
        parseObject.put("startDate", startDate)
        endDate?.let { parseObject.put("endDate", it) }
        parseObject.put("temperatureSchedule", temperatureSchedule)
        parseObject.put("feedType", feedType)
        parseObject.put("waterSource", waterSource)
        parseObject.put("mortalityCount", mortalityCount)
        parseObject.put("healthStatus", healthStatus.name)
        parseObject.put("notes", notes)
        parseObject.put("region", region)
        parseObject.put("updatedAt", Date())
        return parseObject
    }
}

// Egg Batch Data Model (Optional, if detailed egg tracking is needed)
/**
 * Egg Batch Data Model (Optional, if detailed egg tracking is needed)
 */
data class EggBatch(
    val id: String = "",
    val batchName: String,
    /**
     * Link to specific hen if known
     */
    val sourceHenId: String? = null,
    val breed: String,
    val collectionDate: Date,
    val numberOfEggs: Int,
    var fertileEggs: Int? = null,
    var incubationStatus: EggIncubationStatus = EggIncubationStatus.FRESH,
    val notes: String = "",
    val region: String = "",
    val createdBy: String = "",
    val createdAt: Date = Date(),
)

// Enums for Hatching and Bruding
enum class HatchingStatus(val displayName: String, val description: String) {
    PENDING("Pending", "Eggs collected, awaiting incubation"),
    INCUBATING("Incubating", "Eggs are currently in the incubator"),
    HATCHING("Hatching", "Chicks are actively hatching"),
    COMPLETED("Completed", "Hatching process is complete"),
    FAILED("Failed", "Hatching failed due to issues"),
    CANCELLED("Cancelled", "Incubation process was cancelled"),
    UNKNOWN_ERROR("Error", "Unknown error occurred"),
}

enum class ChickHealthStatus(val displayName: String, val description: String) {
    EXCELLENT("Excellent", "Chicks are active, healthy, and growing well"),
    GOOD("Good", "Chicks are generally healthy with minor or no issues"),
    FAIR("Fair", "Chicks show some signs of weakness or minor illness"),
    POOR("Poor", "Chicks are visibly ill or struggling"),
    CRITICAL("Critical", "Chicks require immediate attention"),
    UNKNOWN("Unknown", "Health status not yet determined"),
}

enum class EggIncubationStatus(val displayName: String) {
    FRESH("Fresh"),
    STORED("Stored"),
    PRE_INCUBATION("Pre-incubation"),
    INCUBATING("Incubating"),
    CANDLED_FERTILE("Candled - Fertile"),
    CANDLED_INFERTILE("Candled - Infertile"),
    HATCHED("Hatched"),
}
