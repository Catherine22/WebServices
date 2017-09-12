package com.catherine.webservices.network

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import com.catherine.webservices.services.NetworkHealthService
import com.catherine.webservices.toolkits.CLog
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.*

/**
 * Created by Catherine on 2017/8/14.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
class NetworkHelper(private val ctx: Context) {
    companion object {
        val TAG = "NetworkHelper"


        fun encodeURL(url: String): String {
            val ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%"
            return Uri.encode(url, ALLOWED_URI_CHARS)
        }

        fun getFileNameFromUrl(url:String):String{
            val fileNames = url.split("/")
            return fileNames[fileNames.size - 1]
        }
    }

    fun isNetworkHealth(): Boolean {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        CLog.e(TAG, activeNetwork!!.extraInfo)
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun isWifi(): Boolean {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork.type == ConnectivityManager.TYPE_WIFI
    }

    fun listenToNetworkState() {
        val nhs = Intent(ctx, NetworkHealthService::class.java)
        ctx.startService(nhs)
    }

    fun stopListeningToNetworkState() {
        val nhs = Intent(ctx, NetworkHealthService::class.java)
        ctx.stopService(nhs)
    }


    /**
     * ip
     */
    fun getHostAddress(url: String): String {
        var result = ""
        try {
            val address: InetAddress = InetAddress.getByName(url)
            result = address.hostAddress
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
        return result
    }


    fun getAllHostAddress(url: String): ArrayList<String>? {
        try {
            val address: Array<InetAddress> = InetAddress.getAllByName(url)
            if (address == null || address.isEmpty())
                return null
            return address.indices.mapTo(ArrayList()) { address[it].hostAddress }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * absolute domain name
     */
    fun getHostName(url: String): String {
        var result = ""
        try {
            //IP地址不存在或DNS服务器不允许进行IP地址和域名映射，就返回这个IP地址。
            val address: InetAddress = InetAddress.getByName(url)
            result = address.hostName
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
        return result
    }

    fun getAllHostName(url: String): ArrayList<String>? {
        try {
            val address: Array<InetAddress> = InetAddress.getAllByName(url)
            if (address == null || address.isEmpty())
                return null
            return address.indices.mapTo(ArrayList()) { address[it].hostName }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
        return null
    }


    /**
     * do a reverse DNS lookup and return a domain name
     */
    fun getDNSHostName(url: String): String {
        var result = ""
        try {
            //IP地址不存在或DNS服务器不允许进行IP地址和域名映射，就返回这个IP地址。
            val address: InetAddress = InetAddress.getByName(url)
            result = address.canonicalHostName
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
        return result
    }

    fun getAllDNSHostName(url: String): ArrayList<String>? {
        try {
            val address: Array<InetAddress> = InetAddress.getAllByName(url)
            if (address == null || address.isEmpty())
                return null
            return address.indices.mapTo(ArrayList()) { address[it].canonicalHostName }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
        return null
    }
}