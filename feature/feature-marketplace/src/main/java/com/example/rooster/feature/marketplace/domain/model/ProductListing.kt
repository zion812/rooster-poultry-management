package com.example.rooster.feature.marketplace.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductListing(
    val id: String,
    val sellerId: String, // Links to User ID or a dedicated FarmerProfile ID
    val title: String,
    val description: String,
    val category: ProductCategory,
    val breed: String? = null, // Specific to poultry
    val ageInWeeks: Int? = null, // Specific to poultry
    val weightInKg: Double? = null, // Specific to poultry
    val price: Double,
    val currency: String = "INR",
    val quantityAvailable: Int,
    val imageUrls: List<String> = emptyList(),
    val locationCity: String?, // e.g., Vijayawada
    val locationDistrict: String?, // e.g., Krishna
    val locationState: String = "Andhra Pradesh", // Default or selectable
    val isOrganic: Boolean? = null,
    val isVaccinated: Boolean? = null, // Specific to poultry
    val postedDateTimestamp: Long,
    val updatedDateTimestamp: Long,
    val status: ListingStatus = ListingStatus.ACTIVE,
    val additionalProperties: Map<String, String>? = null // For custom fields depending on category
)
