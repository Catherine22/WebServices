package com.catherine.webservices.network;

import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.toolkits.CLog;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Catherine on 2017/8/23.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MyApache {
    public final static String TAG = "MyApache";

    public Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", Constants.AUTHORIZATION);
        headers.put("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.put("Accept-Language", Locale.getDefault().toString());
        return headers;
    }

    public void doGet(String url) {
        doGet(url, getDefaultHeaders());
    }

    public void doGet(String url, Map<String, String> headers) {
        try {
            CLog.Companion.i(TAG, "doGet");


            HttpGet request = new HttpGet(url);
            //设置标头
            if (headers != null) {
                Set<String> set = headers.keySet();
                Iterator<String> iterator = set.iterator();
                while (iterator.hasNext()) {
                    String name = iterator.next();
                    request.setHeader(name, headers.get(name));
                }
            }
            //连接
            HttpResponse response = MyApplication.INSTANCE.httpClient.execute(request);

            //读取响应
            int code = response.getStatusLine().getStatusCode();
            String phrase = response.getStatusLine().getReasonPhrase();
            CLog.Companion.d(TAG, "code=" + code + ", phrase=" + phrase);
            BufferedReader bf = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), HTTP.UTF_8));
            String line;
            while ((line = bf.readLine()) != null) {
                CLog.Companion.i(TAG, "response:" + line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doPost(String url) {
        doPost(url, getDefaultHeaders());
    }

    public void doPost(String url, Map<String, String> headers) {
    }


}
