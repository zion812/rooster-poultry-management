package com.example.rooster.feature.farm.ui.familytree;

import com.example.rooster.feature.farm.domain.usecase.GetFamilyTreeUseCase;
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
public final class FamilyTreeViewModel_Factory implements Factory<FamilyTreeViewModel> {
  private final Provider<GetFamilyTreeUseCase> getFamilyTreeProvider;

  public FamilyTreeViewModel_Factory(Provider<GetFamilyTreeUseCase> getFamilyTreeProvider) {
    this.getFamilyTreeProvider = getFamilyTreeProvider;
  }

  @Override
  public FamilyTreeViewModel get() {
    return newInstance(getFamilyTreeProvider.get());
  }

  public static FamilyTreeViewModel_Factory create(
      Provider<GetFamilyTreeUseCase> getFamilyTreeProvider) {
    return new FamilyTreeViewModel_Factory(getFamilyTreeProvider);
  }

  public static FamilyTreeViewModel newInstance(GetFamilyTreeUseCase getFamilyTree) {
    return new FamilyTreeViewModel(getFamilyTree);
  }
}
