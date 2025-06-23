package com.example.rooster.feature.farm.ui.growth;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0012\u001a\u00020\u0013R2\u0010\u0005\u001a&\u0012\"\u0012 \u0012\u0004\u0012\u00020\b\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u000b0\n0\t0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R5\u0010\u000e\u001a&\u0012\"\u0012 \u0012\u0004\u0012\u00020\b\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u000b0\n0\t0\u00070\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006\u0014"}, d2 = {"Lcom/example/rooster/feature/farm/ui/growth/GrowthViewModel;", "Landroidx/lifecycle/ViewModel;", "getAllSensorData", "Lcom/example/rooster/feature/farm/domain/usecase/GetAllSensorDataUseCase;", "(Lcom/example/rooster/feature/farm/domain/usecase/GetAllSensorDataUseCase;)V", "_growthData", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "", "", "Lkotlin/Pair;", "", "dateFormat", "Ljava/text/SimpleDateFormat;", "growthData", "Lkotlinx/coroutines/flow/StateFlow;", "getGrowthData", "()Lkotlinx/coroutines/flow/StateFlow;", "loadGrowthMetrics", "", "feature-farm_staging"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class GrowthViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.usecase.GetAllSensorDataUseCase getAllSensorData = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.Map<java.lang.String, java.util.List<kotlin.Pair<java.lang.String, java.lang.Double>>>> _growthData = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.Map<java.lang.String, java.util.List<kotlin.Pair<java.lang.String, java.lang.Double>>>> growthData = null;
    @org.jetbrains.annotations.NotNull()
    private final java.text.SimpleDateFormat dateFormat = null;
    
    @javax.inject.Inject()
    public GrowthViewModel(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.usecase.GetAllSensorDataUseCase getAllSensorData) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.Map<java.lang.String, java.util.List<kotlin.Pair<java.lang.String, java.lang.Double>>>> getGrowthData() {
        return null;
    }
    
    public final void loadGrowthMetrics() {
    }
}