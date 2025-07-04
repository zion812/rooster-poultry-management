package com.example.rooster.core.payment.data.remote // Updated package

import com.example.rooster.core.common.models.payment.CreateOrderRequest
import com.example.rooster.core.common.models.payment.RazorpayOrderResponse
import com.example.rooster.core.common.models.payment.VerifyPaymentRequest
import com.example.rooster.core.common.models.payment.VerifyPaymentResponse
import retrofit2.Response // Using Response wrapper for more control over HTTP status codes
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API service interface for payment-related backend operations.
 * These endpoints interact with the Express backend which then communicates with Razorpay.
 */
interface PaymentApiService {

    /**
     * Calls the backend to create a Razorpay order.
     * The backend will interact with Razorpay SDK to generate the order.
     * Endpoint: POST /api/payments/orders
     */
    @POST("api/payments/orders") // Ensure this path matches your Express backend route
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<RazorpayOrderResponse>
    // Using Response<T> allows checking HTTP success/error codes from the backend itself.
    // The RazorpayOrderResponse should include the backend's own success flag if applicable,
    // in addition to Razorpay's order details and the key_id.

    /**
     * Calls the backend to verify a payment after client-side completion with Razorpay.
     * The backend will verify the Razorpay signature.
     * Endpoint: POST /api/payments/verify
     */
    @POST("api/payments/verify") // Ensure this path matches your Express backend route
    suspend fun verifyPayment(@Body request: VerifyPaymentRequest): Response<VerifyPaymentResponse>
    // VerifyPaymentResponse should indicate if the backend considers the payment verified.
}
