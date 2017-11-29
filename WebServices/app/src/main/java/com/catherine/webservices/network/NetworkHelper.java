package com.catherine.webservices.network;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import com.catherine.webservices.MyApplication;
import com.catherine.webservices.services.NetworkHealthService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import catherine.messagecenter.Client;
import catherine.messagecenter.CustomReceiver;
import catherine.messagecenter.Result;

/**
 * Created by Catherine on 2017/11/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class NetworkHelper {
    private Client client;
    private Context ctx;

    public NetworkHelper() {
        ctx = MyApplication.INSTANCE;
    }

    public static String encodeURL(String url) {
        String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
        return Uri.encode(url, ALLOWED_URI_CHARS);
    }

    public static String getFileNameFromUrl(String url) {
        String[] fileNames = url.split("/");
        return fileNames[fileNames.length - 1];
    }

    public static String formattedUrl(String url) {
        String tmp = url;
        //读取文件
        if (url.startsWith("file:///") || url.startsWith("content://"))
            return tmp;

        //通过scheme开启其他应用
        if (url.contains("://"))
            return tmp;

        //简单判断而已，不考虑.cn .org之类的
        if (url.startsWith("www.") || url.contains(".com")) {
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                tmp = "http://" + url;
//            else
//                tmp = "https://www.google.com/search?q=" + url;
            return tmp;
        }

        return tmp;
    }

    public String getHostAddress(String url) {
        String result = "";
        try {
            InetAddress address = InetAddress.getByName(url);
            result = address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<String> getAllHostAddress(String url) {
        try {
            InetAddress[] address = InetAddress.getAllByName(url);
            if (address == null || address.length == 0)
                return null;
            else {
                List<String> result = new ArrayList<>();
                for (int i = 0; i < address.length; i++)
                    result.add(address[i].getHostAddress());
                return result;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getHostName(String url) {
        String result = "";
        try {
            //IP地址不存在或DNS服务器不允许进行IP地址和域名映射，就返回这个IP地址。
            InetAddress address = InetAddress.getByName(url);
            result = address.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return result;
    }


    public List<String> getAllHostName(String url) {
        try {
            InetAddress[] address = InetAddress.getAllByName(url);
            if (address == null || address.length == 0)
                return null;
            else {
                List<String> result = new ArrayList<>();
                for (int i = 0; i < address.length; i++)
                    result.add(address[i].getHostName());
                return result;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getDNSHostName(String url) {
        String result = "";
        try {
            //IP地址不存在或DNS服务器不允许进行IP地址和域名映射，就返回这个IP地址。
            InetAddress address = InetAddress.getByName(url);
            result = address.getCanonicalHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return result;
    }


    public List<String> getAllDNSHostName(String url) {
        try {
            InetAddress[] address = InetAddress.getAllByName(url);
            if (address == null || address.length == 0)
                return null;
            else {
                List<String> result = new ArrayList<>();
                for (int i = 0; i < address.length; i++)
                    result.add(address[i].getCanonicalHostName());
                return result;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isWifi() {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null)
            return false;
        else
            return (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public boolean isNetworkHealthy() {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null)
            return false;
        else
            return ni.isConnectedOrConnecting();
    }

    public void listenToNetworkState(final NetworkHealthListener listener) {
        client = new Client(ctx, new CustomReceiver() {
            @Override
            public void onBroadcastReceive(Result result) {
                Bundle b = result.getMBundle();
                if (b.getBoolean("isConnectedOrConnecting", false)) {
                    listener.networkConnected(b.getString("typeName", "N/A"));
                } else {
                    listener.networkDisable();
                }
            }
        });
        client.gotMessages("C_NETWORK_STATE");
        if (!isRunningService(ctx, NetworkHealthService.class.getName())) {
            Intent nhs = new Intent(ctx, NetworkHealthService.class);
            ctx.startService(nhs);
        }
    }

    public void stopListeningToNetworkState() {
        client.release();
//        Intent nhs = new Intent(ctx, NetworkHealthService.class);
//        ctx.stopService(nhs);
    }

    private boolean isRunningService(Context ctx, String serviceName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : infos) {
            String runningServiceName = info.service.getClassName();
            if (runningServiceName.equals(serviceName))
                isRunning = true;
        }
        return isRunning;
    }
}
