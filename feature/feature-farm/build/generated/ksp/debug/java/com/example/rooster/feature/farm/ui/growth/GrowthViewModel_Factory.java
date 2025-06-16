package com.example.rooster.feature.farm.ui.growth;

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
public final class GrowthViewModel_Factory implements Factory<GrowthViewModel> {
  private final Provider<GetAllSensorDataUseCase> getAllSensorDataProvider;

  public GrowthViewModel_Factory(Provider<GetAllSensorDataUseCase> getAllSensorDataProvider) {
    this.getAllSensorDataProvider = getAllSensorDataProvider;
  }

  @Override
  public GrowthViewModel get() {
    return newInstance(getAllSensorDataProvider.get());
  }

  public static GrowthViewModel_Factory create(
      Provider<GetAllSensorDataUseCase> getAllSensorDataProvider) {
    return new GrowthViewModel_Factory(getAllSensorDataProvider);
  }

  public static GrowthViewModel newInstance(GetAllSensorDataUseCase getAllSensorData) {
    return new GrowthViewModel(getAllSensorData);
  }
}
