package com.example.rooster.feature.farm.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FarmDatabase_Impl extends FarmDatabase {
  private volatile FlockDao _flockDao;

  private volatile MortalityDao _mortalityDao;

  private volatile VaccinationDao _vaccinationDao;

  private volatile SensorDataDao _sensorDataDao;

  private volatile UpdateDao _updateDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `flocks` (`id` TEXT NOT NULL, `ownerId` TEXT NOT NULL, `fatherId` TEXT, `motherId` TEXT, `type` TEXT NOT NULL, `name` TEXT NOT NULL, `breed` TEXT, `weight` REAL, `certified` INTEGER NOT NULL, `verified` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_flocks_ownerId` ON `flocks` (`ownerId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_flocks_type` ON `flocks` (`type`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_flocks_fatherId` ON `flocks` (`fatherId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_flocks_motherId` ON `flocks` (`motherId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `mortality_records` (`id` TEXT NOT NULL, `fowlId` TEXT NOT NULL, `cause` TEXT NOT NULL, `description` TEXT, `weight` REAL, `photos` TEXT, `recordedAt` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`fowlId`) REFERENCES `flocks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_mortality_records_fowlId` ON `mortality_records` (`fowlId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `vaccination_records` (`id` TEXT NOT NULL, `fowlId` TEXT NOT NULL, `vaccineName` TEXT NOT NULL, `dosage` TEXT, `veterinarian` TEXT, `nextDueDate` INTEGER, `notes` TEXT, `photos` TEXT, `recordedAt` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`fowlId`) REFERENCES `flocks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_vaccination_records_fowlId` ON `vaccination_records` (`fowlId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sensor_data` (`id` TEXT NOT NULL, `deviceId` TEXT NOT NULL, `temperature` REAL, `humidity` REAL, `airQuality` REAL, `lightLevel` REAL, `noiseLevel` REAL, `timestamp` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sensor_data_deviceId` ON `sensor_data` (`deviceId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sensor_data_timestamp` ON `sensor_data` (`timestamp`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `update_records` (`id` TEXT NOT NULL, `fowlId` TEXT NOT NULL, `updateType` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `weight` REAL, `photos` TEXT, `recordedAt` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`fowlId`) REFERENCES `flocks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_update_records_fowlId` ON `update_records` (`fowlId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '74bd727a426348a4404081a547660234')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `flocks`");
        db.execSQL("DROP TABLE IF EXISTS `mortality_records`");
        db.execSQL("DROP TABLE IF EXISTS `vaccination_records`");
        db.execSQL("DROP TABLE IF EXISTS `sensor_data`");
        db.execSQL("DROP TABLE IF EXISTS `update_records`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsFlocks = new HashMap<String, TableInfo.Column>(12);
        _columnsFlocks.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlocks.put("ownerId", new TableInfo.Column("ownerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlocks.put("fatherId", new TableInfo.Column("fatherId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlocks.put("motherId", new TableInfo.Column("motherId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlocks.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlocks.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlocks.put("breed", new TableInfo.Column("breed", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlocks.put("weight", new TableInfo.Column("weight", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlocks.put("certified", new TableInfo.Column("certified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlocks.put("verified", new TableInfo.Column("verified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlocks.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlocks.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFlocks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFlocks = new HashSet<TableInfo.Index>(4);
        _indicesFlocks.add(new TableInfo.Index("index_flocks_ownerId", false, Arrays.asList("ownerId"), Arrays.asList("ASC")));
        _indicesFlocks.add(new TableInfo.Index("index_flocks_type", false, Arrays.asList("type"), Arrays.asList("ASC")));
        _indicesFlocks.add(new TableInfo.Index("index_flocks_fatherId", false, Arrays.asList("fatherId"), Arrays.asList("ASC")));
        _indicesFlocks.add(new TableInfo.Index("index_flocks_motherId", false, Arrays.asList("motherId"), Arrays.asList("ASC")));
        final TableInfo _infoFlocks = new TableInfo("flocks", _columnsFlocks, _foreignKeysFlocks, _indicesFlocks);
        final TableInfo _existingFlocks = TableInfo.read(db, "flocks");
        if (!_infoFlocks.equals(_existingFlocks)) {
          return new RoomOpenHelper.ValidationResult(false, "flocks(com.example.rooster.feature.farm.data.local.FlockEntity).\n"
                  + " Expected:\n" + _infoFlocks + "\n"
                  + " Found:\n" + _existingFlocks);
        }
        final HashMap<String, TableInfo.Column> _columnsMortalityRecords = new HashMap<String, TableInfo.Column>(8);
        _columnsMortalityRecords.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMortalityRecords.put("fowlId", new TableInfo.Column("fowlId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMortalityRecords.put("cause", new TableInfo.Column("cause", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMortalityRecords.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMortalityRecords.put("weight", new TableInfo.Column("weight", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMortalityRecords.put("photos", new TableInfo.Column("photos", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMortalityRecords.put("recordedAt", new TableInfo.Column("recordedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMortalityRecords.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMortalityRecords = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysMortalityRecords.add(new TableInfo.ForeignKey("flocks", "CASCADE", "NO ACTION", Arrays.asList("fowlId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMortalityRecords = new HashSet<TableInfo.Index>(1);
        _indicesMortalityRecords.add(new TableInfo.Index("index_mortality_records_fowlId", false, Arrays.asList("fowlId"), Arrays.asList("ASC")));
        final TableInfo _infoMortalityRecords = new TableInfo("mortality_records", _columnsMortalityRecords, _foreignKeysMortalityRecords, _indicesMortalityRecords);
        final TableInfo _existingMortalityRecords = TableInfo.read(db, "mortality_records");
        if (!_infoMortalityRecords.equals(_existingMortalityRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "mortality_records(com.example.rooster.feature.farm.data.local.MortalityEntity).\n"
                  + " Expected:\n" + _infoMortalityRecords + "\n"
                  + " Found:\n" + _existingMortalityRecords);
        }
        final HashMap<String, TableInfo.Column> _columnsVaccinationRecords = new HashMap<String, TableInfo.Column>(10);
        _columnsVaccinationRecords.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaccinationRecords.put("fowlId", new TableInfo.Column("fowlId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaccinationRecords.put("vaccineName", new TableInfo.Column("vaccineName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaccinationRecords.put("dosage", new TableInfo.Column("dosage", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaccinationRecords.put("veterinarian", new TableInfo.Column("veterinarian", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaccinationRecords.put("nextDueDate", new TableInfo.Column("nextDueDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaccinationRecords.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaccinationRecords.put("photos", new TableInfo.Column("photos", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaccinationRecords.put("recordedAt", new TableInfo.Column("recordedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaccinationRecords.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVaccinationRecords = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysVaccinationRecords.add(new TableInfo.ForeignKey("flocks", "CASCADE", "NO ACTION", Arrays.asList("fowlId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesVaccinationRecords = new HashSet<TableInfo.Index>(1);
        _indicesVaccinationRecords.add(new TableInfo.Index("index_vaccination_records_fowlId", false, Arrays.asList("fowlId"), Arrays.asList("ASC")));
        final TableInfo _infoVaccinationRecords = new TableInfo("vaccination_records", _columnsVaccinationRecords, _foreignKeysVaccinationRecords, _indicesVaccinationRecords);
        final TableInfo _existingVaccinationRecords = TableInfo.read(db, "vaccination_records");
        if (!_infoVaccinationRecords.equals(_existingVaccinationRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "vaccination_records(com.example.rooster.feature.farm.data.local.VaccinationEntity).\n"
                  + " Expected:\n" + _infoVaccinationRecords + "\n"
                  + " Found:\n" + _existingVaccinationRecords);
        }
        final HashMap<String, TableInfo.Column> _columnsSensorData = new HashMap<String, TableInfo.Column>(9);
        _columnsSensorData.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSensorData.put("deviceId", new TableInfo.Column("deviceId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSensorData.put("temperature", new TableInfo.Column("temperature", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSensorData.put("humidity", new TableInfo.Column("humidity", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSensorData.put("airQuality", new TableInfo.Column("airQuality", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSensorData.put("lightLevel", new TableInfo.Column("lightLevel", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSensorData.put("noiseLevel", new TableInfo.Column("noiseLevel", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSensorData.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSensorData.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSensorData = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSensorData = new HashSet<TableInfo.Index>(2);
        _indicesSensorData.add(new TableInfo.Index("index_sensor_data_deviceId", false, Arrays.asList("deviceId"), Arrays.asList("ASC")));
        _indicesSensorData.add(new TableInfo.Index("index_sensor_data_timestamp", false, Arrays.asList("timestamp"), Arrays.asList("ASC")));
        final TableInfo _infoSensorData = new TableInfo("sensor_data", _columnsSensorData, _foreignKeysSensorData, _indicesSensorData);
        final TableInfo _existingSensorData = TableInfo.read(db, "sensor_data");
        if (!_infoSensorData.equals(_existingSensorData)) {
          return new RoomOpenHelper.ValidationResult(false, "sensor_data(com.example.rooster.feature.farm.data.local.SensorDataEntity).\n"
                  + " Expected:\n" + _infoSensorData + "\n"
                  + " Found:\n" + _existingSensorData);
        }
        final HashMap<String, TableInfo.Column> _columnsUpdateRecords = new HashMap<String, TableInfo.Column>(9);
        _columnsUpdateRecords.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUpdateRecords.put("fowlId", new TableInfo.Column("fowlId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUpdateRecords.put("updateType", new TableInfo.Column("updateType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUpdateRecords.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUpdateRecords.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUpdateRecords.put("weight", new TableInfo.Column("weight", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUpdateRecords.put("photos", new TableInfo.Column("photos", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUpdateRecords.put("recordedAt", new TableInfo.Column("recordedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUpdateRecords.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUpdateRecords = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysUpdateRecords.add(new TableInfo.ForeignKey("flocks", "CASCADE", "NO ACTION", Arrays.asList("fowlId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesUpdateRecords = new HashSet<TableInfo.Index>(1);
        _indicesUpdateRecords.add(new TableInfo.Index("index_update_records_fowlId", false, Arrays.asList("fowlId"), Arrays.asList("ASC")));
        final TableInfo _infoUpdateRecords = new TableInfo("update_records", _columnsUpdateRecords, _foreignKeysUpdateRecords, _indicesUpdateRecords);
        final TableInfo _existingUpdateRecords = TableInfo.read(db, "update_records");
        if (!_infoUpdateRecords.equals(_existingUpdateRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "update_records(com.example.rooster.feature.farm.data.local.UpdateEntity).\n"
                  + " Expected:\n" + _infoUpdateRecords + "\n"
                  + " Found:\n" + _existingUpdateRecords);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "74bd727a426348a4404081a547660234", "f8ffbefd9f468e4193f249e2c0dfe83e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "flocks","mortality_records","vaccination_records","sensor_data","update_records");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `flocks`");
      _db.execSQL("DELETE FROM `mortality_records`");
      _db.execSQL("DELETE FROM `vaccination_records`");
      _db.execSQL("DELETE FROM `sensor_data`");
      _db.execSQL("DELETE FROM `update_records`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(FlockDao.class, FlockDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MortalityDao.class, MortalityDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VaccinationDao.class, VaccinationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SensorDataDao.class, SensorDataDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UpdateDao.class, UpdateDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public FlockDao flockDao() {
    if (_flockDao != null) {
      return _flockDao;
    } else {
      synchronized(this) {
        if(_flockDao == null) {
          _flockDao = new FlockDao_Impl(this);
        }
        return _flockDao;
      }
    }
  }

  @Override
  public MortalityDao mortalityDao() {
    if (_mortalityDao != null) {
      return _mortalityDao;
    } else {
      synchronized(this) {
        if(_mortalityDao == null) {
          _mortalityDao = new MortalityDao_Impl(this);
        }
        return _mortalityDao;
      }
    }
  }

  @Override
  public VaccinationDao vaccinationDao() {
    if (_vaccinationDao != null) {
      return _vaccinationDao;
    } else {
      synchronized(this) {
        if(_vaccinationDao == null) {
          _vaccinationDao = new VaccinationDao_Impl(this);
        }
        return _vaccinationDao;
      }
    }
  }

  @Override
  public SensorDataDao sensorDataDao() {
    if (_sensorDataDao != null) {
      return _sensorDataDao;
    } else {
      synchronized(this) {
        if(_sensorDataDao == null) {
          _sensorDataDao = new SensorDataDao_Impl(this);
        }
        return _sensorDataDao;
      }
    }
  }

  @Override
  public UpdateDao updateDao() {
    if (_updateDao != null) {
      return _updateDao;
    } else {
      synchronized(this) {
        if(_updateDao == null) {
          _updateDao = new UpdateDao_Impl(this);
        }
        return _updateDao;
      }
    }
  }
}
