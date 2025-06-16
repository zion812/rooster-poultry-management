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

    fun submitRequest(
        request: VetConsultationRequest,
        onResult: (String) -> Unit,
    ) = viewModelScope.launch {
        _isSubmitting.value = true
        try {
            val newReq = VetRepository.submitRequest(request)
            onResult(newReq.id)
        } catch (_: Exception) {
        } finally {
            _isSubmitting.value = false
        }
    }

    fun loadRequests() =
        viewModelScope.launch {
            try {
                _requests.value = VetRepository.fetchRequests()
            } catch (_: Exception) {
            }
        }
}
