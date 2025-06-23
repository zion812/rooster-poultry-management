package com.example.rooster.feature.farm.data.repository;

import com.example.rooster.feature.farm.data.local.UpdateDao;
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
public final class UpdateRepositoryImpl_Factory implements Factory<UpdateRepositoryImpl> {
  private final Provider<UpdateDao> daoProvider;

  public UpdateRepositoryImpl_Factory(Provider<UpdateDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public UpdateRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static UpdateRepositoryImpl_Factory create(Provider<UpdateDao> daoProvider) {
    return new UpdateRepositoryImpl_Factory(daoProvider);
  }

  public static UpdateRepositoryImpl newInstance(UpdateDao dao) {
    return new UpdateRepositoryImpl(dao);
  }
}
