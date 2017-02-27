package com.easyliu.test.animationdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

public class ValueAnimatorActivity extends AppCompatActivity {

    private static final String TAG = ValueAnimatorActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value_animator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        findViewById(R.id.btn_value_animate_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // performAnimate(view);
                performAnimate2(view);
            }
        });
    }

    //执行动画
    private void performAnimate(final View view) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, (float) (view.getHeight() * 3));
        valueAnimator.setDuration(500);
        valueAnimator.setTarget(view);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                view.setTranslationY(animatedValue);
                view.setTranslationX(animatedValue);
            }
        });
    }

    //执行自定义估计器动画
    private void performAnimate2(final View view) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(1000);
        valueAnimator.setTarget(view);
        valueAnimator.setObjectValues(new PointF(0.0f, 0.0f));
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        //设置自定义估值器
        valueAnimator.setEvaluator(new PointTypeEvaluator());
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PointF pointF = (PointF) valueAnimator.getAnimatedValue();
                view.setTranslationX(pointF.x);
                view.setTranslationY(pointF.y);
            }
        });
//        // 添加动画监听
//        valueAnimator.addListener(new ValueAnimator.AnimatorListener() {
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationStart(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                //动画执行完成之后删除View
//                ViewGroup parent = (ViewGroup) view.getParent();
//                if (parent != null) {
//                    parent.removeView(view);
//                }
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //动画执行完成之后删除View
                ViewGroup parent = (ViewGroup) view.getParent();
                if (parent != null) {
                    parent.removeView(view);
                }
            }
        });
    }

    //自定义估值器
    public class PointTypeEvaluator implements TypeEvaluator<PointF> {
        @Override
        public PointF evaluate(float fraction, PointF pointStart, PointF pointEnd) {
            Log.d(TAG, fraction + "");
            //抛物线运动
            PointF pointF = new PointF();
            pointF.x = 600 * fraction;
            pointF.y = 0.5f * 10 * (fraction) * (fraction) * 120;
            return pointF;
        }
    }
}
