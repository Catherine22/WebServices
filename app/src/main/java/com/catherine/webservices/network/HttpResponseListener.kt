package com.catherine.webservices.network

/**
 * Created by Catherine on 2017/8/28.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
interface HttpResponseListener {
    fun connectSuccess(code: Int, message: String, body: String)
    fun connectFailure(code: Int, message: String, errorStream: String, e: Exception?)
}