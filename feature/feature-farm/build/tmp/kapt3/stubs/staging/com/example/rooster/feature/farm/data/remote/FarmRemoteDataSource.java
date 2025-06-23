package com.example.rooster.feature.farm.data.remote;

/**
 * Mock remote data source for real-time farm data
 * TODO: Replace with actual Firebase implementation
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0015\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00042\u0006\u0010\u0006\u001a\u00020\u0005H\u0002J\"\u0010\u0007\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00040\b2\u0006\u0010\t\u001a\u00020\u0005H\u0002J\"\u0010\n\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00040\b2\u0006\u0010\u000b\u001a\u00020\u0005H\u0002J\"\u0010\f\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00040\b2\u0006\u0010\r\u001a\u00020\u0005H\u0002J\"\u0010\u000e\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00040\b2\u0006\u0010\u000b\u001a\u00020\u0005H\u0002J\"\u0010\u000f\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00040\b2\u0006\u0010\u000b\u001a\u00020\u0005H\u0002J$\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\u0006\u001a\u00020\u0005H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0013\u0010\u0014J$\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\u0016\u001a\u00020\u0005H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0017\u0010\u0014J$\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\u0016\u001a\u00020\u0005H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0019\u0010\u0014J$\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\u0016\u001a\u00020\u0005H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001b\u0010\u0014J(\u0010\u001c\u001a\u001c\u0012\u0018\u0012\u0016\u0012\u0012\u0012\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00040\u00110\u001d2\u0006\u0010\u0006\u001a\u00020\u0005J,\u0010\u001e\u001a \u0012\u001c\u0012\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00040\b0\u00110\u001d2\u0006\u0010\t\u001a\u00020\u0005J,\u0010\u001f\u001a \u0012\u001c\u0012\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00040\b0\u00110\u001d2\u0006\u0010\u000b\u001a\u00020\u0005J,\u0010 \u001a \u0012\u001c\u0012\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00040\b0\u00110\u001d2\u0006\u0010\r\u001a\u00020\u0005J,\u0010!\u001a \u0012\u001c\u0012\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00040\b0\u00110\u001d2\u0006\u0010\u000b\u001a\u00020\u0005J,\u0010\"\u001a \u0012\u001c\u0012\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00040\b0\u00110\u001d2\u0006\u0010\u000b\u001a\u00020\u0005J0\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0012\u0010$\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u0004H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b%\u0010&J0\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0012\u0010(\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u0004H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b)\u0010&J0\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0012\u0010+\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u0004H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b,\u0010&J0\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0012\u0010.\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u0004H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b/\u0010&J0\u00100\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0012\u0010(\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u0004H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b1\u0010&\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u00062"}, d2 = {"Lcom/example/rooster/feature/farm/data/remote/FarmRemoteDataSource;", "", "()V", "createMockFlockData", "", "", "flockId", "createMockFlocksList", "", "ownerId", "createMockMortalityRecords", "fowlId", "createMockSensorData", "deviceId", "createMockUpdateRecords", "createMockVaccinationRecords", "deleteFlock", "Lkotlin/Result;", "", "deleteFlock-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteMortalityRecord", "recordId", "deleteMortalityRecord-gIAlu-s", "deleteUpdateRecord", "deleteUpdateRecord-gIAlu-s", "deleteVaccinationRecord", "deleteVaccinationRecord-gIAlu-s", "getFlockRealTime", "Lkotlinx/coroutines/flow/Flow;", "getFlocksByOwnerRealTime", "getMortalityRecordsRealTime", "getSensorDataRealTime", "getUpdateRecordsRealTime", "getVaccinationRecordsRealTime", "saveFlock", "flockData", "saveFlock-gIAlu-s", "(Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveMortalityRecord", "recordData", "saveMortalityRecord-gIAlu-s", "saveSensorData", "sensorData", "saveSensorData-gIAlu-s", "saveUpdateRecord", "updateData", "saveUpdateRecord-gIAlu-s", "saveVaccinationRecord", "saveVaccinationRecord-gIAlu-s", "feature-farm_staging"})
public final class FarmRemoteDataSource {
    
    @javax.inject.Inject()
    public FarmRemoteDataSource() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.Map<java.lang.String, java.lang.Object>>> getFlockRealTime(@org.jetbrains.annotations.NotNull()
    java.lang.String flockId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.List<java.util.Map<java.lang.String, java.lang.Object>>>> getFlocksByOwnerRealTime(@org.jetbrains.annotations.NotNull()
    java.lang.String ownerId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.List<java.util.Map<java.lang.String, java.lang.Object>>>> getMortalityRecordsRealTime(@org.jetbrains.annotations.NotNull()
    java.lang.String fowlId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.List<java.util.Map<java.lang.String, java.lang.Object>>>> getVaccinationRecordsRealTime(@org.jetbrains.annotations.NotNull()
    java.lang.String fowlId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.List<java.util.Map<java.lang.String, java.lang.Object>>>> getSensorDataRealTime(@org.jetbrains.annotations.NotNull()
    java.lang.String deviceId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.List<java.util.Map<java.lang.String, java.lang.Object>>>> getUpdateRecordsRealTime(@org.jetbrains.annotations.NotNull()
    java.lang.String fowlId) {
        return null;
    }
    
    private final java.util.Map<java.lang.String, java.lang.Object> createMockFlockData(java.lang.String flockId) {
        return null;
    }
    
    private final java.util.List<java.util.Map<java.lang.String, java.lang.Object>> createMockFlocksList(java.lang.String ownerId) {
        return null;
    }
    
    private final java.util.List<java.util.Map<java.lang.String, java.lang.Object>> createMockMortalityRecords(java.lang.String fowlId) {
        return null;
    }
    
    private final java.util.List<java.util.Map<java.lang.String, java.lang.Object>> createMockVaccinationRecords(java.lang.String fowlId) {
        return null;
    }
    
    private final java.util.List<java.util.Map<java.lang.String, java.lang.Object>> createMockSensorData(java.lang.String deviceId) {
        return null;
    }
    
    private final java.util.List<java.util.Map<java.lang.String, java.lang.Object>> createMockUpdateRecords(java.lang.String fowlId) {
        return null;
    }
}