package cl.duoc.agronutri.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class MedicionDao_Impl implements MedicionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Medicion> __insertionAdapterOfMedicion;

  private final SharedSQLiteStatement __preparedStmtOfValidar;

  public MedicionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMedicion = new EntityInsertionAdapter<Medicion>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `mediciones` (`id`,`predioId`,`cultivo`,`etapa`,`ph`,`ce`,`n`,`p`,`k`,`ca`,`mg`,`fotoPath`,`fechaMillis`,`validada`,`pendienteSync`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Medicion entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPredioId());
        statement.bindString(3, entity.getCultivo());
        statement.bindString(4, entity.getEtapa());
        statement.bindDouble(5, entity.getPh());
        statement.bindDouble(6, entity.getCe());
        statement.bindDouble(7, entity.getN());
        statement.bindDouble(8, entity.getP());
        statement.bindDouble(9, entity.getK());
        statement.bindDouble(10, entity.getCa());
        statement.bindDouble(11, entity.getMg());
        if (entity.getFotoPath() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getFotoPath());
        }
        statement.bindLong(13, entity.getFechaMillis());
        final int _tmp = entity.getValidada() ? 1 : 0;
        statement.bindLong(14, _tmp);
        final int _tmp_1 = entity.getPendienteSync() ? 1 : 0;
        statement.bindLong(15, _tmp_1);
      }
    };
    this.__preparedStmtOfValidar = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE mediciones SET validada = 1, pendienteSync = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertar(final Medicion medicion, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMedicion.insertAndReturnId(medicion);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object validar(final int id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfValidar.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfValidar.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MedicionConPredio>> observarHistorial() {
    final String _sql = "SELECT * FROM mediciones ORDER BY fechaMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"predios",
        "mediciones"}, new Callable<List<MedicionConPredio>>() {
      @Override
      @NonNull
      public List<MedicionConPredio> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfPredioId = CursorUtil.getColumnIndexOrThrow(_cursor, "predioId");
            final int _cursorIndexOfCultivo = CursorUtil.getColumnIndexOrThrow(_cursor, "cultivo");
            final int _cursorIndexOfEtapa = CursorUtil.getColumnIndexOrThrow(_cursor, "etapa");
            final int _cursorIndexOfPh = CursorUtil.getColumnIndexOrThrow(_cursor, "ph");
            final int _cursorIndexOfCe = CursorUtil.getColumnIndexOrThrow(_cursor, "ce");
            final int _cursorIndexOfN = CursorUtil.getColumnIndexOrThrow(_cursor, "n");
            final int _cursorIndexOfP = CursorUtil.getColumnIndexOrThrow(_cursor, "p");
            final int _cursorIndexOfK = CursorUtil.getColumnIndexOrThrow(_cursor, "k");
            final int _cursorIndexOfCa = CursorUtil.getColumnIndexOrThrow(_cursor, "ca");
            final int _cursorIndexOfMg = CursorUtil.getColumnIndexOrThrow(_cursor, "mg");
            final int _cursorIndexOfFotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoPath");
            final int _cursorIndexOfFechaMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaMillis");
            final int _cursorIndexOfValidada = CursorUtil.getColumnIndexOrThrow(_cursor, "validada");
            final int _cursorIndexOfPendienteSync = CursorUtil.getColumnIndexOrThrow(_cursor, "pendienteSync");
            final LongSparseArray<Predio> _collectionPredio = new LongSparseArray<Predio>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfPredioId);
              _collectionPredio.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipprediosAsclDuocAgronutriDataLocalPredio(_collectionPredio);
            final List<MedicionConPredio> _result = new ArrayList<MedicionConPredio>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final MedicionConPredio _item;
              final Medicion _tmpMedicion;
              final int _tmpId;
              _tmpId = _cursor.getInt(_cursorIndexOfId);
              final int _tmpPredioId;
              _tmpPredioId = _cursor.getInt(_cursorIndexOfPredioId);
              final String _tmpCultivo;
              _tmpCultivo = _cursor.getString(_cursorIndexOfCultivo);
              final String _tmpEtapa;
              _tmpEtapa = _cursor.getString(_cursorIndexOfEtapa);
              final double _tmpPh;
              _tmpPh = _cursor.getDouble(_cursorIndexOfPh);
              final double _tmpCe;
              _tmpCe = _cursor.getDouble(_cursorIndexOfCe);
              final double _tmpN;
              _tmpN = _cursor.getDouble(_cursorIndexOfN);
              final double _tmpP;
              _tmpP = _cursor.getDouble(_cursorIndexOfP);
              final double _tmpK;
              _tmpK = _cursor.getDouble(_cursorIndexOfK);
              final double _tmpCa;
              _tmpCa = _cursor.getDouble(_cursorIndexOfCa);
              final double _tmpMg;
              _tmpMg = _cursor.getDouble(_cursorIndexOfMg);
              final String _tmpFotoPath;
              if (_cursor.isNull(_cursorIndexOfFotoPath)) {
                _tmpFotoPath = null;
              } else {
                _tmpFotoPath = _cursor.getString(_cursorIndexOfFotoPath);
              }
              final long _tmpFechaMillis;
              _tmpFechaMillis = _cursor.getLong(_cursorIndexOfFechaMillis);
              final boolean _tmpValidada;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfValidada);
              _tmpValidada = _tmp != 0;
              final boolean _tmpPendienteSync;
              final int _tmp_1;
              _tmp_1 = _cursor.getInt(_cursorIndexOfPendienteSync);
              _tmpPendienteSync = _tmp_1 != 0;
              _tmpMedicion = new Medicion(_tmpId,_tmpPredioId,_tmpCultivo,_tmpEtapa,_tmpPh,_tmpCe,_tmpN,_tmpP,_tmpK,_tmpCa,_tmpMg,_tmpFotoPath,_tmpFechaMillis,_tmpValidada,_tmpPendienteSync);
              final Predio _tmpPredio;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfPredioId);
              _tmpPredio = _collectionPredio.get(_tmpKey_1);
              _item = new MedicionConPredio(_tmpMedicion,_tmpPredio);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<MedicionConPredio> observarDetalle(final int id) {
    final String _sql = "SELECT * FROM mediciones WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"predios",
        "mediciones"}, new Callable<MedicionConPredio>() {
      @Override
      @Nullable
      public MedicionConPredio call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfPredioId = CursorUtil.getColumnIndexOrThrow(_cursor, "predioId");
            final int _cursorIndexOfCultivo = CursorUtil.getColumnIndexOrThrow(_cursor, "cultivo");
            final int _cursorIndexOfEtapa = CursorUtil.getColumnIndexOrThrow(_cursor, "etapa");
            final int _cursorIndexOfPh = CursorUtil.getColumnIndexOrThrow(_cursor, "ph");
            final int _cursorIndexOfCe = CursorUtil.getColumnIndexOrThrow(_cursor, "ce");
            final int _cursorIndexOfN = CursorUtil.getColumnIndexOrThrow(_cursor, "n");
            final int _cursorIndexOfP = CursorUtil.getColumnIndexOrThrow(_cursor, "p");
            final int _cursorIndexOfK = CursorUtil.getColumnIndexOrThrow(_cursor, "k");
            final int _cursorIndexOfCa = CursorUtil.getColumnIndexOrThrow(_cursor, "ca");
            final int _cursorIndexOfMg = CursorUtil.getColumnIndexOrThrow(_cursor, "mg");
            final int _cursorIndexOfFotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoPath");
            final int _cursorIndexOfFechaMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaMillis");
            final int _cursorIndexOfValidada = CursorUtil.getColumnIndexOrThrow(_cursor, "validada");
            final int _cursorIndexOfPendienteSync = CursorUtil.getColumnIndexOrThrow(_cursor, "pendienteSync");
            final LongSparseArray<Predio> _collectionPredio = new LongSparseArray<Predio>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfPredioId);
              _collectionPredio.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipprediosAsclDuocAgronutriDataLocalPredio(_collectionPredio);
            final MedicionConPredio _result;
            if (_cursor.moveToFirst()) {
              final Medicion _tmpMedicion;
              final int _tmpId;
              _tmpId = _cursor.getInt(_cursorIndexOfId);
              final int _tmpPredioId;
              _tmpPredioId = _cursor.getInt(_cursorIndexOfPredioId);
              final String _tmpCultivo;
              _tmpCultivo = _cursor.getString(_cursorIndexOfCultivo);
              final String _tmpEtapa;
              _tmpEtapa = _cursor.getString(_cursorIndexOfEtapa);
              final double _tmpPh;
              _tmpPh = _cursor.getDouble(_cursorIndexOfPh);
              final double _tmpCe;
              _tmpCe = _cursor.getDouble(_cursorIndexOfCe);
              final double _tmpN;
              _tmpN = _cursor.getDouble(_cursorIndexOfN);
              final double _tmpP;
              _tmpP = _cursor.getDouble(_cursorIndexOfP);
              final double _tmpK;
              _tmpK = _cursor.getDouble(_cursorIndexOfK);
              final double _tmpCa;
              _tmpCa = _cursor.getDouble(_cursorIndexOfCa);
              final double _tmpMg;
              _tmpMg = _cursor.getDouble(_cursorIndexOfMg);
              final String _tmpFotoPath;
              if (_cursor.isNull(_cursorIndexOfFotoPath)) {
                _tmpFotoPath = null;
              } else {
                _tmpFotoPath = _cursor.getString(_cursorIndexOfFotoPath);
              }
              final long _tmpFechaMillis;
              _tmpFechaMillis = _cursor.getLong(_cursorIndexOfFechaMillis);
              final boolean _tmpValidada;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfValidada);
              _tmpValidada = _tmp != 0;
              final boolean _tmpPendienteSync;
              final int _tmp_1;
              _tmp_1 = _cursor.getInt(_cursorIndexOfPendienteSync);
              _tmpPendienteSync = _tmp_1 != 0;
              _tmpMedicion = new Medicion(_tmpId,_tmpPredioId,_tmpCultivo,_tmpEtapa,_tmpPh,_tmpCe,_tmpN,_tmpP,_tmpK,_tmpCa,_tmpMg,_tmpFotoPath,_tmpFechaMillis,_tmpValidada,_tmpPendienteSync);
              final Predio _tmpPredio;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfPredioId);
              _tmpPredio = _collectionPredio.get(_tmpKey_1);
              _result = new MedicionConPredio(_tmpMedicion,_tmpPredio);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> observarPendientes() {
    final String _sql = "SELECT COUNT(*) FROM mediciones WHERE pendienteSync = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"mediciones"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Object pendientes(final Continuation<? super List<Medicion>> $completion) {
    final String _sql = "SELECT * FROM mediciones WHERE pendienteSync = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Medicion>>() {
      @Override
      @NonNull
      public List<Medicion> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPredioId = CursorUtil.getColumnIndexOrThrow(_cursor, "predioId");
          final int _cursorIndexOfCultivo = CursorUtil.getColumnIndexOrThrow(_cursor, "cultivo");
          final int _cursorIndexOfEtapa = CursorUtil.getColumnIndexOrThrow(_cursor, "etapa");
          final int _cursorIndexOfPh = CursorUtil.getColumnIndexOrThrow(_cursor, "ph");
          final int _cursorIndexOfCe = CursorUtil.getColumnIndexOrThrow(_cursor, "ce");
          final int _cursorIndexOfN = CursorUtil.getColumnIndexOrThrow(_cursor, "n");
          final int _cursorIndexOfP = CursorUtil.getColumnIndexOrThrow(_cursor, "p");
          final int _cursorIndexOfK = CursorUtil.getColumnIndexOrThrow(_cursor, "k");
          final int _cursorIndexOfCa = CursorUtil.getColumnIndexOrThrow(_cursor, "ca");
          final int _cursorIndexOfMg = CursorUtil.getColumnIndexOrThrow(_cursor, "mg");
          final int _cursorIndexOfFotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoPath");
          final int _cursorIndexOfFechaMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaMillis");
          final int _cursorIndexOfValidada = CursorUtil.getColumnIndexOrThrow(_cursor, "validada");
          final int _cursorIndexOfPendienteSync = CursorUtil.getColumnIndexOrThrow(_cursor, "pendienteSync");
          final List<Medicion> _result = new ArrayList<Medicion>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Medicion _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpPredioId;
            _tmpPredioId = _cursor.getInt(_cursorIndexOfPredioId);
            final String _tmpCultivo;
            _tmpCultivo = _cursor.getString(_cursorIndexOfCultivo);
            final String _tmpEtapa;
            _tmpEtapa = _cursor.getString(_cursorIndexOfEtapa);
            final double _tmpPh;
            _tmpPh = _cursor.getDouble(_cursorIndexOfPh);
            final double _tmpCe;
            _tmpCe = _cursor.getDouble(_cursorIndexOfCe);
            final double _tmpN;
            _tmpN = _cursor.getDouble(_cursorIndexOfN);
            final double _tmpP;
            _tmpP = _cursor.getDouble(_cursorIndexOfP);
            final double _tmpK;
            _tmpK = _cursor.getDouble(_cursorIndexOfK);
            final double _tmpCa;
            _tmpCa = _cursor.getDouble(_cursorIndexOfCa);
            final double _tmpMg;
            _tmpMg = _cursor.getDouble(_cursorIndexOfMg);
            final String _tmpFotoPath;
            if (_cursor.isNull(_cursorIndexOfFotoPath)) {
              _tmpFotoPath = null;
            } else {
              _tmpFotoPath = _cursor.getString(_cursorIndexOfFotoPath);
            }
            final long _tmpFechaMillis;
            _tmpFechaMillis = _cursor.getLong(_cursorIndexOfFechaMillis);
            final boolean _tmpValidada;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfValidada);
            _tmpValidada = _tmp != 0;
            final boolean _tmpPendienteSync;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfPendienteSync);
            _tmpPendienteSync = _tmp_1 != 0;
            _item = new Medicion(_tmpId,_tmpPredioId,_tmpCultivo,_tmpEtapa,_tmpPh,_tmpCe,_tmpN,_tmpP,_tmpK,_tmpCa,_tmpMg,_tmpFotoPath,_tmpFechaMillis,_tmpValidada,_tmpPendienteSync);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object marcarSincronizadas(final List<Integer> ids,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE mediciones SET pendienteSync = 0 WHERE id IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (int _item : ids) {
          _stmt.bindLong(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipprediosAsclDuocAgronutriDataLocalPredio(
      @NonNull final LongSparseArray<Predio> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (map) -> {
        __fetchRelationshipprediosAsclDuocAgronutriDataLocalPredio(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`nombre`,`superficieHa` FROM `predios` WHERE `id` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "id");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfNombre = 1;
      final int _cursorIndexOfSuperficieHa = 2;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        if (_map.containsKey(_tmpKey)) {
          final Predio _item_1;
          final int _tmpId;
          _tmpId = _cursor.getInt(_cursorIndexOfId);
          final String _tmpNombre;
          _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
          final double _tmpSuperficieHa;
          _tmpSuperficieHa = _cursor.getDouble(_cursorIndexOfSuperficieHa);
          _item_1 = new Predio(_tmpId,_tmpNombre,_tmpSuperficieHa);
          _map.put(_tmpKey, _item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
