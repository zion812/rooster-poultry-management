package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.data.OrderRepository
import com.example.rooster.models.UserOrder
import com.example.rooster.models.UserOrderStatus
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val _ordersList = MutableStateFlow<List<UserOrder>>(emptyList())
    val ordersList: StateFlow<List<UserOrder>> = _ordersList

    private val _selectedOrder = MutableStateFlow<UserOrder?>(null)
    val selectedOrder: StateFlow<UserOrder?> = _selectedOrder
    override fun onCleared() {
        super.onCleared()
        // Clear state flows to prevent memory leaks
    }


    fun loadOrders() =
        viewModelScope.launch {
            try {
                val fetched = OrderRepository.fetchUserOrders(/* TODO: userId */ "")
                _ordersList.value = fetched
                FirebaseCrashlytics.getInstance().log("Orders loaded: ${fetched.size}")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

    fun selectOrder(orderId: String) {
        _selectedOrder.value = _ordersList.value.find { it.id == orderId }
    }

    fun confirmCOD(orderId: String) =
        viewModelScope.launch {
            try {
                OrderRepository.updateOrderStatus(orderId, UserOrderStatus.ACCEPTED)
                _ordersList.value =
                    _ordersList.value.map {
                        if (it.id == orderId) it.copy(status = UserOrderStatus.ACCEPTED) else it
                    }
                FirebaseCrashlytics.getInstance().log("COD confirmed for order $orderId")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

    fun submitFeedback(
        orderId: String,
        rating: Int,
        comment: String,
    ) = viewModelScope.launch {
        try {
            OrderRepository.submitFeedback(orderId, rating, comment)
            FirebaseCrashlytics.getInstance()
                .log("Feedback submitted for order $orderId: $rating stars")
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}
