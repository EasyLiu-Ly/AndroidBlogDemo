package com.easyliu.splashguidepager;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class GuideViewPagerAdapter extends PagerAdapter {
    private List<View> mViews;
    private Context mContext;
    private static final String[] sTitles = {"PageOne", "PageTwo", "PageThree", "PageFour"};

    public GuideViewPagerAdapter(Context context, List<View> view) {
        this.mViews = view;
        this.mContext = context;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView(mViews.get(position));
    }

    @Override
    public Object instantiateItem(View container, int position) {
        ((ViewPager) container).addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == arg1);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return sTitles[position];
    }

    @Override
    public float getPageWidth(int position) {
        return super.getPageWidth(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return mViews.indexOf(object);
    }
}
