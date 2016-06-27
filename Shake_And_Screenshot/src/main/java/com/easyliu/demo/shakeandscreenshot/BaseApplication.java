package com.easyliu.demo.shakeandscreenshot;

import android.app.Application;

/**
 * Created by LiuYi on 2016/6/27.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandlerUtils.getInstance().init(this);
    }
}
