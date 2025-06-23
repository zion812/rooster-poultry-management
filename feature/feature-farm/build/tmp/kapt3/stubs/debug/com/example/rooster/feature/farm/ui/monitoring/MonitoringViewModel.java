package com.example.rooster.feature.farm.ui.monitoring;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\r\u001a\u00020\u000eR\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u000f"}, d2 = {"Lcom/example/rooster/feature/farm/ui/monitoring/MonitoringViewModel;", "Landroidx/lifecycle/ViewModel;", "getAllSensorData", "Lcom/example/rooster/feature/farm/domain/usecase/GetAllSensorDataUseCase;", "(Lcom/example/rooster/feature/farm/domain/usecase/GetAllSensorDataUseCase;)V", "_sensorData", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Lcom/example/rooster/feature/farm/domain/model/SensorData;", "sensorData", "Lkotlinx/coroutines/flow/StateFlow;", "getSensorData", "()Lkotlinx/coroutines/flow/StateFlow;", "loadSensorData", "", "feature-farm_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class MonitoringViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.usecase.GetAllSensorDataUseCase getAllSensorData = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.SensorData>> _sensorData = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.SensorData>> sensorData = null;
    
    @javax.inject.Inject()
    public MonitoringViewModel(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.usecase.GetAllSensorDataUseCase getAllSensorData) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.rooster.feature.farm.domain.model.SensorData>> getSensorData() {
        return null;
    }
    
    public final void loadSensorData() {
    }
}