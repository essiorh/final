package com.example.ilia.final_exercise.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.*;

public class AppContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.app.content_provider";

    public static final Uri CONTENT_URI_ARTICLES = Uri.parse("content://" + AUTHORITY + "/" + TABLE_ARTICLES);
    public static final Uri CONTENT_URI_CATEGORIES = Uri.parse("content://" + AUTHORITY + "/" + TABLE_CATEGORIES);

    // used for the UriMacher
    private static final int CODE_ONE_ARTICLE = 0;
    private static final int CODE_ALL_ARTICLES = 1;
    private static final int CODE_CATEGORIES = 2;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, TABLE_ARTICLES+"/#", CODE_ONE_ARTICLE);
        URI_MATCHER.addURI(AUTHORITY, TABLE_ARTICLES, CODE_ALL_ARTICLES);
        URI_MATCHER.addURI(AUTHORITY, TABLE_CATEGORIES, CODE_CATEGORIES);
    }

    // database
    private static AppSQLiteOpenHelper dbHelper;

    public synchronized static AppSQLiteOpenHelper getDbHelper(Context context) {
        if (null == dbHelper) {
            dbHelper = new AppSQLiteOpenHelper(context);
        }
        return dbHelper;
    }

    @Override
    public boolean onCreate() {
        getDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Set the table
        queryBuilder.setTables(TABLE_ARTICLES);
        int uriType = URI_MATCHER.match(uri);
        switch (uriType) {
            case CODE_ALL_ARTICLES:
                break;
            case CODE_ONE_ARTICLE:
                // adding the ID to the original query
                queryBuilder.appendWhere(COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }



    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case CODE_ALL_ARTICLES:
                id = sqlDB.insert(TABLE_ARTICLES, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(TABLE_ARTICLES + "/" + id);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case CODE_ALL_ARTICLES:
                rowsDeleted = sqlDB.delete(TABLE_ARTICLES, selection,
                        selectionArgs);
                break;
            case CODE_ONE_ARTICLE:
                String id = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(TABLE_ARTICLES,
                            COLUMN_ID + "= ?",
                            new String[]{id});
                } else {
                    rowsDeleted = sqlDB.delete(TABLE_ARTICLES,
                            COLUMN_ID + "= ? "
                                    + " and " + selection,
                            combine( new String[]{id} , selectionArgs));
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case CODE_ALL_ARTICLES:
                rowsDeleted = sqlDB.delete(TABLE_ARTICLES, selection,
                        selectionArgs);
                break;
            case CODE_ONE_ARTICLE:
                String id = uri.getLastPathSegment();

                rowsDeleted = sqlDB.delete(TABLE_ARTICLES,
                        COLUMN_ID + "=" + id
                                + " and " + selection,
                        selectionArgs);

                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }
    public static String[] combine(String[] a, String[] b) {
        int length = a.length + b.length;
        String[] result = new String[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
