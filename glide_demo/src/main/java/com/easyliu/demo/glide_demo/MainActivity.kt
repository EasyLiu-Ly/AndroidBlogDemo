package com.easyliu.demo.glide_demo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    private val img by lazy {
        findViewById<ImageView>(R.id.iv_img)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Glide.with(this)
                .load("http://i.gtimg.cn/qqlive/images/20191209/i1575881814_1.jpg")
                .centerCrop()
                .circleCrop()
                .placeholder(ColorDrawable(Color.RED))
                .into(img);
    }
}