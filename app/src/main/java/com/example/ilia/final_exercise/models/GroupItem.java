package com.example.ilia.final_exercise.models;

/**
 * Created by ilia on 16.06.15.
 */
public class GroupItem {
    private int _id;
    private String mTitle;


    public GroupItem(int id, String title) {
        _id = id;
        mTitle = title;
    }

    public GroupItem() {
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
