package com.example.rooster.feature.community.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CommunityTypeConverters {
    private val gson = Gson() // Using Gson for simplicity, could use Kotlinx Serialization

    // List<String> converters
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { gson.fromJson(it, object : TypeToken<List<String>>() {}.type) }
    }

    // Add more converters here if needed for other types like Map<ReactionType, Int>
    // For enums, if stored as string, direct String to Enum.valueOf() is often fine in entities/mappers
    // or specific converters can be added if they are part of an entity field.
}
