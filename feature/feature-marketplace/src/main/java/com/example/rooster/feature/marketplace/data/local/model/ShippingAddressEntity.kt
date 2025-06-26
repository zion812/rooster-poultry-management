package com.example.rooster.feature.marketplace.data.local.model

// This class can be @Embedded in OrderEntity
data class ShippingAddressEntity(
    val fullName: String,
    val addressLine1: String,
    val addressLine2: String? = null,
    val city: String,
    val district: String,
    val state: String,
    val postalCode: String,
    val country: String = "IN",
    val phoneNumber: String,
    val landmark: String? = null
)
