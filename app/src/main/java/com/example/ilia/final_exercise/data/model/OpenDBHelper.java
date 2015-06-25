package com.example.ilia.final_exercise.data.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ilia on 16.06.15.
 * @author ilia
 */
public class OpenDBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "articles.db";
	private static final int DB_VERSION = 2;

	public static final String COLUMN_ID = "_id";

	public static final String TABLE_CATEGORIES = "categories";
	public static final String CATEGORIES_TITLE = "title";

	public static final String TABLE_ARTICLES = "articles";
	public static final String ARTICLES_TITLE = "title";
	public static final String ARTICLES_DESCRIPTION = "description";
	public static final String ARTICLES_PHOTO_URL = "photo_url";
	public static final String ARTICLES_PUBLISHED = "published";
	public static final String ARTICLES_CATEGORY_ID = "category_id";
	public static final String ARTICLES_CREATED = "created";
	public static final String ARTICLES_UPDATED = "updated";
	public static final String ARTICLES_OWN = "own";


	private static final String CREATE_TABLE_CATEGORIES = "" +
			"CREATE TABLE " + TABLE_CATEGORIES + "(" +
			COLUMN_ID + " integer primary key," +
			CATEGORIES_TITLE + " text);";


	private static final String CREATE_TABLE_ARTICLES = "" +
			"CREATE TABLE " + TABLE_ARTICLES + "(" +
			COLUMN_ID + " integer primary key," +
			ARTICLES_CATEGORY_ID + " integer," +
			ARTICLES_TITLE + " text," +
			ARTICLES_DESCRIPTION + " text," +
			ARTICLES_PHOTO_URL + " text," +
			ARTICLES_PUBLISHED + " integer," +
			ARTICLES_CREATED + " integer," +
			ARTICLES_UPDATED + " integer," +
			ARTICLES_OWN + " integer);";

	public OpenDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String[] CREATES = {
				CREATE_TABLE_CATEGORIES,
				CREATE_TABLE_ARTICLES
		};
		for (final String table : CREATES) {
			db.execSQL(table);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String[] TABLES = {
				TABLE_CATEGORIES,
				TABLE_ARTICLES
		};
		for (final String table : TABLES) {
			db.execSQL("DROP TABLE IF EXISTS " + table);
		}
		onCreate(db);
	}
}
