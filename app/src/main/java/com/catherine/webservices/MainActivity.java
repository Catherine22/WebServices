package com.catherine.webservices;

import android.app.Activity;
import android.os.Bundle;

import com.catherine.webservices.xml.DOMParser;
import com.catherine.webservices.xml.SAXParser;
import com.catherine.webservices.xml.XMLDelegate;
import com.catherine.webservices.xml.XMLParserListener;
import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.Utils;

import java.io.IOException;

public class MainActivity extends Activity {
    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CLog.d(TAG, "isNetworkHealth:" + Utils.isNetworkHealth(MainActivity.this));
        CLog.d(TAG, "isWifi:" + Utils.isWifi(MainActivity.this));
//        Utils.listenToNetworkState(MainActivity.this);
//        new SampleAsyncTask().execute("param1");
        try {
            XMLDelegate xmlDelegate = new XMLDelegate();
            xmlDelegate.read("name", new SAXParser(getAssets().open("sample.xml"), new XMLParserListener() {
                @Override
                public void onSuccess(String message) {
                    CLog.d(TAG, "onSuccess:" + message);
                }

                @Override
                public void onFail() {
                    CLog.d(TAG, "onFail");
                }
            }));

            xmlDelegate.read("time", new DOMParser(getAssets().open("sample.xml"), new XMLParserListener() {
                @Override
                public void onSuccess(String message) {
                    CLog.d(TAG, "onSuccess:" + message);
                }

                @Override
                public void onFail() {
                    CLog.d(TAG, "onFail");
                }
            }));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
