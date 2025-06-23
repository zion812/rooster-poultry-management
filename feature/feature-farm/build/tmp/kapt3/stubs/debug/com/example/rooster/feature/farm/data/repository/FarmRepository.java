package com.example.rooster.feature.farm.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u001c\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u0006\u001a\u00020\u0007H&J\"\u0010\b\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\t0\u00040\u00032\u0006\u0010\n\u001a\u00020\u0007H&J$\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u00042\u0006\u0010\r\u001a\u00020\u000eH\u00a6@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u000f\u0010\u0010\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0011"}, d2 = {"Lcom/example/rooster/feature/farm/data/repository/FarmRepository;", "", "getFlockById", "Lkotlinx/coroutines/flow/Flow;", "Lkotlin/Result;", "Lcom/example/rooster/feature/farm/domain/model/Flock;", "id", "", "getFlocksByType", "", "type", "registerFlock", "", "data", "Lcom/example/rooster/feature/farm/domain/model/FlockRegistrationData;", "registerFlock-gIAlu-s", "(Lcom/example/rooster/feature/farm/domain/model/FlockRegistrationData;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "feature-farm_debug"})
public abstract interface FarmRepository {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<kotlin.Result<com.example.rooster.feature.farm.domain.model.Flock>> getFlockById(@org.jetbrains.annotations.NotNull()
    java.lang.String id);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>>> getFlocksByType(@org.jetbrains.annotations.NotNull()
    java.lang.String type);
}