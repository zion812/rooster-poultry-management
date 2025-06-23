package com.example.rooster.feature.farm.domain.usecase;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\bf\u0018\u00002\u00020\u0001J\u001d\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a6\u0002\u00a8\u0006\b"}, d2 = {"Lcom/example/rooster/feature/farm/domain/usecase/GetFarmDetailsUseCase;", "", "invoke", "Lkotlinx/coroutines/flow/Flow;", "Lkotlin/Result;", "Lcom/example/rooster/feature/farm/domain/model/Flock;", "farmId", "", "feature-farm_release"})
public abstract interface GetFarmDetailsUseCase {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<kotlin.Result<com.example.rooster.feature.farm.domain.model.Flock>> invoke(@org.jetbrains.annotations.NotNull()
    java.lang.String farmId);
}