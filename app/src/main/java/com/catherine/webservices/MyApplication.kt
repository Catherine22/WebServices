package com.catherine.webservices

import android.app.Application
import android.os.HandlerThread

/**
 * Created by Catherine on 2017/8/17.
 */
class MyApplication : Application() {
    var calHandlerThread: HandlerThread? = null

    companion object {
        var INSTANCE: MyApplication? = null
    }

    override fun onCreate() {
        INSTANCE = this
        calHandlerThread = HandlerThread("Main_handler_thread")
        calHandlerThread?.start()
        super.onCreate()
    }
}