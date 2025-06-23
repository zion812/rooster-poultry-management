package com.example.rooster.feature.farm.ui.details;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0010"}, d2 = {"Lcom/example/rooster/feature/farm/ui/details/FarmDetailsViewModel;", "Landroidx/lifecycle/ViewModel;", "getFarmDetails", "Lcom/example/rooster/feature/farm/domain/usecase/GetFarmDetailsUseCase;", "(Lcom/example/rooster/feature/farm/domain/usecase/GetFarmDetailsUseCase;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/example/rooster/feature/farm/ui/details/FarmDetailsUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "loadDetails", "", "farmId", "", "feature-farm_staging"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class FarmDetailsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.usecase.GetFarmDetailsUseCase getFarmDetails = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.rooster.feature.farm.ui.details.FarmDetailsUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.rooster.feature.farm.ui.details.FarmDetailsUiState> uiState = null;
    
    @javax.inject.Inject()
    public FarmDetailsViewModel(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.usecase.GetFarmDetailsUseCase getFarmDetails) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.rooster.feature.farm.ui.details.FarmDetailsUiState> getUiState() {
        return null;
    }
    
    public final void loadDetails(@org.jetbrains.annotations.NotNull()
    java.lang.String farmId) {
    }
}