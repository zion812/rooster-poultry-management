package com.example.rooster.feature.farm.domain.usecase;

import com.example.rooster.feature.farm.data.repository.VaccinationRepository;
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
public final class DeleteVaccinationRecordUseCaseImpl_Factory implements Factory<DeleteVaccinationRecordUseCaseImpl> {
  private final Provider<VaccinationRepository> repositoryProvider;

  public DeleteVaccinationRecordUseCaseImpl_Factory(
      Provider<VaccinationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public DeleteVaccinationRecordUseCaseImpl get() {
    return newInstance(repositoryProvider.get());
  }

  public static DeleteVaccinationRecordUseCaseImpl_Factory create(
      Provider<VaccinationRepository> repositoryProvider) {
    return new DeleteVaccinationRecordUseCaseImpl_Factory(repositoryProvider);
  }

  public static DeleteVaccinationRecordUseCaseImpl newInstance(VaccinationRepository repository) {
    return new DeleteVaccinationRecordUseCaseImpl(repository);
  }
}
