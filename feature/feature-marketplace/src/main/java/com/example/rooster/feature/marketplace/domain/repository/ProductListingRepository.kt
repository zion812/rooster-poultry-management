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
        forceRefresh: Boolean = false
    ): Flow<Result<List<ProductListing>>>

    fun getProductListingDetails(listingId: String): Flow<Result<ProductListing?>>

    suspend fun createProductListing(listing: ProductListing): Result<String>

    suspend fun updateProductListing(listing: ProductListing): Result<Unit>

    suspend fun deleteProductListing(listingId: String): Result<Unit>

    // Methods for SyncWorker
    suspend fun getUnsyncedProductListings(): List<ProductListing>
    suspend fun syncListing(productListing: ProductListing): Result<Unit>
}