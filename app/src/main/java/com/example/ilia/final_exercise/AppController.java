package com.example.ilia.final_exercise;

import android.app.Application;
import android.content.Context;

/**
 * Created by ilia on 17.06.15.
 *
 * @author ilia
 */
public class AppController extends Application {

    private static Context appContext;

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }
}
