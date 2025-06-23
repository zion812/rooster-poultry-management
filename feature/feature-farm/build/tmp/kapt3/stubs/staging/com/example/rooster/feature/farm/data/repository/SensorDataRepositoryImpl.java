package com.example.rooster.feature.farm.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J$\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\tH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\n\u0010\u000bJ\"\u0010\f\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\u00060\r2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J*\u0010\u0012\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\u00060\r2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J*\u0010\u0015\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\u00060\r2\u0006\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0017\u001a\u00020\tH\u0016J$\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\u0019\u001a\u00020\u000fH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001a\u0010\u001bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u001c"}, d2 = {"Lcom/example/rooster/feature/farm/data/repository/SensorDataRepositoryImpl;", "Lcom/example/rooster/feature/farm/data/repository/SensorDataRepository;", "dao", "Lcom/example/rooster/feature/farm/data/local/SensorDataDao;", "(Lcom/example/rooster/feature/farm/data/local/SensorDataDao;)V", "deleteOldSensorData", "Lkotlin/Result;", "", "cutoffTime", "", "deleteOldSensorData-gIAlu-s", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllSensorData", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/example/rooster/feature/farm/domain/model/SensorData;", "limit", "", "getSensorDataByDevice", "deviceId", "", "getSensorDataByTimeRange", "startTime", "endTime", "saveSensorData", "sensorData", "saveSensorData-gIAlu-s", "(Lcom/example/rooster/feature/farm/domain/model/SensorData;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "feature-farm_staging"})
public final class SensorDataRepositoryImpl implements com.example.rooster.feature.farm.data.repository.SensorDataRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.data.local.SensorDataDao dao = null;
    
    @javax.inject.Inject()
    public SensorDataRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.data.local.SensorDataDao dao) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.List<com.example.rooster.feature.farm.domain.model.SensorData>>> getAllSensorData(int limit) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.List<com.example.rooster.feature.farm.domain.model.SensorData>>> getSensorDataByDevice(@org.jetbrains.annotations.NotNull()
    java.lang.String deviceId, int limit) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.List<com.example.rooster.feature.farm.domain.model.SensorData>>> getSensorDataByTimeRange(long startTime, long endTime) {
        return null;
    }
}