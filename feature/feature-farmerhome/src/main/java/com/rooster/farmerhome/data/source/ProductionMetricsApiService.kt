package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.ProductionSummary
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductionMetricsApiService {

    @GET("farm/production_summary/{farm_id}") // Matches Flask route
    suspend fun getProductionSummary(
        @Path("farm_id") farmId: String
    ): Response<ProductionSummary> // Assuming direct mapping
}
