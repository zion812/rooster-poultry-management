package com.example.rooster.feature.marketplace.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.rooster.feature.marketplace.data.local.MarketplaceTypeConverters
import com.example.rooster.feature.marketplace.domain.model.ProductCategory
import com.example.rooster.feature.marketplace.domain.model.ListingStatus

@Entity(tableName = "product_listings")
@TypeConverters(MarketplaceTypeConverters::class)
data class ProductListingEntity(
    @PrimaryKey val id: String,
    val sellerId: String,
    val title: String,
    val description: String,
    val category: ProductCategory, // Stored as String by TypeConverter if not directly supported
    val breed: String? = null,
    val ageInWeeks: Int? = null,
    val weightInKg: Double? = null,
    val price: Double,
    val currency: String = "INR",
    val quantityAvailable: Int,
    val imageUrls: List<String> = emptyList(), // Handled by TypeConverter
    val locationCity: String?,
    val locationDistrict: String?,
    val locationState: String = "Andhra Pradesh",
    val isOrganic: Boolean? = null,
    val isVaccinated: Boolean? = null,
    val postedDateTimestamp: Long,
    val updatedDateTimestamp: Long,
    val status: ListingStatus = ListingStatus.ACTIVE, // Stored as String by TypeConverter
    val additionalProperties: Map<String, String>? = null, // Handled by TypeConverter
    var needsSync: Boolean = true, // For offline created/updated listings
    var syncAttempts: Int = 0,
    var lastSyncAttemptTimestamp: Long = 0L
)
