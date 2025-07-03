package com.example.rooster.core.common.constants;

import java.util.concurrent.TimeUnit;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\t\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/example/rooster/core/common/constants/WorkConstants;", "", "()V", "AUCTION_SYNC_WORKER", "", "BACKUP_WORK_TAG", "COMMUNITY_SYNC_WORKER", "FARM_DATA_SYNC_WORKER", "INITIAL_DELAY_MILLIS", "", "MARKETPLACE_SYNC_WORKER", "MAX_BACKOFF_MILLIS", "MIN_BACKOFF_MILLIS", "NOTIFICATION_WORK_TAG", "ONE_TIME_WORK_TAG", "PERIODIC_WORK_TAG", "SYNC_WORK_TAG", "UNIQUE_WORK_NAME", "core-common_debug"})
public final class WorkConstants {
    public static final long MIN_BACKOFF_MILLIS = 30000L;
    public static final long MAX_BACKOFF_MILLIS = 300000L;
    public static final long INITIAL_DELAY_MILLIS = 5000L;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String COMMUNITY_SYNC_WORKER = "community_sync_worker";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String MARKETPLACE_SYNC_WORKER = "marketplace_sync_worker";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String AUCTION_SYNC_WORKER = "auction_sync_worker";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String FARM_DATA_SYNC_WORKER = "farm_data_sync_worker";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SYNC_WORK_TAG = "sync_work";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PERIODIC_WORK_TAG = "periodic_work";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ONE_TIME_WORK_TAG = "one_time_work";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String UNIQUE_WORK_NAME = "rooster_sync_work";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String NOTIFICATION_WORK_TAG = "notification_work";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String BACKUP_WORK_TAG = "backup_work";
    @org.jetbrains.annotations.NotNull()
    public static final com.example.rooster.core.common.constants.WorkConstants INSTANCE = null;
    
    private WorkConstants() {
        super();
    }
}