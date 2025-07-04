package com.rooster.buyerhome.domain.repository

import com.rooster.buyerhome.domain.model.SupplierRatingInfo
import kotlinx.coroutines.flow.Flow

interface BuyerSupplierRepository {
    fun getTopRatedSuppliers(count: Int = 5): Flow<List<SupplierRatingInfo>>
    // fun getSupplierDetails(supplierId: String): Flow<SupplierRatingInfo?>
}
