package com.example.rooster.feature.farm.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\b2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\"\u0010\r\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u000e0\t0\b2\u0006\u0010\u000f\u001a\u00020\fH\u0016J\u0010\u0010\u0010\u001a\u00020\n2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0010\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\nH\u0002J\u001c\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00170\u00162\u0006\u0010\u0014\u001a\u00020\nH\u0002J\u001c\u0010\u0018\u001a\u00020\n2\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00170\u0016H\u0002J$\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\t2\u0006\u0010\u001c\u001a\u00020\u001dH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001e\u0010\u001fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006 "}, d2 = {"Lcom/example/rooster/feature/farm/data/repository/NetworkAwareFarmRepository;", "Lcom/example/rooster/feature/farm/data/repository/FarmRepository;", "localDao", "Lcom/example/rooster/feature/farm/data/local/FlockDao;", "remoteDataSource", "Lcom/example/rooster/feature/farm/data/remote/FarmRemoteDataSource;", "(Lcom/example/rooster/feature/farm/data/local/FlockDao;Lcom/example/rooster/feature/farm/data/remote/FarmRemoteDataSource;)V", "getFlockById", "Lkotlinx/coroutines/flow/Flow;", "Lkotlin/Result;", "Lcom/example/rooster/feature/farm/domain/model/Flock;", "id", "", "getFlocksByType", "", "type", "mapEntityToFlock", "entity", "Lcom/example/rooster/feature/farm/data/local/FlockEntity;", "mapFlockToEntity", "flock", "mapFlockToRemote", "", "", "mapRemoteToFlock", "remote", "registerFlock", "", "data", "Lcom/example/rooster/feature/farm/domain/model/FlockRegistrationData;", "registerFlock-gIAlu-s", "(Lcom/example/rooster/feature/farm/domain/model/FlockRegistrationData;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "feature-farm_release"})
public final class NetworkAwareFarmRepository implements com.example.rooster.feature.farm.data.repository.FarmRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.data.local.FlockDao localDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.data.remote.FarmRemoteDataSource remoteDataSource = null;
    
    @javax.inject.Inject()
    public NetworkAwareFarmRepository(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.data.local.FlockDao localDao, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.data.remote.FarmRemoteDataSource remoteDataSource) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<kotlin.Result<com.example.rooster.feature.farm.domain.model.Flock>> getFlockById(@org.jetbrains.annotations.NotNull()
    java.lang.String id) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>>> getFlocksByType(@org.jetbrains.annotations.NotNull()
    java.lang.String type) {
        return null;
    }
    
    private final com.example.rooster.feature.farm.domain.model.Flock mapEntityToFlock(com.example.rooster.feature.farm.data.local.FlockEntity entity) {
        return null;
    }
    
    private final com.example.rooster.feature.farm.data.local.FlockEntity mapFlockToEntity(com.example.rooster.feature.farm.domain.model.Flock flock) {
        return null;
    }
    
    private final com.example.rooster.feature.farm.domain.model.Flock mapRemoteToFlock(java.util.Map<java.lang.String, ? extends java.lang.Object> remote) {
        return null;
    }
    
    private final java.util.Map<java.lang.String, java.lang.Object> mapFlockToRemote(com.example.rooster.feature.farm.domain.model.Flock flock) {
        return null;
    }
}