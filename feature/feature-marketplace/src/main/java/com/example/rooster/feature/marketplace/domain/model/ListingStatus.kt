package com.example.rooster.feature.marketplace.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ListingStatus {
    ACTIVE,       // Currently available for sale
    SOLD_OUT,   // All quantity sold
    EXPIRED,      // Listing duration ended (if applicable)
    DRAFT,        // Saved by seller, not yet public
    PENDING_APPROVAL, // Requires admin approval before going live
    DISABLED      // Disabled by admin or seller
}
