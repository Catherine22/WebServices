package com.catherine.webservices.tasks

import android.os.AsyncTask

import com.catherine.webservices.toolkits.CLog

import java.io.IOException

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * Created by Catherine on 2017/7/17.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

class SampleAsyncTask : AsyncTask<String, Void, Void>() {
    private var client: OkHttpClient? = null

    override fun doInBackground(vararg params: String): Void? {
        client = OkHttpClient()
        try {
            CLog.w(TAG, "run:" + run("http://test.active.mygame.com.tw/OfficialWebsite/SetPictureResources/index.htm"))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    @Throws(IOException::class)
    internal fun run(url: String): String {
        val request = Request.Builder()
                .url(url)
                .build()

        val response = client!!.newCall(request).execute()
        return response.body()!!.string()
    }

    companion object {
        private val TAG = "SampleAsyncTask"
    }
}
