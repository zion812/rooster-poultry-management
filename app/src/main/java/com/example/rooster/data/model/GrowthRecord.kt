package com.example.rooster.data.model

import com.parse.ParseClassName
import com.parse.ParseObject
import java.util.*

@ParseClassName("GrowthRecord")
class GrowthRecord : ParseObject() {
    var birdId: String?
        get() = getString("birdId")
        set(v) = put("birdId", v ?: "")

    var batchId: String?
        get() = getString("batchId")
        set(v) = put("batchId", v ?: "")

    var date: Date?
        get() = getDate("date")
        set(v) = if (v != null) put("date", v) else remove("date")

    var weightGrams: Int
        get() = getInt("weightGrams")
        set(v) = put("weightGrams", v)

    var heightMm: Int
        get() = getInt("heightMm")
        set(v) = put("heightMm", v)

    var vaccinated: Boolean
        get() = getBoolean("vaccinated")
        set(v) = put("vaccinated", v)

    var mortalityFlag: Boolean
        get() = getBoolean("mortalityFlag")
        set(v) = put("mortalityFlag", v)
}
