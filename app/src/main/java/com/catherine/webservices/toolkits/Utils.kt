package com.catherine.webservices.toolkits

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

import com.catherine.webservices.services.NetworkHealthService

/**
 * Created by Catherine on 2017/7/17.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

class Utils {

    val JUNIT_TEST = "MB890CPC"

    fun junitTestSample2(): String {
        return "B0SK80a"
    }

    companion object {
        fun isNetworkHealth(ctx: Context): Boolean {
            val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            CLog.e("Utils", activeNetwork!!.extraInfo)
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }

        fun isWifi(ctx: Context): Boolean {
            val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork.type == ConnectivityManager.TYPE_WIFI
        }

        fun listenToNetworkState(ctx: Context) {
            val nhs = Intent(ctx, NetworkHealthService::class.java)
            ctx.startService(nhs)
        }

        fun stopListeningToNetworkState(ctx: Context) {
            val nhs = Intent(ctx, NetworkHealthService::class.java)
            ctx.stopService(nhs)
        }

        fun junitTestSample1(): String {
            return "CSOp40c"
        }
    }
}
