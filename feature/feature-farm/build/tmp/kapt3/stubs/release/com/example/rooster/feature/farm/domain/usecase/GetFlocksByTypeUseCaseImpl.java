package com.example.rooster.feature.farm.domain.usecase;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J#\u0010\u0005\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u00070\u00062\u0006\u0010\n\u001a\u00020\u000bH\u0096\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/example/rooster/feature/farm/domain/usecase/GetFlocksByTypeUseCaseImpl;", "Lcom/example/rooster/feature/farm/domain/usecase/GetFlocksByTypeUseCase;", "repository", "Lcom/example/rooster/feature/farm/data/repository/FarmRepository;", "(Lcom/example/rooster/feature/farm/data/repository/FarmRepository;)V", "invoke", "Lkotlinx/coroutines/flow/Flow;", "Lkotlin/Result;", "", "Lcom/example/rooster/feature/farm/domain/model/Flock;", "type", "", "feature-farm_release"})
public final class GetFlocksByTypeUseCaseImpl implements com.example.rooster.feature.farm.domain.usecase.GetFlocksByTypeUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.data.repository.FarmRepository repository = null;
    
    public GetFlocksByTypeUseCaseImpl(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.data.repository.FarmRepository repository) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<kotlin.Result<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>>> invoke(@org.jetbrains.annotations.NotNull()
    java.lang.String type) {
        return null;
    }
}