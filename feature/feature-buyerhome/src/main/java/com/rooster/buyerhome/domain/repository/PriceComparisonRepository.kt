package com.rooster.buyerhome.domain.repository

import com.rooster.buyerhome.domain.model.PriceComparisonProduct
import kotlinx.coroutines.flow.Flow

interface PriceComparisonRepository {
    fun getPriceComparisonForProducts(productNames: List<String>): Flow<List<PriceComparisonProduct>>
}
