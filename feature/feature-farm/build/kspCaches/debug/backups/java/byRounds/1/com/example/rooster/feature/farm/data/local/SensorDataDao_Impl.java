package com.example.rooster.feature.farm.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
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
public final class SensorDataDao_Impl implements SensorDataDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SensorDataEntity> __insertionAdapterOfSensorDataEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldData;

  public SensorDataDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSensorDataEntity = new EntityInsertionAdapter<SensorDataEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sensor_data` (`id`,`deviceId`,`temperature`,`humidity`,`airQuality`,`lightLevel`,`noiseLevel`,`timestamp`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SensorDataEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDeviceId());
        if (entity.getTemperature() == null) {
          statement.bindNull(3);
        } else {
          statement.bindDouble(3, entity.getTemperature());
        }
        if (entity.getHumidity() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getHumidity());
        }
        if (entity.getAirQuality() == null) {
          statement.bindNull(5);
        } else {
          statement.bindDouble(5, entity.getAirQuality());
        }
        if (entity.getLightLevel() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getLightLevel());
        }
        if (entity.getNoiseLevel() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getNoiseLevel());
        }
        statement.bindLong(8, entity.getTimestamp());
        statement.bindLong(9, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfDeleteOldData = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM sensor_data WHERE timestamp < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final SensorDataEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSensorDataEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldData(final long cutoffTime, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldData.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoffTime);
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
          __preparedStmtOfDeleteOldData.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SensorDataEntity>> getByDevice(final String deviceId, final int limit) {
    final String _sql = "SELECT * FROM sensor_data WHERE deviceId = ? ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sensor_data"}, new Callable<List<SensorDataEntity>>() {
      @Override
      @NonNull
      public List<SensorDataEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfTemperature = CursorUtil.getColumnIndexOrThrow(_cursor, "temperature");
          final int _cursorIndexOfHumidity = CursorUtil.getColumnIndexOrThrow(_cursor, "humidity");
          final int _cursorIndexOfAirQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "airQuality");
          final int _cursorIndexOfLightLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "lightLevel");
          final int _cursorIndexOfNoiseLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "noiseLevel");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<SensorDataEntity> _result = new ArrayList<SensorDataEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SensorDataEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final Float _tmpTemperature;
            if (_cursor.isNull(_cursorIndexOfTemperature)) {
              _tmpTemperature = null;
            } else {
              _tmpTemperature = _cursor.getFloat(_cursorIndexOfTemperature);
            }
            final Float _tmpHumidity;
            if (_cursor.isNull(_cursorIndexOfHumidity)) {
              _tmpHumidity = null;
            } else {
              _tmpHumidity = _cursor.getFloat(_cursorIndexOfHumidity);
            }
            final Float _tmpAirQuality;
            if (_cursor.isNull(_cursorIndexOfAirQuality)) {
              _tmpAirQuality = null;
            } else {
              _tmpAirQuality = _cursor.getFloat(_cursorIndexOfAirQuality);
            }
            final Float _tmpLightLevel;
            if (_cursor.isNull(_cursorIndexOfLightLevel)) {
              _tmpLightLevel = null;
            } else {
              _tmpLightLevel = _cursor.getFloat(_cursorIndexOfLightLevel);
            }
            final Float _tmpNoiseLevel;
            if (_cursor.isNull(_cursorIndexOfNoiseLevel)) {
              _tmpNoiseLevel = null;
            } else {
              _tmpNoiseLevel = _cursor.getFloat(_cursorIndexOfNoiseLevel);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SensorDataEntity(_tmpId,_tmpDeviceId,_tmpTemperature,_tmpHumidity,_tmpAirQuality,_tmpLightLevel,_tmpNoiseLevel,_tmpTimestamp,_tmpCreatedAt);
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
  public Flow<List<SensorDataEntity>> getAll(final int limit) {
    final String _sql = "SELECT * FROM sensor_data ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sensor_data"}, new Callable<List<SensorDataEntity>>() {
      @Override
      @NonNull
      public List<SensorDataEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfTemperature = CursorUtil.getColumnIndexOrThrow(_cursor, "temperature");
          final int _cursorIndexOfHumidity = CursorUtil.getColumnIndexOrThrow(_cursor, "humidity");
          final int _cursorIndexOfAirQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "airQuality");
          final int _cursorIndexOfLightLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "lightLevel");
          final int _cursorIndexOfNoiseLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "noiseLevel");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<SensorDataEntity> _result = new ArrayList<SensorDataEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SensorDataEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final Float _tmpTemperature;
            if (_cursor.isNull(_cursorIndexOfTemperature)) {
              _tmpTemperature = null;
            } else {
              _tmpTemperature = _cursor.getFloat(_cursorIndexOfTemperature);
            }
            final Float _tmpHumidity;
            if (_cursor.isNull(_cursorIndexOfHumidity)) {
              _tmpHumidity = null;
            } else {
              _tmpHumidity = _cursor.getFloat(_cursorIndexOfHumidity);
            }
            final Float _tmpAirQuality;
            if (_cursor.isNull(_cursorIndexOfAirQuality)) {
              _tmpAirQuality = null;
            } else {
              _tmpAirQuality = _cursor.getFloat(_cursorIndexOfAirQuality);
            }
            final Float _tmpLightLevel;
            if (_cursor.isNull(_cursorIndexOfLightLevel)) {
              _tmpLightLevel = null;
            } else {
              _tmpLightLevel = _cursor.getFloat(_cursorIndexOfLightLevel);
            }
            final Float _tmpNoiseLevel;
            if (_cursor.isNull(_cursorIndexOfNoiseLevel)) {
              _tmpNoiseLevel = null;
            } else {
              _tmpNoiseLevel = _cursor.getFloat(_cursorIndexOfNoiseLevel);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SensorDataEntity(_tmpId,_tmpDeviceId,_tmpTemperature,_tmpHumidity,_tmpAirQuality,_tmpLightLevel,_tmpNoiseLevel,_tmpTimestamp,_tmpCreatedAt);
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
  public Flow<List<SensorDataEntity>> getByTimeRange(final long startTime, final long endTime) {
    final String _sql = "SELECT * FROM sensor_data WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sensor_data"}, new Callable<List<SensorDataEntity>>() {
      @Override
      @NonNull
      public List<SensorDataEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfTemperature = CursorUtil.getColumnIndexOrThrow(_cursor, "temperature");
          final int _cursorIndexOfHumidity = CursorUtil.getColumnIndexOrThrow(_cursor, "humidity");
          final int _cursorIndexOfAirQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "airQuality");
          final int _cursorIndexOfLightLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "lightLevel");
          final int _cursorIndexOfNoiseLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "noiseLevel");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<SensorDataEntity> _result = new ArrayList<SensorDataEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SensorDataEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final Float _tmpTemperature;
            if (_cursor.isNull(_cursorIndexOfTemperature)) {
              _tmpTemperature = null;
            } else {
              _tmpTemperature = _cursor.getFloat(_cursorIndexOfTemperature);
            }
            final Float _tmpHumidity;
            if (_cursor.isNull(_cursorIndexOfHumidity)) {
              _tmpHumidity = null;
            } else {
              _tmpHumidity = _cursor.getFloat(_cursorIndexOfHumidity);
            }
            final Float _tmpAirQuality;
            if (_cursor.isNull(_cursorIndexOfAirQuality)) {
              _tmpAirQuality = null;
            } else {
              _tmpAirQuality = _cursor.getFloat(_cursorIndexOfAirQuality);
            }
            final Float _tmpLightLevel;
            if (_cursor.isNull(_cursorIndexOfLightLevel)) {
              _tmpLightLevel = null;
            } else {
              _tmpLightLevel = _cursor.getFloat(_cursorIndexOfLightLevel);
            }
            final Float _tmpNoiseLevel;
            if (_cursor.isNull(_cursorIndexOfNoiseLevel)) {
              _tmpNoiseLevel = null;
            } else {
              _tmpNoiseLevel = _cursor.getFloat(_cursorIndexOfNoiseLevel);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SensorDataEntity(_tmpId,_tmpDeviceId,_tmpTemperature,_tmpHumidity,_tmpAirQuality,_tmpLightLevel,_tmpNoiseLevel,_tmpTimestamp,_tmpCreatedAt);
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
