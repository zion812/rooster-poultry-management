package com.example.rooster.feature.marketplace.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rooster.core.common.model.Supplier
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplierDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: Supplier): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuppliers(suppliers: List<Supplier>)

    @Update
    suspend fun updateSupplier(supplier: Supplier)

    @Query("SELECT * FROM suppliers WHERE id = :supplierId")
    fun getSupplierById(supplierId: String): Flow<Supplier?>

    @Query("SELECT * FROM suppliers")
    fun getAllSuppliers(): Flow<List<Supplier>>

    @Query("DELETE FROM suppliers WHERE id = :supplierId")
    suspend fun deleteSupplierById(supplierId: String)

    @Query("DELETE FROM suppliers")
    suspend fun clearAllSuppliers()

    // For sync purposes
    @Query("SELECT * FROM suppliers WHERE needsSync = 1")
    suspend fun getSuppliersForSync(): List<Supplier>
}
