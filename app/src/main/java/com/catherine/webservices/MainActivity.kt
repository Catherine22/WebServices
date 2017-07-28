package com.catherine.webservices

import android.app.Activity
import android.os.Bundle

import com.catherine.webservices.xml.SAXParser
import com.catherine.webservices.xml.XMLDelegate
import com.catherine.webservices.xml.XMLParserListener
import com.catherine.webservices.toolkits.CLog
import com.catherine.webservices.sample.KotlinTemplate
import com.catherine.webservices.sample.player.Player
import com.catherine.webservices.toolkits.Utils
import com.catherine.webservices.xml.DOMParser
import org.dom4j.Document
import java.io.File

import java.io.IOException

class MainActivity : Activity() {

    companion object {
        private val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        CLog.d(TAG, "isNetworkHealth:${Utils.isNetworkHealth(this@MainActivity)}")
        CLog.d(TAG, "isWifi:${Utils.isWifi(this@MainActivity)}")

        //        Utils.listenToNetworkState(MainActivity.this);
        //        new SampleAsyncTask().execute("param1");

        testKotlin()
//        testXML()
    }

    fun testKotlin() {
        val tmp = KotlinTemplate()
        tmp.printSth()
        tmp.printSth("20f8-ads3bqwe-9d8vasd", "3f-s1v0m3")

//        tmp.doRecursive()

        val player = Player()
        player.play("http://ws.stream.qqmusic.qq.com/C2000012Ppbd3hjGOK.m4a")
        player.pause()
        player.resume()
        player.seekTo(30000)
        player.stop()

        val json = assets.open("SimpleMission.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
        tmp.textGson(json)
    }

    fun testXML() {
        try {
            val xmlDelegate = XMLDelegate()
            xmlDelegate.read("name", assets.open("sample.xml"), SAXParser(), object : XMLParserListener {
                override fun onSuccess(doc: Document) {
                    CLog.d(TAG, "onSuccess:" + doc.asXML())
                }

                override fun onSuccess(message: String) {
                    CLog.d(TAG, "onSuccess:" + message)
                }

                override fun onSuccess(message: List<String>) {
                    CLog.d(TAG, "onSuccess:" + message)
                }

                override fun onFail() {
                    CLog.d(TAG, "onFail")
                }
            })

            xmlDelegate.read("time", assets.open("sample.xml"), DOMParser(), object : XMLParserListener {
                override fun onSuccess(doc: Document) {
                    CLog.d(TAG, "onSuccess:${doc.asXML()}")
                }

                override fun onSuccess(message: String) {
                    CLog.d(TAG, "onSuccess:$message")
                }

                override fun onSuccess(message: List<String>) {
                    CLog.d(TAG, "onSuccess:$message")
                }

                override fun onFail() {
                    CLog.d(TAG, "onFail")
                }
            })
            xmlDelegate.modify(assets.open("sample.xml"), object : XMLParserListener {
                override fun onSuccess(doc: Document) {
                    CLog.d(TAG, "onSuccess:${doc.asXML()}")
                }

                override fun onSuccess(message: String) {
                    CLog.d(TAG, "onSuccess:$message")
                }

                override fun onSuccess(message: List<String>) {
                    CLog.d(TAG, "onSuccess:$message")
                }

                override fun onFail() {
                    CLog.d(TAG, "onFail")
                }
            })
            xmlDelegate.romove(assets.open("sample.xml"), object : XMLParserListener {
                override fun onSuccess(doc: Document) {
                    CLog.d(TAG, "onSuccess:${doc.asXML()}")
                }

                override fun onSuccess(message: String) {
                    CLog.d(TAG, "onSuccess:$message")
                }

                override fun onSuccess(message: List<String>) {
                    CLog.d(TAG, "onSuccess:$message")
                }

                override fun onFail() {
                    CLog.d(TAG, "onFail")
                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
