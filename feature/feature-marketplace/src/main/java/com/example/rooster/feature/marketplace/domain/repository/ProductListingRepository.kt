package com.example.rooster.feature.marketplace.domain.repository

import com.example.rooster.core.common.Result // Assuming common Result type
import com.example.rooster.feature.marketplace.domain.model.ProductListing
import com.example.rooster.feature.marketplace.domain.model.ProductCategory
import kotlinx.coroutines.flow.Flow

interface ProductListingRepository {
    fun getProductListings(
        category: ProductCategory? = null,
        sellerId: String? = null,
        searchTerm: String? = null,
 jules/arch-assessment-1
        forceRefresh: Boolean = false,
        pageSize: Int = 10, // Default page size
        lastVisibleTimestamp: Long? = null,
        lastVisibleDocId: String? = null
    ): Flow<Result<List<ProductListing>>>

    fun getProductListingDetails(listingId: String): Flow<Result<ProductListing?>> // Stays same for now
=======
        forceRefresh: Boolean = false
    ): Flow<Result<List<ProductListing>>>

    fun getProductListingDetails(listingId: String): Flow<Result<ProductListing?>>
 main

    suspend fun createProductListing(listing: ProductListing): Result<String> // Returns ID of created listing

    suspend fun updateProductListing(listing: ProductListing): Result<Unit>

    suspend fun deleteProductListing(listingId: String): Result<Unit>

    // Methods for SyncWorker
 feature/phase1-foundations-community-likes
    suspend fun getUnsyncedProductListingEntities(): List<com.example.rooster.feature.marketplace.data.local.model.ProductListingEntity>
    suspend fun syncListingRemote(productListing: ProductListing): Result<Unit>
    suspend fun updateLocalListing(listingEntity: com.example.rooster.feature.marketplace.data.local.model.ProductListingEntity) // For worker
    fun mapListingEntityToDomain(listingEntity: com.example.rooster.feature.marketplace.data.local.model.ProductListingEntity): ProductListing // For worker
=======
    suspend fun getUnsyncedProductListings(): List<ProductListing>
    suspend fun syncListing(productListing: ProductListing): Result<Unit>
 main


    // TODO: Add methods for user's own listings, favorites, etc.
}
