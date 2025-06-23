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
import java.lang.Float;
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
public final class MortalityDao_Impl implements MortalityDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MortalityEntity> __insertionAdapterOfMortalityEntity;

  private final EntityDeletionOrUpdateAdapter<MortalityEntity> __deletionAdapterOfMortalityEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public MortalityDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMortalityEntity = new EntityInsertionAdapter<MortalityEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `mortality_records` (`id`,`fowlId`,`cause`,`description`,`weight`,`photos`,`recordedAt`,`createdAt`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MortalityEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getFowlId());
        statement.bindString(3, entity.getCause());
        if (entity.getDescription() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDescription());
        }
        if (entity.getWeight() == null) {
          statement.bindNull(5);
        } else {
          statement.bindDouble(5, entity.getWeight());
        }
        if (entity.getPhotos() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getPhotos());
        }
        statement.bindLong(7, entity.getRecordedAt());
        statement.bindLong(8, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfMortalityEntity = new EntityDeletionOrUpdateAdapter<MortalityEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `mortality_records` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MortalityEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM mortality_records WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final MortalityEntity entity, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMortalityEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final MortalityEntity entity, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMortalityEntity.handle(entity);
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
  public Flow<List<MortalityEntity>> getMortalityForFowl(final String fowlId) {
    final String _sql = "SELECT * FROM mortality_records WHERE fowlId = ? ORDER BY recordedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fowlId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"mortality_records"}, new Callable<List<MortalityEntity>>() {
      @Override
      @NonNull
      public List<MortalityEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFowlId = CursorUtil.getColumnIndexOrThrow(_cursor, "fowlId");
          final int _cursorIndexOfCause = CursorUtil.getColumnIndexOrThrow(_cursor, "cause");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfRecordedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "recordedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<MortalityEntity> _result = new ArrayList<MortalityEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MortalityEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFowlId;
            _tmpFowlId = _cursor.getString(_cursorIndexOfFowlId);
            final String _tmpCause;
            _tmpCause = _cursor.getString(_cursorIndexOfCause);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final Float _tmpWeight;
            if (_cursor.isNull(_cursorIndexOfWeight)) {
              _tmpWeight = null;
            } else {
              _tmpWeight = _cursor.getFloat(_cursorIndexOfWeight);
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
            _item = new MortalityEntity(_tmpId,_tmpFowlId,_tmpCause,_tmpDescription,_tmpWeight,_tmpPhotos,_tmpRecordedAt,_tmpCreatedAt);
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
  public Flow<List<MortalityEntity>> getAllMortality() {
    final String _sql = "SELECT * FROM mortality_records ORDER BY recordedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"mortality_records"}, new Callable<List<MortalityEntity>>() {
      @Override
      @NonNull
      public List<MortalityEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFowlId = CursorUtil.getColumnIndexOrThrow(_cursor, "fowlId");
          final int _cursorIndexOfCause = CursorUtil.getColumnIndexOrThrow(_cursor, "cause");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfRecordedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "recordedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<MortalityEntity> _result = new ArrayList<MortalityEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MortalityEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFowlId;
            _tmpFowlId = _cursor.getString(_cursorIndexOfFowlId);
            final String _tmpCause;
            _tmpCause = _cursor.getString(_cursorIndexOfCause);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final Float _tmpWeight;
            if (_cursor.isNull(_cursorIndexOfWeight)) {
              _tmpWeight = null;
            } else {
              _tmpWeight = _cursor.getFloat(_cursorIndexOfWeight);
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
            _item = new MortalityEntity(_tmpId,_tmpFowlId,_tmpCause,_tmpDescription,_tmpWeight,_tmpPhotos,_tmpRecordedAt,_tmpCreatedAt);
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
