package com.easyliu.demo.camerademo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private String mSavePhotoPath = null;
    private File mSavePhotoFile = null;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_SELECT_PHOTO = 2;
    private static final int REQUEST_CROP_PHOTO = 3;
    private ImageView iv_diaplay = null;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_diaplay = (ImageView) findViewById(R.id.iv_display);
        findViewById(R.id.btn_take_photo).setOnClickListener(new onClickListener());
        findViewById(R.id.btn_select_photo).setOnClickListener(new onClickListener());
        // Create the File where the photo should go
        try {
            mSavePhotoFile = createImageFile();//创建一个文件，得到的照片保存在这个文件当中
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:  //拍照
                    //注意，如果拍照的时候设置了MediaStore.EXTRA_OUTPUT，data.getData=null
                    startPhotoZoom(Uri.fromFile(mSavePhotoFile), 256, 256);
                    break;
                case REQUEST_SELECT_PHOTO://选择图片
                    startPhotoZoom(data.getData(), 256, 256);
                    break;
                case REQUEST_CROP_PHOTO:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        //把图片显示到ImgeView
                        iv_diaplay.setImageBitmap(photo);
                        //把图片加入图库
                        galleryAddPic();
                    }
                    break;
            }
        }
    }

    /**
     * 从图库选择照片
     */
    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_PHOTO);
    }

    /**
     * 启动拍照
     */
    private void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Continue only if the File was successfully created
            if (mSavePhotoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(mSavePhotoFile));//设置文件保存的URI
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * 裁剪照片
     */
    public void startPhotoZoom(Uri uri, int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", width/height);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        // 图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:返回uri，false：不返回uri
        // 同一个地址下 裁剪的图片覆盖之前得到的图片
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mSavePhotoFile));
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    /**
     * 创建保存拍照得到的图片的文件
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        if (Utils.checkSDCardAvaliable()) {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "CameraDemo");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(TAG, "failed to create directory");
                    return null;
                }
            }
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp;
            String suffix = ".jpg";
            File image = new File(mediaStorageDir + File.separator + imageFileName + suffix);
            mSavePhotoPath = image.getAbsolutePath();
            return image;
        }
        return null;
    }

    /**
     * 触发系统的media scanner来把图片加入Media Provider's database
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mSavePhotoPath);//
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);  //设置URI
        this.sendBroadcast(mediaScanIntent);  //发送广播
    }

    /**
     * 点击拍照
     */
    private class onClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    startCamera();
                    break;
                case R.id.btn_select_photo:
                    selectPhoto();
                    break;
            }
        }
    }
}
