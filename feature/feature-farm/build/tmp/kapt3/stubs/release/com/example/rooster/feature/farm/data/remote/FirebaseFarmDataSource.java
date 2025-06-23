package com.example.rooster.feature.farm.data.remote;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0000\n\u0002\u0010 \n\u0002\b\u0010\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J$\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\u000bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\f\u0010\rJ(\u0010\u000e\u001a\u001c\u0012\u0018\u0012\u0016\u0012\u0012\u0012\u0010\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00100\b0\u000f2\u0006\u0010\n\u001a\u00020\u000bJ,\u0010\u0011\u001a \u0012\u001c\u0012\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\u00100\u00120\b0\u000f2\u0006\u0010\u0013\u001a\u00020\u000bJ,\u0010\u0014\u001a \u0012\u001c\u0012\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\u00100\u00120\b0\u000f2\u0006\u0010\u0015\u001a\u00020\u000bJ,\u0010\u0016\u001a \u0012\u001c\u0012\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\u00100\u00120\b0\u000f2\u0006\u0010\u0017\u001a\u00020\u000bJ0\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\u0010H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001a\u0010\u001bJ0\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0012\u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\u0010H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001e\u0010\u001bJ0\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0012\u0010 \u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\u0010H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b!\u0010\u001bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\""}, d2 = {"Lcom/example/rooster/feature/farm/data/remote/FirebaseFarmDataSource;", "", "firestore", "Lcom/google/firebase/firestore/FirebaseFirestore;", "realtimeDatabase", "Lcom/google/firebase/database/DatabaseReference;", "(Lcom/google/firebase/firestore/FirebaseFirestore;Lcom/google/firebase/database/DatabaseReference;)V", "deleteFlock", "Lkotlin/Result;", "", "flockId", "", "deleteFlock-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getFlockRealTime", "Lkotlinx/coroutines/flow/Flow;", "", "getFlocksByOwnerRealTime", "", "ownerId", "getMortalityRecordsRealTime", "fowlId", "getSensorDataRealTime", "deviceId", "saveFlock", "flockData", "saveFlock-gIAlu-s", "(Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveMortalityRecord", "recordData", "saveMortalityRecord-gIAlu-s", "saveSensorData", "sensorData", "saveSensorData-gIAlu-s", "feature-farm_release"})
public final class FirebaseFarmDataSource {
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.firestore.FirebaseFirestore firestore = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.database.DatabaseReference realtimeDatabase = null;
    
    @javax.inject.Inject()
    public FirebaseFarmDataSource(@org.jetbrains.annotations.NotNull()
    com.google.firebase.firestore.FirebaseFirestore firestore, @org.jetbrains.annotations.NotNull()
    com.google.firebase.database.DatabaseReference realtimeDatabase) {
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
    public final kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.List<java.util.Map<java.lang.String, java.lang.Object>>>> getSensorDataRealTime(@org.jetbrains.annotations.NotNull()
    java.lang.String deviceId) {
        return null;
    }
}