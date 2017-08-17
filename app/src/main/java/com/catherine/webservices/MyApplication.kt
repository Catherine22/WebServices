package com.catherine.webservices

import android.app.Application
import android.os.HandlerThread

/**
 * Created by Catherine on 2017/8/17.
 */
class MyApplication : Application() {

    companion object {
        lateinit var INSTANCE: MyApplication
    }

    lateinit var mainHandlerThread: HandlerThread

    override fun onCreate() {
        INSTANCE = this
        mainHandlerThread = HandlerThread("Main_handler_thread")
        mainHandlerThread.start()
        super.onCreate()
    }
}