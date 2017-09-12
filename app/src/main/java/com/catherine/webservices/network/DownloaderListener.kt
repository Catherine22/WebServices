package com.catherine.webservices.network

/**
 * Created by Catherine on 2017/8/28.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
interface DownloaderListener {
    fun update(downloadedLength: Int, LENGTH: Int)
    fun connectFailure(response: HttpResponse, e: Exception?)
}