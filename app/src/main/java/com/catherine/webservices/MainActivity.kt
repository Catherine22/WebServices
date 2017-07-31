package com.catherine.webservices

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import com.catherine.webservices.adapters.MainRvAdapter

import com.catherine.webservices.xml.SAXParser
import com.catherine.webservices.xml.XMLDelegate
import com.catherine.webservices.xml.XMLParserListener
import com.catherine.webservices.toolkits.CLog
import com.catherine.webservices.sample.KotlinTemplate
import com.catherine.webservices.sample.player.Player
import com.catherine.webservices.toolkits.Utils
import com.catherine.webservices.views.DividerItemDecoration
import com.catherine.webservices.xml.DOMParser
import org.dom4j.Document
import kotlinx.android.synthetic.main.activity_main.*

import java.io.IOException

class MainActivity : Activity() {

    companion object {
        private val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setView()


        CLog.d(TAG, "isNetworkHealth:${Utils.isNetworkHealth(this@MainActivity)}")
        CLog.d(TAG, "isWifi:${Utils.isWifi(this@MainActivity)}")

        //        Utils.listenToNetworkState(MainActivity.this);
        //        new SampleAsyncTask().execute("param1");

        testKotlin()
//        testXML()
    }

    private fun setView() {
        val students = arrayListOf("Kris", "Caroline", "Alma")
        rv_main_list.addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL_LIST))
        rv_main_list.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        rv_main_list.adapter = MainRvAdapter(this@MainActivity, students, object : MainRvAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                CLog.d(TAG, "Click $position")
            }

            override fun onItemLongClick(view: View, position: Int) {
                CLog.d(TAG, "Long click $position")
            }
        })
    }

    fun testKotlin() {
        val tmp = KotlinTemplate()
        tmp.basicSyntax()
        tmp.printSth()
        tmp.printSth("20f8-ads3bqwe-9d8vasd", "3f-s1v0m3")

        tmp.doRecursive()

        val player = Player()
        player.play("http://ws.stream.qqmusic.qq.com/C2000012Ppbd3hjGOK.m4a")
        player.pause()
        player.resume()
        player.seekTo(30000)
        player.stop()

        tmp.callJava()
        tmp.runOnNewThread()
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
