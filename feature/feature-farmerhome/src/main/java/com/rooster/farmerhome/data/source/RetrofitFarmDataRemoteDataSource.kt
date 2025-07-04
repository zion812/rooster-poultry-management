package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.FarmBasicInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RetrofitFarmDataRemoteDataSource @Inject constructor(
    private val farmDataApiService: FarmDataApiService
) : FarmDataRemoteDataSource {

    override fun getFarmBasicInfo(farmId: String): Flow<FarmBasicInfo?> = flow {
        try {
            val response = farmDataApiService.getFarmDetails(farmId)
            if (response.isSuccessful) {
                // The FarmBasicInfo model on Android side does not have an 'error' field.
                // If the API returns a 200 OK with an error structure, that would be a mismatch.
                // Assuming 200 OK means success and the body is FarmBasicInfo.
                // HTTP errors (404 etc.) are caught by response.isSuccessful.
                emit(response.body())
            } else {
                // Handle HTTP error codes (4xx, 5xx)
                // For a 404, response.body() will be null.
                // For other errors, errorBody might contain more info.
                // val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
                // Log.e("FarmDataApi", "API Error: ${response.code()} - $errorBody")
                emit(null) // Indicate error or not found by emitting null
            }
        } catch (e: Exception) {
            // Handle network exceptions, etc.
            // Log.e("FarmDataApi", "Network Error: ${e.message}", e)
            emit(null) // Indicate error by emitting null
        }
    }.flowOn(Dispatchers.IO)
}
