package com.example.ilia.final_exercise.data.containers;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.ilia.final_exercise.data.model.OpenDBHelper;

/**
 * Created by ilia on 16.06.15.
 * @author ilia
 */
public class Category implements Parcelable {
	private long id;
	private String title;

	public Category(long id, String title) {
		this.id = id;
		this.title = title;
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

	public static Category fromCursor(Cursor c) {
		int idColId = c.getColumnIndex(OpenDBHelper.COLUMN_ID);
		int titleColId = c.getColumnIndex(OpenDBHelper.CATEGORIES_TITLE);

		return new Category(
				c.getLong(idColId),
				c.getString(titleColId));

	}

	//-------------- Parcelable -----------------

	public ContentValues buildContentValues() {
		ContentValues cv = new ContentValues();
		if (id >= 0) {
			cv.put(OpenDBHelper.COLUMN_ID, id);
		}
		cv.put(OpenDBHelper.CATEGORIES_TITLE, title);
		return cv;
	}

	public Category(Parcel in) {
		id = in.readLong();
		title = in.readString();
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

	public static final Creator<Category> CREATOR = new Creator<Category>() {
		// распаковываем объект из Parcel
		public Category createFromParcel(Parcel in) {
			return new Category(in);
		}

		public Category[] newArray(int size) {
			return new Category[size];
		}
	};
}