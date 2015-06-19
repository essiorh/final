package com.teach.firstapp.android.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static com.teach.firstapp.android.database.AppSQLiteOpenHelper.*;

public class AppContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.app.content_provider";

    private static final String PATH_USERS = "users";
    private static final String PATH_GROUPS = "groups";

    public static final Uri CONTENT_URI_USERS = Uri.parse("content://" + AUTHORITY + "/" + PATH_USERS);
    public static final Uri CONTENT_URI_GROUPS = Uri.parse("content://" + AUTHORITY + "/" + PATH_GROUPS);

    private static final int CODE_USERS = 0;
    private static final int CODE_GROUPS = 1;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, PATH_USERS, CODE_USERS);
        URI_MATCHER.addURI(AUTHORITY, PATH_GROUPS, CODE_GROUPS);
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
            case CODE_GROUPS:
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
            case CODE_USERS:
                table = TABLE_USERS;
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
