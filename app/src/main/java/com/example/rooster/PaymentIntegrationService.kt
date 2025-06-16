package com.example.rooster

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.parse.ParseObject
import com.parse.ParseUser
import com.razorpay.Checkout
import org.json.JSONObject

/*
 * PaymentIntegrationService - Handles payment processing and COD orders
 * Integrates with Razorpay for online payments and manages COD workflow
 */
class PaymentIntegrationService(private val context: Context) {
    // Callbacks are not strictly needed here if PaymentActivity handles results
    // private var currentOnSuccess: ((String) -> Unit)? = null
    // private var currentOnFailure: ((String) -> Unit)? = null

    companion object {
        @Volatile
        private var INSTANCE: PaymentIntegrationService? = null

        fun getInstance(context: Context): PaymentIntegrationService =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PaymentIntegrationService(context.applicationContext).also {
                    INSTANCE = it
                }
            }
    }

    /*
     * Updated initiatePayment to use Razorpay
     */
    fun initiatePayment(
        navController: NavController,
        listing: SafeListing,
        isTeluguMode: Boolean,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val activity = context as? Activity
        if (activity == null) {
            onFailure("Payment failed: Context is not an Activity for Razorpay.")
            Log.e("PaymentService", "Context is not an Activity, required for Razorpay.")
            return
        }

        try {
            val checkout = Checkout()
            checkout.setKeyID("rzp_test_YOUR_KEY_ID") // IMPORTANT: Replace with your actual test key ID

            val options = JSONObject()
            options.put(
                "name",
                if (isTeluguMode) "రూస్టర్ మార్కెట్‌ప్లేస్" else "Rooster Marketplace",
            )
            options.put(
                "description",
                "Payment for ${listing.breed}",
            )
            options.put(
                "image",
                "https://s3.amazonaws.com/rzp-mobile/images/rzp.png",
            )
            options.put("theme.color", "#FF5722")
            options.put("currency", "INR")
            options.put("amount", (listing.price * 100).toInt())
            options.put("retry.enabled", true)
            options.put("retry.max_count", 4)

            val prefill = JSONObject()
            val currentUser = ParseUser.getCurrentUser()
            if (currentUser != null) {
                prefill.put("email", currentUser.email ?: "")
                prefill.put("contact", currentUser.getString("phone") ?: "")
            }
            options.put("prefill", prefill)

            checkout.open(activity, options)
        } catch (e: Exception) {
            Log.e("PaymentService", "Error initiating Razorpay payment", e)
            onFailure("Payment failed: ${e.message ?: "Unknown Razorpay error"}")
        }
    }

    fun verifyPayment(
        paymentId: String,
        orderId: String,
        signature: String,
        onSuccess: (ParseObject) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        Log.d(
            "PaymentService",
            "Client-side: Verifying payment - PaymentID: $paymentId, OrderID: $orderId, Signature: $signature",
        )
        try {
            val transaction = ParseObject("Transaction")
            transaction.put("paymentId", paymentId)
            transaction.put("razorpayOrderId", orderId)
            transaction.put("razorpaySignature", signature)
            transaction.put("status", "PENDING_VERIFICATION")
            transaction.put("user", ParseUser.getCurrentUser())
            transaction.saveInBackground { e ->
                if (e == null) {
                    onSuccess(transaction)
                    Log.d(
                        "PaymentService",
                        "Transaction logged client-side: ${transaction.objectId}",
                    )
                } else {
                    onFailure("Failed to save transaction log: ${e.localizedMessage}")
                    Log.e("PaymentService", "Failed to save transaction log", e)
                }
            }
        } catch (e: Exception) {
            onFailure("Transaction logging error: ${e.message}")
            Log.e("PaymentService", "Transaction logging error", e)
        }
    }

    fun processCODOrder(
        listing: SafeListing,
        deliveryAddress: String,
        specialInstructions: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        try {
            val order = ParseObject("Order")
            order.put("user", ParseUser.getCurrentUser())
            order.put("listingId", listing.id)
            order.put("totalAmount", listing.price)
            order.put("paymentMethod", "COD")
            order.put("status", "PENDING_CONFIRMATION")
            order.put("deliveryAddress", deliveryAddress)
            order.put("specialInstructions", specialInstructions)
            order.saveInBackground { e ->
                if (e == null) {
                    onSuccess(order.objectId)
                    Log.d("PaymentService", "COD Order placed: ${order.objectId}")
                } else {
                    onFailure("Failed to place COD order: ${e.localizedMessage}")
                    Log.e("PaymentService", "Failed to place COD order", e)
                }
            }
        } catch (e: Exception) {
            onFailure("Error creating COD order: ${e.message}")
            Log.e("PaymentService", "Error creating COD order", e)
        }
    }
}

@Composable
fun PaymentMethodsDialog(
    listing: SafeListing,
    isTeluguMode: Boolean,
    onPaymentSelected: (PaymentMethod) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isTeluguMode) "చెల్లింపు పద్ధతిని ఎంచుకోండి" else "Select Payment Method") },
        text = {
            Column {
                Text("${if (isTeluguMode) "వస్తువు" else "Item"}: ${listing.breed}")
                Text("${if (isTeluguMode) "మొత్తం" else "Amount"}: ₹${listing.price}")
            }
        },
        confirmButton = {
            Button(onClick = { onPaymentSelected(PaymentMethod.RAZORPAY) }) {
                Text(if (isTeluguMode) "Razorpayతో చెల్లించండి" else "Pay with Razorpay")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(if (isTeluguMode) "క్యాష్ ఆన్ డెలివరీ" else "Cash on Delivery")
            }
        },
    )
}

@Composable
fun CODAddressDialog(
    listing: SafeListing,
    isTeluguMode: Boolean,
    onConfirm: (address: String, instructions: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var address by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isTeluguMode) "COD ఆర్డర్ వివరాలు" else "COD Order Details") },
        text = {
            Column {
                Text(
                    "${if (isTeluguMode) "వస్తువు" else "Item"}: ${listing.breed}, ${if (isTeluguMode) "ధర" else "Price"}: ₹${listing.price}",
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(if (isTeluguMode) "డెలివరీ చిరునామా" else "Delivery Address") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text(if (isTeluguMode) "ప్రత్యేక సూచనలు" else "Special Instructions") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(if (isTeluguMode) "(ఐచ్ఛికం)" else "(Optional)") },
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (address.isNotBlank()) onConfirm(address, instructions) },
                enabled = address.isNotBlank(),
            ) {
                Text(if (isTeluguMode) "COD ఆర్డర్‌ను ఉంచండి" else "Place COD Order")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        },
    )
}
