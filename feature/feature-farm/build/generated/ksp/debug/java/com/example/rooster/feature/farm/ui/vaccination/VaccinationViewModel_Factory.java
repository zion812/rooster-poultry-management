package com.example.rooster.feature.farm.ui.vaccination;

import com.example.rooster.feature.farm.domain.usecase.GetVaccinationRecordsUseCase;
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
public final class VaccinationViewModel_Factory implements Factory<VaccinationViewModel> {
  private final Provider<GetVaccinationRecordsUseCase> getVaccinationRecordsProvider;

  public VaccinationViewModel_Factory(
      Provider<GetVaccinationRecordsUseCase> getVaccinationRecordsProvider) {
    this.getVaccinationRecordsProvider = getVaccinationRecordsProvider;
  }

  @Override
  public VaccinationViewModel get() {
    return newInstance(getVaccinationRecordsProvider.get());
  }

  public static VaccinationViewModel_Factory create(
      Provider<GetVaccinationRecordsUseCase> getVaccinationRecordsProvider) {
    return new VaccinationViewModel_Factory(getVaccinationRecordsProvider);
  }

  public static VaccinationViewModel newInstance(
      GetVaccinationRecordsUseCase getVaccinationRecords) {
    return new VaccinationViewModel(getVaccinationRecords);
  }
}
