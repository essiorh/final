package com.teach.firstapp;

import android.app.Application;
import android.content.Context;

public class TestApplication extends Application{

    private static TestApplication instance;
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        instance = this;
    }

    public TestApplication getInstance() {
        return instance;
    }

    public static Context getAppContext(){
        return appContext;
    }
}
