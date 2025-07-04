package com.example.rooster.feature.marketplace.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rooster.core.common.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: String): Flow<Product?>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductByIdSync(productId: String): Product?

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    fun getProductsByCategory(categoryId: String): Flow<List<Product>>

    // Basic search by name, can be expanded with FTS4 or more complex queries
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<Product>>

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProductById(productId: String)

    @Query("DELETE FROM products")
    suspend fun clearAllProducts()

    // For sync purposes
    @Query("SELECT * FROM products WHERE needsSync = 1")
    suspend fun getProductsForSync(): List<Product>

    // For pagination (example, might need adjustments based on actual API)
    @Query("SELECT * FROM products ORDER BY name ASC LIMIT :limit OFFSET :offset")
    fun getProductsPaged(limit: Int, offset: Int): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE supplierId = :supplierId")
    fun getProductsBySupplier(supplierId: String): Flow<List<Product>>
}
