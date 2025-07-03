package com.example.rooster.core.common.domain.repository;

/**
 * Interface for managing user token balances and operations.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J*\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00030\u0007H\u00a6@\u00a2\u0006\u0002\u0010\tJ,\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00030\u0007H\u00a6@\u00a2\u0006\u0002\u0010\tJ\"\u0010\u000b\u001a\u00020\u00032\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00030\u0007H\u00a6@\u00a2\u0006\u0002\u0010\f\u00a8\u0006\r"}, d2 = {"Lcom/example/rooster/core/common/domain/repository/TokenRepository;", "", "addTokens", "", "count", "", "onResult", "Lkotlin/Function1;", "", "(ILkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deductTokens", "loadTokenBalance", "(Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "core-common_staging"})
public abstract interface TokenRepository {
    
    /**
     * Retrieves the current user's token balance.
     * This might return a Flow if the balance can change and UI needs to observe it.
     * Or a simple suspend function if it's a one-time fetch for an operation.
     * For now, let's use a suspend fun that takes a callback, similar to the old TokenService.
     * A Flow-based approach would be more modern.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object loadTokenBalance(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onResult, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Deducts a specified number of tokens (usually 1) from the current user.
     * @param count The number of tokens to deduct.
     * @param onResult Callback indicating success (true) or failure (false).
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deductTokens(int count, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onResult, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Adds a specified number of tokens to the current user.
     * Typically called after a successful token purchase.
     * @param count The number of tokens to add.
     * @param onResult Callback indicating success (true) or failure (false).
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addTokens(int count, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onResult, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Interface for managing user token balances and operations.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}