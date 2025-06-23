package com.example.rooster.feature.farm.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001e\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\b2\b\b\u0002\u0010\u000b\u001a\u00020\fH\'J&\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\b2\u0006\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u000b\u001a\u00020\fH\'J$\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\b2\u0006\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u0012\u001a\u00020\u0005H\'J\u0016\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u0015\u00a8\u0006\u0016"}, d2 = {"Lcom/example/rooster/feature/farm/data/local/SensorDataDao;", "", "deleteOldData", "", "cutoffTime", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAll", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/example/rooster/feature/farm/data/local/SensorDataEntity;", "limit", "", "getByDevice", "deviceId", "", "getByTimeRange", "startTime", "endTime", "insert", "entity", "(Lcom/example/rooster/feature/farm/data/local/SensorDataEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "feature-farm_staging"})
@androidx.room.Dao()
public abstract interface SensorDataDao {
    
    @androidx.room.Query(value = "SELECT * FROM sensor_data WHERE deviceId = :deviceId ORDER BY timestamp DESC LIMIT :limit")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.rooster.feature.farm.data.local.SensorDataEntity>> getByDevice(@org.jetbrains.annotations.NotNull()
    java.lang.String deviceId, int limit);
    
    @androidx.room.Query(value = "SELECT * FROM sensor_data ORDER BY timestamp DESC LIMIT :limit")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.rooster.feature.farm.data.local.SensorDataEntity>> getAll(int limit);
    
    @androidx.room.Query(value = "SELECT * FROM sensor_data WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.rooster.feature.farm.data.local.SensorDataEntity>> getByTimeRange(long startTime, long endTime);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.data.local.SensorDataEntity entity, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM sensor_data WHERE timestamp < :cutoffTime")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteOldData(long cutoffTime, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}