package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.data.OrderRepository
import com.example.rooster.models.UserOrderStatus
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    /**
     * Places a new order in Parse: COD or Online, with optional advance.
     * Calls back with new order ID on success.
     */
    fun placeOrder(
        listingId: String,
        method: String,
        advance: Boolean,
        onResult: (orderId: String) -> Unit,
    ) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val user = com.parse.ParseUser.getCurrentUser()
                val buyerId = user?.objectId ?: "guest"
                val newOrder =
                    OrderRepository.createOrder(
                        productName = listingId, // TODO map to listing
                        method = method,
                        advance = advance,
                        status = UserOrderStatus.PENDING,
                        buyerId = buyerId,
                    )
                onResult(newOrder.id)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            } finally {
                _isProcessing.value = false
            }
        }
    }
}
