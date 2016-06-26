package com.easyliu.demo.camerademo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by LiuYi on 2016/6/11.
 */
public class Utils {
    /**
     * 把图片保存到SD卡
     * @param bitmap
     * @param targetPath
     */
    public static void SavePhotoToSdCard(Bitmap bitmap, String targetPath) {

        FileOutputStream fileOutputStream = null;
        File file = new File(targetPath);
        try {
            fileOutputStream = new FileOutputStream(file);
            if (bitmap != null) {
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                        fileOutputStream)) {
                    fileOutputStream.flush();
                }
            }
        } catch (FileNotFoundException e) {
            file.delete();
            e.printStackTrace();
        } catch (IOException e) {
            file.delete();
            e.printStackTrace();
        } finally {
            try {
                // 到最后一定要关闭
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 压缩图片
     */
    public  static  void scalePic(String path,int targetW,int targetH) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true; //刚开始设置为真
        BitmapFactory.decodeFile(path, bmOptions);//得到BitmapFactory.Options
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inSampleSize = scaleFactor; //采样因子
        bmOptions.inJustDecodeBounds = false; //设置为False

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);//重新解码bitmap
    }

    public static boolean checkSDCardAvaliable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }
}
