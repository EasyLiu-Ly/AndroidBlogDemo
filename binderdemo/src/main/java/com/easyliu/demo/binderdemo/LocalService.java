package com.easyliu.demo.binderdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LocalService extends Service {
    private static final String TAG = LocalService.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();
    private OnDataArrivedListener mOnDataArrivedListener;
    private ScheduledExecutorService mThreadPool;//定时器线程池
    private int mCount = 0;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        LocalService getService() {
            return LocalService.this;
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    /**
     * 定时任务
     */
    final TimerTask task = new TimerTask() {

        @Override
        public void run() {
            if (mOnDataArrivedListener != null) {
                mCount++;
                if (mCount == 1000) {
                    mCount = 0;
                }
                mOnDataArrivedListener.onDataArrived(mCount);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        //开启定时任务
        mThreadPool = Executors.newScheduledThreadPool(1);
        mThreadPool.scheduleAtFixedRate(task, 0, 1000, TimeUnit.MILLISECONDS);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //关闭定时器
        if (mThreadPool != null) {
            mThreadPool.shutdownNow();
        }
        return true;
    }

    /**
     * method for clients
     */
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }

    /**
     * 设置监听
     *
     * @param onDataArrivedListener
     */
    public void setOnDataArrivedListener(OnDataArrivedListener onDataArrivedListener) {
        this.mOnDataArrivedListener = onDataArrivedListener;
    }
}

