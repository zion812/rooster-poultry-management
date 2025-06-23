package com.example.rooster.feature.farm.domain.usecase;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u001b\u0010\u0002\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050\u00040\u0003H\u00a6\u0002\u00a8\u0006\u0007"}, d2 = {"Lcom/example/rooster/feature/farm/domain/usecase/GetAllSensorDataUseCase;", "", "invoke", "Lkotlinx/coroutines/flow/Flow;", "Lkotlin/Result;", "", "Lcom/example/rooster/feature/farm/domain/model/SensorData;", "feature-farm_debug"})
public abstract interface GetAllSensorDataUseCase {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.List<com.example.rooster.feature.farm.domain.model.SensorData>>> invoke();
}