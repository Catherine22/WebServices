package com.catherine.webservices.toolkits

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.catherine.webservices.services.NetworkHealthService
import java.net.InetAddress

/**
 * Created by Catherine on 2017/8/14.
 */
class NetworkHelper {
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

        fun getIp(url: String): String? {
            return InetAddress.getByName(url).toString()
        }

        fun getLocalIp(): String? {
            return InetAddress.getLocalHost().toString()
        }
    }
}