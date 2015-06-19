package com.teach.firstapp.javacode.classes;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public abstract class BaseActivity extends Activity implements IProgress {

    View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentId());
        progress = findViewById(getProgressViewId());
    }

    @Override
    public void showProgress(){
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress(){
        progress.setVisibility(View.GONE);
    }

    abstract int getProgressViewId();
    abstract int getContentId();
}
