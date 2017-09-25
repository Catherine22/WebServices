package com.catherine.webservices.toolkits

import android.util.Log

import com.catherine.webservices.BuildConfig

/**
 * Created by Catherine on 2017/7/17.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

/**
 * 宣告了internal后，该class就不能被其它module访问
 */
internal class CLog {
    companion object {
        private val DEBUG = BuildConfig.SHOW_LOG
        fun getTag(): String {
            var tag = ""
            val ste = Thread.currentThread().stackTrace
            ste.indices
                    .filter { ste[it].methodName == "getTag" }
                    .forEach { tag = "(${ste[it + 1].fileName}:${ste[it + 1].lineNumber})" }
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
    }


//    final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
//    for (int i = 0; i < ste.length; i++) {
//        if (ste[i].getMethodName().equals("getTag")) {
//            tag = "(" + ste[i + 1].getFileName() + ":" + ste[i + 1].getLineNumber() + ")";
//        }
//    }
}