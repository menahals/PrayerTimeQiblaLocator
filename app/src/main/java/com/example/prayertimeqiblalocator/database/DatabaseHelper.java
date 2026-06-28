package com.example.prayertimeqiblalocator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PrayerApp.db";
    private static final int DATABASE_VERSION = 1;

    // TABLE: settings
    public static final String TABLE_SETTINGS = "settings";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_TASBIH_COUNT = "tasbih_count";
    public static final String COLUMN_TASBIH_TARGET = "tasbih_target";
    public static final String COLUMN_24_HOUR = "time_format";
    public static final String COLUMN_NOTIFICATIONS = "notifications";
    public static final String COLUMN_HAPTIC = "haptic";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createSettingsTable =
                "CREATE TABLE " + TABLE_SETTINGS + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_CITY + " TEXT, " +
                        COLUMN_TASBIH_COUNT + " INTEGER, " +
                        COLUMN_TASBIH_TARGET + " INTEGER, " +
                        COLUMN_24_HOUR + " INTEGER, " +
                        COLUMN_NOTIFICATIONS + " INTEGER, " +
                        COLUMN_HAPTIC + " INTEGER" +
                        ")";

        db.execSQL(createSettingsTable);

        // Default row
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY, "Al Ain");
        values.put(COLUMN_TASBIH_COUNT, 0);
        values.put(COLUMN_TASBIH_TARGET, 33);
        values.put(COLUMN_24_HOUR, 1);
        values.put(COLUMN_NOTIFICATIONS, 1);
        values.put(COLUMN_HAPTIC, 1);

        db.insert(TABLE_SETTINGS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        onCreate(db);
    }

    // SAVE CITY
    public void updateCity(String city) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY, city);

        db.update(TABLE_SETTINGS, values, "id=?", new String[]{"1"});
    }

    // GET CITY
    public String getCity() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_CITY +
                        " FROM " + TABLE_SETTINGS +
                        " WHERE id=1", null);

        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }

        return "Al Ain";
    }

    // SAVE TASBIH COUNT
    public void updateTasbihCount(int count) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TASBIH_COUNT, count);

        db.update(TABLE_SETTINGS, values, "id=?", new String[]{"1"});
    }

    // GET TASBIH COUNT
    public int getTasbihCount() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_TASBIH_COUNT +
                        " FROM " + TABLE_SETTINGS +
                        " WHERE id=1", null);

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }

        return 0;
    }

    // SAVE TASBIH TARGET
    public void updateTasbihTarget(int target) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TASBIH_TARGET, target);

        db.update(TABLE_SETTINGS, values, "id=?", new String[]{"1"});
    }

    // GET TASBIH TARGET
    public int getTasbihTarget() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_TASBIH_TARGET +
                        " FROM " + TABLE_SETTINGS +
                        " WHERE id=1", null);

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }

        return 33;
    }
}