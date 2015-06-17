package com.example.ilia.final_exercise.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.*;

public class AppContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.app.content_provider";

    public static final Uri CONTENT_URI_ARTICLES = Uri.parse("content://" + AUTHORITY + "/" + TABLE_ARTICLES);
    public static final Uri CONTENT_URI_CATEGORIES = Uri.parse("content://" + AUTHORITY + "/" + TABLE_CATEGORIES);

    private static final int CODE_ARTICLES = 0;
    private static final int CODE_CATEGORIES = 1;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, TABLE_ARTICLES, CODE_ARTICLES);
        URI_MATCHER.addURI(AUTHORITY, TABLE_CATEGORIES, CODE_CATEGORIES);
    }

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
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case CODE_CATEGORIES:
                return getGroups();
        }
        String table = parseUri(uri);
        Cursor cursor = dbHelper.getReadableDatabase()
                .query(table, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table = parseUri(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(table, null, values);
        if (-1 == id) {
            throw new RuntimeException("Record wasn't saved.");
        }
        Uri resultUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(resultUri, null);

        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table = parseUri(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(table, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table = parseUri(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.update(table, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    private String parseUri(Uri uri) {
        return parseUri(URI_MATCHER.match(uri));
    }

    private String parseUri(int match) {
        String table = null;
        switch (match) {
            case CODE_ARTICLES:
                table = TABLE_ARTICLES;
                break;
            default:
                throw new IllegalArgumentException("Invalid code: " + match);
        }
        return table;
    }

    /*
    custom sql queries
     */

    private Cursor getGroups(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "select ...";
        return db.rawQuery(sql, null);
    }

}
