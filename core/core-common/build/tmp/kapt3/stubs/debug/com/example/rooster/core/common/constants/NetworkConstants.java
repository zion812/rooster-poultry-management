package com.example.rooster.core.common.constants;

import java.util.concurrent.TimeUnit;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/example/rooster/core/common/constants/NetworkConstants;", "", "()V", "CONNECT_TIMEOUT", "", "READ_TIMEOUT", "TIMEOUT_UNIT", "Ljava/util/concurrent/TimeUnit;", "getTIMEOUT_UNIT", "()Ljava/util/concurrent/TimeUnit;", "WRITE_TIMEOUT", "core-common_debug"})
public final class NetworkConstants {
    public static final long CONNECT_TIMEOUT = 30L;
    public static final long READ_TIMEOUT = 30L;
    public static final long WRITE_TIMEOUT = 30L;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.concurrent.TimeUnit TIMEOUT_UNIT = java.util.concurrent.TimeUnit.SECONDS;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.rooster.core.common.constants.NetworkConstants INSTANCE = null;
    
    private NetworkConstants() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.concurrent.TimeUnit getTIMEOUT_UNIT() {
        return null;
    }
}