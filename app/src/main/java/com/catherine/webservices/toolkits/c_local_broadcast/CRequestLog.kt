package com.catherine.webservices.toolkits.c_local_broadcast

/**
 * Created by Catherine on 2017/8/17.
 */
interface CRequestLog {
    fun onFail(id: LocalBroadcastIDs, message: ErrorMessages)
}