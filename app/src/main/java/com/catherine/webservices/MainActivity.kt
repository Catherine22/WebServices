package com.catherine.webservices

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import com.catherine.webservices.adapters.MainRvAdapter
import com.catherine.webservices.parcelables.NetworkInfoParcelable

import com.catherine.webservices.xml.SAXParser
import com.catherine.webservices.xml.XMLDelegate
import com.catherine.webservices.xml.XMLParserListener
import com.catherine.webservices.toolkits.CLog
import com.catherine.webservices.sample.KotlinTemplate
import com.catherine.webservices.sample.player.Player
import com.catherine.webservices.toolkits.NetworkHelper
import com.catherine.webservices.toolkits.c_local_broadcast.CCallback
import com.catherine.webservices.toolkits.c_local_broadcast.CResponse
import com.catherine.webservices.toolkits.c_local_broadcast.CResult
import com.catherine.webservices.toolkits.c_local_broadcast.LocalBroadcastIDs
import com.catherine.webservices.views.DividerItemDecoration
import com.catherine.webservices.xml.DOMParser
import org.dom4j.Document
import kotlinx.android.synthetic.main.activity_main.*

import java.io.IOException

class MainActivity : Activity() {
    companion object {
        private val TAG = "MainActivity"
        private val students = arrayListOf("Kris", "Caroline", "Alma")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setView()


        CLog.d(TAG, "isNetworkHealth:${NetworkHelper.isNetworkHealth(this@MainActivity)}")
        CLog.d(TAG, "isWifi:${NetworkHelper.isWifi(this@MainActivity)}")
//                    CLog.d(TAG, "getIP:${NetworkHelper.getIp("http://www.baidu.com/")}")
//                    CLog.d(TAG, "getLocalIP:${NetworkHelper.getLocalIp()}")
        NetworkHelper.listenToNetworkState(this@MainActivity)

        val checkStateWork: Handler = Handler(MyApplication.INSTANCE.mainHandlerThread.looper)
        checkStateWork.post {
        }


        val networkHelthCallback: CResponse = CResponse(this, object : CCallback {
            override fun result(result: CResult) {
                CLog.i(TAG, "Got callback")
                val parcelable: NetworkInfoParcelable = result.parcelable as NetworkInfoParcelable
                CLog.i(TAG, parcelable.toString())
            }
        })
        networkHelthCallback.getNetworkInfoParcelable(LocalBroadcastIDs.NetworkHealthCallback)


//        testKotlin()
//        testXML()
    }

    private fun setView() {
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark)
        srl_container.setOnRefreshListener {
            CLog.d(TAG, "refresh")
            srl_container.isRefreshing = false
        }

        rv_main_list.addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL_LIST))
        rv_main_list.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        rv_main_list.adapter = MainRvAdapter(this@MainActivity, students, object : MainRvAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                CLog.d(TAG, "Click $position")
                when (position) {
                    0 -> {
                    }
                    1 -> {

                    }
                }
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
