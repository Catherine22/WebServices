package com.catherine.webservices.toolkits;

import android.util.Log;

import com.catherine.webservices.BuildConfig;

/**
 * Created by Catherine on 2017/7/17.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class CLog {
    private static final boolean DEBUG = BuildConfig.SHOW_LOG;

    /**
     * Not work while proguard enabled
     *
     * @return class simple name
     */
    public static String getTag() {
        String tag = "";
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            if (ste[i].getMethodName().equals("getTag")) {
                tag = "(" + ste[i + 1].getFileName() + ":" + ste[i + 1].getLineNumber() + ")";
            }
        }
        return tag;
    }

    public static void v(String tab, String message) {
        if (DEBUG) {
            Log.v(tab, message);
        }
    }

    public static void d(String tab, String message) {
        if (DEBUG) {
            Log.d(tab, message);
        }
    }

    public static void e(String tab, String message) {
        if (DEBUG) {
            Log.e(tab, message);
        }
    }

    public static void w(String tab, String message) {
        if (DEBUG) {
            Log.w(tab, message);
        }
    }

    public static void i(String tab, String message) {
        if (DEBUG) {
            Log.i(tab, message);
        }
    }

    public static void v(String message) {
        if (DEBUG) {
            Log.v(getTag(), message);
        }
    }

    public static void d(String message) {
        if (DEBUG) {
            Log.d(getTag(), message);
        }
    }

    public static void e(String message) {
        if (DEBUG) {
            Log.e(getTag(), message);
        }
    }

    public static void w(String message) {
        if (DEBUG) {
            Log.w(getTag(), message);
        }
    }

    public static void i(String message) {
        if (DEBUG) {
            Log.i(getTag(), message);
        }
    }
}