package com.rooster.buyerhome.data.source

import com.rooster.buyerhome.domain.model.PriceComparisonProduct
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random

class MockPriceComparisonRemoteDataSource @Inject constructor() : PriceComparisonRemoteDataSource {
    override fun getPriceComparisonForProducts(productNames: List<String>): Flow<List<PriceComparisonProduct>> = flow {
        delay(600) // Simulate delay
        val results = productNames.mapNotNull { productName ->
            // Only generate data for a few known products for mock
            when (productName.lowercase()) {
                "broilers", "broiler chicken" -> PriceComparisonProduct(
                    productName = "Broiler Chicken",
                    averageMarketPrice = "₹${Random.nextInt(150, 190)}/kg",
                    yourLastPaidPrice = if (Random.nextBoolean()) "₹${Random.nextInt(155, 185)}/kg" else null,
                    bestAvailablePrice = "₹${Random.nextInt(145, 170)}/kg (SellerX)"
                )
                "eggs", "chicken eggs" -> PriceComparisonProduct(
                    productName = "Chicken Eggs",
                    averageMarketPrice = "₹${Random.nextInt(70, 95)}/dozen",
                    yourLastPaidPrice = if (Random.nextBoolean()) "₹${Random.nextInt(75, 90)}/dozen" else null,
                    bestAvailablePrice = "₹${Random.nextInt(65, 80)}/dozen (FarmFresh)"
                )
                else -> null // Return null for products not in mock set
            }
        }
        emit(results)
    }
}
