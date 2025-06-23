package com.example.rooster.feature.farm.ui.navigation;

import com.example.rooster.feature.farm.domain.usecase.GetFarmDetailsUseCase;
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
public final class FarmMainViewModel_Factory implements Factory<FarmMainViewModel> {
  private final Provider<GetFarmDetailsUseCase> getFarmDetailsUseCaseProvider;

  public FarmMainViewModel_Factory(Provider<GetFarmDetailsUseCase> getFarmDetailsUseCaseProvider) {
    this.getFarmDetailsUseCaseProvider = getFarmDetailsUseCaseProvider;
  }

  @Override
  public FarmMainViewModel get() {
    return newInstance(getFarmDetailsUseCaseProvider.get());
  }

  public static FarmMainViewModel_Factory create(
      Provider<GetFarmDetailsUseCase> getFarmDetailsUseCaseProvider) {
    return new FarmMainViewModel_Factory(getFarmDetailsUseCaseProvider);
  }

  public static FarmMainViewModel newInstance(GetFarmDetailsUseCase getFarmDetailsUseCase) {
    return new FarmMainViewModel(getFarmDetailsUseCase);
  }
}
