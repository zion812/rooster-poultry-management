package com.example.rooster.core.common.user;

/**
 * Provides access to the current authenticated user's ID.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001J\n\u0010\u0007\u001a\u0004\u0018\u00010\u0004H&R\u001a\u0010\u0002\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00040\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\b"}, d2 = {"Lcom/example/rooster/core/common/user/UserIdProvider;", "", "currentUserIdFlow", "Lkotlinx/coroutines/flow/Flow;", "", "getCurrentUserIdFlow", "()Lkotlinx/coroutines/flow/Flow;", "getCurrentUserId", "core-common_debug"})
public abstract interface UserIdProvider {
    
    /**
     * Gets the current user's ID.
     * @return The user ID string, or null if no user is authenticated.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.String getCurrentUserId();
    
    /**
     * A flow that emits the current user's ID.
     * Emits null if no user is authenticated.
     * This can be used to reactively observe changes in authentication state.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.String> getCurrentUserIdFlow();
}