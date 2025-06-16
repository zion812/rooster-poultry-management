package com.example.rooster.data.model

import com.parse.ParseClassName
import com.parse.ParseObject
import java.util.*

@ParseClassName("MortalityLog")
class MortalityLog : ParseObject() {
    var birdId: String?
        get() = getString("birdId")
        set(v) = put("birdId", v ?: "")

    var date: Date?
        get() = getDate("date")
        set(v) = if (v != null) put("date", v) else remove("date")

    var cause: String?
        get() = getString("cause")
        set(v) = put("cause", v ?: "")

    var attachments: List<String>?
        get() = getList<String>("attachments")
        set(v) = put("attachments", v ?: emptyList<String>())
}
