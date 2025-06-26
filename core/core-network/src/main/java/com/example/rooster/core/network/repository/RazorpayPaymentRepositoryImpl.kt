package com.example.rooster.core.network.repository

import com.example.rooster.core.common.Result
import com.example.rooster.core.common.domain.repository.PaymentRepository
import com.example.rooster.core.common.models.payment.CreateOrderRequest
import com.example.rooster.core.common.models.payment.RazorpayOrderResponse
import com.example.rooster.core.common.models.payment.VerifyPaymentRequest
import com.example.rooster.core.common.models.payment.VerifyPaymentResponse
import com.example.rooster.core.network.retrofit.PaymentApiService
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.HttpException // For catching HTTP errors specifically
import java.io.IOException // For network errors
import javax.inject.Inject

class RazorpayPaymentRepositoryImpl @Inject constructor(
    private val paymentApiService: PaymentApiService
) : PaymentRepository {

    override suspend fun createRazorpayOrder(orderRequest: CreateOrderRequest): Result<RazorpayOrderResponse> {
        return try {
            val response = paymentApiService.createOrder(orderRequest)
            if (response.isSuccessful && response.body() != null) {
                // Assuming the backend response itself contains a success flag or structure
                // that RazorpayOrderResponse reflects. If RazorpayOrderResponse directly IS the success body:
                Result.Success(response.body()!!)
            } else {
                // Handle backend error response (e.g. 4xx, 5xx from our Express server)
                val errorBody = response.errorBody()?.string() ?: "Unknown error creating order"
                FirebaseCrashlytics.getInstance().log("CreateOrder API error: ${response.code()} - $errorBody")
                Result.Error(HttpException(response)) // Propagate HttpException
            }
        } catch (e: HttpException) {
            // Retrofit HTTP error
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.Error(e)
        } catch (e: IOException) {
            // Network error (no internet, server down)
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.Error(e)
        } catch (e: Exception) {
            // Other unexpected errors (e.g., serialization issues if not caught by converter)
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.Error(e)
        }
    }

    override suspend fun verifyRazorpayPayment(verifyRequest: VerifyPaymentRequest): Result<VerifyPaymentResponse> {
        return try {
            val response = paymentApiService.verifyPayment(verifyRequest)
            if (response.isSuccessful && response.body() != null) {
                // Assuming VerifyPaymentResponse includes a 'success' field from the backend
                if (response.body()!!.success) {
                    Result.Success(response.body()!!)
                } else {
                    // Backend verification failed (e.g., signature mismatch)
                    FirebaseCrashlytics.getInstance().log("Payment verification failed by backend: ${response.body()!!.message}")
                    Result.Error(Exception("Backend verification failed: ${response.body()!!.message}"))
                }
            } else {
                // Handle backend error response
                val errorBody = response.errorBody()?.string() ?: "Unknown error verifying payment"
                FirebaseCrashlytics.getInstance().log("VerifyPayment API error: ${response.code()} - $errorBody")
                Result.Error(HttpException(response))
            }
        } catch (e: HttpException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.Error(e)
        } catch (e: IOException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.Error(e)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.Error(e)
        }
    }
}
