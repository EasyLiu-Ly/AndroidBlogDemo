package com.easyliu.demo.camerademo;

import android.app.Application;

/**
 * Created by LiuYi on 2016/6/11.
 */
public class BaseApplication extends Application implements Thread.UncaughtExceptionHandler{
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

    }
}
