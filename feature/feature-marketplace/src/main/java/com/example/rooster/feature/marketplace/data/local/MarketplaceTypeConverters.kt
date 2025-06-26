package com.example.rooster.feature.marketplace.data.local

import androidx.room.TypeConverter
import com.example.rooster.feature.marketplace.domain.model.ProductCategory
import com.example.rooster.feature.marketplace.domain.model.ListingStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MarketplaceTypeConverters {
    private val gson = Gson()

    // List<String> converters
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { gson.fromJson(it, object : TypeToken<List<String>>() {}.type) }
    }

    // Map<String, String> converters
    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringMap(value: String?): Map<String, String>? {
        return value?.let { gson.fromJson(it, object : TypeToken<Map<String, String>>() {}.type) }
    }

    // ProductCategory enum converters
    @TypeConverter
    fun fromProductCategory(category: ProductCategory?): String? {
        return category?.name
    }

    @TypeConverter
    fun toProductCategory(name: String?): ProductCategory? {
        return name?.let { ProductCategory.valueOf(it) }
    }

    // ListingStatus enum converters
    @TypeConverter
    fun fromListingStatus(status: ListingStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toListingStatus(name: String?): ListingStatus? {
        return name?.let { ListingStatus.valueOf(it) }
    }

    // OrderStatus enum converters
    @TypeConverter
    fun fromOrderStatus(status: com.example.rooster.feature.marketplace.domain.model.OrderStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toOrderStatus(name: String?): com.example.rooster.feature.marketplace.domain.model.OrderStatus? {
        return name?.let { com.example.rooster.feature.marketplace.domain.model.OrderStatus.valueOf(it) }
    }

    // PaymentDetails converters (domain model to JSON String)
    @TypeConverter
    fun fromPaymentDetails(paymentDetails: com.example.rooster.feature.marketplace.domain.model.PaymentDetails?): String? {
        return paymentDetails?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toPaymentDetails(json: String?): com.example.rooster.feature.marketplace.domain.model.PaymentDetails? {
        return json?.let { gson.fromJson(it, com.example.rooster.feature.marketplace.domain.model.PaymentDetails::class.java) }
    }
}
