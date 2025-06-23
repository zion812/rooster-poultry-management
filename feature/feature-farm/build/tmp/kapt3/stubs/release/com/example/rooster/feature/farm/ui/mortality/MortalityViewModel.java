package com.example.rooster.feature.farm.ui.mortality;

/**
 * ViewModel for managing mortality records following MVVM architecture.
 * Handles complex business logic, error states, and data persistence
 * with proper dependency injection and clean architecture patterns.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ&\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u000b2\u0006\u0010\u001c\u001a\u00020\u000b2\u0006\u0010\u001d\u001a\u00020\u000bJ\u0006\u0010\u001e\u001a\u00020\u0019J\u000e\u0010\u001f\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u000bJ\u0016\u0010 \u001a\u00020\u00192\u0006\u0010!\u001a\u00020\u000b2\u0006\u0010\u001a\u001a\u00020\u000bR\u0016\u0010\t\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000f0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0011\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\r0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u001d\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000f0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0014R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Lcom/example/rooster/feature/farm/ui/mortality/MortalityViewModel;", "Landroidx/lifecycle/ViewModel;", "getMortalityRecordsUseCase", "Lcom/example/rooster/feature/farm/domain/usecase/GetMortalityRecordsUseCase;", "saveMortalityRecordsUseCase", "Lcom/example/rooster/feature/farm/domain/usecase/SaveMortalityRecordsUseCase;", "deleteMortalityRecordUseCase", "Lcom/example/rooster/feature/farm/domain/usecase/DeleteMortalityRecordUseCase;", "(Lcom/example/rooster/feature/farm/domain/usecase/GetMortalityRecordsUseCase;Lcom/example/rooster/feature/farm/domain/usecase/SaveMortalityRecordsUseCase;Lcom/example/rooster/feature/farm/domain/usecase/DeleteMortalityRecordUseCase;)V", "_error", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_isLoading", "", "_records", "", "Lcom/example/rooster/feature/farm/domain/model/MortalityRecord;", "error", "Lkotlinx/coroutines/flow/StateFlow;", "getError", "()Lkotlinx/coroutines/flow/StateFlow;", "isLoading", "records", "getRecords", "addRecord", "", "fowlId", "cause", "description", "attachment", "clearError", "loadMortality", "removeRecord", "recordId", "feature-farm_release"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class MortalityViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.usecase.GetMortalityRecordsUseCase getMortalityRecordsUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.usecase.SaveMortalityRecordsUseCase saveMortalityRecordsUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.usecase.DeleteMortalityRecordUseCase deleteMortalityRecordUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.MortalityRecord>> _records = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.MortalityRecord>> records = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _error = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> error = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading = null;
    
    @javax.inject.Inject()
    public MortalityViewModel(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.usecase.GetMortalityRecordsUseCase getMortalityRecordsUseCase, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.usecase.SaveMortalityRecordsUseCase saveMortalityRecordsUseCase, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.usecase.DeleteMortalityRecordUseCase deleteMortalityRecordUseCase) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.MortalityRecord>> getRecords() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getError() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading() {
        return null;
    }
    
    /**
     * Load mortality records for a specific fowl with comprehensive error handling
     */
    public final void loadMortality(@org.jetbrains.annotations.NotNull()
    java.lang.String fowlId) {
    }
    
    /**
     * Add new mortality record with validation and error handling
     */
    public final void addRecord(@org.jetbrains.annotations.NotNull()
    java.lang.String fowlId, @org.jetbrains.annotations.NotNull()
    java.lang.String cause, @org.jetbrains.annotations.NotNull()
    java.lang.String description, @org.jetbrains.annotations.NotNull()
    java.lang.String attachment) {
    }
    
    /**
     * Remove mortality record with confirmation and error handling
     */
    public final void removeRecord(@org.jetbrains.annotations.NotNull()
    java.lang.String recordId, @org.jetbrains.annotations.NotNull()
    java.lang.String fowlId) {
    }
    
    /**
     * Clear current error state
     */
    public final void clearError() {
    }
}