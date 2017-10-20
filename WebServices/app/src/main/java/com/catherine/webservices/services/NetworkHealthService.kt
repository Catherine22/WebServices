package com.catherine.webservices.services

import android.annotation.TargetApi
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import catherine.messagecenter.AsyncResponse
import catherine.messagecenter.Server
import com.catherine.webservices.Commands
import com.catherine.webservices.toolkits.CLog

/**
 * Created by Catherine on 2017/7/17.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

class NetworkHealthService : Service() {
    private var internetReceiver: InternetConnectivityReceiver? = null
    private var sv: Server? = null

    companion object {
        private val TAG = "NetworkHealthService"
    }

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
                sv = Server(context, object : AsyncResponse {
                    override fun onFailure(errorCode: Int) {
                        CLog.e(TAG, "LocalBroadCast error:$errorCode")
                    }
                })
                if (intent.extras != null) {
                    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val ni = connectivityManager.activeNetworkInfo
                    if (ni != null) {
                        if (ni.isConnectedOrConnecting) {
                            val b = Bundle()
                            b.putBoolean("isConnectedOrConnecting", true)
                            b.putString("typeName", ni.typeName)
                            sv?.pushBundle(Commands.C_NETWORK_STATE, b)
                            Toast.makeText(this@NetworkHealthService, ni.typeName + " network connected", Toast.LENGTH_LONG).show()
                        } else {
                            //NetworkInfo对象为空 则代表没有网络
                            val b = Bundle()
                            b.putBoolean("isConnectedOrConnecting", false)
                            sv?.pushBundle(Commands.C_NETWORK_STATE, b)
                            CLog.e(TAG, "Network disabled")
                            Toast.makeText(this@NetworkHealthService, "Network disabled", Toast.LENGTH_LONG).show()
                        }
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
}


fun NetworkInfo.State.toInt(): Int {
    val r = when (this) {
        NetworkInfo.State.CONNECTED -> 0
        NetworkInfo.State.CONNECTING -> 1
        NetworkInfo.State.DISCONNECTED -> 2
        NetworkInfo.State.DISCONNECTING -> 3
        NetworkInfo.State.SUSPENDED -> 4
        NetworkInfo.State.UNKNOWN -> 5
    }
    return r
}

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun NetworkInfo.DetailedState.toInt(): Int {
    val r = when (this) {
        NetworkInfo.DetailedState.AUTHENTICATING -> 0
        NetworkInfo.DetailedState.BLOCKED -> 1
        NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK -> 2
        NetworkInfo.DetailedState.CONNECTED -> 3
        NetworkInfo.DetailedState.CONNECTING -> 4
        NetworkInfo.DetailedState.DISCONNECTED -> 5
        NetworkInfo.DetailedState.DISCONNECTING -> 6
        NetworkInfo.DetailedState.FAILED -> 7
        NetworkInfo.DetailedState.IDLE -> 8
        NetworkInfo.DetailedState.OBTAINING_IPADDR -> 9
        NetworkInfo.DetailedState.SCANNING -> 10
        NetworkInfo.DetailedState.SUSPENDED -> 11
        NetworkInfo.DetailedState.VERIFYING_POOR_LINK -> 12
    }
    return r
}