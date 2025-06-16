package com.example.rooster.data.model

import com.parse.ParseClassName
import com.parse.ParseObject
import java.util.*

@ParseClassName("EggBatch")
class EggBatch : ParseObject() {
    var cycleId: String?
        get() = getString("cycleId")
        set(v) = put("cycleId", v ?: "")

    var laidDate: Date?
        get() = getDate("laidDate")
        set(v) = if (v != null) put("laidDate", v) else remove("laidDate")

    var eggCount: Int
        get() = getInt("eggCount")
        set(v) = put("eggCount", v)
}
