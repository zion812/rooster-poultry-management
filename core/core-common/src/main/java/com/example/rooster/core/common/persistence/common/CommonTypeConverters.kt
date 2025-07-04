package com.example.rooster.core.common.persistence.common

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class CommonTypeConverters {
    private val json = Json { ignoreUnknownKeys = true; classDiscriminator = "#CLASS" }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.let { json.encodeToString(ListSerializer(kotlinx.serialization.builtins.serializer()), it) }
    }

    @TypeConverter
    fun toStringList(jsonString: String?): List<String>? {
        return jsonString?.let { json.decodeFromString(ListSerializer(kotlinx.serialization.builtins.serializer()), it) }
    }
}
