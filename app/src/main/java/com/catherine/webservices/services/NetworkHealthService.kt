package com.catherine.webservices.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.IBinder
import android.widget.Toast

import com.catherine.webservices.toolkits.CLog

/**
 * Created by Catherine on 2017/7/17.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

class NetworkHealthService : Service() {
    private var internetReceiver: InternetConnectivityReceiver? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        internetReceiver = InternetConnectivityReceiver()
        val internetIntentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        internetReceiver = InternetConnectivityReceiver()
        registerReceiver(internetReceiver, internetIntentFilter)
    }

    inner class InternetConnectivityReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (intent.extras != null) {
                    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val ni = connectivityManager.activeNetworkInfo
                    if (ni != null && ni.isConnectedOrConnecting) {
                        Toast.makeText(this@NetworkHealthService, ni.typeName + " network connected", Toast.LENGTH_LONG).show()
                    } else {
                        CLog.e(TAG, "Network disabled")
                        Toast.makeText(this@NetworkHealthService, "Network disabled", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                unregisterReceiver(internetReceiver)
                e.printStackTrace()
            }

        }
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(internetReceiver)
        } catch (e: Exception) {
            unregisterReceiver(internetReceiver)
            e.printStackTrace()
        }

        super.onDestroy()
    }

    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }

    companion object {
        private val TAG = "NetworkHealthService"
    }
}