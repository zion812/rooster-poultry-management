package com.example.rooster.core.common.worker;

/**
 * Base class for sync workers
 * This provides the foundation for background synchronization tasks
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b&\u0018\u0000 \u00062\u00020\u0001:\u0002\u0006\u0007B\u0005\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u0004H\u00a6@\u00a2\u0006\u0002\u0010\u0005\u00a8\u0006\b"}, d2 = {"Lcom/example/rooster/core/common/worker/SyncWorkerBase;", "", "()V", "doWork", "Lcom/example/rooster/core/common/worker/SyncWorkerBase$Result;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "Result", "core-common_debug"})
public abstract class SyncWorkerBase {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String COMMUNITY_SYNC_WORKER = "community_sync_worker";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String MARKETPLACE_SYNC_WORKER = "marketplace_sync_worker";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String AUCTION_SYNC_WORKER = "auction_sync_worker";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String FARM_DATA_SYNC_WORKER = "farm_data_sync_worker";
    @org.jetbrains.annotations.NotNull()
    public static final com.example.rooster.core.common.worker.SyncWorkerBase.Companion Companion = null;
    
    public SyncWorkerBase() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object doWork(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.rooster.core.common.worker.SyncWorkerBase.Result> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/example/rooster/core/common/worker/SyncWorkerBase$Companion;", "", "()V", "AUCTION_SYNC_WORKER", "", "COMMUNITY_SYNC_WORKER", "FARM_DATA_SYNC_WORKER", "MARKETPLACE_SYNC_WORKER", "core-common_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0003\u0003\u0004\u0005B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0003\u0006\u0007\b\u00a8\u0006\t"}, d2 = {"Lcom/example/rooster/core/common/worker/SyncWorkerBase$Result;", "", "()V", "Failure", "Retry", "Success", "Lcom/example/rooster/core/common/worker/SyncWorkerBase$Result$Failure;", "Lcom/example/rooster/core/common/worker/SyncWorkerBase$Result$Retry;", "Lcom/example/rooster/core/common/worker/SyncWorkerBase$Result$Success;", "core-common_debug"})
    public static abstract class Result {
        
        private Result() {
            super();
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Lcom/example/rooster/core/common/worker/SyncWorkerBase$Result$Failure;", "Lcom/example/rooster/core/common/worker/SyncWorkerBase$Result;", "error", "", "(Ljava/lang/String;)V", "getError", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "core-common_debug"})
        public static final class Failure extends com.example.rooster.core.common.worker.SyncWorkerBase.Result {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String error = null;
            
            public Failure(@org.jetbrains.annotations.NotNull()
            java.lang.String error) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getError() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.example.rooster.core.common.worker.SyncWorkerBase.Result.Failure copy(@org.jetbrains.annotations.NotNull()
            java.lang.String error) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/example/rooster/core/common/worker/SyncWorkerBase$Result$Retry;", "Lcom/example/rooster/core/common/worker/SyncWorkerBase$Result;", "()V", "core-common_debug"})
        public static final class Retry extends com.example.rooster.core.common.worker.SyncWorkerBase.Result {
            @org.jetbrains.annotations.NotNull()
            public static final com.example.rooster.core.common.worker.SyncWorkerBase.Result.Retry INSTANCE = null;
            
            private Retry() {
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/example/rooster/core/common/worker/SyncWorkerBase$Result$Success;", "Lcom/example/rooster/core/common/worker/SyncWorkerBase$Result;", "()V", "core-common_debug"})
        public static final class Success extends com.example.rooster.core.common.worker.SyncWorkerBase.Result {
            @org.jetbrains.annotations.NotNull()
            public static final com.example.rooster.core.common.worker.SyncWorkerBase.Result.Success INSTANCE = null;
            
            private Success() {
            }
        }
    }
}