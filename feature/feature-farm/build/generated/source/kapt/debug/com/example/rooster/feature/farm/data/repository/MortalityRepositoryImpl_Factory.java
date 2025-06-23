package com.example.rooster.feature.farm.data.repository;

import com.example.rooster.feature.farm.data.local.MortalityDao;
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
public final class MortalityRepositoryImpl_Factory implements Factory<MortalityRepositoryImpl> {
  private final Provider<MortalityDao> daoProvider;

  public MortalityRepositoryImpl_Factory(Provider<MortalityDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public MortalityRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static MortalityRepositoryImpl_Factory create(Provider<MortalityDao> daoProvider) {
    return new MortalityRepositoryImpl_Factory(daoProvider);
  }

  public static MortalityRepositoryImpl newInstance(MortalityDao dao) {
    return new MortalityRepositoryImpl(dao);
  }
}
