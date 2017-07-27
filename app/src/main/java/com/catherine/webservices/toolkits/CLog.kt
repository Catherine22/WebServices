package com.catherine.webservices.toolkits

import android.util.Log

import com.catherine.webservices.BuildConfig

/**
 * Created by Catherine on 2017/7/17.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

internal class CLog {

    companion object {
        val DEBUG = BuildConfig.SHOW_LOG

        fun getTag(): String {
            var tag = ""
            val ste = Thread.currentThread().stackTrace
            for (i in ste.indices) {
                if (ste[i].methodName == "getTag") {
                    tag = "(${ste[i + 1].fileName}:${ste[i + 1].lineNumber})"
                }
            }
            return tag
        }

        fun v(tag: String = "Default TAG", message: String = "Default message") {
            if (DEBUG)
                Log.v(tag, message)
        }

        fun d(tag: String = "Default TAG", message: String = "Default message") {
            if (DEBUG)
                Log.d(tag, message)
        }

        fun i(tag: String = "Default TAG", message: String = "Default message") {
            if (DEBUG)
                Log.i(tag, message)
        }

        fun w(tag: String = "Default TAG", message: String = "Default message") {
            if (DEBUG)
                Log.w(tag, message)
        }

        fun e(tag: String = "Default TAG", message: String = "Default message") {
            if (DEBUG)
                Log.e(tag, message)
        }

        fun v(message: String = "Default message") {
            if (DEBUG)
                Log.v(getTag(), message)
        }

        fun d(message: String = "Default message") {
            if (DEBUG)
                Log.d(getTag(), message)
        }

        fun i(message: String = "Default message") {
            if (DEBUG)
                Log.i(getTag(), message)
        }

        fun w(message: String = "Default message") {
            if (DEBUG)
                Log.w(getTag(), message)
        }

        fun e(message: String = "Default message") {
            if (DEBUG)
                Log.e(getTag(), message)
        }
    }


//    final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
//    for (int i = 0; i < ste.length; i++) {
//        if (ste[i].getMethodName().equals("getTag")) {
//            tag = "(" + ste[i + 1].getFileName() + ":" + ste[i + 1].getLineNumber() + ")";
//        }
//    }
}