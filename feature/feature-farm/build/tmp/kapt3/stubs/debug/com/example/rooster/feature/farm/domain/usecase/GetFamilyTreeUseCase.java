package com.example.rooster.feature.farm.domain.usecase;

/**
 * Returns the lineage (ancestors) for a given fowl, up to a depth of generations.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\bf\u0018\u00002\u00020\u0001J-\u0010\u0002\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050\u00040\u00032\u0006\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\nH\u00a6\u0002\u00a8\u0006\u000b"}, d2 = {"Lcom/example/rooster/feature/farm/domain/usecase/GetFamilyTreeUseCase;", "", "invoke", "Lkotlinx/coroutines/flow/Flow;", "Lkotlin/Result;", "", "Lcom/example/rooster/feature/farm/domain/model/Flock;", "fowlId", "", "generations", "", "feature-farm_debug"})
public abstract interface GetFamilyTreeUseCase {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>>> invoke(@org.jetbrains.annotations.NotNull()
    java.lang.String fowlId, int generations);
    
    /**
     * Returns the lineage (ancestors) for a given fowl, up to a depth of generations.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}