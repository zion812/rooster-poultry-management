package com.example.rooster.feature.farm.data.repository;

import com.example.rooster.feature.farm.data.local.VaccinationDao;
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
public final class VaccinationRepositoryImpl_Factory implements Factory<VaccinationRepositoryImpl> {
  private final Provider<VaccinationDao> daoProvider;

  public VaccinationRepositoryImpl_Factory(Provider<VaccinationDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public VaccinationRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static VaccinationRepositoryImpl_Factory create(Provider<VaccinationDao> daoProvider) {
    return new VaccinationRepositoryImpl_Factory(daoProvider);
  }

  public static VaccinationRepositoryImpl newInstance(VaccinationDao dao) {
    return new VaccinationRepositoryImpl(dao);
  }
}
