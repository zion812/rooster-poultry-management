package com.example.rooster.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AppEventBus {
    // Emits one-shot messages to show as snackbars or toasts
    private val _snackbarMessages = MutableSharedFlow<String>()
    val snackbarMessages = _snackbarMessages.asSharedFlow()

    suspend fun postSnackbar(message: String) {
        _snackbarMessages.emit(message)
    }
}
