package com.easyliu.demo.binderdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LocalServiceActivity extends AppCompatActivity {
    private boolean mIsBound;
    private LocalService mBoundService;
    private Button btn_bound;
    private Button btn_unbound;
    private Button btn_call_func;
    private TextView tv_display;
    private IncomingHandler mHandler;
    private static final int MSG_FROM_SERVICE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        mHandler = new IncomingHandler();
    }

    private void initViews() {
        btn_bound = (Button) findViewById(R.id.btn_bound);
        btn_unbound = (Button) findViewById(R.id.btn_ubound);
        btn_call_func = (Button) findViewById(R.id.btn_call_func);
        btn_bound.setOnClickListener(mListener);
        btn_unbound.setOnClickListener(mListener);
        btn_call_func.setOnClickListener(mListener);
        tv_display = (TextView) findViewById(R.id.tv_display);
    }

    /**
     * 接收信息
     */
    private final class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FROM_SERVICE:
                    tv_display.setText(msg.obj + "");
                    break;
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((LocalService.LocalBinder) service).getService();
            //回调监听
            mBoundService.setOnDataArrivedListener(new OnDataArrivedListener() {
                @Override
                public void onDataArrived(int data) {
                    //不是在主线程，不能直接操作主UI,切换到主线程
                   mHandler.obtainMessage(MSG_FROM_SERVICE, data).sendToTarget();
                }
            });
            // Tell the user about this for our demo.
            Toast.makeText(LocalServiceActivity.this, R.string.local_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
            Toast.makeText(LocalServiceActivity.this, R.string.local_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 绑定服务
     */
    void doBindService() {
        bindService(new Intent(LocalServiceActivity.this,
                LocalService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    /**
     * 解除绑定
     */
    void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    private OnClickListener mListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_bound:
                    doBindService();
                    break;
                case R.id.btn_ubound:
                    doUnbindService();
                    break;
                case R.id.btn_call_func:
                    Toast.makeText(LocalServiceActivity.this, "number:" + mBoundService.getRandomNumber(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        doUnbindService();
    }
}
