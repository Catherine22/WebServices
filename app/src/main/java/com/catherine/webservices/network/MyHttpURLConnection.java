package com.catherine.webservices.network;


import com.catherine.webservices.Constants;

import java.io.IOException;
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

    public static void doGet(String url, Map<String, String> headers) {
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
                Iterator<String> iterator = set.iterator();
                while (iterator.hasNext()) {
                    String name = iterator.next();
                    conn.setRequestProperty(name, headers.get(name));
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
