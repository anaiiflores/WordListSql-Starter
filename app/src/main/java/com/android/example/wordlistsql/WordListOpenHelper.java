package com.android.example.wordlistsql;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WordListOpenHelper extends SQLiteOpenHelper {

    // Log tag
    private static final String TAG = WordListOpenHelper.class.getSimpleName();

    // Versión (si subes esto, se ejecuta onUpgrade)
    private static final int DATABASE_VERSION = 1;

    // Nombre BD
    private static final String DATABASE_NAME = "wordlist";

    // Tabla
    private static final String WORD_LIST_TABLE = "word_entries";

    // Columnas
    public static final String KEY_ID = "_id";
    public static final String KEY_WORD = "word";

    // Columnas para query simples
    private static final String[] COLUMNS = { KEY_ID, KEY_WORD };

    // SQL: crear tabla
    private static final String WORD_LIST_TABLE_CREATE =
            "CREATE TABLE " + WORD_LIST_TABLE + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY, " +  // autoincrement si no pasas id
                    KEY_WORD + " TEXT );";

    // Guardamos referencias para no pedir db cada vez
    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public WordListOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Se llama SOLO la primera vez que se crea la BD (o tras borrar datos)
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1) Crear tabla
        db.execSQL(WORD_LIST_TABLE_CREATE);

        // 2) Cargar datos “semilla”
        fillDatabaseWithData(db);
    }

    // Si cambias DATABASE_VERSION, se llama aquí
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ". Destroying old data.");
        db.execSQL("DROP TABLE IF EXISTS " + WORD_LIST_TABLE);
        onCreate(db); // recrea tabla + seed
    }

    // --- Seed: inserta palabras al crear BD ---
    private void fillDatabaseWithData(SQLiteDatabase db) {
        String[] words = {
                "Android", "Adapter", "ListView", "AsyncTask",
                "Android Studio", "SQLiteDatabase", "SQLiteOpenHelper",
                "Data model", "ViewHolder", "Android Performance",
                "OnClickListener", "LinkedList"
        };

        ContentValues values = new ContentValues();

        for (String w : words) {
            values.put(KEY_WORD, w);     // columna -> valor
            db.insert(WORD_LIST_TABLE, null, values);
        }
    }

    // --- Task 3: query por posición (para mostrar en RecyclerView) ---
    public WordItem query(int position) {
        // LIMIT position,1 -> “dame solo 1 fila empezando en position”
        String query = "SELECT * FROM " + WORD_LIST_TABLE +
                " ORDER BY " + KEY_WORD + " ASC " +
                " LIMIT " + position + ",1";

        Cursor cursor = null;
        WordItem entry = new WordItem();

        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }

            cursor = mReadableDB.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int wordIndex = cursor.getColumnIndex(KEY_WORD);

                entry.setId(cursor.getInt(idIndex));
                entry.setWord(cursor.getString(wordIndex));
            }

        } catch (Exception e) {
            Log.d(TAG, "QUERY EXCEPTION! " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }

        return entry;
    }

    // --- Task 5: insert ---
    public long insert(String word) {
        long newId = 0;

        ContentValues values = new ContentValues();
        values.put(KEY_WORD, word);

        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }

            newId = mWritableDB.insert(WORD_LIST_TABLE, null, values);

        } catch (Exception e) {
            Log.d(TAG, "INSERT EXCEPTION! " + e.getMessage());
        }

        return newId;
    }

    // --- Task 5.3: count para el RecyclerView ---
    public long count() {
        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            return DatabaseUtils.queryNumEntries(mReadableDB, WORD_LIST_TABLE);
        } catch (Exception e) {
            Log.d(TAG, "COUNT EXCEPTION! " + e.getMessage());
            return 0;
        }
    }

    // --- Task 6: delete por id ---
    public int delete(int id) {
        int deleted = 0;

        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }

            deleted = mWritableDB.delete(
                    WORD_LIST_TABLE,
                    KEY_ID + " = ?",
                    new String[]{ String.valueOf(id) }
            );

        } catch (Exception e) {
            Log.d(TAG, "DELETE EXCEPTION! " + e.getMessage());
        }

        return deleted;
    }

    // --- Task 7: update por id ---
    public int update(int id, String word) {
        int rowsUpdated = -1;

        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }

            ContentValues values = new ContentValues();
            values.put(KEY_WORD, word);

            rowsUpdated = mWritableDB.update(
                    WORD_LIST_TABLE,
                    values,
                    KEY_ID + " = ?",
                    new String[]{ String.valueOf(id) }
            );

        } catch (Exception e) {
            Log.d(TAG, "UPDATE EXCEPTION! " + e.getMessage());
        }

        return rowsUpdated;
    }

    // --- 10.1B Search: buscar substring usando query() (más seguro) ---
    public Cursor search(String searchString) {
        // Solo queremos la columna word
        String[] columns = new String[]{ KEY_WORD };

        // LIKE necesita % para buscar “contiene”
        searchString = "%" + searchString + "%";

        String where = KEY_WORD + " LIKE ?";
        String[] whereArgs = new String[]{ searchString };

        Cursor cursor = null;

        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }

            cursor = mReadableDB.query(
                    WORD_LIST_TABLE,   // table
                    columns,           // columns
                    where,             // selection (sin WHERE)
                    whereArgs,         // selectionArgs
                    null,              // groupBy
                    null,              // having
                    KEY_WORD + " ASC"  // orderBy
            );

        } catch (Exception e) {
            Log.d(TAG, "SEARCH EXCEPTION! " + e.getMessage());
        }

        return cursor;
    }
}