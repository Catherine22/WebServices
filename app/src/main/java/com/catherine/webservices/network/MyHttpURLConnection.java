package com.catherine.webservices.network;


import android.text.TextUtils;

import com.catherine.webservices.Constants;
import com.catherine.webservices.toolkits.StreamUtils;

import org.apache.http.protocol.HTTP;

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

    public static Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", Constants.AUTHORIZATION);
        headers.put("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.put("Accept-Language", Locale.getDefault().toString());
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

    public void doGet(String url, HttpResponseListener listener) {
        doGet(url, getDefaultHeaders(), listener);
    }

    public void doGet(String url, Map<String, String> headers, HttpResponseListener listener) {
        int code = -1;
        String msg = "";
        String response = "";
        String error = "";
        Exception e = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            //默认GET请求，所以可略
            conn.setRequestMethod("GET");
            //默认可读服务器读结果流，所以可略
            conn.setDoInput(true);
            //禁用网络缓存
            conn.setUseCaches(false);


            //设置标头
            if (headers != null) {
                Set<String> set = headers.keySet();
                for (String name : set) {
                    conn.setRequestProperty(name, headers.get(name));
                }
            }

//            CLog.Companion.i(TAG, "url: " + url);
//            CLog.Companion.i(TAG, "Content Encoding: " + conn.getContentEncoding());
//            CLog.Companion.i(TAG, "Content Length: " + conn.getContentLength());
//            CLog.Companion.i(TAG, "Content Type: " + conn.getContentType());
//            CLog.Companion.i(TAG, "Date: " + conn.getDate());
//            CLog.Companion.i(TAG, "Expiration: " + conn.getExpiration());
//            CLog.Companion.i(TAG, "Last Modified: " + conn.getLastModified());

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
            listener.connectSuccess(code, msg, response);
        else
            listener.connectFailure(code, msg, error, e);
    }

    public void doPost(String url, String body, HttpResponseListener listener) {
        doPost(url, getDefaultHeaders(), body, listener);
    }

    public void doPost(String url, Map<String, String> headers, String body, HttpResponseListener listener) {
        int code = -1;
        String msg = "";
        String response = "";
        String error = "";
        Exception e = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            //默认可读服务器读结果流，所以可略
            conn.setDoInput(true);
            //禁用网络缓存
            conn.setUseCaches(false);


            //设置标头
            if (headers != null) {
                Set<String> set = headers.keySet();
                for (String name : set) {
                    conn.setRequestProperty(name, headers.get(name));
                }
            }

            //获取conn的输出流
            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes(HTTP.UTF_8));
            os.close();

//            CLog.Companion.i(TAG, "url: " + url);
//            CLog.Companion.i(TAG, "Content Encoding: " + conn.getContentEncoding());
//            CLog.Companion.i(TAG, "Content Length: " + conn.getContentLength());
//            CLog.Companion.i(TAG, "Content Type: " + conn.getContentType());
//            CLog.Companion.i(TAG, "Date: " + conn.getDate());
//            CLog.Companion.i(TAG, "Expiration: " + conn.getExpiration());
//            CLog.Companion.i(TAG, "Last Modified: " + conn.getLastModified());

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
            listener.connectSuccess(code, msg, response);
        else
            listener.connectFailure(code, msg, error, e);
    }
}
