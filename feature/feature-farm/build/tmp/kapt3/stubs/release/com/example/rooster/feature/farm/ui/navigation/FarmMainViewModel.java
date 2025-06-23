package com.example.rooster.feature.farm.ui.navigation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00070\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\t0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u000b0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000f\u00a8\u0006\u0017"}, d2 = {"Lcom/example/rooster/feature/farm/ui/navigation/FarmMainViewModel;", "Landroidx/lifecycle/ViewModel;", "getFarmDetailsUseCase", "Lcom/example/rooster/feature/farm/domain/usecase/GetFarmDetailsUseCase;", "(Lcom/example/rooster/feature/farm/domain/usecase/GetFarmDetailsUseCase;)V", "_farmState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/example/rooster/feature/farm/ui/navigation/FarmState;", "_flockStats", "Lcom/example/rooster/feature/farm/ui/navigation/FlockStats;", "_isRefreshing", "", "farmState", "Lkotlinx/coroutines/flow/StateFlow;", "getFarmState", "()Lkotlinx/coroutines/flow/StateFlow;", "flockStats", "getFlockStats", "isRefreshing", "loadFarmDetails", "", "farmId", "", "feature-farm_release"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class FarmMainViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.usecase.GetFarmDetailsUseCase getFarmDetailsUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.rooster.feature.farm.ui.navigation.FarmState> _farmState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.rooster.feature.farm.ui.navigation.FarmState> farmState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.rooster.feature.farm.ui.navigation.FlockStats> _flockStats = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.rooster.feature.farm.ui.navigation.FlockStats> flockStats = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isRefreshing = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isRefreshing = null;
    
    @javax.inject.Inject()
    public FarmMainViewModel(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.usecase.GetFarmDetailsUseCase getFarmDetailsUseCase) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.rooster.feature.farm.ui.navigation.FarmState> getFarmState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.rooster.feature.farm.ui.navigation.FlockStats> getFlockStats() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isRefreshing() {
        return null;
    }
    
    /**
     * Stub: no-op
     */
    public final void loadFarmDetails(@org.jetbrains.annotations.NotNull()
    java.lang.String farmId) {
    }
}