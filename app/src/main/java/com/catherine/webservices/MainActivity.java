package com.catherine.webservices;

import android.app.Activity;
import android.os.Bundle;

import com.catherine.webservices.tasks.SampleAsyncTask;
import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.Utils;

public class MainActivity extends Activity {
    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CLog.d(TAG, "isNetworkHealth:" + Utils.isNetworkHealth(MainActivity.this));
        CLog.d(TAG, "isWifi:" + Utils.isWifi(MainActivity.this));
        Utils.listenToNetworkState(MainActivity.this);
        new SampleAsyncTask().execute("param1");
    }


}
