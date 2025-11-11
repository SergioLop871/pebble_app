package com.example.pebble_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Pebble.db";
    private static final int DATABASE_VERSION = 1;

    // Tables

    // FOCUS SESSION
    private static final String TABLE_FOCUS_SESSION = "focus_session";
    private static final String COLUMN_FOCUS_SESSION_ID = "id";
    private static final String COLUMN_FOCUS_SESSION_NAME = "focus_session_name";
    private static final String COLUMN_FOCUS_SESSION_DESCRIPTION = "focus_session_description";
    private static final String COLUMN_FOCUS_SESSION_EMOJI = "focus_session_emoji";
    private static final String COLUMN_FOCUS_SESSION_BEGIN_TIME = "focus_session_begin_time";
    private static final String COLUMN_FOCUS_SESSION_END_TIME = "focus_session_end_time";

    // SESSION DAY
    private static final String TABLE_SESSION_DAY = "session_day";
    private static final String COLUMN_SESSION_DAY_ID = "id";
    private static final String COLUMN_SESSION_ID = "focus_session_id";
    private static final String COLUMN_WEEK_DAY = "week_day";

    // SESSION APP
    private static final String TABLE_SESSION_APP = "session_app";
    private static final String COLUMN_SESSION_APP_ID = "id";
    private static final String COLUMN_APP_NAME = "app_name";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_fs_table_query = "CREATE TABLE " + TABLE_FOCUS_SESSION +
                        " (" + COLUMN_FOCUS_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_FOCUS_SESSION_NAME + " TEXT, " +
                        COLUMN_FOCUS_SESSION_DESCRIPTION + " TEXT, " +
                        COLUMN_FOCUS_SESSION_EMOJI + " TEXT, " +
                        COLUMN_FOCUS_SESSION_BEGIN_TIME + " TEXT, " +
                        COLUMN_FOCUS_SESSION_END_TIME + " TEXT);";
        String create_sd_table_query =  "CREATE TABLE " + TABLE_SESSION_DAY +
                " (" + COLUMN_SESSION_DAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SESSION_ID + " INTEGER, " +
                COLUMN_WEEK_DAY + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_SESSION_ID + ") REFERENCES " + TABLE_FOCUS_SESSION + "(" + COLUMN_FOCUS_SESSION_ID + ") ON DELETE CASCADE);";
        String create_sa_table_query =  "CREATE TABLE " + TABLE_SESSION_APP +
                " (" + COLUMN_SESSION_APP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SESSION_ID + " INTEGER, " +
                COLUMN_APP_NAME + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_SESSION_ID + ") REFERENCES " + TABLE_FOCUS_SESSION + "(" + COLUMN_FOCUS_SESSION_ID + ") ON DELETE CASCADE);";
        db.execSQL(create_fs_table_query);
        db.execSQL(create_sd_table_query);
        db.execSQL(create_sa_table_query);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOCUS_SESSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION_DAY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION_APP);
        onCreate(db);
    }

    // Guardar una sesión de enfoque en la base de datos (local)
    void addFocusSession(String name, String description, String emoji, String begin_time, String end_time, ArrayList<String> days, ArrayList<String> apps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv =  new ContentValues();

        cv.put(COLUMN_FOCUS_SESSION_NAME, name);
        cv.put(COLUMN_FOCUS_SESSION_DESCRIPTION, description);
        cv.put(COLUMN_FOCUS_SESSION_EMOJI, emoji);
        cv.put(COLUMN_FOCUS_SESSION_BEGIN_TIME, begin_time);
        cv.put(COLUMN_FOCUS_SESSION_END_TIME, end_time);

        // Insertar la sesión
        long result = db.insert(TABLE_FOCUS_SESSION, null, cv);

        if (result == -1) {
            Toast.makeText(context, "Error al crear sesión de enfoque", Toast.LENGTH_SHORT).show();
        }

        // Insertar los días asociados (en TABLE_SESSION_DAY)
        for (String day : days) {
            ContentValues dayValues = new ContentValues();
            dayValues.put(COLUMN_SESSION_ID, result);  // referencia a la sesión creada
            dayValues.put(COLUMN_WEEK_DAY, day);
            db.insert(TABLE_SESSION_DAY, null, dayValues);
        }

        // Insertar las apps a bloquear por la sesión (en TABLE_SESSION_APP)
        for (String app : apps) {
            ContentValues appsValues = new ContentValues();
            appsValues.put(COLUMN_SESSION_ID, result);
            appsValues.put(COLUMN_APP_NAME, app);
            db.insert(TABLE_SESSION_APP, null, appsValues);
        }

        Toast.makeText(context, "Sesión de enfoque creada", Toast.LENGTH_SHORT).show();
    }

    public void deleteFocusSession(int sessionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FOCUS_SESSION, COLUMN_FOCUS_SESSION_ID + " = ?", new String[]{String.valueOf(sessionId)});
    }

    void updateFocusSession(int sessionId, String name, String description, String emoji,
                            String begin_time, String end_time,
                            ArrayList<String> days, ArrayList<String> apps) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Actualizar sesión
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FOCUS_SESSION_NAME, name);
        cv.put(COLUMN_FOCUS_SESSION_DESCRIPTION, description);
        cv.put(COLUMN_FOCUS_SESSION_EMOJI, emoji);
        cv.put(COLUMN_FOCUS_SESSION_BEGIN_TIME, begin_time);
        cv.put(COLUMN_FOCUS_SESSION_END_TIME, end_time);

        db.update(TABLE_FOCUS_SESSION, cv, COLUMN_FOCUS_SESSION_ID + " = ?",
                new String[]{String.valueOf(sessionId)});

        // Borrar días y volver a agregar
        db.delete(TABLE_SESSION_DAY, COLUMN_SESSION_ID + " = ?", new String[]{String.valueOf(sessionId)});
        for (String day : days) {
            ContentValues dayValues = new ContentValues();
            dayValues.put(COLUMN_SESSION_ID, sessionId);
            dayValues.put(COLUMN_WEEK_DAY, day);
            db.insert(TABLE_SESSION_DAY, null, dayValues);
        }

        // Borrar apps y volver a agregar
        db.delete(TABLE_SESSION_APP, COLUMN_SESSION_ID + " = ?", new String[]{String.valueOf(sessionId)});
        for (String app : apps) {
            ContentValues appValues = new ContentValues();
            appValues.put(COLUMN_SESSION_ID, sessionId);
            appValues.put(COLUMN_APP_NAME, app);
            db.insert(TABLE_SESSION_APP, null, appValues);
        }
    }

    Cursor readAllFocusData() {
        String query = "SELECT * FROM " + TABLE_FOCUS_SESSION;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    Cursor ReadFocusSession(long sessionId) {
        String query = "SELECT " + COLUMN_FOCUS_SESSION_NAME + " ," + COLUMN_FOCUS_SESSION_DESCRIPTION +
                " ," + COLUMN_FOCUS_SESSION_EMOJI + " ," + COLUMN_FOCUS_SESSION_BEGIN_TIME + " ," +
                COLUMN_FOCUS_SESSION_END_TIME + " FROM " + TABLE_FOCUS_SESSION +
                " WHERE " + COLUMN_FOCUS_SESSION_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            String[] selectionArgs = { String.valueOf(sessionId) };
            cursor = db.rawQuery(query, selectionArgs);
        }
        return cursor;
    }

    Cursor ReadSessionDays(long sessionId) {
        String query = "SELECT " + COLUMN_WEEK_DAY + " FROM " + TABLE_SESSION_DAY +
                " WHERE " + COLUMN_SESSION_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            String[] selectionArgs = { String.valueOf(sessionId) };
            cursor = db.rawQuery(query, selectionArgs);
        }
        return cursor;
    }

    Cursor ReadSessionApps(long sessionId) {
        String query = "SELECT " + COLUMN_APP_NAME + " FROM " + TABLE_SESSION_APP +
                " WHERE " + COLUMN_SESSION_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            String[] selectionArgs = { String.valueOf(sessionId) };
            cursor = db.rawQuery(query, selectionArgs);
        }
        return cursor;
    }
}