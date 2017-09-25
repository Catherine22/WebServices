package com.catherine.webservices.network

/**
 * Created by Catherine on 2017/9/14.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
interface NetworkHealthListener {
    fun networkConnected(type: String)
    fun networkDisable()
}