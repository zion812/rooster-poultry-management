package com.example.rooster.feature.marketplace.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rooster.core.common.model.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<Review>)

    @Update
    suspend fun updateReview(review: Review)

    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    fun getReviewById(reviewId: String): Flow<Review?>

    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY timestamp DESC")
    fun getReviewsForProduct(productId: String): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE supplierId = :supplierId ORDER BY timestamp DESC")
    fun getReviewsForSupplier(supplierId: String): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE userId = :userId ORDER BY timestamp DESC")
    fun getReviewsByUser(userId: String): Flow<List<Review>>

    @Query("DELETE FROM reviews WHERE id = :reviewId")
    suspend fun deleteReviewById(reviewId: String)

    @Query("DELETE FROM reviews WHERE productId = :productId")
    suspend fun deleteReviewsForProduct(productId: String)

    @Query("DELETE FROM reviews")
    suspend fun clearAllReviews()

    // For sync purposes
    @Query("SELECT * FROM reviews WHERE needsSync = 1")
    suspend fun getReviewsForSync(): List<Review>
}
