package com.example.rooster.core.common.event;

/**
 * A simple singleton event bus for app-wide communication for specific, well-defined events.
 * Use judiciously to avoid making it a god object.
 * Particularly useful for Activity -> ViewModel communication where direct callbacks are complex.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0005H\u0086@\u00a2\u0006\u0002\u0010\rR\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t\u00a8\u0006\u000e"}, d2 = {"Lcom/example/rooster/core/common/event/AppEventBus;", "", "()V", "_paymentEvents", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/example/rooster/core/common/event/PaymentEvent;", "paymentEvents", "Lkotlinx/coroutines/flow/SharedFlow;", "getPaymentEvents", "()Lkotlinx/coroutines/flow/SharedFlow;", "publishPaymentEvent", "", "event", "(Lcom/example/rooster/core/common/event/PaymentEvent;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "core-common_release"})
public final class AppEventBus {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.example.rooster.core.common.event.PaymentEvent> _paymentEvents = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<com.example.rooster.core.common.event.PaymentEvent> paymentEvents = null;
    
    @javax.inject.Inject()
    public AppEventBus() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<com.example.rooster.core.common.event.PaymentEvent> getPaymentEvents() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object publishPaymentEvent(@org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.event.PaymentEvent event, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}