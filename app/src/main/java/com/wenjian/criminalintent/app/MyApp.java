package com.wenjian.criminalintent.app;

import android.app.Application;

import com.orhanobut.logger.Logger;

/**
 * Created by douliu on 2016/12/8.
 */
public class MyApp extends Application {

    public static final String TAG = "my_tag";

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init(TAG);
    }
}
