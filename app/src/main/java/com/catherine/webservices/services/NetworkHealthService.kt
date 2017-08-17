package com.catherine.webservices.services

import android.annotation.TargetApi
import android.app.Service
import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.widget.Toast
import com.catherine.webservices.parcelables.NetworkInfoParcelable

import com.catherine.webservices.toolkits.CLog
import com.catherine.webservices.toolkits.c_local_broadcast.CRequest
import com.catherine.webservices.toolkits.c_local_broadcast.CRequestLog
import com.catherine.webservices.toolkits.c_local_broadcast.ErrorMessages
import com.catherine.webservices.toolkits.c_local_broadcast.LocalBroadcastIDs

/**
 * Created by Catherine on 2017/7/17.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

class NetworkHealthService : Service() {
    private var internetReceiver: InternetConnectivityReceiver? = null
    private var request = CRequest(this, object : CRequestLog {
        override fun onFail(id: LocalBroadcastIDs, message: ErrorMessages) {
            CLog.d(TAG, "Broadcast($id):$message")
        }
    })

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
                if (intent.extras != null) {
                    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val ni = connectivityManager.activeNetworkInfo
                    val info: NetworkInfoParcelable = NetworkInfoParcelable()
                    if (ni != null) {
                        info.mDetailedState = ni.detailedState.toInt()
                        info.mExtraInfo = ni.extraInfo
                        info.mNetworkType = ni.type
                        info.mReason = ni.reason
                        info.mState = ni.state.toInt()
                        info.mIsState = booleanArrayOf(ni.isConnected, ni.isConnectedOrConnecting, ni.isFailover, ni.isAvailable, ni.isRoaming)
                        info.mSubtype = ni.subtype
                        info.mSubtypeName = ni.subtypeName
                        info.mTypeName = ni.typeName
                        if (ni.isConnectedOrConnecting) {
                            request.sendNetworkInfo(LocalBroadcastIDs.NetworkHealthCallback, info)
                            Toast.makeText(this@NetworkHealthService, ni.typeName + " network connected", Toast.LENGTH_LONG).show()
                        } else {
                            CLog.e(TAG, "Network disabled")
                            request.sendNetworkInfo(LocalBroadcastIDs.NetworkHealthCallback, info)
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