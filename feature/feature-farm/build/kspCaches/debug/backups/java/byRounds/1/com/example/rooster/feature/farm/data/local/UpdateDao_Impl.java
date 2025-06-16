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
public final class UpdateDao_Impl implements UpdateDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UpdateEntity> __insertionAdapterOfUpdateEntity;

  private final EntityDeletionOrUpdateAdapter<UpdateEntity> __deletionAdapterOfUpdateEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public UpdateDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUpdateEntity = new EntityInsertionAdapter<UpdateEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `update_records` (`id`,`fowlId`,`updateType`,`title`,`description`,`weight`,`photos`,`recordedAt`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UpdateEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getFowlId());
        statement.bindString(3, entity.getUpdateType());
        statement.bindString(4, entity.getTitle());
        statement.bindString(5, entity.getDescription());
        if (entity.getWeight() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getWeight());
        }
        if (entity.getPhotos() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPhotos());
        }
        statement.bindLong(8, entity.getRecordedAt());
        statement.bindLong(9, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfUpdateEntity = new EntityDeletionOrUpdateAdapter<UpdateEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `update_records` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UpdateEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM update_records WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final UpdateEntity entity, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUpdateEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final UpdateEntity entity, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfUpdateEntity.handle(entity);
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
  public Flow<List<UpdateEntity>> getUpdatesForFowl(final String fowlId) {
    final String _sql = "SELECT * FROM update_records WHERE fowlId = ? ORDER BY recordedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fowlId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"update_records"}, new Callable<List<UpdateEntity>>() {
      @Override
      @NonNull
      public List<UpdateEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFowlId = CursorUtil.getColumnIndexOrThrow(_cursor, "fowlId");
          final int _cursorIndexOfUpdateType = CursorUtil.getColumnIndexOrThrow(_cursor, "updateType");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfRecordedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "recordedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<UpdateEntity> _result = new ArrayList<UpdateEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UpdateEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFowlId;
            _tmpFowlId = _cursor.getString(_cursorIndexOfFowlId);
            final String _tmpUpdateType;
            _tmpUpdateType = _cursor.getString(_cursorIndexOfUpdateType);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
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
            _item = new UpdateEntity(_tmpId,_tmpFowlId,_tmpUpdateType,_tmpTitle,_tmpDescription,_tmpWeight,_tmpPhotos,_tmpRecordedAt,_tmpCreatedAt);
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
  public Flow<List<UpdateEntity>> getRecentUpdates(final int limit) {
    final String _sql = "SELECT * FROM update_records ORDER BY recordedAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"update_records"}, new Callable<List<UpdateEntity>>() {
      @Override
      @NonNull
      public List<UpdateEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFowlId = CursorUtil.getColumnIndexOrThrow(_cursor, "fowlId");
          final int _cursorIndexOfUpdateType = CursorUtil.getColumnIndexOrThrow(_cursor, "updateType");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfRecordedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "recordedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<UpdateEntity> _result = new ArrayList<UpdateEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UpdateEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFowlId;
            _tmpFowlId = _cursor.getString(_cursorIndexOfFowlId);
            final String _tmpUpdateType;
            _tmpUpdateType = _cursor.getString(_cursorIndexOfUpdateType);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
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
            _item = new UpdateEntity(_tmpId,_tmpFowlId,_tmpUpdateType,_tmpTitle,_tmpDescription,_tmpWeight,_tmpPhotos,_tmpRecordedAt,_tmpCreatedAt);
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
