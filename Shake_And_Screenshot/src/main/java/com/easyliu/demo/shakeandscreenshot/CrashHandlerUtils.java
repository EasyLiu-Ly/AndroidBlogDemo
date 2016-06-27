package com.easyliu.demo.shakeandscreenshot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

/**
 * 异常处理工具类
 *
 * @author v_easyliu
 */
public class CrashHandlerUtils implements UncaughtExceptionHandler {
    private static final String TAG = CrashHandlerUtils.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static final String PATH = Environment
            .getExternalStoragePublicDirectory("Crash_Handler") + "/log/";
    private static final String sFILE_NAME = "crash-";
    private static final String sFILE_NAME_SUFFIX = ".trace";
    private Context mContext;
    private UncaughtExceptionHandler mDefaultExceptionHandler;

    // 防止初始化
    private CrashHandlerUtils() {

    }

    // 静态内部类
    private static class CrashHandlerHolder {
        private static final CrashHandlerUtils sInstance = new CrashHandlerUtils();
    }

    // 单例
    public static CrashHandlerUtils getInstance() {
        return CrashHandlerHolder.sInstance;
    }

    public void init(Context context) {
        // 得到系统默认的异常处理器
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        mContext = context.getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            dumpExceptionToSDCard(ex);
            uploadExceptionToServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ex.printStackTrace();

        if (mDefaultExceptionHandler != null) {
            mDefaultExceptionHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 保存到SD卡
     *
     * @param ex
     * @throws IOException
     */
    private void dumpExceptionToSDCard(Throwable ex) throws IOException {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            if (DEBUG) {
                Log.w(TAG, "sdcard unmounted,skip dump exception");
            }
            return;
        }
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyy-MM-dd-HH-mm-ss")
                .format(new Date(current));
        File file = new File(dir, sFILE_NAME + time + sFILE_NAME_SUFFIX);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
                    file)));
            pw.print(time);
            pw.println();
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace(pw);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "dump crash info failed!");
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传给服务器
     */
    private void uploadExceptionToServer() {

    }

    /**
     * 手机信息
     *
     * @param pw
     * @throws NameNotFoundException
     */
    private void dumpPhoneInfo(PrintWriter pw) throws NameNotFoundException {
        PackageManager pManager = mContext.getPackageManager();
        PackageInfo pInfo = pManager.getPackageInfo(mContext.getPackageName(),
                PackageManager.GET_ACTIVITIES);
        pw.print("App Version:");
        pw.print(pInfo.versionName);
        pw.print("_");
        pw.println(pInfo.versionCode);

        // Android版本号
        pw.print("OS Version:");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        // 手机制造商
        pw.print("Vendor:");
        pw.println(Build.MANUFACTURER);

        // 手机型号
        pw.print("Model:");
        pw.println(Build.MODEL);

        // cpu架构
        pw.print("CPU ABI:");
        pw.println(Build.CPU_ABI);
    }
}
