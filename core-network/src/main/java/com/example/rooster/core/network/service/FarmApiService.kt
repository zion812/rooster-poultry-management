package com.example.rooster.core.network.service

import com.example.rooster.core.network.dto.FarmDto
import com.example.rooster.core.network.dto.FarmInputDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FarmApiService {

    @GET("farms")
    suspend fun getFarms(@Query("q") searchQuery: String? = null): Response<List<FarmDto>>

    @POST("farms")
    suspend fun createFarm(@Body farmInputDto: FarmInputDto): Response<FarmDto>

    @GET("farms/{farm_id}")
    suspend fun getFarmById(@Path("farm_id") farmId: String): Response<FarmDto>

    @PUT("farms/{farm_id}")
    suspend fun updateFarm(
        @Path("farm_id") farmId: String,
        @Body farmInputDto: FarmInputDto
    ): Response<FarmDto>

    @DELETE("farms/{farm_id}")
    suspend fun deleteFarm(@Path("farm_id") farmId: String): Response<Unit> // No content for 204
}
