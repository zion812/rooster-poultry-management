package com.example.rooster.data.model

import com.parse.ParseClassName
import com.parse.ParseObject
import java.util.*

@ParseClassName("ChickBatch")
class ChickBatch : ParseObject() {
    var eggBatchId: String?
        get() = getString("eggBatchId")
        set(v) = put("eggBatchId", v ?: "")

    var hatchDate: Date?
        get() = getDate("hatchDate")
        set(v) = if (v != null) put("hatchDate", v) else remove("hatchDate")

    var chickCount: Int
        get() = getInt("chickCount")
        set(v) = put("chickCount", v)
}
