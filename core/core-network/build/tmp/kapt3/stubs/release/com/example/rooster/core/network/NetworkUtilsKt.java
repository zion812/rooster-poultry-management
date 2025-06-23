package com.example.rooster.core.network;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000*\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0003\n\u0000\u001a8\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u00022\u001c\u0010\u0003\u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u0005\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u0004H\u0086@\u00a2\u0006\u0002\u0010\u0007\u001a\"\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00010\t\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\t\u001a\n\u0010\n\u001a\u00020\u000b*\u00020\f\u00a8\u0006\r"}, d2 = {"safeNetworkCall", "Lcom/example/rooster/core/common/Result;", "T", "call", "Lkotlin/Function1;", "Lkotlin/coroutines/Continuation;", "", "(Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "asNetworkResult", "Lkotlinx/coroutines/flow/Flow;", "toNetworkError", "Lcom/example/rooster/core/network/NetworkError;", "", "core-network_release"})
public final class NetworkUtilsKt {
    
    /**
     * Converts throwable to appropriate NetworkError
     */
    @org.jetbrains.annotations.NotNull()
    public static final com.example.rooster.core.network.NetworkError toNetworkError(@org.jetbrains.annotations.NotNull()
    java.lang.Throwable $this$toNetworkError) {
        return null;
    }
    
    /**
     * Extension to safely execute network calls
     */
    @org.jetbrains.annotations.Nullable()
    public static final <T extends java.lang.Object>java.lang.Object safeNetworkCall(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super kotlin.coroutines.Continuation<? super T>, ? extends java.lang.Object> call, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.rooster.core.common.Result<? extends T>> $completion) {
        return null;
    }
    
    /**
     * Extension for Flow to handle network errors
     */
    @org.jetbrains.annotations.NotNull()
    public static final <T extends java.lang.Object>kotlinx.coroutines.flow.Flow<com.example.rooster.core.common.Result<T>> asNetworkResult(@org.jetbrains.annotations.NotNull()
    kotlinx.coroutines.flow.Flow<? extends T> $this$asNetworkResult) {
        return null;
    }
}