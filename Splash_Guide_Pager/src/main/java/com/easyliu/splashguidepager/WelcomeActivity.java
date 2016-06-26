package com.easyliu.splashguidepager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeActivity extends Activity {
	private static final int DELAY = 2000;// 延时
	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	private boolean mIsFirst = false;
	private static final String START_KEY = "isFirst";
	@SuppressLint("HandlerLeak")
	//接收消息
	private Handler hander = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case GO_HOME:
					gohome();
					break;
				case GO_GUIDE:
					goguide();
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		init();
	}

	private void init() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		// 读取保存的键的值
		mIsFirst = preferences.getBoolean(START_KEY, true);
		if (!mIsFirst) {
			hander.sendEmptyMessageDelayed(GO_HOME, DELAY);
		} else {
			hander.sendEmptyMessageDelayed(GO_GUIDE, DELAY);
			Editor editor = preferences.edit();
			editor.putBoolean(START_KEY, false);
			editor.commit();
		}
	}

	// 去主界面
	private void gohome() {
		startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
		finish();
	}

	// 去引导界面
	private void goguide() {
		startActivity(new Intent(WelcomeActivity.this, GuideActivity.class));
		finish();
	}
}
