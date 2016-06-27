package com.easyliu.demo.shakeandscreenshot;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class BaseActivity extends AppCompatActivity {
    private ShakeListener mShakeListener;
    private Vibrator mVibrator;
    private int mNetworkStatus;
    private static final String sPicturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
            "/com.easyliu.demo.shakeandscreenshot/";
    private static final String sPictureName = "ScreenShot.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 动态注册广播
        IntentFilter filter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(mReceiver, filter);
        // 摇一摇
        mShakeListener = new ShakeListener(this);
        startShake();
        // 初始化振动器
        mVibrator = (Vibrator) getApplicationContext().getSystemService(
                VIBRATOR_SERVICE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopShake();
        this.unregisterReceiver(mReceiver);
    }

    /**
     * 开始震动
     */
    public void startVibrate() {
        mVibrator.vibrate(new long[]{500, 200, 500, 200}, -1);
    }

    /**
     * 取消震动
     */
    public void cancelVibrate() {
        mVibrator.cancel();
    }

    /**
     * 开启摇一摇
     */
    public void startShake() {
        mShakeListener.start();
        mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                startVibrate();
                //弹出对话框
                new AlertDialog.Builder(BaseActivity.this)
                        .setTitle(R.string.alert_dialog_two_buttons_title)
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //截屏反馈
                                AppUtils.saveScreenShotPicture(getWindow().getDecorView(), sPicturePath, sPictureName);
                            }
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                        .create();
            }
        });
    }

    /**
     * 关闭摇一摇
     */
    public void stopShake() {
        if (mShakeListener != null) {
            mShakeListener.stop();
            mShakeListener = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    // 网络连接广播
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(
                    ConnectivityManager.CONNECTIVITY_ACTION)) {
                // 得到网络状态
                mNetworkStatus = AppUtils.isOnline(getApplicationContext());
                // 强制继承这个方法
                networkStatusChanged(mNetworkStatus);
            }
        }
    };

    // 抽象方法
    public abstract void networkStatusChanged(int status);
}
