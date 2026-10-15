package cl.duoc.agronutri.data.local;

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
public final class AppDatabase_Impl extends AppDatabase {
  private volatile PredioDao _predioDao;

  private volatile MedicionDao _medicionDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `predios` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre` TEXT NOT NULL, `superficieHa` REAL NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `mediciones` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `predioId` INTEGER NOT NULL, `cultivo` TEXT NOT NULL, `etapa` TEXT NOT NULL, `ph` REAL NOT NULL, `ce` REAL NOT NULL, `n` REAL NOT NULL, `p` REAL NOT NULL, `k` REAL NOT NULL, `ca` REAL NOT NULL, `mg` REAL NOT NULL, `fotoPath` TEXT, `fechaMillis` INTEGER NOT NULL, `validada` INTEGER NOT NULL, `pendienteSync` INTEGER NOT NULL, FOREIGN KEY(`predioId`) REFERENCES `predios`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_mediciones_predioId` ON `mediciones` (`predioId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '18d246c1877aed8b711b4a902f6f9023')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `predios`");
        db.execSQL("DROP TABLE IF EXISTS `mediciones`");
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
        final HashMap<String, TableInfo.Column> _columnsPredios = new HashMap<String, TableInfo.Column>(3);
        _columnsPredios.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPredios.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPredios.put("superficieHa", new TableInfo.Column("superficieHa", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPredios = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPredios = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPredios = new TableInfo("predios", _columnsPredios, _foreignKeysPredios, _indicesPredios);
        final TableInfo _existingPredios = TableInfo.read(db, "predios");
        if (!_infoPredios.equals(_existingPredios)) {
          return new RoomOpenHelper.ValidationResult(false, "predios(cl.duoc.agronutri.data.local.Predio).\n"
                  + " Expected:\n" + _infoPredios + "\n"
                  + " Found:\n" + _existingPredios);
        }
        final HashMap<String, TableInfo.Column> _columnsMediciones = new HashMap<String, TableInfo.Column>(15);
        _columnsMediciones.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediciones.put("predioId", new TableInfo.Column("predioId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediciones.put("cultivo", new TableInfo.Column("cultivo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediciones.put("etapa", new TableInfo.Column("etapa", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediciones.put("ph", new TableInfo.Column("ph", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediciones.put("ce", new TableInfo.Column("ce", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediciones.put("n", new TableInfo.Column("n", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediciones.put("p", new TableInfo.Column("p", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediciones.put("k", new TableInfo.Column("k", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediciones.put("ca", new TableInfo.Column("ca", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediciones.put("mg", new TableInfo.Column("mg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediciones.put("fotoPath", new TableInfo.Column("fotoPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediciones.put("fechaMillis", new TableInfo.Column("fechaMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediciones.put("validada", new TableInfo.Column("validada", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediciones.put("pendienteSync", new TableInfo.Column("pendienteSync", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMediciones = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysMediciones.add(new TableInfo.ForeignKey("predios", "CASCADE", "NO ACTION", Arrays.asList("predioId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMediciones = new HashSet<TableInfo.Index>(1);
        _indicesMediciones.add(new TableInfo.Index("index_mediciones_predioId", false, Arrays.asList("predioId"), Arrays.asList("ASC")));
        final TableInfo _infoMediciones = new TableInfo("mediciones", _columnsMediciones, _foreignKeysMediciones, _indicesMediciones);
        final TableInfo _existingMediciones = TableInfo.read(db, "mediciones");
        if (!_infoMediciones.equals(_existingMediciones)) {
          return new RoomOpenHelper.ValidationResult(false, "mediciones(cl.duoc.agronutri.data.local.Medicion).\n"
                  + " Expected:\n" + _infoMediciones + "\n"
                  + " Found:\n" + _existingMediciones);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "18d246c1877aed8b711b4a902f6f9023", "719757d8da9c845e16c244569025d06b");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "predios","mediciones");
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
      _db.execSQL("DELETE FROM `predios`");
      _db.execSQL("DELETE FROM `mediciones`");
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
    _typeConvertersMap.put(PredioDao.class, PredioDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MedicionDao.class, MedicionDao_Impl.getRequiredConverters());
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
  public PredioDao predioDao() {
    if (_predioDao != null) {
      return _predioDao;
    } else {
      synchronized(this) {
        if(_predioDao == null) {
          _predioDao = new PredioDao_Impl(this);
        }
        return _predioDao;
      }
    }
  }

  @Override
  public MedicionDao medicionDao() {
    if (_medicionDao != null) {
      return _medicionDao;
    } else {
      synchronized(this) {
        if(_medicionDao == null) {
          _medicionDao = new MedicionDao_Impl(this);
        }
        return _medicionDao;
      }
    }
  }
}
