package com.example.rooster.core.common.constants;

import java.util.concurrent.TimeUnit;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/example/rooster/core/common/constants/CacheConstants;", "", "()V", "CACHE_MAX_AGE", "", "CACHE_MAX_STALE", "CACHE_SIZE", "", "core-common_debug"})
public final class CacheConstants {
    public static final long CACHE_SIZE = 10485760L;
    public static final int CACHE_MAX_AGE = 300;
    public static final int CACHE_MAX_STALE = 86400;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.rooster.core.common.constants.CacheConstants INSTANCE = null;
    
    private CacheConstants() {
        super();
    }
}