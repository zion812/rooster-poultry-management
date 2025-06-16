package com.example.rooster.feature.farm.ui.details;

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
public final class FarmDetailsViewModel_Factory implements Factory<FarmDetailsViewModel> {
  private final Provider<GetFarmDetailsUseCase> getFarmDetailsProvider;

  public FarmDetailsViewModel_Factory(Provider<GetFarmDetailsUseCase> getFarmDetailsProvider) {
    this.getFarmDetailsProvider = getFarmDetailsProvider;
  }

  @Override
  public FarmDetailsViewModel get() {
    return newInstance(getFarmDetailsProvider.get());
  }

  public static FarmDetailsViewModel_Factory create(
      Provider<GetFarmDetailsUseCase> getFarmDetailsProvider) {
    return new FarmDetailsViewModel_Factory(getFarmDetailsProvider);
  }

  public static FarmDetailsViewModel newInstance(GetFarmDetailsUseCase getFarmDetails) {
    return new FarmDetailsViewModel(getFarmDetails);
  }
}
