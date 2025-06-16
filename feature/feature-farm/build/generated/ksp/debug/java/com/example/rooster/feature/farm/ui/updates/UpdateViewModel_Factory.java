package com.example.rooster.feature.farm.ui.updates;

import com.example.rooster.feature.farm.domain.usecase.DeleteUpdateRecordUseCase;
import com.example.rooster.feature.farm.domain.usecase.GetUpdateRecordsUseCase;
import com.example.rooster.feature.farm.domain.usecase.SaveUpdateRecordsUseCase;
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
public final class UpdateViewModel_Factory implements Factory<UpdateViewModel> {
  private final Provider<GetUpdateRecordsUseCase> getUpdateRecordsProvider;

  private final Provider<SaveUpdateRecordsUseCase> saveUpdateRecordsProvider;

  private final Provider<DeleteUpdateRecordUseCase> deleteUpdateRecordProvider;

  public UpdateViewModel_Factory(Provider<GetUpdateRecordsUseCase> getUpdateRecordsProvider,
      Provider<SaveUpdateRecordsUseCase> saveUpdateRecordsProvider,
      Provider<DeleteUpdateRecordUseCase> deleteUpdateRecordProvider) {
    this.getUpdateRecordsProvider = getUpdateRecordsProvider;
    this.saveUpdateRecordsProvider = saveUpdateRecordsProvider;
    this.deleteUpdateRecordProvider = deleteUpdateRecordProvider;
  }

  @Override
  public UpdateViewModel get() {
    return newInstance(getUpdateRecordsProvider.get(), saveUpdateRecordsProvider.get(), deleteUpdateRecordProvider.get());
  }

  public static UpdateViewModel_Factory create(
      Provider<GetUpdateRecordsUseCase> getUpdateRecordsProvider,
      Provider<SaveUpdateRecordsUseCase> saveUpdateRecordsProvider,
      Provider<DeleteUpdateRecordUseCase> deleteUpdateRecordProvider) {
    return new UpdateViewModel_Factory(getUpdateRecordsProvider, saveUpdateRecordsProvider, deleteUpdateRecordProvider);
  }

  public static UpdateViewModel newInstance(GetUpdateRecordsUseCase getUpdateRecords,
      SaveUpdateRecordsUseCase saveUpdateRecords, DeleteUpdateRecordUseCase deleteUpdateRecord) {
    return new UpdateViewModel(getUpdateRecords, saveUpdateRecords, deleteUpdateRecord);
  }
}
