package com.example.ilia.final_exercise.models;

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

    public ArticleItem(int id, String title, String discr, boolean publ, int category_id, String creat_at, String update_at) {
        _id = id;
        mTitle = title;
        mDescription = discr;
        mPublished = publ;
        mCategory_id = category_id;
        mCreate_at = creat_at;
        mUpdate_at = update_at;
    }

    public ArticleItem() {
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
}
