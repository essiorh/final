package com.example.ilia.final_exercise.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.*;

/**
 * Created by ilia on 16.06.15.
 */
public class ArticleItem {
    private int _id;
    private String mTitle;
    private String mDescription;
    private boolean mPublished;
    private int mCategory_id;
    private String mCreate_at;
    private String mUpdate_at;
    private Boolean mOwn;
    private String mPhoto;

    private ArticleItem(int id, String title, String descr, boolean publ, int category_id, String creat_at, String update_at, boolean own, String  photo) {
        this._id = id;
        this.mTitle = title;
        this.mDescription = descr;
        this.mPublished = publ;
        this.mCategory_id = category_id;
        this.mCreate_at = creat_at;
        this.mUpdate_at = update_at;
        this.mOwn = own;
        this.mPhoto = photo;
    }
    public ArticleItem(){}

    public ArticleItem(String title, String descr, boolean publ, int category_id, String creat_at, String update_at, boolean own, String photo) {
        this(-1, title, descr, publ, category_id, creat_at, update_at, own, photo);
    }

    public ContentValues buildContentValues() {
        ContentValues cv = new ContentValues();
        if (_id >= 0) {
            cv.put(COLUMN_ID, _id);
        }
        cv.put(COLUMN_TITLE, mTitle);
        cv.put(ARTICLES_COLUMN_DESCRIPTION, mDescription);
        cv.put(ARTICLES_COLUMN_PUBLISHED, mPublished);
        cv.put(ARTICLES_COLUMN_CATEGORY_ID, mCategory_id);
        cv.put(ARTICLES_COLUMN_CREATE_AT, mCreate_at);
        cv.put(ARTICLES_COLUMN_UPDATE_AT, mUpdate_at);
        cv.put(ARTICLES_COLUMN_OWN, mOwn);
        cv.put(ARTICLES_COLUMN_PHOTO, mOwn);

        return cv;
    }

    public static ArticleItem fromCursor(Cursor c) throws MalformedURLException {
        int idColId = c.getColumnIndexOrThrow(COLUMN_ID);
        int titleColId = c.getColumnIndexOrThrow(COLUMN_TITLE);
        int descrColId = c.getColumnIndexOrThrow(ARTICLES_COLUMN_DESCRIPTION);
        //int publColId = c.getColumnIndexOrThrow(ARTICLES_COLUMN_PUBLISHED);
        int categoryIdColId = c.getColumnIndexOrThrow(ARTICLES_COLUMN_CATEGORY_ID);
        //int creatAtColId = c.getColumnIndexOrThrow(ARTICLES_COLUMN_CREATE_AT);
        //int updateAtColId = c.getColumnIndexOrThrow(ARTICLES_COLUMN_UPDATE_AT);
        int ownColId = c.getColumnIndex(ARTICLES_COLUMN_OWN);
        //int photoColId = c.getColumnIndexOrThrow(ARTICLES_COLUMN_PHOTO);

        return new ArticleItem(
                c.getInt(idColId),
                c.getString(titleColId),
                c.getString(descrColId),
                //c.getInt(publColId) == 1,
                true,
                c.getInt(categoryIdColId),
                //c.getString(creatAtColId),
                "",
                //c.getString(updateAtColId),
                "",
                c.getInt(ownColId) == 1,
                //new URL(c.getString(photoColId)));
                "");
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

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public boolean ismPublished() {
        return mPublished;
    }

    public void setmPublished(boolean mPublished) {
        this.mPublished = mPublished;
    }

    public int getmCategory_id() {
        return mCategory_id;
    }

    public void setmCategory_id(int mCategory_id) {
        this.mCategory_id = mCategory_id;
    }

    public String getmCreate_at() {
        return mCreate_at;
    }

    public void setmCreate_at(String mCreate_at) {
        this.mCreate_at = mCreate_at;
    }

    public String getmUpdate_at() {
        return mUpdate_at;
    }

    public void setmUpdate_at(String mUpdate_at) {
        this.mUpdate_at = mUpdate_at;
    }

    public Boolean getmOwn() {
        return mOwn;
    }

    public void setmOwn(Boolean mOwn) {
        this.mOwn = mOwn;
    }

    public String getmPhoto() {
        return mPhoto;
    }

    public void setmPhoto(String mPhoto) {
        this.mPhoto = mPhoto;
    }
}