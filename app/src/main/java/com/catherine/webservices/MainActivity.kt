package com.catherine.webservices

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import com.catherine.webservices.adapters.MainRvAdapter
import com.catherine.webservices.network.MyApache

import com.catherine.webservices.xml.SAXParser
import com.catherine.webservices.xml.XMLDelegate
import com.catherine.webservices.xml.XMLParserListener
import com.catherine.webservices.toolkits.CLog
import com.catherine.webservices.sample.KotlinTemplate
import com.catherine.webservices.sample.player.Player
import com.catherine.webservices.network.NetworkHelper
import com.catherine.webservices.views.DividerItemDecoration
import com.catherine.webservices.xml.DOMParser
import org.dom4j.Document
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair

import java.io.IOException
import java.util.*

/**
 * Created by Catherine on 2017/8/14.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
class MainActivity : Activity() {
    companion object {
        private val TAG = "MainActivity"
        private val students = arrayListOf("Apache HttpGet", "Apache HttpPost", "Alma")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setView()


        val checkStateWork = Handler(MyApplication.INSTANCE.calHandlerThread.looper)
        checkStateWork.post {
            val networkHelper = NetworkHelper(this)
            CLog.d(TAG, "isNetworkHealth:${networkHelper.isNetworkHealth()}")
            CLog.d(TAG, "isWifi:${networkHelper.isWifi()}")
            networkHelper.listenToNetworkState()
        }


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
                        val networkTask = Handler(MyApplication.INSTANCE.calHandlerThread.looper)
                        networkTask.post {
                            val headers = MyApache.getDefaultHeaders()
                            headers["h1"] = "Hi there!"
                            headers["h2"] = "I am a mobile phone."
                            MyApache.doGet(Constants.HOST + "LoginServlet?name=zhangsan&password=123456", headers)
                            MyApache.doGet("http://dictionary.cambridge.org/zhs/%E6%90%9C%E7%B4%A2/%E8%8B%B1%E8%AF%AD-%E6%B1%89%E8%AF%AD-%E7%AE%80%E4%BD%93/direct/?q=philosopher")
                        }

                    }
                    1 -> {
                        val networkTask = Handler(MyApplication.INSTANCE.calHandlerThread.looper)
                        networkTask.post {
                            val headers = MyApache.getDefaultHeaders()
                            headers["Authorization"] = "12345"
                            val nameValuePairs = ArrayList<NameValuePair>()
                            nameValuePairs.add(BasicNameValuePair("name", "zhangsan"))
                            nameValuePairs.add(BasicNameValuePair("password", "123456"))
                            MyApache.doPost(Constants.HOST + "LoginServlet", headers, nameValuePairs)
                        }

                        networkTask.post {
                            val nameValuePairs = ArrayList<NameValuePair>()
                            nameValuePairs.add(BasicNameValuePair("name", ""))
                            nameValuePairs.add(BasicNameValuePair("password", ""))
                            MyApache.doPost(Constants.HOST + "LoginServlet", nameValuePairs)
                        }

                        networkTask.post {
                            val nameValuePairs = ArrayList<NameValuePair>()
                            nameValuePairs.add(BasicNameValuePair("name", "zhangsan"))
                            nameValuePairs.add(BasicNameValuePair("password", "123456"))
                            MyApache.doPost(Constants.HOST + "LoginServlet", nameValuePairs)
                        }
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
