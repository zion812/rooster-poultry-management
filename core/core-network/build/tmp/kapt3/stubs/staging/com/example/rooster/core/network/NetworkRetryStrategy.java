package com.example.rooster.core.network;

/**
 * Retry mechanism for failed network calls
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u00132\u00020\u0001:\u0001\u0013B\u0005\u00a2\u0006\u0002\u0010\u0002Jj\u0010\u0003\u001a\u0002H\u0004\"\u0004\b\u0000\u0010\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\u0018\b\u0002\u0010\u000b\u001a\u0012\u0012\b\u0012\u00060\rj\u0002`\u000e\u0012\u0004\u0012\u00020\u000f0\f2\u001c\u0010\u0010\u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00040\u0011\u0012\u0006\u0012\u0004\u0018\u00010\u00010\fH\u0086@\u00a2\u0006\u0002\u0010\u0012\u00a8\u0006\u0014"}, d2 = {"Lcom/example/rooster/core/network/NetworkRetryStrategy;", "", "()V", "retryWithBackoff", "T", "maxRetries", "", "initialDelay", "", "backoffMultiplier", "", "shouldRetry", "Lkotlin/Function1;", "Ljava/lang/Exception;", "Lkotlin/Exception;", "", "block", "Lkotlin/coroutines/Continuation;", "(IJDLkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "core-network_staging"})
public final class NetworkRetryStrategy {
    public static final int DEFAULT_MAX_RETRIES = 3;
    public static final long DEFAULT_INITIAL_DELAY = 1000L;
    public static final double DEFAULT_BACKOFF_MULTIPLIER = 2.0;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.rooster.core.network.NetworkRetryStrategy.Companion Companion = null;
    
    public NetworkRetryStrategy() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final <T extends java.lang.Object>java.lang.Object retryWithBackoff(int maxRetries, long initialDelay, double backoffMultiplier, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Exception, java.lang.Boolean> shouldRetry, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super kotlin.coroutines.Continuation<? super T>, ? extends java.lang.Object> block, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super T> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/example/rooster/core/network/NetworkRetryStrategy$Companion;", "", "()V", "DEFAULT_BACKOFF_MULTIPLIER", "", "DEFAULT_INITIAL_DELAY", "", "DEFAULT_MAX_RETRIES", "", "core-network_staging"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}