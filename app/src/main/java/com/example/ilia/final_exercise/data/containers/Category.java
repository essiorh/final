package com.example.ilia.final_exercise.data.containers;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import static com.example.ilia.final_exercise.data.model.OpenDBHelper.CATEGORIES_TITLE;
import static com.example.ilia.final_exercise.data.model.OpenDBHelper.COLUMN_ID;

/**
 * Created by ilia on 16.06.15.
 *
 * @author ilia
 */
public class Category implements Parcelable {

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
    private long id;
    private String title;

    public Category(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Category(Parcel in) {
        id = in.readLong();
        title = in.readString();
    }

    public static Category fromCursor(Cursor c) {
        int idColId = c.getColumnIndex(COLUMN_ID);
        int titleColId = c.getColumnIndex(CATEGORIES_TITLE);

        return new Category(
                c.getLong(idColId),
                c.getString(titleColId));

    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }

    public ContentValues buildContentValues() {
        ContentValues cv = new ContentValues();
        if (id >= 0) {
            cv.put(COLUMN_ID, id);
        }
        cv.put(CATEGORIES_TITLE, title);
        return cv;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
    }
}