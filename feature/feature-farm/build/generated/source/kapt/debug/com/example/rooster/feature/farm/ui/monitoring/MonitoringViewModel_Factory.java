package com.example.rooster.feature.farm.ui.monitoring;

import com.example.rooster.feature.farm.domain.usecase.GetAllSensorDataUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class MonitoringViewModel_Factory implements Factory<MonitoringViewModel> {
  private final Provider<GetAllSensorDataUseCase> getAllSensorDataProvider;

  public MonitoringViewModel_Factory(Provider<GetAllSensorDataUseCase> getAllSensorDataProvider) {
    this.getAllSensorDataProvider = getAllSensorDataProvider;
  }

  @Override
  public MonitoringViewModel get() {
    return newInstance(getAllSensorDataProvider.get());
  }

  public static MonitoringViewModel_Factory create(
      Provider<GetAllSensorDataUseCase> getAllSensorDataProvider) {
    return new MonitoringViewModel_Factory(getAllSensorDataProvider);
  }

  public static MonitoringViewModel newInstance(GetAllSensorDataUseCase getAllSensorData) {
    return new MonitoringViewModel(getAllSensorData);
  }
}
