package com.example.rooster.feature.farm.ui.board;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019R\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u001d\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u001d\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000f\u00a8\u0006\u001a"}, d2 = {"Lcom/example/rooster/feature/farm/ui/board/FarmBoardViewModel;", "Landroidx/lifecycle/ViewModel;", "getFlocksByType", "Lcom/example/rooster/feature/farm/domain/usecase/GetFlocksByTypeUseCase;", "(Lcom/example/rooster/feature/farm/domain/usecase/GetFlocksByTypeUseCase;)V", "_breeders", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Lcom/example/rooster/feature/farm/domain/model/Flock;", "_chicks", "_fowls", "_hens", "breeders", "Lkotlinx/coroutines/flow/StateFlow;", "getBreeders", "()Lkotlinx/coroutines/flow/StateFlow;", "chicks", "getChicks", "fowls", "getFowls", "hens", "getHens", "loadBoard", "", "farmId", "", "feature-farm_release"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class FarmBoardViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.usecase.GetFlocksByTypeUseCase getFlocksByType = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>> _fowls = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>> fowls = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>> _hens = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>> hens = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>> _breeders = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>> breeders = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>> _chicks = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>> chicks = null;
    
    @javax.inject.Inject()
    public FarmBoardViewModel(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.usecase.GetFlocksByTypeUseCase getFlocksByType) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>> getFowls() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>> getHens() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>> getBreeders() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>> getChicks() {
        return null;
    }
    
    public final void loadBoard(@org.jetbrains.annotations.NotNull()
    java.lang.String farmId) {
    }
}