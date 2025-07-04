package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.FarmBasicInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FarmDataApiService {

    @GET("farm/details/{farm_id}") // Matches Flask route
    suspend fun getFarmDetails(
        @Path("farm_id") farmId: String
    ): Response<FarmBasicInfo> // Assuming direct mapping

    // Optional: For the /farms endpoint, if needed directly by home screen
    // For now, focusing on getFarmDetails as per FarmBasicInfo requirement
    /*
    @GET("farms")
    suspend fun getAllFarmsSummary(): Response<List<FarmSummaryItem>> // Define FarmSummaryItem if needed
    */
}
