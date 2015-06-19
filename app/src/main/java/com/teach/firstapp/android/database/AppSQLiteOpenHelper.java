package com.teach.firstapp.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "app.db";
    private static final int DB_VERSION = 1;

    public static final String COLUMN_ID = "_id";

    public static final String TABLE_USERS = "users";
    public static final String USERS_COLUMN_NAME = "name";
    public static final String USERS_COLUMN_CODE = "code";

    private static final String CREATE_TABLE_USERS = "" +
            "CREATE TABLE " + TABLE_USERS + "(" +
            COLUMN_ID + " integer primary key autoincrement," +
            USERS_COLUMN_NAME   + " text," +
            USERS_COLUMN_CODE  + " integer" +
            ");";

    public AppSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String[] CREATES = {
                CREATE_TABLE_USERS,
        };
        for (final String table : CREATES) {
            db.execSQL(table);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String[] TABLES = {
                TABLE_USERS,
        };
        for (final String table : TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
    }
}
