package com.example.rooster.data.parse

import com.parse.ParseObject
import com.parse.ParseClassName
import java.util.Date

@ParseClassName("BroadcastEvent")
class BroadcastEventParse : ParseObject() {
    var userId: String?
        get() = getString("userId")
        set(value) = put("userId", value ?: "")

    var type: String?
        get() = getString("type")
        set(value) = put("type", value ?: "")

    var startTime: Date?
        get() = getDate("startTime")
        set(value) = put("startTime", value ?: Date())

    var isLive: Boolean
        get() = getBoolean("isLive")
        set(value) = put("isLive", value)
}

@ParseClassName("CertificationRequest")
class CertificationRequestParse : ParseObject() {
    var farmerId: String?
        get() = getString("farmerId")
        set(value) = put("farmerId", value ?: "")

    var docs: List<String>?
        get() = getList("docs")
        set(value) = put("docs", value ?: emptyList<String>())

    var status: String?
        get() = getString("status")
        set(value) = put("status", value ?: "PENDING")

    var submittedAt: Date?
        get() = getDate("submittedAt")
        set(value) = put("submittedAt", value ?: Date())
}

@ParseClassName("EventItem")
class EventItemParse : ParseObject() {
    var title: String?
        get() = getString("title")
        set(value) = put("title", value ?: "")

    var description: String?
        get() = getString("description")
        set(value) = put("description", value ?: "")

    var date: Date?
        get() = getDate("date")
        set(value) = put("date", value ?: Date())

    var type: String?
        get() = getString("type")
        set(value) = put("type", value ?: "")

    var participants: List<String>?
        get() = getList("participants")
        set(value) = put("participants", value ?: emptyList<String>())
}

@ParseClassName("SuggestionItem")
class SuggestionItemParse : ParseObject() {
    var birdId: String?
        get() = getString("birdId")
        set(value) = put("birdId", value ?: "")

    var message: String?
        get() = getString("message")
        set(value) = put("message", value ?: "")

    var date: Date?
        get() = getDate("date")
        set(value) = put("date", value ?: Date())
}

@ParseClassName("TraceabilityEvent")
class TraceabilityEventParse : ParseObject() {
    var birdId: String?
        get() = getString("birdId")
        set(value) = put("birdId", value ?: "")

    var eventType: String?
        get() = getString("eventType")
        set(value) = put("eventType", value ?: "")

    var description: String?
        get() = getString("description")
        set(value) = put("description", value ?: "")

    var timestamp: Date?
        get() = getDate("timestamp")
        set(value) = put("timestamp", value ?: Date())
}

@ParseClassName("VaccinationTemplate")
class VaccinationTemplateParse : ParseObject() {
    var farmId: String?
        get() = getString("farmId")
        set(value) = put("farmId", value ?: "")

    var name: String?
        get() = getString("name")
        set(value) = put("name", value ?: "")

    var schedule: List<String>?
        get() = getList("schedule")
        set(value) = put("schedule", value ?: emptyList<String>())

    var uploadedAt: Date?
        get() = getDate("uploadedAt")
        set(value) = put("uploadedAt", value ?: Date())
}

@ParseClassName("CommunityGroup")
class CommunityGroupParse : ParseObject() {
    var name: String?
        get() = getString("name")
        set(value) = put("name", value ?: "")

    var memberCount: Int
        get() = getInt("memberCount")
        set(value) = put("memberCount", value)

    var type: String?
        get() = getString("type")
        set(value) = put("type", value ?: "")
}

@ParseClassName("MarketplaceListing")
class MarketplaceListingParse : ParseObject() {
    var title: String?
        get() = getString("title")
        set(value) = put("title", value ?: "")

    var description: String?
        get() = getString("description")
        set(value) = put("description", value ?: "")

    var price: Double
        get() = getDouble("price")
        set(value) = put("price", value)

    var sellerId: String?
        get() = getString("sellerId")
        set(value) = put("sellerId", value ?: "")

    var isAvailable: Boolean
        get() = getBoolean("isAvailable")
        set(value) = put("isAvailable", value)

    fun toSafeListing(): com.example.rooster.core.common.model.SafeMarketplaceListing {
        return com.example.rooster.core.common.model.SafeMarketplaceListing(
            id = objectId ?: "",
            title = title ?: "",
            description = description ?: "",
            price = price,
            sellerId = sellerId ?: "",
            isAvailable = isAvailable
        )
    }
}

// Extension functions for conversion
fun CommunityGroupParse.toCommunityGroup(): com.example.rooster.core.common.model.CommunityGroup {
    return com.example.rooster.core.common.model.CommunityGroup(
        id = objectId ?: "",
        name = name ?: "",
        memberCount = memberCount,
        type = type ?: ""
    )
}