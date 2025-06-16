package com.example.rooster.data.model

import com.parse.ParseClassName
import com.parse.ParseObject
import java.util.*

@ParseClassName("QuarantineLog")
class QuarantineLog : ParseObject() {
    var birdId: String?
        get() = getString("birdId")
        set(v) = put("birdId", v ?: "")

    var startDate: Date?
        get() = getDate("startDate")
        set(v) = if (v != null) put("startDate", v) else remove("startDate")

    var endDate: Date?
        get() = getDate("endDate")
        set(v) = if (v != null) put("endDate", v) else remove("endDate")

    var reason: String?
        get() = getString("reason")
        set(v) = put("reason", v ?: "")

    var medicalLogs: List<String>?
        get() = getList<String>("medicalLogs")
        set(v) = put("medicalLogs", v ?: emptyList<String>())
}
