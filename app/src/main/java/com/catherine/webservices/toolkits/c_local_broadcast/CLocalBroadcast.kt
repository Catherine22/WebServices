package com.catherine.webservices.toolkits.c_local_broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.support.v4.content.LocalBroadcastManager
import com.catherine.webservices.parcelables.NetworkInfoParcelable
import android.content.IntentFilter


/**
 * Created by Catherine on 2017/8/17.
 */
class CRequest(val ctx: Context, val errorLog: CRequestLog) {
    fun sendNetworkInfo(id: LocalBroadcastIDs, message: Parcelable) {
        if (CMessageQueue.requests.contains(id)) {
            errorLog.onFail(id, ErrorMessages.RUNNING_NO_DUPLICATE_IDS_AT_THE_SAME_TIME)
            return
        }

        val broadcast: Intent = Intent()
        broadcast.putExtra(id.toString(), message)
        CMessageQueue.requests[id] = CType.PARCELABLE
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(broadcast)
    }
}

class CResponse(val ctx: Context, val callback: CCallback) {

    fun getNetworkInfoParcelable(id: LocalBroadcastIDs) {
        var localBroadcastManager: LocalBroadcastManager = LocalBroadcastManager.getInstance(ctx)
        val intentFilter = IntentFilter()
        intentFilter.addAction(id.toString())
        val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (CMessageQueue.requests.containsKey(id)) {
                    val result: CResult = CResult(intent?.getParcelableExtra<NetworkInfoParcelable>(id.name))
                    callback.result(result)
                }
            }
        }
        localBroadcastManager.registerReceiver(receiver, intentFilter)
    }
}

