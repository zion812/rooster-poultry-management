package com.example.rooster.feature.farm.ui.mortality;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000:\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a.\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0003\u001a>\u0010\n\u001a\u00020\u00012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u001e\u0010\f\u001a\u001a\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00010\r2\u0006\u0010\u0006\u001a\u00020\u0007H\u0003\u001a<\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u000e2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00010\u00132\b\b\u0002\u0010\u0014\u001a\u00020\u0015H\u0007\u00a8\u0006\u0016"}, d2 = {"MortalityRecordCard", "", "record", "Lcom/example/rooster/feature/farm/domain/model/MortalityRecord;", "onDelete", "Lkotlin/Function0;", "isLoading", "", "sdf", "Ljava/text/SimpleDateFormat;", "MortalityRecordDialog", "onDismiss", "onSave", "Lkotlin/Function3;", "", "MortalityScreen", "fowlId", "onBack", "onError", "Lkotlin/Function1;", "viewModel", "Lcom/example/rooster/feature/farm/ui/mortality/MortalityViewModel;", "feature-farm_debug"})
public final class MortalityScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void MortalityScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String fowlId, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onError, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.ui.mortality.MortalityViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MortalityRecordCard(com.example.rooster.feature.farm.domain.model.MortalityRecord record, kotlin.jvm.functions.Function0<kotlin.Unit> onDelete, boolean isLoading, java.text.SimpleDateFormat sdf) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MortalityRecordDialog(kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, kotlin.jvm.functions.Function3<? super java.lang.String, ? super java.lang.String, ? super java.lang.String, kotlin.Unit> onSave, boolean isLoading) {
    }
}