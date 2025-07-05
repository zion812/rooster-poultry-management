package com.example.rooster.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class FarmDto(
    val id: String,
    val name: String,
    val location: String,
    val farmerId: String,
    val farmerName: String,
    val totalFowl: Int = 0,
    val activeFowl: Int = 0,
    val farmType: String = "POULTRY", // POULTRY, MIXED, ORGANIC
    val establishedDate: String, // ISO date string
    val contactNumber: String = "",
    val email: String = "",
    val address: String = "",
    val coordinates: CoordinatesDto? = null,
    val certifications: List<String> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: String, // ISO timestamp
    val updatedAt: String  // ISO timestamp
)

@Serializable
data class CoordinatesDto(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class FarmAnalyticsDto(
    val farmId: String,
    val totalFowl: Int = 0,
    val activeFowl: Int = 0,
    val avgHealthScore: Double = 0.0,
    val monthlyRevenue: Double = 0.0,
    val weatherCondition: String = "Fair",
    val alertsCount: Int = 0,
    val complianceScore: Double = 0.95,
    val nearbyMarkets: Int = 3,
    val aiRecommendations: List<String> = emptyList(),
    val mortalityRate: Double = 0.0,
    val feedConsumption: Double = 0.0,
    val eggProduction: Int = 0,
    val vaccinationStatus: String = "UP_TO_DATE", // UP_TO_DATE, PENDING, OVERDUE
    val lastUpdated: String // ISO timestamp
)

@Serializable
data class FowlRecordDto(
    val id: String,
    val farmId: String,
    val breed: String,
    val age: Int, // in weeks
    val healthScore: Double,
    val lastCheckup: String, // ISO date string
    val vaccinations: List<String> = emptyList(),
    val weight: Double, // in kg
    val isActive: Boolean = true,
    val gender: String = "UNKNOWN", // MALE, FEMALE, UNKNOWN
    val batchId: String? = null,
    val parentId: String? = null,
    val acquisitionDate: String, // ISO date string
    val acquisitionSource: String = "BRED", // BRED, PURCHASED, TRANSFERRED
    val notes: String = "",
    val imageUrls: List<String> = emptyList(),
    val createdAt: String, // ISO timestamp
    val updatedAt: String  // ISO timestamp
)

@Serializable
data class HealthAlertDto(
    val id: String,
    val farmId: String,
    val fowlId: String? = null, // null for farm-wide alerts
    val type: String, // VACCINATION_DUE, HEALTH_CHECK, WEIGHT_LOSS, BEHAVIOR_CHANGE, DISEASE_OUTBREAK
    val severity: String, // LOW, MEDIUM, HIGH, CRITICAL
    val title: String,
    val description: String,
    val actionRequired: String,
    val isResolved: Boolean = false,
    val resolvedAt: String? = null,
    val resolvedBy: String? = null,
    val dueDate: String? = null, // ISO date string
    val createdAt: String, // ISO timestamp
    val updatedAt: String  // ISO timestamp
)