package com.example.rooster.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.rooster.BuildConfig
import com.example.rooster.R
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject

class PaymentActivity : ComponentActivity(), PaymentResultWithDataListener {
    private lateinit var orderId: String
    private var amountPaise: Int = 0

    companion object {
        const val EXTRA_ORDER_ID = "EXTRA_ORDER_ID"
        const val EXTRA_AMOUNT_PAISE = "EXTRA_AMOUNT_PAISE"
        const val EXTRA_EMAIL = "EXTRA_EMAIL"
        const val EXTRA_PHONE = "EXTRA_PHONE"

        // For testing, to simulate success without actual payment
        const val EXTRA_TEST_MODE_SUCCESS = "EXTRA_TEST_MODE_SUCCESS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderId = intent.getStringExtra(EXTRA_ORDER_ID) ?: ""
        amountPaise = intent.getIntExtra(EXTRA_AMOUNT_PAISE, 0)
        val email = intent.getStringExtra(EXTRA_EMAIL) ?: ""
        val phone = intent.getStringExtra(EXTRA_PHONE) ?: ""
        val testModeSuccess = intent.getBooleanExtra(EXTRA_TEST_MODE_SUCCESS, false)

        if (orderId.isEmpty() || amountPaise <= 0) {
            Log.e("PaymentActivity", "Missing orderId or amountPaise")
            finishWithResult(Activity.RESULT_CANCELED)
            return
        }

        if (testModeSuccess) {
            Log.d("PaymentActivity", "Test mode: Simulating payment success for order $orderId")
            PaymentManager.saveResult(orderId, amountPaise, "SUCCESS", "test_payment_id", null)
            finishWithResult(Activity.RESULT_OK, "test_payment_id")
            return
        }

        startPayment(orderId, amountPaise, email, phone)
    }

    private fun startPayment(
        orderId: String,
        amountPaise: Int,
        email: String,
        phone: String,
    ) {
        val checkout = Checkout()
        checkout.setKeyID(BuildConfig.RAZORPAY_KEY)
        checkout.setImage(R.drawable.ic_launcher_foreground)

        val options =
            JSONObject().apply {
                put("name", "Rooster Marketplace")
                put("description", "Order #$orderId")
                // put("order_id", orderId) // Use Razorpay order_id if pre-generated
                put("currency", "INR")
                put("amount", amountPaise) // paise
                put(
                    "prefill",
                    JSONObject().apply {
                        put("email", email)
                        put("contact", phone)
                    },
                )
                put(
                    "theme",
                    JSONObject().apply {
                        put("color", "#3F51B5") // Example color
                    },
                )
            }

        try {
            checkout.open(this, options)
        } catch (e: Exception) {
            Log.e("PaymentActivity", "Error starting Razorpay checkout", e)
            PaymentManager.saveResult(orderId, amountPaise, "FAILED", null, e.message)
            finishWithResult(Activity.RESULT_CANCELED)
        }
    }

    override fun onPaymentSuccess(
        razorpayPaymentId: String?,
        paymentData: PaymentData?,
    ) {
        Log.d("PaymentActivity", "Payment Success: $razorpayPaymentId, Data: ${paymentData?.data}")
        PaymentManager.saveResult(orderId, amountPaise, "SUCCESS", razorpayPaymentId, null)
        finishWithResult(Activity.RESULT_OK, razorpayPaymentId)
    }

    override fun onPaymentError(
        code: Int,
        description: String?,
        paymentData: PaymentData?,
    ) {
        Log.w(
            "PaymentActivity",
            "Payment Error: Code: $code, Desc: $description, Data: ${paymentData?.data}",
        )
        val status =
            if (code == Checkout.NETWORK_ERROR || code == Checkout.PAYMENT_CANCELED) "CANCELLED" else "FAILED"
        PaymentManager.saveResult(orderId, amountPaise, status, null, description)
        finishWithResult(Activity.RESULT_CANCELED)
    }

    private fun finishWithResult(
        resultCode: Int,
        paymentId: String? = null,
    ) {
        val resultIntent = Intent()
        paymentId?.let { resultIntent.putExtra("paymentId", it) }
        setResult(resultCode, resultIntent)
        finish()
    }
}
