package com.teach.firstapp.javacode.classes;

import android.os.Bundle;

/**
 * Created by Михаил on 27.05.2015.
 */
public class SecondActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    int getContentId() {
        return 0;
    }

    @Override
    int getProgressViewId() {
        return 0;//R.id.some_id
    }
}
