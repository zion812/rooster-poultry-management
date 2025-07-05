package com.example.rooster

import com.parse.ParseObject
import java.util.Date

// Data models for the marketplace functionality

data class GrowthUpdate(val week: Int, val weight: Double)

data class SafeListing(
    val id: String = "",
    val imageUrl: String = "",
    val breed: String = "",
    val age: Int = 0,
    val price: Double = 0.0,
    val owner: String = "",
    val sellerId: String = "", // Firebase UID of the seller
    val createdAt: Date = Date(),
    val isActive: Boolean = true,
    // Traceability fields
    val fatherId: String = "N/A",
    val motherId: String = "N/A",
    val vaccinations: List<String> = emptyList(),
    val growthUpdates: List<GrowthUpdate> = emptyList(),
    val isBreeder: Boolean = false,
    val isBloodlineVerified: Boolean = false,
) {
    companion object {
        fun fromParseObject(parseObject: ParseObject): SafeListing {
            // Safely extract ParseUser data without requiring fetchIfNeeded()
            val ownerUser = parseObject.getParseUser("owner")
            val ownerName =
                try {
                    // Use getString method instead of direct property access for safety
                    ownerUser?.getString("username") ?: "Unknown Seller"
                } catch (e: IllegalStateException) {
                    // ParseUser data not fetched - use fallback
                    "Unknown Seller"
                } catch (e: Exception) {
                    // Any other error - use fallback
                    "Unknown Seller"
                }

            val sellerId =
                try {
                    ownerUser?.getString("firebaseUid") ?: ownerUser?.objectId ?: ""
                } catch (e: IllegalStateException) {
                    // ParseUser data not fetched - use objectId as fallback
                    ownerUser?.objectId ?: ""
                } catch (e: Exception) {
                    // Any other error - use objectId as fallback
                    ownerUser?.objectId ?: ""
                }

            // Extract traceability fields
            val fatherId = parseObject.getString("fatherId") ?: "N/A"
            val motherId = parseObject.getString("motherId") ?: "N/A"
            val vaccinations = parseObject.getList<String>("vaccinations") ?: emptyList()
            val growthUpdatesRaw =
                parseObject.getList<Map<String, Any>>("growthUpdates") ?: emptyList()
            val growthUpdates =
                growthUpdatesRaw.mapNotNull {
                    val week = (it["week"] as? Number)?.toInt()
                    val weight = (it["weight"] as? Number)?.toDouble()
                    if (week != null && weight != null) GrowthUpdate(week, weight) else null
                }
            val isBreeder = parseObject.getBoolean("isBreeder")
            val isBloodlineVerified = parseObject.getBoolean("isBloodlineVerified")

            return SafeListing(
                id = parseObject.objectId ?: "",
                imageUrl = parseObject.getParseFile("image")?.url ?: "",
                breed = parseObject.getString("breed") ?: "",
                age = parseObject.getInt("age"),
                price =
                    parseObject.getString("price")?.toDoubleOrNull()
                        ?: 0.0,
                // Handle price as string from backend
                owner = ownerName,
                sellerId = sellerId,
                createdAt = parseObject.createdAt ?: Date(),
                isActive = parseObject.getBoolean("isActive"),
                fatherId = fatherId,
                motherId = motherId,
                vaccinations = vaccinations,
                growthUpdates = growthUpdates,
                isBreeder = isBreeder,
                isBloodlineVerified = isBloodlineVerified,
            )
        }
    }
}
