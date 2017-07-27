package com.catherine.webservices

import android.app.Activity
import android.os.Bundle

import com.catherine.webservices.xml.SAXParser
import com.catherine.webservices.xml.XMLDelegate
import com.catherine.webservices.xml.XMLParserListener
import com.catherine.webservices.toolkits.CLog
import com.catherine.webservices.toolkits.KotlinTemplate
import com.catherine.webservices.toolkits.Utils
import kotlinx.android.synthetic.main.activity_main.*
import org.dom4j.Document

import java.io.IOException

class MainActivity : Activity() {

    companion object {
        private val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tmp = KotlinTemplate()
        tmp.printSth()
        tmp.printSth("20f8-ads3bqwe-9d8vasd", "3f-s1v0m3")


        CLog.d(TAG, "isNetworkHealth:${Utils.isNetworkHealth(this@MainActivity)}")
        CLog.d(TAG, "isWifi:${Utils.isWifi(this@MainActivity)}")

        //        Utils.listenToNetworkState(MainActivity.this);
        //        new SampleAsyncTask().execute("param1");
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

            //            xmlDelegate.read("time", getAssets().open("sample.xml"), new DOMParser(), new XMLParserListener() {
            //            @Override
            //            public void onSuccess(Document doc) {
            //                CLog.d(TAG, "onSuccess:" + doc.asXML());
            //            }
            //                @Override
            //                public void onSuccess(String message) {
            //                    CLog.d(TAG, "onSuccess:" + message);
            //                }
            //
            //                @Override
            //                public void onSuccess(List<String> message) {
            //                    CLog.d(TAG, "onSuccess:" + message);
            //                }
            //
            //                @Override
            //                public void onFail() {
            //                    CLog.d(TAG, "onFail");
            //                }
            //            });
            //            xmlDelegate.modify(getAssets().open("sample.xml"), new XMLParserListener() {
            //                @Override
            //                public void onSuccess(Document doc) {
            //                    CLog.d(TAG, "onSuccess:" + doc.asXML());
            //                }
            //
            //                @Override
            //                public void onSuccess(String message) {
            //                    CLog.d(TAG, "onSuccess:" + message);
            //                }
            //
            //                @Override
            //                public void onSuccess(List<String> message) {
            //                    CLog.d(TAG, "onSuccess:" + message);
            //                }
            //
            //                @Override
            //                public void onFail() {
            //                    CLog.d(TAG, "onFail");
            //                }
            //            });
            //            xmlDelegate.romove(getAssets().open("sample.xml"), new XMLParserListener() {
            //                @Override
            //                public void onSuccess(Document doc) {
            //                    CLog.d(TAG, "onSuccess:" + doc.asXML());
            //                }
            //
            //                @Override
            //                public void onSuccess(String message) {
            //                    CLog.d(TAG, "onSuccess:" + message);
            //                }
            //
            //                @Override
            //                public void onSuccess(List<String> message) {
            //                    CLog.d(TAG, "onSuccess:" + message);
            //                }
            //
            //                @Override
            //                public void onFail() {
            //                    CLog.d(TAG, "onFail");
            //                }
            //            });
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


}
