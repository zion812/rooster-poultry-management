package com.example.rooster.feature.farm.ui.registry;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fJ\u000e\u0010\u0010\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\u000fJ\u000e\u0010\u0012\u001a\u00020\r2\u0006\u0010\u0013\u001a\u00020\u0014J\b\u0010\u0015\u001a\u00020\rH\u0002J\u0016\u0010\u0016\u001a\u00020\r2\u0006\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u0019J\u0016\u0010\u001a\u001a\u00020\r2\u0006\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u000fJ\u000e\u0010\u001c\u001a\u00020\r2\u0006\u0010\u001d\u001a\u00020\u001eR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u001f"}, d2 = {"Lcom/example/rooster/feature/farm/ui/registry/FlockRegistryViewModel;", "Landroidx/lifecycle/ViewModel;", "registerFlockUseCase", "Lcom/example/rooster/feature/farm/domain/usecase/RegisterFlockUseCase;", "(Lcom/example/rooster/feature/farm/domain/usecase/RegisterFlockUseCase;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/example/rooster/feature/farm/ui/registry/FlockRegistryUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "addProofPhoto", "", "photoUrl", "", "submitRegistration", "farmId", "updateAgeGroup", "ageGroup", "Lcom/example/rooster/feature/farm/domain/model/AgeGroup;", "updateCanSubmit", "updateDateField", "fieldName", "date", "Ljava/util/Date;", "updateField", "value", "updateRegistryType", "registryType", "Lcom/example/rooster/feature/farm/domain/model/RegistryType;", "feature-farm_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class FlockRegistryViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.usecase.RegisterFlockUseCase registerFlockUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.rooster.feature.farm.ui.registry.FlockRegistryUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.rooster.feature.farm.ui.registry.FlockRegistryUiState> uiState = null;
    
    @javax.inject.Inject()
    public FlockRegistryViewModel(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.usecase.RegisterFlockUseCase registerFlockUseCase) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.rooster.feature.farm.ui.registry.FlockRegistryUiState> getUiState() {
        return null;
    }
    
    public final void updateRegistryType(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.RegistryType registryType) {
    }
    
    public final void updateAgeGroup(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.AgeGroup ageGroup) {
    }
    
    public final void updateField(@org.jetbrains.annotations.NotNull()
    java.lang.String fieldName, @org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void updateDateField(@org.jetbrains.annotations.NotNull()
    java.lang.String fieldName, @org.jetbrains.annotations.NotNull()
    java.util.Date date) {
    }
    
    public final void addProofPhoto(@org.jetbrains.annotations.NotNull()
    java.lang.String photoUrl) {
    }
    
    public final void submitRegistration(@org.jetbrains.annotations.NotNull()
    java.lang.String farmId) {
    }
    
    private final void updateCanSubmit() {
    }
}