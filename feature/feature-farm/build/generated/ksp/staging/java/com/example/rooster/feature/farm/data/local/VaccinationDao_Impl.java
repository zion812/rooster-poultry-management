package com.example.rooster.feature.farm.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class VaccinationDao_Impl implements VaccinationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VaccinationEntity> __insertionAdapterOfVaccinationEntity;

  private final EntityDeletionOrUpdateAdapter<VaccinationEntity> __deletionAdapterOfVaccinationEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public VaccinationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVaccinationEntity = new EntityInsertionAdapter<VaccinationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `vaccination_records` (`id`,`fowlId`,`vaccineName`,`dosage`,`veterinarian`,`nextDueDate`,`notes`,`photos`,`recordedAt`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VaccinationEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getFowlId());
        statement.bindString(3, entity.getVaccineName());
        if (entity.getDosage() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDosage());
        }
        if (entity.getVeterinarian() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getVeterinarian());
        }
        if (entity.getNextDueDate() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getNextDueDate());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getNotes());
        }
        if (entity.getPhotos() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getPhotos());
        }
        statement.bindLong(9, entity.getRecordedAt());
        statement.bindLong(10, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfVaccinationEntity = new EntityDeletionOrUpdateAdapter<VaccinationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `vaccination_records` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VaccinationEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM vaccination_records WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final VaccinationEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfVaccinationEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final VaccinationEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfVaccinationEntity.handle(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VaccinationEntity>> getVaccinationForFowl(final String fowlId) {
    final String _sql = "SELECT * FROM vaccination_records WHERE fowlId = ? ORDER BY recordedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fowlId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vaccination_records"}, new Callable<List<VaccinationEntity>>() {
      @Override
      @NonNull
      public List<VaccinationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFowlId = CursorUtil.getColumnIndexOrThrow(_cursor, "fowlId");
          final int _cursorIndexOfVaccineName = CursorUtil.getColumnIndexOrThrow(_cursor, "vaccineName");
          final int _cursorIndexOfDosage = CursorUtil.getColumnIndexOrThrow(_cursor, "dosage");
          final int _cursorIndexOfVeterinarian = CursorUtil.getColumnIndexOrThrow(_cursor, "veterinarian");
          final int _cursorIndexOfNextDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueDate");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfRecordedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "recordedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<VaccinationEntity> _result = new ArrayList<VaccinationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VaccinationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFowlId;
            _tmpFowlId = _cursor.getString(_cursorIndexOfFowlId);
            final String _tmpVaccineName;
            _tmpVaccineName = _cursor.getString(_cursorIndexOfVaccineName);
            final String _tmpDosage;
            if (_cursor.isNull(_cursorIndexOfDosage)) {
              _tmpDosage = null;
            } else {
              _tmpDosage = _cursor.getString(_cursorIndexOfDosage);
            }
            final String _tmpVeterinarian;
            if (_cursor.isNull(_cursorIndexOfVeterinarian)) {
              _tmpVeterinarian = null;
            } else {
              _tmpVeterinarian = _cursor.getString(_cursorIndexOfVeterinarian);
            }
            final Long _tmpNextDueDate;
            if (_cursor.isNull(_cursorIndexOfNextDueDate)) {
              _tmpNextDueDate = null;
            } else {
              _tmpNextDueDate = _cursor.getLong(_cursorIndexOfNextDueDate);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpPhotos;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmpPhotos = null;
            } else {
              _tmpPhotos = _cursor.getString(_cursorIndexOfPhotos);
            }
            final long _tmpRecordedAt;
            _tmpRecordedAt = _cursor.getLong(_cursorIndexOfRecordedAt);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new VaccinationEntity(_tmpId,_tmpFowlId,_tmpVaccineName,_tmpDosage,_tmpVeterinarian,_tmpNextDueDate,_tmpNotes,_tmpPhotos,_tmpRecordedAt,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<VaccinationEntity>> getUpcomingVaccinations(final long date) {
    final String _sql = "SELECT * FROM vaccination_records WHERE nextDueDate <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vaccination_records"}, new Callable<List<VaccinationEntity>>() {
      @Override
      @NonNull
      public List<VaccinationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFowlId = CursorUtil.getColumnIndexOrThrow(_cursor, "fowlId");
          final int _cursorIndexOfVaccineName = CursorUtil.getColumnIndexOrThrow(_cursor, "vaccineName");
          final int _cursorIndexOfDosage = CursorUtil.getColumnIndexOrThrow(_cursor, "dosage");
          final int _cursorIndexOfVeterinarian = CursorUtil.getColumnIndexOrThrow(_cursor, "veterinarian");
          final int _cursorIndexOfNextDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueDate");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfRecordedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "recordedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<VaccinationEntity> _result = new ArrayList<VaccinationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VaccinationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFowlId;
            _tmpFowlId = _cursor.getString(_cursorIndexOfFowlId);
            final String _tmpVaccineName;
            _tmpVaccineName = _cursor.getString(_cursorIndexOfVaccineName);
            final String _tmpDosage;
            if (_cursor.isNull(_cursorIndexOfDosage)) {
              _tmpDosage = null;
            } else {
              _tmpDosage = _cursor.getString(_cursorIndexOfDosage);
            }
            final String _tmpVeterinarian;
            if (_cursor.isNull(_cursorIndexOfVeterinarian)) {
              _tmpVeterinarian = null;
            } else {
              _tmpVeterinarian = _cursor.getString(_cursorIndexOfVeterinarian);
            }
            final Long _tmpNextDueDate;
            if (_cursor.isNull(_cursorIndexOfNextDueDate)) {
              _tmpNextDueDate = null;
            } else {
              _tmpNextDueDate = _cursor.getLong(_cursorIndexOfNextDueDate);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpPhotos;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmpPhotos = null;
            } else {
              _tmpPhotos = _cursor.getString(_cursorIndexOfPhotos);
            }
            final long _tmpRecordedAt;
            _tmpRecordedAt = _cursor.getLong(_cursorIndexOfRecordedAt);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new VaccinationEntity(_tmpId,_tmpFowlId,_tmpVaccineName,_tmpDosage,_tmpVeterinarian,_tmpNextDueDate,_tmpNotes,_tmpPhotos,_tmpRecordedAt,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
