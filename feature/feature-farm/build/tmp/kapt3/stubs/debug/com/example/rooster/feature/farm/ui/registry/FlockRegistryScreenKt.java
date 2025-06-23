package com.example.rooster.feature.farm.ui.registry;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000F\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a&\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a2\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\b2\u0018\u0010\t\u001a\u0014\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\n2\u0006\u0010\f\u001a\u00020\rH\u0003\u001a<\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u000b2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u00112\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\u0013\u001a\u00020\u0014H\u0007\u001a&\u0010\u0015\u001a\u00020\u00012\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0017\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u00a8\u0006\u0019"}, d2 = {"AgeGroupSelector", "", "selectedAgeGroup", "Lcom/example/rooster/feature/farm/domain/model/AgeGroup;", "onAgeGroupSelected", "Lkotlin/Function1;", "BasicRegistrationForm", "uiState", "Lcom/example/rooster/feature/farm/ui/registry/FlockRegistryUiState;", "onFieldUpdate", "Lkotlin/Function2;", "", "dateFormatter", "Ljava/text/SimpleDateFormat;", "FlockRegistryScreen", "farmId", "onBack", "Lkotlin/Function0;", "onError", "viewModel", "Lcom/example/rooster/feature/farm/ui/registry/FlockRegistryViewModel;", "RegistryTypeSelector", "selectedType", "Lcom/example/rooster/feature/farm/domain/model/RegistryType;", "onTypeSelected", "feature-farm_debug"})
public final class FlockRegistryScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void FlockRegistryScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String farmId, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onError, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.ui.registry.FlockRegistryViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void RegistryTypeSelector(com.example.rooster.feature.farm.domain.model.RegistryType selectedType, kotlin.jvm.functions.Function1<? super com.example.rooster.feature.farm.domain.model.RegistryType, kotlin.Unit> onTypeSelected) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AgeGroupSelector(com.example.rooster.feature.farm.domain.model.AgeGroup selectedAgeGroup, kotlin.jvm.functions.Function1<? super com.example.rooster.feature.farm.domain.model.AgeGroup, kotlin.Unit> onAgeGroupSelected) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void BasicRegistrationForm(com.example.rooster.feature.farm.ui.registry.FlockRegistryUiState uiState, kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.String, kotlin.Unit> onFieldUpdate, java.text.SimpleDateFormat dateFormatter) {
    }
}