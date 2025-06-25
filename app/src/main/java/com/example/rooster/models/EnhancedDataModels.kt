package com.example.rooster.models

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import java.util.*

/**
 * Enhanced ChickenRecord with coinDeducted field for safe spending
 */
@ParseClassName("ChickenRecord")
class ChickenRecord : ParseObject() {
    // Existing fields
    var name: String?
        get() = getString("name")
        set(value) = put("name", value ?: "")

    var breed: String?
        get() = getString("breed")
        set(value) = put("breed", value ?: "")

    var birthDate: Date?
        get() = getDate("birthDate")
        set(value) = put("birthDate", value ?: Date())

    var color: String?
        get() = getString("color")
        set(value) = put("color", value ?: "")

    var gender: String?
        get() = getString("gender")
        set(value) = put("gender", value ?: "")

    var currentOwner: String?
        get() = getString("currentOwner")
        set(value) = put("currentOwner", value ?: "")

    var isVerified: Boolean
        get() = getBoolean("isVerified")
        set(value) = put("isVerified", value)

    var status: String?
        get() = getString("status")
        set(value) = put("status", value ?: "Active")

    var parentMale: String?
        get() = getString("parentMale")
        set(value) {
            if (value != null) put("parentMale", value)
        }

    var parentFemale: String?
        get() = getString("parentFemale")
        set(value) {
            if (value != null) put("parentFemale", value)
        }

    var primaryImageUrl: String?
        get() = getString("primaryImageUrl")
        set(value) {
            if (value != null) put("primaryImageUrl", value)
        }

    var location: String?
        get() = getString("location")
        set(value) {
            if (value != null) put("location", value)
        }

    var price: Double
        get() = getDouble("price")
        set(value) = put("price", value)

    // NEW: Safe coin spending field
    var coinDeducted: Boolean
        get() = getBoolean("coinDeducted")
        set(value) = put("coinDeducted", value)
}

/**
 * Enhanced VerificationRecord with coinDeducted field
 */
@ParseClassName("VerificationRecord")
class VerificationRecord : ParseObject() {
    var fowlId: String?
        get() = getString("fowlId")
        set(value) = put("fowlId", value ?: "")

    var verificationType: String?
        get() = getString("verificationType") // "15_week", "40_week", "annual"
        set(value) = put("verificationType", value ?: "")

    var verificationDate: Date?
        get() = getDate("verificationDate")
        set(value) = put("verificationDate", value ?: Date())

    var weight: Double
        get() = getDouble("weight")
        set(value) = put("weight", value)

    var height: Double
        get() = getDouble("height")
        set(value) = put("height", value)

    var color: String?
        get() = getString("color")
        set(value) = put("color", value ?: "")

    var healthStatus: String?
        get() = getString("healthStatus")
        set(value) = put("healthStatus", value ?: "")

    var verifiedBy: String?
        get() = getString("verifiedBy")
        set(value) = put("verifiedBy", value ?: "")

    var photoUrls: List<String>?
        get() = getList<String>("photoUrls")
        set(value) {
            if (value != null) put("photoUrls", value)
        }

    var notes: String?
        get() = getString("notes")
        set(value) {
            if (value != null) put("notes", value)
        }

    var location: String?
        get() = getString("location")
        set(value) {
            if (value != null) put("location", value)
        }

    var isValid: Boolean
        get() = getBoolean("isValid")
        set(value) = put("isValid", value)

    // NEW: Safe coin spending field
    var coinDeducted: Boolean
        get() = getBoolean("coinDeducted")
        set(value) = put("coinDeducted", value)
}

/**
 * Enhanced TransferRequest with coinDeducted field
 */
@ParseClassName("TransferRequest")
class TransferRequest : ParseObject() {
    var fowlId: String?
        get() = getString("fowlId")
        set(value) = put("fowlId", value ?: "")

    var fromOwnerId: String?
        get() = getString("fromOwnerId")
        set(value) = put("fromOwnerId", value ?: "")

    var toOwnerId: String?
        get() = getString("toOwnerId")
        set(value) = put("toOwnerId", value ?: "")

    var requestDate: Date?
        get() = getDate("requestDate")
        set(value) = put("requestDate", value ?: Date())

    var completionDate: Date?
        get() = getDate("completionDate")
        set(value) {
            if (value != null) put("completionDate", value)
        }

    var status: String?
        get() = getString("status") // "PENDING", "APPROVED", "COMPLETED", "CANCELLED"
        set(value) = put("status", value ?: "PENDING")

