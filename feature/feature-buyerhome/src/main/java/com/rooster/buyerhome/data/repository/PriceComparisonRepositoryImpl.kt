package com.rooster.buyerhome.data.repository

import com.rooster.buyerhome.data.source.PriceComparisonRemoteDataSource
import com.rooster.buyerhome.domain.model.PriceComparisonProduct
import com.rooster.buyerhome.domain.repository.PriceComparisonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PriceComparisonRepositoryImpl @Inject constructor(
    private val remoteDataSource: PriceComparisonRemoteDataSource
    // TODO: Add localDataSource for caching
) : PriceComparisonRepository {
    override fun getPriceComparisonForProducts(productNames: List<String>): Flow<List<PriceComparisonProduct>> {
        return remoteDataSource.getPriceComparisonForProducts(productNames)
    }
}
