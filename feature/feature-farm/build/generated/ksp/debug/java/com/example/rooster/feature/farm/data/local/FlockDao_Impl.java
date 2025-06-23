package com.example.rooster.feature.farm.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
public final class FlockDao_Impl implements FlockDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FlockEntity> __insertionAdapterOfFlockEntity;

  private final EntityDeletionOrUpdateAdapter<FlockEntity> __deletionAdapterOfFlockEntity;

  private final EntityDeletionOrUpdateAdapter<FlockEntity> __updateAdapterOfFlockEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public FlockDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFlockEntity = new EntityInsertionAdapter<FlockEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `flocks` (`id`,`ownerId`,`fatherId`,`motherId`,`type`,`name`,`breed`,`weight`,`certified`,`verified`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FlockEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getOwnerId());
        if (entity.getFatherId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getFatherId());
        }
        if (entity.getMotherId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getMotherId());
        }
        statement.bindString(5, entity.getType());
        statement.bindString(6, entity.getName());
        if (entity.getBreed() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getBreed());
        }
        if (entity.getWeight() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getWeight());
        }
        final int _tmp = entity.getCertified() ? 1 : 0;
        statement.bindLong(9, _tmp);
        final int _tmp_1 = entity.getVerified() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        statement.bindLong(11, entity.getCreatedAt());
        statement.bindLong(12, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfFlockEntity = new EntityDeletionOrUpdateAdapter<FlockEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `flocks` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FlockEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfFlockEntity = new EntityDeletionOrUpdateAdapter<FlockEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `flocks` SET `id` = ?,`ownerId` = ?,`fatherId` = ?,`motherId` = ?,`type` = ?,`name` = ?,`breed` = ?,`weight` = ?,`certified` = ?,`verified` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FlockEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getOwnerId());
        if (entity.getFatherId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getFatherId());
        }
        if (entity.getMotherId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getMotherId());
        }
        statement.bindString(5, entity.getType());
        statement.bindString(6, entity.getName());
        if (entity.getBreed() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getBreed());
        }
        if (entity.getWeight() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getWeight());
        }
        final int _tmp = entity.getCertified() ? 1 : 0;
        statement.bindLong(9, _tmp);
        final int _tmp_1 = entity.getVerified() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        statement.bindLong(11, entity.getCreatedAt());
        statement.bindLong(12, entity.getUpdatedAt());
        statement.bindString(13, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM flocks WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final FlockEntity entity, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFlockEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final FlockEntity entity, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFlockEntity.handle(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final FlockEntity entity, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFlockEntity.handle(entity);
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
  public Flow<FlockEntity> getById(final String id) {
    final String _sql = "SELECT * FROM flocks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flocks"}, new Callable<FlockEntity>() {
      @Override
      @Nullable
      public FlockEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfOwnerId = CursorUtil.getColumnIndexOrThrow(_cursor, "ownerId");
          final int _cursorIndexOfFatherId = CursorUtil.getColumnIndexOrThrow(_cursor, "fatherId");
          final int _cursorIndexOfMotherId = CursorUtil.getColumnIndexOrThrow(_cursor, "motherId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBreed = CursorUtil.getColumnIndexOrThrow(_cursor, "breed");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfCertified = CursorUtil.getColumnIndexOrThrow(_cursor, "certified");
          final int _cursorIndexOfVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "verified");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final FlockEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpOwnerId;
            _tmpOwnerId = _cursor.getString(_cursorIndexOfOwnerId);
            final String _tmpFatherId;
            if (_cursor.isNull(_cursorIndexOfFatherId)) {
              _tmpFatherId = null;
            } else {
              _tmpFatherId = _cursor.getString(_cursorIndexOfFatherId);
            }
            final String _tmpMotherId;
            if (_cursor.isNull(_cursorIndexOfMotherId)) {
              _tmpMotherId = null;
            } else {
              _tmpMotherId = _cursor.getString(_cursorIndexOfMotherId);
            }
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBreed;
            if (_cursor.isNull(_cursorIndexOfBreed)) {
              _tmpBreed = null;
            } else {
              _tmpBreed = _cursor.getString(_cursorIndexOfBreed);
            }
            final Float _tmpWeight;
            if (_cursor.isNull(_cursorIndexOfWeight)) {
              _tmpWeight = null;
            } else {
              _tmpWeight = _cursor.getFloat(_cursorIndexOfWeight);
            }
            final boolean _tmpCertified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCertified);
            _tmpCertified = _tmp != 0;
            final boolean _tmpVerified;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfVerified);
            _tmpVerified = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new FlockEntity(_tmpId,_tmpOwnerId,_tmpFatherId,_tmpMotherId,_tmpType,_tmpName,_tmpBreed,_tmpWeight,_tmpCertified,_tmpVerified,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
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
  public Flow<List<FlockEntity>> getByOwner(final String ownerId) {
    final String _sql = "SELECT * FROM flocks WHERE ownerId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, ownerId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flocks"}, new Callable<List<FlockEntity>>() {
      @Override
      @NonNull
      public List<FlockEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfOwnerId = CursorUtil.getColumnIndexOrThrow(_cursor, "ownerId");
          final int _cursorIndexOfFatherId = CursorUtil.getColumnIndexOrThrow(_cursor, "fatherId");
          final int _cursorIndexOfMotherId = CursorUtil.getColumnIndexOrThrow(_cursor, "motherId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBreed = CursorUtil.getColumnIndexOrThrow(_cursor, "breed");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfCertified = CursorUtil.getColumnIndexOrThrow(_cursor, "certified");
          final int _cursorIndexOfVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "verified");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<FlockEntity> _result = new ArrayList<FlockEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FlockEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpOwnerId;
            _tmpOwnerId = _cursor.getString(_cursorIndexOfOwnerId);
            final String _tmpFatherId;
            if (_cursor.isNull(_cursorIndexOfFatherId)) {
              _tmpFatherId = null;
            } else {
              _tmpFatherId = _cursor.getString(_cursorIndexOfFatherId);
            }
            final String _tmpMotherId;
            if (_cursor.isNull(_cursorIndexOfMotherId)) {
              _tmpMotherId = null;
            } else {
              _tmpMotherId = _cursor.getString(_cursorIndexOfMotherId);
            }
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBreed;
            if (_cursor.isNull(_cursorIndexOfBreed)) {
              _tmpBreed = null;
            } else {
              _tmpBreed = _cursor.getString(_cursorIndexOfBreed);
            }
            final Float _tmpWeight;
            if (_cursor.isNull(_cursorIndexOfWeight)) {
              _tmpWeight = null;
            } else {
              _tmpWeight = _cursor.getFloat(_cursorIndexOfWeight);
            }
            final boolean _tmpCertified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCertified);
            _tmpCertified = _tmp != 0;
            final boolean _tmpVerified;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfVerified);
            _tmpVerified = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new FlockEntity(_tmpId,_tmpOwnerId,_tmpFatherId,_tmpMotherId,_tmpType,_tmpName,_tmpBreed,_tmpWeight,_tmpCertified,_tmpVerified,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<FlockEntity>> getByType(final String type) {
    final String _sql = "SELECT * FROM flocks WHERE type = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flocks"}, new Callable<List<FlockEntity>>() {
      @Override
      @NonNull
      public List<FlockEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfOwnerId = CursorUtil.getColumnIndexOrThrow(_cursor, "ownerId");
          final int _cursorIndexOfFatherId = CursorUtil.getColumnIndexOrThrow(_cursor, "fatherId");
          final int _cursorIndexOfMotherId = CursorUtil.getColumnIndexOrThrow(_cursor, "motherId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBreed = CursorUtil.getColumnIndexOrThrow(_cursor, "breed");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfCertified = CursorUtil.getColumnIndexOrThrow(_cursor, "certified");
          final int _cursorIndexOfVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "verified");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<FlockEntity> _result = new ArrayList<FlockEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FlockEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpOwnerId;
            _tmpOwnerId = _cursor.getString(_cursorIndexOfOwnerId);
            final String _tmpFatherId;
            if (_cursor.isNull(_cursorIndexOfFatherId)) {
              _tmpFatherId = null;
            } else {
              _tmpFatherId = _cursor.getString(_cursorIndexOfFatherId);
            }
            final String _tmpMotherId;
            if (_cursor.isNull(_cursorIndexOfMotherId)) {
              _tmpMotherId = null;
            } else {
              _tmpMotherId = _cursor.getString(_cursorIndexOfMotherId);
            }
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBreed;
            if (_cursor.isNull(_cursorIndexOfBreed)) {
              _tmpBreed = null;
            } else {
              _tmpBreed = _cursor.getString(_cursorIndexOfBreed);
            }
            final Float _tmpWeight;
            if (_cursor.isNull(_cursorIndexOfWeight)) {
              _tmpWeight = null;
            } else {
              _tmpWeight = _cursor.getFloat(_cursorIndexOfWeight);
            }
            final boolean _tmpCertified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCertified);
            _tmpCertified = _tmp != 0;
            final boolean _tmpVerified;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfVerified);
            _tmpVerified = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new FlockEntity(_tmpId,_tmpOwnerId,_tmpFatherId,_tmpMotherId,_tmpType,_tmpName,_tmpBreed,_tmpWeight,_tmpCertified,_tmpVerified,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<FlockEntity>> getOffspring(final String parentId) {
    final String _sql = "SELECT * FROM flocks WHERE fatherId = ? OR motherId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, parentId);
    _argIndex = 2;
    _statement.bindString(_argIndex, parentId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flocks"}, new Callable<List<FlockEntity>>() {
      @Override
      @NonNull
      public List<FlockEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfOwnerId = CursorUtil.getColumnIndexOrThrow(_cursor, "ownerId");
          final int _cursorIndexOfFatherId = CursorUtil.getColumnIndexOrThrow(_cursor, "fatherId");
          final int _cursorIndexOfMotherId = CursorUtil.getColumnIndexOrThrow(_cursor, "motherId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBreed = CursorUtil.getColumnIndexOrThrow(_cursor, "breed");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfCertified = CursorUtil.getColumnIndexOrThrow(_cursor, "certified");
          final int _cursorIndexOfVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "verified");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<FlockEntity> _result = new ArrayList<FlockEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FlockEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpOwnerId;
            _tmpOwnerId = _cursor.getString(_cursorIndexOfOwnerId);
            final String _tmpFatherId;
            if (_cursor.isNull(_cursorIndexOfFatherId)) {
              _tmpFatherId = null;
            } else {
              _tmpFatherId = _cursor.getString(_cursorIndexOfFatherId);
            }
            final String _tmpMotherId;
            if (_cursor.isNull(_cursorIndexOfMotherId)) {
              _tmpMotherId = null;
            } else {
              _tmpMotherId = _cursor.getString(_cursorIndexOfMotherId);
            }
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBreed;
            if (_cursor.isNull(_cursorIndexOfBreed)) {
              _tmpBreed = null;
            } else {
              _tmpBreed = _cursor.getString(_cursorIndexOfBreed);
            }
            final Float _tmpWeight;
            if (_cursor.isNull(_cursorIndexOfWeight)) {
              _tmpWeight = null;
            } else {
              _tmpWeight = _cursor.getFloat(_cursorIndexOfWeight);
            }
            final boolean _tmpCertified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCertified);
            _tmpCertified = _tmp != 0;
            final boolean _tmpVerified;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfVerified);
            _tmpVerified = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new FlockEntity(_tmpId,_tmpOwnerId,_tmpFatherId,_tmpMotherId,_tmpType,_tmpName,_tmpBreed,_tmpWeight,_tmpCertified,_tmpVerified,_tmpCreatedAt,_tmpUpdatedAt);
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
