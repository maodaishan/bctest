package com.maods.bctest;

import android.app.Application;
import android.content.Context;

/**
 * Created by MAODS on 2019/1/15.
 */

public class BCTestApp extends Application {
    private static Context instance;

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = getApplicationContext();
    }

    public static Context getContext()
    {
        return instance;
    }
}
