package com.teach.firstapp.android.database;

import android.content.ContentValues;
import android.database.Cursor;

public class User {

    private long id;
    private String name;
    private int code;

    private User(long id, String name, int code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public User(String name, int code) {
        this(-1, name, code);
    }


    public ContentValues buildContentValues() {
        ContentValues cv = new ContentValues();
        if (id >= 0) {
            cv.put(AppSQLiteOpenHelper.COLUMN_ID, id);
        }
        cv.put(AppSQLiteOpenHelper.USERS_COLUMN_NAME, name);
        cv.put(AppSQLiteOpenHelper.USERS_COLUMN_CODE, code);
        return cv;
    }

    public static User fromCursor(Cursor c){
        int idColId = c.getColumnIndex(AppSQLiteOpenHelper.COLUMN_ID);
        int nameColId = c.getColumnIndex(AppSQLiteOpenHelper.USERS_COLUMN_NAME);
        int codeColId = c.getColumnIndex(AppSQLiteOpenHelper.USERS_COLUMN_CODE);

        return new User(
                c.getInt(idColId),
                c.getString(nameColId),
                c.getInt(codeColId));
    }
}
