package com.example.rooster.feature.farm.ui.mortality;

import com.example.rooster.feature.farm.domain.usecase.DeleteMortalityRecordUseCase;
import com.example.rooster.feature.farm.domain.usecase.GetMortalityRecordsUseCase;
import com.example.rooster.feature.farm.domain.usecase.SaveMortalityRecordsUseCase;
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
public final class MortalityViewModel_Factory implements Factory<MortalityViewModel> {
  private final Provider<GetMortalityRecordsUseCase> getMortalityRecordsUseCaseProvider;

  private final Provider<SaveMortalityRecordsUseCase> saveMortalityRecordsUseCaseProvider;

  private final Provider<DeleteMortalityRecordUseCase> deleteMortalityRecordUseCaseProvider;

  public MortalityViewModel_Factory(
      Provider<GetMortalityRecordsUseCase> getMortalityRecordsUseCaseProvider,
      Provider<SaveMortalityRecordsUseCase> saveMortalityRecordsUseCaseProvider,
      Provider<DeleteMortalityRecordUseCase> deleteMortalityRecordUseCaseProvider) {
    this.getMortalityRecordsUseCaseProvider = getMortalityRecordsUseCaseProvider;
    this.saveMortalityRecordsUseCaseProvider = saveMortalityRecordsUseCaseProvider;
    this.deleteMortalityRecordUseCaseProvider = deleteMortalityRecordUseCaseProvider;
  }

  @Override
  public MortalityViewModel get() {
    return newInstance(getMortalityRecordsUseCaseProvider.get(), saveMortalityRecordsUseCaseProvider.get(), deleteMortalityRecordUseCaseProvider.get());
  }

  public static MortalityViewModel_Factory create(
      Provider<GetMortalityRecordsUseCase> getMortalityRecordsUseCaseProvider,
      Provider<SaveMortalityRecordsUseCase> saveMortalityRecordsUseCaseProvider,
      Provider<DeleteMortalityRecordUseCase> deleteMortalityRecordUseCaseProvider) {
    return new MortalityViewModel_Factory(getMortalityRecordsUseCaseProvider, saveMortalityRecordsUseCaseProvider, deleteMortalityRecordUseCaseProvider);
  }

  public static MortalityViewModel newInstance(
      GetMortalityRecordsUseCase getMortalityRecordsUseCase,
      SaveMortalityRecordsUseCase saveMortalityRecordsUseCase,
      DeleteMortalityRecordUseCase deleteMortalityRecordUseCase) {
    return new MortalityViewModel(getMortalityRecordsUseCase, saveMortalityRecordsUseCase, deleteMortalityRecordUseCase);
  }
}
