package com.easyliu.demo.shakeandscreenshot;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by LiuYi on 2016/6/26.
 */
public class AppUtils {
    public static final int CONNECT_WIFI = 1;
    public static final int CONNECT_MOBILE = -1;
    public static final int CONNECT_NONE = 0;

    // 是不是已经联网以及连接的是什么网络
    public static int isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null && (networkInfo.isConnected())) {
            return CONNECT_WIFI;
        }
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo != null && (networkInfo.isConnected())) {
            return CONNECT_MOBILE;
        }
        return CONNECT_NONE;
    }

    /**
     * 保存截屏图片
     *
     * @param view
     * @param picturePath
     * @param pictureName
     */
    public static void saveScreenShotPicture(View view, String picturePath, String pictureName) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        File f = new File(picturePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(new File(picturePath, pictureName));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
            fout.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
