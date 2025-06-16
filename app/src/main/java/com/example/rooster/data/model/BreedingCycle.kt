package com.example.rooster.data.model

import com.parse.ParseClassName
import com.parse.ParseObject
import java.util.*

@ParseClassName("BreedingCycle")
class BreedingCycle : ParseObject() {
    var roosterId: String?
        get() = getString("roosterId")
        set(v) = put("roosterId", v ?: "")

    var henId: String?
        get() = getString("henId")
        set(v) = put("henId", v ?: "")

    var startDate: Date?
        get() = getDate("startDate")
        set(v) = if (v != null) put("startDate", v) else remove("startDate")

    var expectedHatchDate: Date?
        get() = getDate("expectedHatchDate")
        set(v) = if (v != null) put("expectedHatchDate", v) else remove("expectedHatchDate")

    var status: String?
        get() = getString("status")
        set(v) = put("status", v ?: "INCUBATING")

    var traceable: Boolean
        get() = getBoolean("traceable")
        set(v) = put("traceable", v)
}
