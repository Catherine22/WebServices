package com.catherine.webservices.toolkits;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.catherine.webservices.services.NetworkHealthService;

/**
 * Created by Catherine on 2017/7/17.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class Utils {
    public static boolean isNetworkHealth(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        CLog.e("Utils", activeNetwork.getExtraInfo());
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isWifi(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static void listenToNetworkState(Context ctx) {
        Intent nhs = new Intent(ctx, NetworkHealthService.class);
        ctx.startService(nhs);
    }

    public static void stopListeningToNetworkState(Context ctx) {
        Intent nhs = new Intent(ctx, NetworkHealthService.class);
        ctx.stopService(nhs);
    }
}
