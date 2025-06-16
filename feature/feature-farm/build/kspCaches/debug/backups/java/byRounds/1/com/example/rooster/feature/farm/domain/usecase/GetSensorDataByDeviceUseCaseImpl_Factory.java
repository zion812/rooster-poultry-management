package com.example.rooster.feature.farm.domain.usecase;

import com.example.rooster.feature.farm.data.repository.SensorDataRepository;
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
public final class GetSensorDataByDeviceUseCaseImpl_Factory implements Factory<GetSensorDataByDeviceUseCaseImpl> {
  private final Provider<SensorDataRepository> repositoryProvider;

  public GetSensorDataByDeviceUseCaseImpl_Factory(
      Provider<SensorDataRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetSensorDataByDeviceUseCaseImpl get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetSensorDataByDeviceUseCaseImpl_Factory create(
      Provider<SensorDataRepository> repositoryProvider) {
    return new GetSensorDataByDeviceUseCaseImpl_Factory(repositoryProvider);
  }

  public static GetSensorDataByDeviceUseCaseImpl newInstance(SensorDataRepository repository) {
    return new GetSensorDataByDeviceUseCaseImpl(repository);
  }
}
