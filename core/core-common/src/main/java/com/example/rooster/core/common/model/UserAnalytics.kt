package com.example.rooster.core.common.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductAnalyticInfo(
    val productName: String,
    val quantity: Int,
    val totalValue: Double
)

@Serializable
data class CategorySpending(
    val categoryName: String,
    val amount: Double
)

@Serializable
data class UserAnalytics(
    val userId: String,
    val totalOrders: Int,
    val totalSpent: Double,
    val averageOrderValue: Double,
    val topProductsByQuantity: List<ProductAnalyticInfo>,
    val topProductsByValue: List<ProductAnalyticInfo>,
    val spendingByCategory: List<CategorySpending>,
    val lastReportDate: Long = System.currentTimeMillis()
    // This model is for representing calculated analytics.
    // It might not be stored directly in Room as a single entity,
    // but rather constructed on-the-fly or its components stored/cached if needed.
    // For now, it's a DTO.
)
