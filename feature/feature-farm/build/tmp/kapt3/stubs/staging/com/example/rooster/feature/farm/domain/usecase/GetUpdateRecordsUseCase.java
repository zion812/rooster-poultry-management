package com.example.rooster.feature.farm.domain.usecase;

/**
 * Fetches update records for a given fowl ID
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\bf\u0018\u00002\u00020\u0001J#\u0010\u0002\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050\u00040\u00032\u0006\u0010\u0007\u001a\u00020\bH\u00a6\u0002\u00a8\u0006\t"}, d2 = {"Lcom/example/rooster/feature/farm/domain/usecase/GetUpdateRecordsUseCase;", "", "invoke", "Lkotlinx/coroutines/flow/Flow;", "Lkotlin/Result;", "", "Lcom/example/rooster/feature/farm/domain/model/UpdateRecord;", "fowlId", "", "feature-farm_staging"})
public abstract interface GetUpdateRecordsUseCase {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.List<com.example.rooster.feature.farm.domain.model.UpdateRecord>>> invoke(@org.jetbrains.annotations.NotNull()
    java.lang.String fowlId);
}