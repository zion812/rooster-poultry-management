package com.example.rooster.core.database.util

import com.example.rooster.core.common.model.OrderItem
import org.junit.Assert.*
import org.junit.Test

class CommonTypeConvertersTest {
    private val converter = CommonTypeConverters()

    @Test
    fun `fromStringList converts list to json string`() {
        val list = listOf("apple", "banana", "cherry")
        val jsonString = converter.fromStringList(list)
        assertEquals("""["apple","banana","cherry"]""", jsonString)
    }

    @Test
    fun `toStringList converts json string to list`() {
        val jsonString = """["apple","banana","cherry"]"""
        val list = converter.toStringList(jsonString)
        assertEquals(listOf("apple", "banana", "cherry"), list)
    }

    @Test
    fun `fromStringList with null returns null`() {
        assertNull(converter.fromStringList(null))
    }

    @Test
    fun `toStringList with null returns null`() {
        assertNull(converter.toStringList(null))
    }

    @Test
    fun `fromStringList with empty list returns empty json array string`() {
        val list = emptyList<String>()
        val jsonString = converter.fromStringList(list)
        assertEquals("""[]""", jsonString)
    }

    @Test
    fun `toStringList with empty json array string returns empty list`() {
        val jsonString = """[]"""
        val list = converter.toStringList(jsonString)
        assertTrue(list?.isEmpty() ?: false)
    }
}

class OrderItemListConverterTest {
    private val converter = OrderItemListConverter()

    @Test
    fun `fromOrderItemList converts list of OrderItems to json string`() {
        val orderItems = listOf(
            OrderItem("prod1", "Product 1", 2, 10.0, "url1"),
            OrderItem("prod2", "Product 2", 1, 20.0, null)
        )
        val jsonString = converter.fromOrderItemList(orderItems)
        // Note: JSON field order might vary, so direct string comparison can be brittle.
        // A more robust test would parse the JSON and compare objects, or check for substrings.
        // For simplicity here, we'll assume a consistent serialization order from kotlinx.serialization.
        val expectedJson = """[{"productId":"prod1","productName":"Product 1","quantity":2,"pricePerUnit":10.0,"imageUrl":"url1"},{"productId":"prod2","productName":"Product 2","quantity":1,"pricePerUnit":20.0,"imageUrl":null}]"""
        assertEquals(expectedJson, jsonString)
    }

    @Test
    fun `toOrderItemList converts json string to list of OrderItems`() {
        val jsonString = """[{"productId":"prod1","productName":"Product 1","quantity":2,"pricePerUnit":10.0,"imageUrl":"url1"},{"productId":"prod2","productName":"Product 2","quantity":1,"pricePerUnit":20.0,"imageUrl":null}]"""
        val orderItems = converter.toOrderItemList(jsonString)
        val expectedOrderItems = listOf(
            OrderItem("prod1", "Product 1", 2, 10.0, "url1"),
            OrderItem("prod2", "Product 2", 1, 20.0, null)
        )
        assertEquals(expectedOrderItems, orderItems)
    }

    @Test
    fun `fromOrderItemList with null returns null`() {
        assertNull(converter.fromOrderItemList(null))
    }

    @Test
    fun `toOrderItemList with null returns null`() {
        assertNull(converter.toOrderItemList(null))
    }

    @Test
    fun `fromOrderItemList with empty list returns empty json array`() {
        val orderItems = emptyList<OrderItem>()
        val jsonString = converter.fromOrderItemList(orderItems)
        assertEquals("[]", jsonString)
    }

    @Test
    fun `toOrderItemList with empty json array returns empty list`() {
        val jsonString = "[]"
        val orderItems = converter.toOrderItemList(jsonString)
        assertTrue(orderItems?.isEmpty() ?: false)
    }
}
