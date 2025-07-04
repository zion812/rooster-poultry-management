package com.rooster.buyerhome.data.source

import com.rooster.buyerhome.domain.model.SupplierRatingInfo
import kotlinx.coroutines.flow.Flow

interface BuyerSupplierRemoteDataSource {
    fun getTopRatedSuppliers(count: Int): Flow<List<SupplierRatingInfo>>
}
