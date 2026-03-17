package com.example.petmedtracker.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.petmedtracker.data.local.entity.MedicationEntity;
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
public final class MedicationDao_Impl implements MedicationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MedicationEntity> __insertionAdapterOfMedicationEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public MedicationDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMedicationEntity = new EntityInsertionAdapter<MedicationEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `medications` (`id`,`petId`,`medicationName`,`dosage`,`frequency`,`notesInstructions`,`startDate`,`duration`,`voiceNotePath`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, MedicationEntity value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getPetId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getPetId());
        }
        if (value.getMedicationName() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getMedicationName());
        }
        if (value.getDosage() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDosage());
        }
        if (value.getFrequency() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getFrequency());
        }
        if (value.getNotesInstructions() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getNotesInstructions());
        }
        if (value.getStartDate() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getStartDate());
        }
        if (value.getDuration() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getDuration());
        }
        if (value.getVoiceNotePath() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getVoiceNotePath());
        }
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM medications WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<MedicationEntity> medications,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMedicationEntity.insert(medications);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object insert(final MedicationEntity medication,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMedicationEntity.insert(medication);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteById(final String id, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        if (id == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, id);
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<MedicationEntity>> getMedicationsByPetId(final String petId) {
    final String _sql = "SELECT * FROM medications WHERE petId = ? ORDER BY startDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (petId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, petId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[]{"medications"}, new Callable<List<MedicationEntity>>() {
      @Override
      public List<MedicationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPetId = CursorUtil.getColumnIndexOrThrow(_cursor, "petId");
          final int _cursorIndexOfMedicationName = CursorUtil.getColumnIndexOrThrow(_cursor, "medicationName");
          final int _cursorIndexOfDosage = CursorUtil.getColumnIndexOrThrow(_cursor, "dosage");
          final int _cursorIndexOfFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "frequency");
          final int _cursorIndexOfNotesInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "notesInstructions");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfVoiceNotePath = CursorUtil.getColumnIndexOrThrow(_cursor, "voiceNotePath");
          final List<MedicationEntity> _result = new ArrayList<MedicationEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final MedicationEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpPetId;
            if (_cursor.isNull(_cursorIndexOfPetId)) {
              _tmpPetId = null;
            } else {
              _tmpPetId = _cursor.getString(_cursorIndexOfPetId);
            }
            final String _tmpMedicationName;
            if (_cursor.isNull(_cursorIndexOfMedicationName)) {
              _tmpMedicationName = null;
            } else {
              _tmpMedicationName = _cursor.getString(_cursorIndexOfMedicationName);
            }
            final String _tmpDosage;
            if (_cursor.isNull(_cursorIndexOfDosage)) {
              _tmpDosage = null;
            } else {
              _tmpDosage = _cursor.getString(_cursorIndexOfDosage);
            }
            final String _tmpFrequency;
            if (_cursor.isNull(_cursorIndexOfFrequency)) {
              _tmpFrequency = null;
            } else {
              _tmpFrequency = _cursor.getString(_cursorIndexOfFrequency);
            }
            final String _tmpNotesInstructions;
            if (_cursor.isNull(_cursorIndexOfNotesInstructions)) {
              _tmpNotesInstructions = null;
            } else {
              _tmpNotesInstructions = _cursor.getString(_cursorIndexOfNotesInstructions);
            }
            final String _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate);
            }
            final String _tmpDuration;
            if (_cursor.isNull(_cursorIndexOfDuration)) {
              _tmpDuration = null;
            } else {
              _tmpDuration = _cursor.getString(_cursorIndexOfDuration);
            }
            final String _tmpVoiceNotePath;
            if (_cursor.isNull(_cursorIndexOfVoiceNotePath)) {
              _tmpVoiceNotePath = null;
            } else {
              _tmpVoiceNotePath = _cursor.getString(_cursorIndexOfVoiceNotePath);
            }
            _item = new MedicationEntity(_tmpId,_tmpPetId,_tmpMedicationName,_tmpDosage,_tmpFrequency,_tmpNotesInstructions,_tmpStartDate,_tmpDuration,_tmpVoiceNotePath);
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
  public Object getMedicationsByPetIdOnce(final String petId,
      final Continuation<? super List<MedicationEntity>> continuation) {
    final String _sql = "SELECT * FROM medications WHERE petId = ? ORDER BY startDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (petId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, petId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MedicationEntity>>() {
      @Override
      public List<MedicationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPetId = CursorUtil.getColumnIndexOrThrow(_cursor, "petId");
          final int _cursorIndexOfMedicationName = CursorUtil.getColumnIndexOrThrow(_cursor, "medicationName");
          final int _cursorIndexOfDosage = CursorUtil.getColumnIndexOrThrow(_cursor, "dosage");
          final int _cursorIndexOfFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "frequency");
          final int _cursorIndexOfNotesInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "notesInstructions");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfVoiceNotePath = CursorUtil.getColumnIndexOrThrow(_cursor, "voiceNotePath");
          final List<MedicationEntity> _result = new ArrayList<MedicationEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final MedicationEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpPetId;
            if (_cursor.isNull(_cursorIndexOfPetId)) {
              _tmpPetId = null;
            } else {
              _tmpPetId = _cursor.getString(_cursorIndexOfPetId);
            }
            final String _tmpMedicationName;
            if (_cursor.isNull(_cursorIndexOfMedicationName)) {
              _tmpMedicationName = null;
            } else {
              _tmpMedicationName = _cursor.getString(_cursorIndexOfMedicationName);
            }
            final String _tmpDosage;
            if (_cursor.isNull(_cursorIndexOfDosage)) {
              _tmpDosage = null;
            } else {
              _tmpDosage = _cursor.getString(_cursorIndexOfDosage);
            }
            final String _tmpFrequency;
            if (_cursor.isNull(_cursorIndexOfFrequency)) {
              _tmpFrequency = null;
            } else {
              _tmpFrequency = _cursor.getString(_cursorIndexOfFrequency);
            }
            final String _tmpNotesInstructions;
            if (_cursor.isNull(_cursorIndexOfNotesInstructions)) {
              _tmpNotesInstructions = null;
            } else {
              _tmpNotesInstructions = _cursor.getString(_cursorIndexOfNotesInstructions);
            }
            final String _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate);
            }
            final String _tmpDuration;
            if (_cursor.isNull(_cursorIndexOfDuration)) {
              _tmpDuration = null;
            } else {
              _tmpDuration = _cursor.getString(_cursorIndexOfDuration);
            }
            final String _tmpVoiceNotePath;
            if (_cursor.isNull(_cursorIndexOfVoiceNotePath)) {
              _tmpVoiceNotePath = null;
            } else {
              _tmpVoiceNotePath = _cursor.getString(_cursorIndexOfVoiceNotePath);
            }
            _item = new MedicationEntity(_tmpId,_tmpPetId,_tmpMedicationName,_tmpDosage,_tmpFrequency,_tmpNotesInstructions,_tmpStartDate,_tmpDuration,_tmpVoiceNotePath);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  @Override
  public Object getMedicationById(final String id,
      final Continuation<? super MedicationEntity> continuation) {
    final String _sql = "SELECT * FROM medications WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MedicationEntity>() {
      @Override
      public MedicationEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPetId = CursorUtil.getColumnIndexOrThrow(_cursor, "petId");
          final int _cursorIndexOfMedicationName = CursorUtil.getColumnIndexOrThrow(_cursor, "medicationName");
          final int _cursorIndexOfDosage = CursorUtil.getColumnIndexOrThrow(_cursor, "dosage");
          final int _cursorIndexOfFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "frequency");
          final int _cursorIndexOfNotesInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "notesInstructions");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfVoiceNotePath = CursorUtil.getColumnIndexOrThrow(_cursor, "voiceNotePath");
          final MedicationEntity _result;
          if(_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpPetId;
            if (_cursor.isNull(_cursorIndexOfPetId)) {
              _tmpPetId = null;
            } else {
              _tmpPetId = _cursor.getString(_cursorIndexOfPetId);
            }
            final String _tmpMedicationName;
            if (_cursor.isNull(_cursorIndexOfMedicationName)) {
              _tmpMedicationName = null;
            } else {
              _tmpMedicationName = _cursor.getString(_cursorIndexOfMedicationName);
            }
            final String _tmpDosage;
            if (_cursor.isNull(_cursorIndexOfDosage)) {
              _tmpDosage = null;
            } else {
              _tmpDosage = _cursor.getString(_cursorIndexOfDosage);
            }
            final String _tmpFrequency;
            if (_cursor.isNull(_cursorIndexOfFrequency)) {
              _tmpFrequency = null;
            } else {
              _tmpFrequency = _cursor.getString(_cursorIndexOfFrequency);
            }
            final String _tmpNotesInstructions;
            if (_cursor.isNull(_cursorIndexOfNotesInstructions)) {
              _tmpNotesInstructions = null;
            } else {
              _tmpNotesInstructions = _cursor.getString(_cursorIndexOfNotesInstructions);
            }
            final String _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate);
            }
            final String _tmpDuration;
            if (_cursor.isNull(_cursorIndexOfDuration)) {
              _tmpDuration = null;
            } else {
              _tmpDuration = _cursor.getString(_cursorIndexOfDuration);
            }
            final String _tmpVoiceNotePath;
            if (_cursor.isNull(_cursorIndexOfVoiceNotePath)) {
              _tmpVoiceNotePath = null;
            } else {
              _tmpVoiceNotePath = _cursor.getString(_cursorIndexOfVoiceNotePath);
            }
            _result = new MedicationEntity(_tmpId,_tmpPetId,_tmpMedicationName,_tmpDosage,_tmpFrequency,_tmpNotesInstructions,_tmpStartDate,_tmpDuration,_tmpVoiceNotePath);
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
