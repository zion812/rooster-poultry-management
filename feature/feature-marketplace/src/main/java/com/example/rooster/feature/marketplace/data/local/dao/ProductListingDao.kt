package com.example.rooster.feature.marketplace.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rooster.feature.marketplace.data.local.model.ProductListingEntity
import com.example.rooster.feature.marketplace.domain.model.ProductCategory
import com.example.rooster.feature.marketplace.domain.model.ListingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductListingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: ProductListingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListings(listings: List<ProductListingEntity>)

    @Update
    suspend fun updateListing(listing: ProductListingEntity)

    @Query("SELECT * FROM product_listings WHERE id = :listingId")
    fun getListingById(listingId: String): Flow<ProductListingEntity?>

    @Query("SELECT * FROM product_listings WHERE id = :listingId AND needsSync = 1")
    suspend fun getUnsyncedListingByIdSuspend(listingId: String): ProductListingEntity?

    @Query("SELECT * FROM product_listings ORDER BY postedDateTimestamp DESC")
    fun getAllListings(): Flow<List<ProductListingEntity>>

    @Query("SELECT * FROM product_listings WHERE sellerId = :sellerId ORDER BY postedDateTimestamp DESC")
    fun getListingsBySeller(sellerId: String): Flow<List<ProductListingEntity>>

    @Query("SELECT * FROM product_listings WHERE category = :category ORDER BY postedDateTimestamp DESC")
    fun getListingsByCategory(category: ProductCategory): Flow<List<ProductListingEntity>>

    @Query("SELECT * FROM product_listings WHERE status = :status ORDER BY postedDateTimestamp DESC")
    fun getListingsByStatus(status: ListingStatus): Flow<List<ProductListingEntity>>

    @Query("SELECT * FROM product_listings WHERE needsSync = 1")
    suspend fun getUnsyncedListingsSuspend(): List<ProductListingEntity>

    @Query("DELETE FROM product_listings WHERE id = :listingId")
    suspend fun deleteListingById(listingId: String)

    @Query("DELETE FROM product_listings")
    suspend fun clearAllListings()
}
