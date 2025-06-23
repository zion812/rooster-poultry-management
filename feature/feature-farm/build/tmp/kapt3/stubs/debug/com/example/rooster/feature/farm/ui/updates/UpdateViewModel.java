package com.example.rooster.feature.farm.ui.updates;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ0\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u000b2\b\u0010\u001d\u001a\u0004\u0018\u00010\u000bJ\u000e\u0010\u001e\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u000bJ\u0016\u0010\u001f\u001a\u00020\u00162\u0006\u0010 \u001a\u00020\u000b2\u0006\u0010\u0017\u001a\u00020\u000bR\u0016\u0010\t\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u000f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0012R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006!"}, d2 = {"Lcom/example/rooster/feature/farm/ui/updates/UpdateViewModel;", "Landroidx/lifecycle/ViewModel;", "getUpdateRecords", "Lcom/example/rooster/feature/farm/domain/usecase/GetUpdateRecordsUseCase;", "saveUpdateRecords", "Lcom/example/rooster/feature/farm/domain/usecase/SaveUpdateRecordsUseCase;", "deleteUpdateRecord", "Lcom/example/rooster/feature/farm/domain/usecase/DeleteUpdateRecordUseCase;", "(Lcom/example/rooster/feature/farm/domain/usecase/GetUpdateRecordsUseCase;Lcom/example/rooster/feature/farm/domain/usecase/SaveUpdateRecordsUseCase;Lcom/example/rooster/feature/farm/domain/usecase/DeleteUpdateRecordUseCase;)V", "_error", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_records", "", "Lcom/example/rooster/feature/farm/domain/model/UpdateRecord;", "error", "Lkotlinx/coroutines/flow/StateFlow;", "getError", "()Lkotlinx/coroutines/flow/StateFlow;", "records", "getRecords", "addUpdate", "", "fowlId", "type", "Lcom/example/rooster/feature/farm/domain/model/UpdateType;", "date", "Ljava/util/Date;", "details", "attachmentUrl", "loadUpdates", "removeUpdate", "id", "feature-farm_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class UpdateViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.usecase.GetUpdateRecordsUseCase getUpdateRecords = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.usecase.SaveUpdateRecordsUseCase saveUpdateRecords = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.usecase.DeleteUpdateRecordUseCase deleteUpdateRecord = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.UpdateRecord>> _records = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.UpdateRecord>> records = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _error = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> error = null;
    
    @javax.inject.Inject()
    public UpdateViewModel(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.usecase.GetUpdateRecordsUseCase getUpdateRecords, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.usecase.SaveUpdateRecordsUseCase saveUpdateRecords, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.usecase.DeleteUpdateRecordUseCase deleteUpdateRecord) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.UpdateRecord>> getRecords() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getError() {
        return null;
    }
    
    public final void loadUpdates(@org.jetbrains.annotations.NotNull()
    java.lang.String fowlId) {
    }
    
    public final void addUpdate(@org.jetbrains.annotations.NotNull()
    java.lang.String fowlId, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.UpdateType type, @org.jetbrains.annotations.NotNull()
    java.util.Date date, @org.jetbrains.annotations.NotNull()
    java.lang.String details, @org.jetbrains.annotations.Nullable()
    java.lang.String attachmentUrl) {
    }
    
    public final void removeUpdate(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String fowlId) {
    }
}