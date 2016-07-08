package com.easyliu.demo.orientaionsensor.activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.easyliu.demo.orientationsensor.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private SensorManager mSensorManager;
    private Sensor mSensorAcc;
    private Sensor mSensorMag;
    private Sensor mSensorGyro;
    private ScheduledExecutorService mThreadPool;
    private TextView tv_yaw;
    private TextView tv_pitch;
    private TextView tv_roll;
    private TextView tv_yaw1;
    private TextView tv_pitch1;
    private TextView tv_roll1;
    private SensorHandler mHandler;
    private MadgwickAHRS mMadgwickAHRS;
    private MahonyAHRS mMahonyAHRS;
    private float[] mAccValues = new float[3];
    private float[] mMagValues = new float[3];
    private float[] mGyroValues = new float[3];
    private static final int MSG_ATTITUDE_UPDATE = 1;
    private static final String KEY_ATTITUDE = "key_attitude";
    private static final String KEY_ATTITUDE1 = "key_attitude1";
    private float[][] mFifoAcc = new float[3][5];
    private float[][] mFifoGyro = new float[3][5];
    private float[][] mFifoMag = new float[3][5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSensors();
        initViews();
        mHandler = new SensorHandler(this);
        mMadgwickAHRS = new MadgwickAHRS(1f / 50f, 0.1f);
        mMahonyAHRS = new MahonyAHRS(1f / 50f, 5f);
    }

    /**
     * 初始化传感器
     */
    private void initSensors() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMag = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        tv_yaw = (TextView) findViewById(R.id.tv_yaw);
        tv_pitch = (TextView) findViewById(R.id.tv_pitch);
        tv_roll = (TextView) findViewById(R.id.tv_roll);
        tv_yaw1 = (TextView) findViewById(R.id.tv_yaw1);
        tv_pitch1 = (TextView) findViewById(R.id.tv_pitch1);
        tv_roll1 = (TextView) findViewById(R.id.tv_roll1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mAccSensorEventListener, mSensorAcc, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(mMagSensorEventListener, mSensorMag, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(mGyroSensorEventListener, mSensorGyro, SensorManager.SENSOR_DELAY_GAME);
        mThreadPool = Executors.newScheduledThreadPool(1);
        mThreadPool.scheduleAtFixedRate(mTimerTask, 0, 20, TimeUnit.MILLISECONDS);
    }

    /**
     * 定时任务,定时计算姿态
     */
    private final Runnable mTimerTask = new Runnable() {
        @Override
        public void run() {
            float[] attitudeValues = new float[3];
            float[] attitudeValues1 = new float[3];
            float[] R = new float[9];
            //得到旋转矩阵
            SensorManager.getRotationMatrix(R, null, mAccValues, mMagValues);
            //得到姿态
            SensorManager.getOrientation(R, attitudeValues1);
            //mMadgwickAHRS.getYawPitchRoll(mAccValues, mGyroValues, mMagValues, attitudeValues);
            mMahonyAHRS.getYawPitchRoll(mAccValues,mGyroValues,mMagValues,attitudeValues);
            //发送Message
            Bundle bundle = new Bundle();
            bundle.putFloatArray(KEY_ATTITUDE, attitudeValues);
            bundle.putFloatArray(KEY_ATTITUDE1, attitudeValues1);
            Message message = new Message();
            message.setData(bundle);
            message.what = MSG_ATTITUDE_UPDATE;
            mHandler.sendMessage(message);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mAccSensorEventListener);
        mSensorManager.unregisterListener(mMagSensorEventListener);
        mSensorManager.unregisterListener(mGyroSensorEventListener);
        for (int i = 0; i < mFifoAcc.length; i++) {
            for (int j = 0; j < mFifoAcc[0].length; j++) {
                mFifoAcc[i][j] = 0;
            }
        }
        for (int i = 0; i < mFifoGyro.length; i++) {
            for (int j = 0; j < mFifoGyro[0].length; j++) {
                mFifoGyro[i][j] = 0;
            }
        }
        for (int i = 0; i < mFifoMag.length; i++) {
            for (int j = 0; j < mFifoMag[0].length; j++) {
                mFifoMag[i][j] = 0;
            }
        }

        if (mThreadPool != null) {
            mThreadPool.shutdownNow();
        }
    }

    /**
     * 消息回调,使用弱引用，防止内存泄漏
     */
    private static class SensorHandler extends Handler {
        private final WeakReference<MainActivity> mParent;

        public SensorHandler(MainActivity parent) {
            mParent = new WeakReference<MainActivity>(parent);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity parent = this.mParent.get();
            if (parent == null)
                return;
            switch (msg.what) {
                case MSG_ATTITUDE_UPDATE:
                    float[] attitudeValues = msg.getData().getFloatArray(KEY_ATTITUDE);
                    float[] attitudeValues1 = msg.getData().getFloatArray(KEY_ATTITUDE1);
                    parent.tv_yaw.setText("Yaw:" + attitudeValues[0]);
                    parent.tv_pitch.setText("Pitch:" + attitudeValues[1]);
                    parent.tv_roll.setText("Roll:" + attitudeValues[2]);
                    parent.tv_yaw1.setText("YawMadgwickAHRS:" + Math.toDegrees(attitudeValues1[0]));
                    parent.tv_pitch1.setText("PitchMadgwickAHRS:" + Math.toDegrees(attitudeValues1[1]));
                    parent.tv_roll1.setText("RollMadgwickAHRS:" + Math.toDegrees(attitudeValues1[2]));
                    break;
            }
        }
    }

    /**
     * 加速度监听
     */
    private SensorEventListener mAccSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            mAccValues = event.values.clone();
            fifoFilter(mAccValues, mFifoAcc);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    /**
     * 磁力计监听
     */
    private SensorEventListener mMagSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            mMagValues = event.values.clone();
            fifoFilter(mMagValues, mFifoMag);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    /**
     * 角速度监听
     */
    private SensorEventListener mGyroSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            mGyroValues = event.values.clone();
            fifoFilter(mGyroValues, mFifoGyro);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * 对数据进行FIFO滤波
     *
     * @param data
     * @param fifo
     */
    public static void fifoFilter(float[] data, float[][] fifo) {
        int fifoLength = fifo[0].length;
        for (int i = 1; i < fifoLength; i++) {
            fifo[0][i - 1] = fifo[0][i];
            fifo[1][i - 1] = fifo[1][i];
            fifo[2][i - 1] = fifo[2][i];
        }
        fifo[0][fifoLength - 1] = data[0];
        fifo[1][fifoLength - 1] = data[1];
        fifo[2][fifoLength - 1] = data[2];

        float sum = 0;
        for (int i = 0; i < fifoLength; i++) {
            sum += fifo[0][i];
        }
        data[0] = (sum / fifoLength);

        sum = 0;
        for (int i = 0; i < fifoLength; i++) {
            sum += fifo[1][i];
        }
        data[1] = (sum / fifoLength);

        sum = 0;
        for (int i = 0; i < fifoLength; i++) {
            sum += fifo[2][i];
        }
        data[2] = (sum / fifoLength);
    }
}