    var transferPrice: Double
        get() = getDouble("transferPrice")
        set(value) = put("transferPrice", value)

    var paymentMethod: String?
        get() = getString("paymentMethod")
        set(value) {
            if (value != null) put("paymentMethod", value)
        }

    var proofPhotoUrl: String?
        get() = getString("proofPhotoUrl")
        set(value) {
            if (value != null) put("proofPhotoUrl", value)
        }

    var proofPhoto: ParseFile?
        get() = getParseFile("proofPhoto")
        set(value) {
            if (value != null) put("proofPhoto", value)
        }

    var proofLatitude: Double
        get() = getDouble("proofLatitude")
        set(value) = put("proofLatitude", value)

    var proofLongitude: Double
        get() = getDouble("proofLongitude")
        set(value) = put("proofLongitude", value)

    var senderConfirmed: Boolean
        get() = getBoolean("senderConfirmed")
        set(value) = put("senderConfirmed", value)

    var receiverConfirmed: Boolean
        get() = getBoolean("receiverConfirmed")
        set(value) = put("receiverConfirmed", value)

    var transferNotes: String?
        get() = getString("transferNotes")
        set(value) {
            if (value != null) put("transferNotes", value)
        }

    // NEW: Safe coin spending field
    var coinDeducted: Boolean
        get() = getBoolean("coinDeducted")
        set(value) = put("coinDeducted", value)
}

/**
 * Enhanced Listing with coinDeducted field
 */
@ParseClassName("Listing")
class Listing : ParseObject() {
    var title: String?
        get() = getString("title")
        set(value) = put("title", value ?: "")

    var description: String?
        get() = getString("description")
        set(value) = put("description", value ?: "")

    var price: Double
        get() = getDouble("price")
        set(value) = put("price", value)

    var fowlId: String?
        get() = getString("fowlId")
        set(value) = put("fowlId", value ?: "")

    var sellerId: String?
        get() = getString("sellerId")
        set(value) = put("sellerId", value ?: "")

    var imageUrls: List<String>?
        get() = getList<String>("imageUrls")
        set(value) {
            if (value != null) put("imageUrls", value)
        }

    var category: String?
        get() = getString("category") // "rooster", "hen", "chick", "egg"
        set(value) = put("category", value ?: "")

    var breed: String?
        get() = getString("breed")
        set(value) = put("breed", value ?: "")

    var age: String?
        get() = getString("age")
        set(value) = put("age", value ?: "")

    var location: String?
        get() = getString("location")
        set(value) = put("location", value ?: "")

    var isActive: Boolean
        get() = getBoolean("isActive")
        set(value) = put("isActive", value)

    var isPremium: Boolean
        get() = getBoolean("isPremium")
        set(value) = put("isPremium", value)

    var isFeatured: Boolean
        get() = getBoolean("isFeatured")
        set(value) = put("isFeatured", value)

    var listingType: String?
        get() = getString("listingType") // "standard", "premium", "featured"
        set(value) = put("listingType", value ?: "standard")

    // NEW: Safe coin spending field
    var coinDeducted: Boolean
        get() = getBoolean("coinDeducted")
        set(value) = put("coinDeducted", value)
}

/**
 * Enhanced MaintenanceRecord for annual charges
 */
@ParseClassName("MaintenanceRecord")
class MaintenanceRecord : ParseObject() {
    var userId: String?
        get() = getString("userId")
        set(value) = put("userId", value ?: "")

    var fowlIds: List<String>?
        get() = getList<String>("fowlIds")
        set(value) {
            if (value != null) put("fowlIds", value)
        }

    var maintenanceYear: Int
        get() = getInt("maintenanceYear")
        set(value) = put("maintenanceYear", value)

    var processedDate: Date?
        get() = getDate("processedDate")
        set(value) = put("processedDate", value ?: Date())

    var totalFowlCount: Int
        get() = getInt("totalFowlCount")
        set(value) = put("totalFowlCount", value)

    var totalCost: Int
        get() = getInt("totalCost")
        set(value) = put("totalCost", value)

    var status: String?
        get() = getString("status") // "PROCESSED", "PENDING", "FAILED"
        set(value) = put("status", value ?: "PENDING")

    var notes: String?
        get() = getString("notes")
        set(value) {
            if (value != null) put("notes", value)
        }

    // NEW: Safe coin spending field
    var coinDeducted: Boolean
        get() = getBoolean("coinDeducted")
        set(value) = put("coinDeducted", value)
}
