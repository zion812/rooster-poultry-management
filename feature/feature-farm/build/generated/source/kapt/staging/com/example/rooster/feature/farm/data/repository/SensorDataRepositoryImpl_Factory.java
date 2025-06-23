package com.example.rooster.feature.farm.data.repository;

import com.example.rooster.feature.farm.data.local.SensorDataDao;
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
public final class SensorDataRepositoryImpl_Factory implements Factory<SensorDataRepositoryImpl> {
  private final Provider<SensorDataDao> daoProvider;

  public SensorDataRepositoryImpl_Factory(Provider<SensorDataDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public SensorDataRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static SensorDataRepositoryImpl_Factory create(Provider<SensorDataDao> daoProvider) {
    return new SensorDataRepositoryImpl_Factory(daoProvider);
  }

  public static SensorDataRepositoryImpl newInstance(SensorDataDao dao) {
    return new SensorDataRepositoryImpl(dao);
  }
}
