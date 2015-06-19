package com.teach.firstapp.javacode.classes;

import android.os.Bundle;

public class FirstActivity extends BaseActivity {

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
