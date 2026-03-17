package com.example.petmedtracker.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.petmedtracker.data.local.entity.PetEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class PetDao_Impl implements PetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PetEntity> __insertionAdapterOfPetEntity;

  public PetDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPetEntity = new EntityInsertionAdapter<PetEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `pets` (`id`,`name`,`species`,`breed`,`birthday`) VALUES (?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, PetEntity value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        if (value.getSpecies() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getSpecies());
        }
        if (value.getBreed() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getBreed());
        }
        if (value.getBirthday() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getBirthday());
        }
      }
    };
  }

  @Override
  public Object insertAll(final List<PetEntity> pets,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPetEntity.insert(pets);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object insert(final PetEntity pet, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPetEntity.insert(pet);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<PetEntity>> getPets() {
    final String _sql = "SELECT * FROM pets ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"pets"}, new Callable<List<PetEntity>>() {
      @Override
      public List<PetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfSpecies = CursorUtil.getColumnIndexOrThrow(_cursor, "species");
          final int _cursorIndexOfBreed = CursorUtil.getColumnIndexOrThrow(_cursor, "breed");
          final int _cursorIndexOfBirthday = CursorUtil.getColumnIndexOrThrow(_cursor, "birthday");
          final List<PetEntity> _result = new ArrayList<PetEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final PetEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpSpecies;
            if (_cursor.isNull(_cursorIndexOfSpecies)) {
              _tmpSpecies = null;
            } else {
              _tmpSpecies = _cursor.getString(_cursorIndexOfSpecies);
            }
            final String _tmpBreed;
            if (_cursor.isNull(_cursorIndexOfBreed)) {
              _tmpBreed = null;
            } else {
              _tmpBreed = _cursor.getString(_cursorIndexOfBreed);
            }
            final String _tmpBirthday;
            if (_cursor.isNull(_cursorIndexOfBirthday)) {
              _tmpBirthday = null;
            } else {
              _tmpBirthday = _cursor.getString(_cursorIndexOfBirthday);
            }
            _item = new PetEntity(_tmpId,_tmpName,_tmpSpecies,_tmpBreed,_tmpBirthday);
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
  public Flow<PetEntity> getPetById(final String id) {
    final String _sql = "SELECT * FROM pets WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[]{"pets"}, new Callable<PetEntity>() {
      @Override
      public PetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfSpecies = CursorUtil.getColumnIndexOrThrow(_cursor, "species");
          final int _cursorIndexOfBreed = CursorUtil.getColumnIndexOrThrow(_cursor, "breed");
          final int _cursorIndexOfBirthday = CursorUtil.getColumnIndexOrThrow(_cursor, "birthday");
          final PetEntity _result;
          if(_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpSpecies;
            if (_cursor.isNull(_cursorIndexOfSpecies)) {
              _tmpSpecies = null;
            } else {
              _tmpSpecies = _cursor.getString(_cursorIndexOfSpecies);
            }
            final String _tmpBreed;
            if (_cursor.isNull(_cursorIndexOfBreed)) {
              _tmpBreed = null;
            } else {
              _tmpBreed = _cursor.getString(_cursorIndexOfBreed);
            }
            final String _tmpBirthday;
            if (_cursor.isNull(_cursorIndexOfBirthday)) {
              _tmpBirthday = null;
            } else {
              _tmpBirthday = _cursor.getString(_cursorIndexOfBirthday);
            }
            _result = new PetEntity(_tmpId,_tmpName,_tmpSpecies,_tmpBreed,_tmpBirthday);
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
  public Object getPetByIdOnce(final String id,
      final Continuation<? super PetEntity> continuation) {
    final String _sql = "SELECT * FROM pets WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PetEntity>() {
      @Override
      public PetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfSpecies = CursorUtil.getColumnIndexOrThrow(_cursor, "species");
          final int _cursorIndexOfBreed = CursorUtil.getColumnIndexOrThrow(_cursor, "breed");
          final int _cursorIndexOfBirthday = CursorUtil.getColumnIndexOrThrow(_cursor, "birthday");
          final PetEntity _result;
          if(_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpSpecies;
            if (_cursor.isNull(_cursorIndexOfSpecies)) {
              _tmpSpecies = null;
            } else {
              _tmpSpecies = _cursor.getString(_cursorIndexOfSpecies);
            }
            final String _tmpBreed;
            if (_cursor.isNull(_cursorIndexOfBreed)) {
              _tmpBreed = null;
            } else {
              _tmpBreed = _cursor.getString(_cursorIndexOfBreed);
            }
            final String _tmpBirthday;
            if (_cursor.isNull(_cursorIndexOfBirthday)) {
              _tmpBirthday = null;
            } else {
              _tmpBirthday = _cursor.getString(_cursorIndexOfBirthday);
            }
            _result = new PetEntity(_tmpId,_tmpName,_tmpSpecies,_tmpBreed,_tmpBirthday);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
