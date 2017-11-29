package com.catherine.webservices.toolkits;

import android.util.Log;

import com.catherine.webservices.BuildConfig;
import com.catherine.webservices.MyApplication;

/**
 * Created by Catherine on 2017/11/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class CLog {

    public static void v(String tab, String message) {
        if (BuildConfig.SHOW_LOG || MyApplication.INSTANCE.getPackageName().contains(".test")) {
            Log.v(tab, message);
        }
    }

    public static void d(String tab, String message) {
        if (BuildConfig.SHOW_LOG || MyApplication.INSTANCE.getPackageName().contains(".test")) {
            Log.d(tab, message);
        }
    }

    public static void w(String tab, String message) {
        if (BuildConfig.SHOW_LOG || MyApplication.INSTANCE.getPackageName().contains(".test")) {
            Log.w(tab, message);
        }
    }

    public static void e(String tab, String message) {
        if (BuildConfig.SHOW_LOG || MyApplication.INSTANCE.getPackageName().contains(".test")) {
            Log.e(tab, message);
        }
    }

    public static void i(String tab, String message) {
        if (BuildConfig.SHOW_LOG || MyApplication.INSTANCE.getPackageName().contains(".test")) {
            Log.i(tab, message);
        }
    }
}
