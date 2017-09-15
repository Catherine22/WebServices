package com.catherine.webservices.network;


import android.net.http.HttpResponseCache;
import android.os.Build;
import android.text.TextUtils;

import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.StreamUtils;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Catherine on 2017/8/24.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MyHttpURLConnection {
    public final static String TAG = "MyHttpURLConnection";
    public final static int CONNECT_TIMEOUT = 10000;
    public final static int MAX_CACHE_SIZE = 10 * 1024 * 1024;//10M

    public static Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", Constants.AUTHORIZATION);
        headers.put("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.put("Accept-Language", Locale.getDefault().toString());
        headers.put("Connection", "keep-alive");
        return headers;
    }

    public static String getSimpleStringBody(Map<String, String> nameValuePairs) {
        StringBuilder sb = new StringBuilder();
        if (nameValuePairs != null) {
            Set<String> set = nameValuePairs.keySet();
            for (String name : set) {
                sb.append(name);
                sb.append("=");
                sb.append(nameValuePairs.get(name));
                sb.append("&");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public void doGet(HttpRequest request, HttpResponseListener listener) {
        int code = -1;
        String msg = "";
        String response = "";
        String error = "";
        Exception e = null;
        try {
            boolean cacheable = isCacheable(request);
            if (cacheable) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                    Class.forName("android.net.http.HttpResponseCache").getMethod("install", File.class, long.class).invoke(null, MyApplication.INSTANCE.getDiskCacheDir("entity"), MyHttpURLConnection.MAX_CACHE_SIZE);
                else
                    HttpResponseCache.install(MyApplication.INSTANCE.getDiskCacheDir("entity"), MyHttpURLConnection.MAX_CACHE_SIZE);
            } else {

            }

            HttpURLConnection conn = (HttpURLConnection) new URL(request.getUrl()).openConnection();
            //默认GET请求，所以可略
            conn.setRequestMethod("GET");
            //默认可读服务器读结果流，所以可略
            conn.setDoInput(true);
            conn.setUseCaches(cacheable);
            conn.setConnectTimeout(CONNECT_TIMEOUT);

            //设置标头
            if (request.getHeaders() != null) {
                Set<String> set = request.getHeaders().keySet();
                for (String name : set) {
                    conn.setRequestProperty(name, request.getHeaders().get(name));
                }
            }

            long currentTime = System.currentTimeMillis();
            String cacheControl = conn.getHeaderField("Cache-Control");
            long expires = conn.getHeaderFieldDate("Expires", currentTime);
            String lastModified = conn.getHeaderField("Last-Modified");

            CLog.Companion.i(TAG, "Cache-Control: " + cacheControl);
            CLog.Companion.i(TAG, "Expires: " + expires);
            CLog.Companion.i(TAG, "Last Modified: " + lastModified);

            conn.connect();

            code = conn.getResponseCode();
            msg = conn.getResponseMessage();
            StreamUtils su = new StreamUtils();
            InputStream is = conn.getInputStream();
            if (is != null) {
                response = su.getString(is);
                is.close();
            }

            is = conn.getErrorStream();
            if (is != null) {
                error = su.getString(is);
                is.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            e = ex;
        }

        if (e == null && TextUtils.isEmpty(error))
            listener.connectSuccess(new HttpResponse.Builder().code(code).codeString(msg).body(response).build());
        else
            listener.connectFailure(new HttpResponse.Builder().code(code).codeString(msg).errorMessage(response).build(), e);

        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null)
            cache.flush();

    }

    public void doPost(HttpRequest request, HttpResponseListener listener) {
        int code = -1;
        String msg = "";
        String response = "";
        String error = "";
        Exception e = null;
        try {
            boolean cacheable = isCacheable(request);
            if (cacheable) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                    Class.forName("android.net.http.HttpResponseCache").getMethod("install", File.class, long.class).invoke(null, MyApplication.INSTANCE.getDiskCacheDir("entity"), MyHttpURLConnection.MAX_CACHE_SIZE);
                else
                    HttpResponseCache.install(MyApplication.INSTANCE.getDiskCacheDir("entity"), MyHttpURLConnection.MAX_CACHE_SIZE);
            }

            HttpURLConnection conn = (HttpURLConnection) new URL(request.getUrl()).openConnection();
            conn.setRequestMethod("POST");
            //默认可读服务器读结果流，所以可略
            conn.setDoInput(true);
            conn.setUseCaches(cacheable);
            conn.setConnectTimeout(CONNECT_TIMEOUT);


            //设置标头
            if (request.getHeaders() != null) {
                Set<String> set = request.getHeaders().keySet();
                for (String name : set) {
                    conn.setRequestProperty(name, request.getHeaders().get(name));
                }
            }

            //获取conn的输出流
            OutputStream os = conn.getOutputStream();
            os.write(request.getBody().getBytes(HTTP.UTF_8));
            os.close();

            long currentTime = System.currentTimeMillis();
            String cacheControl = conn.getHeaderField("Cache-Control");
            long expires = conn.getHeaderFieldDate("Expires", currentTime);
            String lastModified = conn.getHeaderField("Last-Modified");
            CLog.Companion.i(TAG, "Cache-Control: " + cacheControl);
            CLog.Companion.i(TAG, "Expires: " + expires);
            CLog.Companion.i(TAG, "Last Modified: " + lastModified);

            conn.connect();

            code = conn.getResponseCode();
            msg = conn.getResponseMessage();
            StreamUtils su = new StreamUtils();

            InputStream is = conn.getInputStream();
            if (is != null) {
                response = su.getString(is);
                is.close();
            }

            is = conn.getErrorStream();
            if (is != null) {
                error = su.getString(is);
                is.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            e = ex;
        }
        if (e == null && TextUtils.isEmpty(error))
            listener.connectSuccess(new HttpResponse.Builder().code(code).codeString(msg).body(response).build());
        else
            listener.connectFailure(new HttpResponse.Builder().code(code).codeString(msg).errorMessage(response).build(), e);

        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null)
            cache.flush();
    }

    private static boolean isCacheable(HttpRequest request) {
        CacheControl cacheControl = request.getCacheControl();
        if (cacheControl == null)
            return false;
        else {
            if (cacheControl.isNoStore() || cacheControl.isNoCache() || cacheControl.getMaxAgeSeconds() <= 0)
                return false;
            else
                return true;
        }
    }
}
