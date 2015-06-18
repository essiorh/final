package com.example.ilia.final_exercise.database;

import android.content.ContentValues;
import android.database.Cursor;

import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.*;

/**
 * Created by ilia on 16.06.15.
 */
public class GroupItem {
    private int _id;
    private String mTitle;


    public GroupItem(int id, String title) {
        this._id = id;
        this.mTitle = title;
    }

    public GroupItem(String title) {
        this (-1,title);
    }
    public ContentValues buildContentValues() {
        ContentValues cv = new ContentValues();
        if (_id >=0 ) {
            cv.put(COLUMN_ID,_id);
        }
        cv.put(COLUMN_TITLE, mTitle);

        return cv;
    }

    public static GroupItem fromCursor(Cursor c) {
        int idColId=c.getColumnIndex(COLUMN_ID);
        int titleColId=c.getColumnIndex(COLUMN_TITLE);

        return new GroupItem(
                c.getInt(idColId),
                c.getString(titleColId));
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }
}
