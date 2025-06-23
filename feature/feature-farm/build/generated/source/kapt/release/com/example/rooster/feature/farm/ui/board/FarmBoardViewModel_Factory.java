package com.example.rooster.feature.farm.ui.board;

import com.example.rooster.feature.farm.domain.usecase.GetFlocksByTypeUseCase;
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
public final class FarmBoardViewModel_Factory implements Factory<FarmBoardViewModel> {
  private final Provider<GetFlocksByTypeUseCase> getFlocksByTypeProvider;

  public FarmBoardViewModel_Factory(Provider<GetFlocksByTypeUseCase> getFlocksByTypeProvider) {
    this.getFlocksByTypeProvider = getFlocksByTypeProvider;
  }

  @Override
  public FarmBoardViewModel get() {
    return newInstance(getFlocksByTypeProvider.get());
  }

  public static FarmBoardViewModel_Factory create(
      Provider<GetFlocksByTypeUseCase> getFlocksByTypeProvider) {
    return new FarmBoardViewModel_Factory(getFlocksByTypeProvider);
  }

  public static FarmBoardViewModel newInstance(GetFlocksByTypeUseCase getFlocksByType) {
    return new FarmBoardViewModel(getFlocksByType);
  }
}
