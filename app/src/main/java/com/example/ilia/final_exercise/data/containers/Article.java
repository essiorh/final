package com.example.ilia.final_exercise.data.containers;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.ilia.final_exercise.data.model.OpenDBHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by ilia on 16.06.15.
 * @author ilia
 */
public class Article implements Parcelable {
	private long id;
	@Expose
	private String title;
	@Expose
	private String description;
	private PhotoContainer photo;
	@Expose
	private boolean published;
	@Expose
	@SerializedName("category_id")
	private long categoryId;
	@SerializedName("created_at")
	private Date createdAt;
	@SerializedName("updated_at")
	private Date updatedAt;
	private boolean own;

	public Article(long id
			, String title, String description, String photoUrl, boolean isPublished
			, long categoryId, long created, long updated, boolean own) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.photo = new PhotoContainer(photoUrl);
		this.published = isPublished;
		this.categoryId = categoryId;
		this.createdAt = new Date(created);
		this.updatedAt = new Date(updated);
		this.own = own;

	}

	public ContentValues buildContentValues() {
		ContentValues cv = new ContentValues();
		if (id >= 0) {
			cv.put(OpenDBHelper.COLUMN_ID, id);
		}
		cv.put(OpenDBHelper.ARTICLES_TITLE, title);
		cv.put(OpenDBHelper.ARTICLES_DESCRIPTION, description);
		if (photo != null) {
			cv.put(OpenDBHelper.ARTICLES_PHOTO_URL, photo.getUrl());
		}
		cv.put(OpenDBHelper.ARTICLES_PUBLISHED, published);
		cv.put(OpenDBHelper.ARTICLES_CATEGORY_ID, categoryId);
		cv.put(OpenDBHelper.ARTICLES_CREATED, createdAt.getTime());
		cv.put(OpenDBHelper.ARTICLES_UPDATED, updatedAt.getTime());
		cv.put(OpenDBHelper.ARTICLES_OWN, own);

		return cv;
	}

	public static Article fromCursor(Cursor c) {
		int idColId = c.getColumnIndex(OpenDBHelper.COLUMN_ID);
		int titleColId = c.getColumnIndex(OpenDBHelper.ARTICLES_TITLE);
		int descriptionColId = c.getColumnIndex(OpenDBHelper.ARTICLES_DESCRIPTION);
		int photoUrlColId = c.getColumnIndex(OpenDBHelper.ARTICLES_PHOTO_URL);
		int isPublishColId = c.getColumnIndex(OpenDBHelper.ARTICLES_PUBLISHED);
		int categoryColId = c.getColumnIndex(OpenDBHelper.ARTICLES_CATEGORY_ID);
		int createdColId = c.getColumnIndex(OpenDBHelper.ARTICLES_CREATED);
		int updatedColId = c.getColumnIndex(OpenDBHelper.ARTICLES_PUBLISHED);
		int isMineColId = c.getColumnIndex(OpenDBHelper.ARTICLES_OWN);

		return new Article(
				c.getLong(idColId),
				c.getString(titleColId),
				c.getString(descriptionColId),
				c.getString(photoUrlColId),
				c.getInt(isPublishColId) == 1,
				c.getLong(categoryColId),
				c.getLong(createdColId),
				c.getLong(updatedColId),
				c.getInt(isMineColId) == 1
		);
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getPhotoUrl() {
		return photo.getUrl();
	}

	public boolean isPublished() {
		return published;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public Date getCreated() {
		return createdAt;
	}

	public boolean getIsMine() {
		return own;
	}

	public Article(Parcel in) {

		id = in.readLong();
		title = in.readString();
		description = in.readString();
		photo = in.readParcelable(PhotoContainer.class.getClassLoader());
		published = in.readInt() == 1;
		categoryId = in.readLong();
		createdAt = new Date(in.readLong());
		updatedAt = new Date(in.readLong());
		own = in.readInt() == 1;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(title);
		dest.writeString(description);
		dest.writeParcelable(photo, flags);
		dest.writeInt(published ? 1 : 0);
		dest.writeLong(categoryId);
		dest.writeLong(createdAt.getTime());
		dest.writeLong(updatedAt.getTime());
		dest.writeInt(own ? 1 : 0);
	}

	public static final Creator<Article> CREATOR = new Creator<Article>() {

		public Article createFromParcel(Parcel in) {
			return new Article(in);
		}

		public Article[] newArray(int size) {
			return new Article[size];
		}
	};
}
