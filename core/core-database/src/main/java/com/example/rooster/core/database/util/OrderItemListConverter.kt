package com.example.rooster.core.database.util

import androidx.room.TypeConverter
import com.example.rooster.core.common.model.OrderItem // Assuming OrderItem is in core-common model package
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.KSerializer

/**
 * Room [TypeConverter] for converting a list of [OrderItem] objects to and from a JSON string.
 * This allows storing `List<OrderItem>` directly in a Room entity field.
 *
 * Relies on `kotlinx.serialization` for JSON processing. The [OrderItem] class
 * must be `@Serializable`.
 */
class OrderItemListConverter {
    /**
     * Configured Json instance for serialization.
     * `ignoreUnknownKeys = true` makes parsing more resilient to schema changes.
     */
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Serializer for `List<OrderItem>`.
     * This is pre-initialized for efficiency.
     */
    private val listOrderItemSerializer: KSerializer<List<OrderItem>> = ListSerializer(OrderItem.serializer())

    /**
     * Converts a list of [OrderItem] objects to its JSON string representation.
     * @param orderItems The list of [OrderItem]s to convert. Can be null.
     * @return A JSON string representation of the list, or null if the input list was null.
     */
    @TypeConverter
    fun fromOrderItemList(orderItems: List<OrderItem>?): String? {
        return orderItems?.let { json.encodeToString(listOrderItemSerializer, it) }
    }

    /**
     * Converts a JSON string representation back to a list of [OrderItem] objects.
     * @param jsonString The JSON string to convert. Can be null.
     * @return A list of [OrderItem]s, or null if the input JSON string was null or invalid.
     */
    @TypeConverter
    fun toOrderItemList(jsonString: String?): List<OrderItem>? {
        return jsonString?.let {
            try {
                json.decodeFromString(listOrderItemSerializer, it)
            } catch (e: Exception) {
                // Optionally log error, e.g., Timber.e(e, "Failed to deserialize OrderItem list from JSON: $jsonString")
                null // Return null or emptyList() based on error handling strategy
            }
        }
    }
}
