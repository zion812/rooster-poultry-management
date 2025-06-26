package com.example.rooster.core.common.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A simple singleton event bus for app-wide communication for specific, well-defined events.
 * Use judiciously to avoid making it a god object.
 * Particularly useful for Activity -> ViewModel communication where direct callbacks are complex.
 */
@Singleton
class AppEventBus @Inject constructor() {

    // Example for Payment Events
    private val _paymentEvents = MutableSharedFlow<PaymentEvent>(extraBufferCapacity = 1) // Buffer to avoid losing events if no collector yet
    val paymentEvents = _paymentEvents.asSharedFlow()

    suspend fun publishPaymentEvent(event: PaymentEvent) {
        _paymentEvents.emit(event)
    }

    // Add other event types here if needed, each with its own SharedFlow
    // e.g., for global navigation requests, user session changes, etc.
    // private val _navigationEvents = MutableSharedFlow<NavigationCommand>()
    // val navigationEvents = _navigationEvents.asSharedFlow()
    // suspend fun publishNavigationEvent(command: NavigationCommand) { ... }
}
