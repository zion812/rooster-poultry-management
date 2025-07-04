package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.FarmBasicInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

class MockFarmDataRemoteDataSource @Inject constructor() : FarmDataRemoteDataSource {

    private val farms = mutableMapOf<String, FarmBasicInfo>()

    private fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(System.currentTimeMillis() - Random.nextInt(1, 30) * 24 * 60 * 60 * 1000L))
    }

    init {
        // Pre-populate with some mock farms
        val farm1 = FarmBasicInfo(
            farmId = "farm123",
            farmName = "Green Valley Poultry",
            location = "Krishna District, AP",
            ownerName = "Mr. S. Patel",
            activeFlockCount = 3,
            totalCapacity = 2500,
            lastHealthCheckDate = getFormattedDate()
        )
        val farm2 = FarmBasicInfo(
            farmId = "farm456",
            farmName = "Sunrise Eggs Co.",
            location = "Near Vijayawada, AP",
            ownerName = "Mrs. L. Devi",
            activeFlockCount = 5,
            totalCapacity = 10000,
            lastHealthCheckDate = getFormattedDate()
        )
        farms[farm1.farmId] = farm1
        farms[farm2.farmId] = farm2
    }

    override fun getFarmBasicInfo(farmId: String): Flow<FarmBasicInfo?> = flow {
        delay(600) // Simulate network delay
        if (farmId == "error_farm") {
            emit(null) // Simulate an error or farm not found
        } else {
            emit(farms[farmId] ?: farms.values.firstOrNull()) // Return specific farm or a default one if ID not matched for mock
        }
    }
}
