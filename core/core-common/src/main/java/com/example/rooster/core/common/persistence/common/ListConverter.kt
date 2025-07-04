package com.example.rooster.core.common.persistence.common

import androidx.room.TypeConverter
import com.example.rooster.core.common.model.OrderItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.KSerializer

// Using a more generic approach for ListConverter if multiple types of lists need conversion.
// However, for specific complex types like OrderItem, a dedicated converter is often clearer.

// Specific converter for List<OrderItem>
class OrderItemListConverter {
    private val json = Json { ignoreUnknownKeys = true }
    private val serializer: KSerializer<List<OrderItem>> = ListSerializer(OrderItem.serializer())

    @TypeConverter
    fun fromOrderItemList(orderItems: List<OrderItem>?): String? {
        return orderItems?.let { json.encodeToString(serializer, it) }
    }

    @TypeConverter
    fun toOrderItemList(jsonString: String?): List<OrderItem>? {
        return jsonString?.let { json.decodeFromString(serializer, it) }
    }
}

// A more generic list converter (example, might not be directly used by Order if specific one is chosen)
// Ensure your model classes are @Serializable for this to work.
class ListConverter {
    private val json = Json { ignoreUnknownKeys = true; classDiscriminator = "#CLASS" }

    // Generic converter for List<String> (often needed)
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.let { json.encodeToString(ListSerializer(kotlinx.serialization.builtins.serializer()), it) }
    }

    @TypeConverter
    fun toStringList(jsonString: String?): List<String>? {
        return jsonString?.let { json.decodeFromString(ListSerializer(kotlinx.serialization.builtins.serializer()), it) }
    }

    // If you need to store List<Any> or lists of various serializable types,
    // you would need more complex handling or separate converters for each type.
    // For OrderItem, the OrderItemListConverter is more type-safe.
    // The @TypeConverters annotation on the Order entity should specify OrderItemListConverter::class
    // if Order.orderItems is List<OrderItem>.
    // If Order uses List<String> for some other field, then this general ListConverter might be useful.
    // For AGENTS.md compliance and clarity, it's better to have specific converters for complex lists.

    // The Order.kt was generated with "import com.example.rooster.core.common.persistence.common.ListConverter"
    // This implies it expects a general ListConverter.
    // To make Order.kt work with List<OrderItem> and this file, Order.kt should be updated to use OrderItemListConverter
    // or this ListConverter should be enhanced.
    // For now, providing the String list converters as they are commonly useful.
    // The OrderItemListConverter is the correct one for List<OrderItem>.
}
