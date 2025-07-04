package com.rooster.buyerhome.data.source

import com.rooster.buyerhome.domain.model.PriceComparisonProduct
import kotlinx.coroutines.flow.Flow

interface PriceComparisonRemoteDataSource {
    fun getPriceComparisonForProducts(productNames: List<String>): Flow<List<PriceComparisonProduct>>
}
