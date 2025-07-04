package com.rooster.buyerhome.data.repository

import com.rooster.buyerhome.data.source.BuyerSupplierRemoteDataSource
import com.rooster.buyerhome.domain.model.SupplierRatingInfo
import com.rooster.buyerhome.domain.repository.BuyerSupplierRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BuyerSupplierRepositoryImpl @Inject constructor(
    private val remoteDataSource: BuyerSupplierRemoteDataSource
    // TODO: Add localDataSource for caching
) : BuyerSupplierRepository {
    override fun getTopRatedSuppliers(count: Int): Flow<List<SupplierRatingInfo>> {
        return remoteDataSource.getTopRatedSuppliers(count)
    }
}
