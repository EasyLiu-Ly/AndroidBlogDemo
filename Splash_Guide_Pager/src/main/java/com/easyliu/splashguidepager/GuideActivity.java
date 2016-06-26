package com.easyliu.splashguidepager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.easyliu.splashguidepager.R;


public class GuideActivity extends Activity {
	private ViewPager pager;
	private GuideViewPagerAdapter adapter;// 自定义适配器
	private List<View> views; // 视图列表
	private ImageView[] dots;
	private RelativeLayout layout_guide = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		initViews();
		initdots();
	}

	/**
	 *初始化Views
	 */
	private void initViews() {
		layout_guide = (RelativeLayout) findViewById(R.id.layout_guide);
		views = new ArrayList<View>();
        //加入图片
		ImageView imageView = new ImageView(this);
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setImageResource(R.drawable.guide_1);
		views.add(imageView);

		imageView = new ImageView(this);
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setImageResource(R.drawable.guide_2);
		views.add(imageView);

		imageView = new ImageView(this);
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setImageResource(R.drawable.guide_3);
		views.add(imageView);
        //加入最后一页布局
		LayoutInflater inflater = LayoutInflater.from(this);
		views.add(inflater
				.inflate(R.layout.layout_guide_pager_end, null));
		adapter = new GuideViewPagerAdapter(this, views);
		pager = (ViewPager) findViewById(R.id.pager);
		// 设置适配器
		pager.setAdapter(adapter);
		// 注册监听器
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				for (int i = 0; i < views.size(); i++) {
					if (arg0 == i) {
						dots[i].setImageResource(R.drawable.login_point_selected);
					} else {
						dots[i].setImageResource(R.drawable.login_point);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		// 点击按钮跳转到主界面
		views.get(views.size() - 1).findViewById(R.id.btn_start)
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(new Intent(GuideActivity.this,
								MainActivity.class));
						finish();
					}
				});
	}

	/**
	 * 初始化点
	 */
	private void initdots() {
		dots = new ImageView[views.size()];
		for (int i = 0; i < views.size(); i++) {
			dots[i] = new ImageView(this);
			if (i == 0) {
				dots[i].setImageResource(R.drawable.login_point_selected);
			} else {
				dots[i].setImageResource(R.drawable.login_point);
			}
			dots[i].setScaleType(ScaleType.FIT_XY);
			// 设置位置
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			RelativeLayout.LayoutParams lParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lParams.topMargin = dm.heightPixels*19/20;
			lParams.leftMargin = dm.widthPixels / 2 + (i - views.size() / 2)
					* dm.widthPixels/20;
			layout_guide.addView(dots[i], lParams);
		}
	}
}
