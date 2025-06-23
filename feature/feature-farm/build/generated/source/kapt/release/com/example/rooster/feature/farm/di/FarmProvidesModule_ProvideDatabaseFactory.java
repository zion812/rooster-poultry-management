package com.example.rooster.feature.farm.di;

import android.content.Context;
import com.example.rooster.feature.farm.data.local.FarmDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class FarmProvidesModule_ProvideDatabaseFactory implements Factory<FarmDatabase> {
  private final Provider<Context> contextProvider;

  public FarmProvidesModule_ProvideDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public FarmDatabase get() {
    return provideDatabase(contextProvider.get());
  }

  public static FarmProvidesModule_ProvideDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new FarmProvidesModule_ProvideDatabaseFactory(contextProvider);
  }

  public static FarmDatabase provideDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(FarmProvidesModule.INSTANCE.provideDatabase(context));
  }
}
