package com.easyliu.demo.shakeandscreenshot;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeListener implements SensorEventListener {
	// 速度阈值，当摇晃速度达到这值后产生作用
	private static final int SPEED_SHRESHOLD = 2000;
	// 两次检测的时间间隔
	private static final int UPTATE_INTERVAL_TIME = 70;
	// 传感器管理器
	private SensorManager mSensorManager;
	// 传感器
	private Sensor mSensor;
	// 加速度监听器
	private OnShakeListener mOnShakeListener;
	// 上下文
	private Context mContext;
	// 手机上一个位置时加速度值
	private float mLastX;
	private float mLastY;
	private float mLastZ;
	// 上次检测时间
	private long mLastUpdateTime;

	public ShakeListener(Context context) {
		this.mContext = context;
	}

	// 开始
	public void start() {
		// 获得传感器管理器
		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager != null) {
			// 获得重力传感器
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		// 注册
		if (mSensor != null) {
			mSensorManager.registerListener(this, mSensor,
					SensorManager.SENSOR_DELAY_UI);
		}
	}

	// 停止检测
	public void stop() {
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(this);
		}
	}

	// 设置重力感应监听器
	public void setOnShakeListener(OnShakeListener listener) {
		mOnShakeListener = listener;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// 现在检测时间
		long currentUpdateTime = System.currentTimeMillis();
		// 两次检测的时间间隔
		long timeInterval = currentUpdateTime - mLastUpdateTime;
		// 判断是否达到了检测时间间隔
		if (timeInterval < UPTATE_INTERVAL_TIME)
			return;
		// 现在的时间变成last时间
		mLastUpdateTime = currentUpdateTime;

		// 获得x,y,z加速度
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		// 获得x,y,z的变化值
		float deltaX = x - mLastX;
		float deltaY = y - mLastY;
		float deltaZ = z - mLastZ;

		// 将现在的加速度变成last加速度
		mLastX = x;
		mLastY = y;
		mLastZ = z;

		double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
				* deltaZ)
				/ timeInterval * 10000;
		// 达到速度阀值，发出提示
		if (speed >= SPEED_SHRESHOLD) {
			mOnShakeListener.onShake();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	// 摇晃监听接口
	public interface OnShakeListener {
		public void onShake();
	}

}
