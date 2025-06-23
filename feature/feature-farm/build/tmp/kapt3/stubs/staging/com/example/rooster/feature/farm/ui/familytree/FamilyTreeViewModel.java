package com.example.rooster.feature.farm.ui.familytree;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010R\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/example/rooster/feature/farm/ui/familytree/FamilyTreeViewModel;", "Landroidx/lifecycle/ViewModel;", "getFamilyTree", "Lcom/example/rooster/feature/farm/domain/usecase/GetFamilyTreeUseCase;", "(Lcom/example/rooster/feature/farm/domain/usecase/GetFamilyTreeUseCase;)V", "_ancestors", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Lcom/example/rooster/feature/farm/domain/model/Flock;", "ancestors", "Lkotlinx/coroutines/flow/StateFlow;", "getAncestors", "()Lkotlinx/coroutines/flow/StateFlow;", "loadFamilyTree", "", "fowlId", "", "feature-farm_staging"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class FamilyTreeViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.usecase.GetFamilyTreeUseCase getFamilyTree = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>> _ancestors = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>> ancestors = null;
    
    @javax.inject.Inject()
    public FamilyTreeViewModel(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.usecase.GetFamilyTreeUseCase getFamilyTree) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.Flock>> getAncestors() {
        return null;
    }
    
    public final void loadFamilyTree(@org.jetbrains.annotations.NotNull()
    java.lang.String fowlId) {
    }
}