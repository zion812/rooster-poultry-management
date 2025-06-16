package com.example.rooster.payment

import android.app.Activity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseClassName
import com.parse.ParseObject
import com.razorpay.Checkout
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

/** Possible outcomes of a payment attempt */
sealed class PaymentOutcome {
    data class Success(val razorpayPaymentId: String) : PaymentOutcome()

    data class Error(val code: Int, val description: String?) : PaymentOutcome()

    object Cancelled : PaymentOutcome()
}

/**
 * Small wrapper around Razorpay SDK exposing a suspend function.
 * Works only when RemoteConfig key `payments_enabled=true`.
 */
object PaymentManager {
    private const val TAG = "PaymentManager"

    suspend fun pay(
        activity: Activity,
        orderId: String,
        amountPaise: Int,
        userEmail: String,
        userPhone: String,
    ): PaymentOutcome {
        val deferred = CompletableDeferred<PaymentOutcome>()

        val checkout = Checkout()
        // Use Razorpay key from BuildConfig for test/release
        checkout.setKeyID(com.example.rooster.BuildConfig.RAZORPAY_KEY)
        // TODO: For dynamic key management, consider RemoteConfig or BuildConfig fields

        val options =
            JSONObject().apply {
                put("name", "Rooster Marketplace")
                put("description", "Order #$orderId")
                put("order_id", orderId)
                put("currency", "INR")
                put("amount", amountPaise) // paise
                put(
                    "prefill",
                    JSONObject().apply {
                        put("email", userEmail)
                        put("contact", userPhone)
                    },
                )
            }

        // SDK requires the calling Activity to implement PaymentResultListener.
        // Here we just open the checkout; the Activity should forward the result
        // to PaymentManager.handleResult(...) which will complete the deferred.
        checkout.setImage(com.example.rooster.R.drawable.ic_launcher_foreground)
        checkout.open(activity, options)

        // Fallback: if no callback within 60s, mark cancelled
        GlobalScope.launch {
            delay(60_000)
            if (!deferred.isCompleted) {
                deferred.complete(PaymentOutcome.Cancelled)
            }
        }

        return deferred.await()
    }

    fun saveResult(
        orderId: String,
        amountPaise: Int,
        status: String,
        paymentId: String?,
        error: String?,
    ) {
        try {
            PaymentResult().apply {
                put("orderId", orderId)
                put("amountPaise", amountPaise)
                put("status", status)
                paymentId?.let { put("razorpayPaymentId", it) }
                error?.let { put("error", it) }
            }.saveInBackground()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}

@ParseClassName("PaymentResult")
class PaymentResult : ParseObject()
