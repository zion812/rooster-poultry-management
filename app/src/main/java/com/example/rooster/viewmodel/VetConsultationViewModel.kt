package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.data.VetRepository
import com.example.rooster.models.VetConsultationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VetConsultationViewModel : ViewModel() {
    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _requests = MutableStateFlow<List<VetConsultationRequest>>(emptyList())
    val requests: StateFlow<List<VetConsultationRequest>> = _requests

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun submitRequest(
        request: VetConsultationRequest,
        onResult: (String?) -> Unit, // Changed to String? to allow null on error
    ) = viewModelScope.launch {
        _isSubmitting.value = true
        _error.value = null // Clear previous error
        try {
            val newReq = VetRepository.submitRequest(request) // Assuming this can throw
            onResult(newReq.id)
        } catch (e: Exception) {
            // In a real app, map exception to a user-friendly error message string
            _error.value = "Failed to submit request: ${e.message}"
            onResult(null) // Indicate failure
        } finally {
            _isSubmitting.value = false
        }
    }

    fun loadRequests() =
        viewModelScope.launch {
            _error.value = null // Clear previous error
            try {
                _requests.value = VetRepository.fetchRequests() // Assuming this can throw
            } catch (e: Exception) {
                _error.value = "Failed to load requests: ${e.message}"
            }
        }

    fun clearError() {
        _error.value = null
    }
}
