package com.catherine.webservices.network;


import com.catherine.webservices.Constants;
import com.catherine.webservices.toolkits.CLog;

import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
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

    public void doGet(String url) {
        doGet(url, getDefaultHeaders());
    }

    public void doGet(String url, Map<String, String> headers) {
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

            conn.connect();

            CLog.Companion.i(TAG, "response code:" + conn.getResponseCode()+" message:" + conn.getResponseMessage());
            if (conn.getInputStream() != null) {
                BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream(), HTTP.UTF_8));
                String line;
                while ((line = bf.readLine()) != null) {
                    CLog.Companion.i(TAG, "response:" + line);
                }
                conn.getInputStream().close();
            }

            if (conn.getErrorStream() != null) {
                BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getErrorStream(), HTTP.UTF_8));
                String error;
                while ((error = bf.readLine()) != null) {
                    CLog.Companion.e(TAG, "error message:" + error);
                }
                conn.getErrorStream().close();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doPost(String url, String body) {
        doPost(url, getDefaultHeaders(), body);
    }

    public void doPost(String url, Map<String, String> headers, String body) {
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
                Iterator<String> iterator = set.iterator();
                while (iterator.hasNext()) {
                    String name = iterator.next();
                    conn.setRequestProperty(name, headers.get(name));
                }
            }

            //获取conn的输出流
            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes(HTTP.UTF_8));
            os.close();

            conn.connect();


            CLog.Companion.i(TAG, "response code:" + conn.getResponseCode()+" message:" + conn.getResponseMessage());
            if (conn.getInputStream() != null) {
                BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream(), HTTP.UTF_8));
                String line;
                while ((line = bf.readLine()) != null) {
                    CLog.Companion.i(TAG, "response:" + line);
                }
                conn.getInputStream().close();
            }

            if (conn.getErrorStream() != null) {
                BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getErrorStream(), HTTP.UTF_8));
                String error;
                while ((error = bf.readLine()) != null) {
                    CLog.Companion.e(TAG, "error message:" + error);
                }
                conn.getErrorStream().close();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
