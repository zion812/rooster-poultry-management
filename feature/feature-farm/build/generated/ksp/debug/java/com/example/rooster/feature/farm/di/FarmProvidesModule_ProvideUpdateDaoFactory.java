package com.example.rooster.feature.farm.di;

import com.example.rooster.feature.farm.data.local.FarmDatabase;
import com.example.rooster.feature.farm.data.local.UpdateDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class FarmProvidesModule_ProvideUpdateDaoFactory implements Factory<UpdateDao> {
  private final Provider<FarmDatabase> dbProvider;

  public FarmProvidesModule_ProvideUpdateDaoFactory(Provider<FarmDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public UpdateDao get() {
    return provideUpdateDao(dbProvider.get());
  }

  public static FarmProvidesModule_ProvideUpdateDaoFactory create(
      Provider<FarmDatabase> dbProvider) {
    return new FarmProvidesModule_ProvideUpdateDaoFactory(dbProvider);
  }

  public static UpdateDao provideUpdateDao(FarmDatabase db) {
    return Preconditions.checkNotNullFromProvides(FarmProvidesModule.INSTANCE.provideUpdateDao(db));
  }
}
