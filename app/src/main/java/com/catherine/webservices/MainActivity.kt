package com.catherine.webservices

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentActivity
import com.catherine.webservices.adapters.MainViewPagerAdapter

import com.catherine.webservices.xml.SAXParser
import com.catherine.webservices.xml.XMLDelegate
import com.catherine.webservices.xml.XMLParserListener
import com.catherine.webservices.toolkits.CLog
import com.catherine.webservices.sample.KotlinTemplate
import com.catherine.webservices.sample.player.Player
import com.catherine.webservices.network.NetworkHelper
import com.catherine.webservices.xml.DOMParser
import org.dom4j.Document

import java.io.IOException
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by Catherine on 2017/8/14.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
class MainActivity : FragmentActivity() {
    companion object {
        private val TAG = "MainActivity"
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
        vp_content.adapter = MainViewPagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(vp_content)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab == tabLayout.getTabAt(0)) {
                    vp_content.currentItem = 0
                } else if (tab == tabLayout.getTabAt(1)) {
                    vp_content.currentItem = 1
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

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
