package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.ProductionSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RetrofitProductionMetricsRemoteDataSource @Inject constructor(
    private val productionMetricsApiService: ProductionMetricsApiService
) : ProductionMetricsRemoteDataSource {

    override fun getProductionSummary(farmId: String): Flow<ProductionSummary> = flow {
        try {
            val response = productionMetricsApiService.getProductionSummary(farmId)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(it)
                } ?: run {
                    // Create a ProductionSummary with an error or default state
                    // For now, re-throwing or emitting an error state would be better
                    // but the Flow type is ProductionSummary, not ProductionSummary?
                    // This indicates the design expects a summary or throws.
                    // For simplicity now, if body is null on 200, it's an issue.
                    throw NullPointerException("Empty response body for successful production summary request")
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
                // Ideally, parse this errorBody if it's a structured JSON error from API
                // For now, just throw an exception that will be caught by .catch in ViewModel
                throw Exception("API Error for Production Summary: ${response.code()} - $errorBody")
            }
        } catch (e: Exception) {
            // Log.e("ProdMetricsApi", "Network Error: ${e.message}", e)
            // Rethrow to be handled by the ViewModel's .catch operator
            throw e
        }
    }.flowOn(Dispatchers.IO)
}
