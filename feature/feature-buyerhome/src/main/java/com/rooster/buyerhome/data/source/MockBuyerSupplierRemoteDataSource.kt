package com.rooster.buyerhome.data.source

import com.rooster.buyerhome.domain.model.SupplierRatingInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random

class MockBuyerSupplierRemoteDataSource @Inject constructor() : BuyerSupplierRemoteDataSource {
    override fun getTopRatedSuppliers(count: Int): Flow<List<SupplierRatingInfo>> = flow {
        delay(500) // Simulate delay
        val suppliers = listOf(
            SupplierRatingInfo(
                supplierId = "sup001",
                supplierName = "Krishna Poultry Farms",
                averageRating = Random.nextDouble(4.0, 5.0).toFloat(),
                numberOfReviews = Random.nextInt(50, 200),
                profileImageUrl = null // TODO: Add placeholder image URL
            ),
            SupplierRatingInfo(
                supplierId = "sup002",
                supplierName = "Rural Farms Collective",
                averageRating = Random.nextDouble(3.5, 4.8).toFloat(),
                numberOfReviews = Random.nextInt(30, 150),
                profileImageUrl = null
            ),
            SupplierRatingInfo(
                supplierId = "sup003",
                supplierName = "AgriSupplies Inc.",
                averageRating = Random.nextDouble(4.2, 4.9).toFloat(),
                numberOfReviews = Random.nextInt(100, 300),
                profileImageUrl = null
            ),
            SupplierRatingInfo(
                supplierId = "sup004",
                supplierName = "Deccan Agri Products",
                averageRating = Random.nextDouble(3.0, 4.5).toFloat(),
                numberOfReviews = Random.nextInt(20, 100),
                profileImageUrl = null
            ),
            SupplierRatingInfo(
                supplierId = "sup005",
                supplierName = "Godavari Feeds & Poultry",
                averageRating = Random.nextDouble(4.5, 5.0).toFloat(),
                numberOfReviews = Random.nextInt(80, 250),
                profileImageUrl = null
            )
        )
        emit(suppliers.shuffled().take(count))
    }
}
