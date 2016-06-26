package com.easyliu.demo.shakeandscreenshot;

import android.os.Bundle;
import android.view.View;

public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }
}
