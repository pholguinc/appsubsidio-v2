package com.example.edusubsidio.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "beeqrA.db";
    private static final int DATABASE_VERSION = 1;
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String setTimeZoneQuery = "PRAGMA foreign_keys = ON;";
        db.execSQL(setTimeZoneQuery);
        String setTimeZone = "PRAGMA timezone = 'auto';";
        db.execSQL(setTimeZone);

        String createTableUsuarioSQL = "CREATE TABLE usuario ("
                + "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "usu_usuario TEXT, "
                + "usu_contra TEXT)";
        db.execSQL(createTableUsuarioSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
