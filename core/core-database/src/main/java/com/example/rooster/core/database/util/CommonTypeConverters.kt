package com.example.rooster.core.database.util

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * Provides common [TypeConverter] methods for Room database.
 * This class includes converters for basic collections like `List<String>`.
 * It uses `kotlinx.serialization.json.Json` for serializing and deserializing.
 */
class CommonTypeConverters {
    /**
     * Configured Json instance for serialization.
     * - `ignoreUnknownKeys = true`: Allows new fields to be added to JSON without breaking older clients.
     * - `classDiscriminator = "#CLASS"`: Used if serializing polymorphic types, though not strictly necessary for `List<String>`.
     */
    private val json = Json {
        ignoreUnknownKeys = true
        classDiscriminator = "#CLASS" // Included for consistency, may not be used by simple list<string>
    }

    /**
     * Converts a list of strings to its JSON string representation for database storage.
     * @param list The list of strings to convert. Can be null.
     * @return A JSON string representation of the list, or null if the input list was null.
     */
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.let { json.encodeToString(ListSerializer(kotlinx.serialization.builtins.serializer()), it) }
    }

    /**
     * Converts a JSON string representation back to a list of strings.
     * @param jsonString The JSON string to convert. Can be null.
     * @return A list of strings, or null if the input JSON string was null or invalid.
     */
    @TypeConverter
    fun toStringList(jsonString: String?): List<String>? {
        return jsonString?.let {
            try {
                json.decodeFromString(ListSerializer(kotlinx.serialization.builtins.serializer()), it)
            } catch (e: Exception) {
                // Optionally log error, e.g., Timber.e(e, "Failed to deserialize string list from JSON: $jsonString")
                null // Return null or emptyList() depending on desired error handling
            }
        }
    }
}
