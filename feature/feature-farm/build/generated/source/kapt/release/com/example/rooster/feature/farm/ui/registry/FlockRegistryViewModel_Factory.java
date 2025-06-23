package com.example.rooster.feature.farm.ui.registry;

import com.example.rooster.feature.farm.domain.usecase.RegisterFlockUseCase;
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
public final class FlockRegistryViewModel_Factory implements Factory<FlockRegistryViewModel> {
  private final Provider<RegisterFlockUseCase> registerFlockUseCaseProvider;

  public FlockRegistryViewModel_Factory(
      Provider<RegisterFlockUseCase> registerFlockUseCaseProvider) {
    this.registerFlockUseCaseProvider = registerFlockUseCaseProvider;
  }

  @Override
  public FlockRegistryViewModel get() {
    return newInstance(registerFlockUseCaseProvider.get());
  }

  public static FlockRegistryViewModel_Factory create(
      Provider<RegisterFlockUseCase> registerFlockUseCaseProvider) {
    return new FlockRegistryViewModel_Factory(registerFlockUseCaseProvider);
  }

  public static FlockRegistryViewModel newInstance(RegisterFlockUseCase registerFlockUseCase) {
    return new FlockRegistryViewModel(registerFlockUseCase);
  }
}
