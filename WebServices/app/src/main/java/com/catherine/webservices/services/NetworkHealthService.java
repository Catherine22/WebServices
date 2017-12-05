package com.catherine.webservices.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.IgnoreProguard;

import catherine.messagecenter.AsyncResponse;
import catherine.messagecenter.Server;

/**
 * Created by Catherine on 2017/11/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class NetworkHealthService extends Service implements IgnoreProguard {
    private final static String TAG = NetworkHealthService.class.getSimpleName();
    private InternetConnectivityReceiver internetReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        internetReceiver = new InternetConnectivityReceiver();
        IntentFilter internetIntentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internetReceiver, internetIntentFilter);
    }

    class InternetConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Server sv = new Server(context, new AsyncResponse() {
                    @Override
                    public void onFailure(int errorCode) {
                        CLog.e(TAG, "onFailure:" + errorCode);
                    }
                });
                if (intent.getExtras() != null) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
                    if (ni != null) {
                        if (ni.isConnectedOrConnecting()) {
                            Bundle b = new Bundle();
                            b.putBoolean("isConnectedOrConnecting", true);
                            b.putString("typeName", ni.getTypeName());
                            sv.pushBundle("C_NETWORK_STATE", b);
                            CLog.i(TAG, ni.getTypeName() + " network connected");
                        } else {
                            Bundle b = new Bundle();
                            b.putBoolean("isConnectedOrConnecting", false);
                            sv.pushBundle("C_NETWORK_STATE", b);
                            CLog.e(TAG, "Network disabled");
                        }
                    }
                }
            } catch (Exception e) {
                unregisterReceiver(internetReceiver);
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(internetReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
