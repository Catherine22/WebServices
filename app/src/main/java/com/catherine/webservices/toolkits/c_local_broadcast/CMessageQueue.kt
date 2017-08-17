package com.catherine.webservices.toolkits.c_local_broadcast

import java.util.*

/**
 * Created by Catherine on 2017/8/17.
 */
class CMessageQueue {
    companion object {
        var requests: HashMap<LocalBroadcastIDs, CType> = HashMap()
    }
}

enum class CType {
    PARCELABLE
}