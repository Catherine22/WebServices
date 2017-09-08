package com.catherine.webservices.network;

import android.text.TextUtils;

import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.StreamUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
    private HttpResponseListener listener;

    public MyApache(HttpResponseListener listener) {
        this.listener = listener;
    }

    public static Map<String, String> getDefaultHeaders() {
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
        int code = -1;
        String msg = "";
        String response = "";
        String error = "";
        Exception e = null;
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
            HttpResponse r = MyApplication.INSTANCE.httpClient.execute(request);

            //读取响应
            code = r.getStatusLine().getStatusCode();
            msg = r.getStatusLine().getReasonPhrase();
            StreamUtils su = new StreamUtils();
            InputStream is = r.getEntity().getContent();
            if (is != null) {
                response = su.getString(is);
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

    public void doPost(String url, List<NameValuePair> nameValuePairs) {
        doPost(url, getDefaultHeaders(), nameValuePairs);
    }

    public void doPost(String url, Map<String, String> headers, List<NameValuePair> nameValuePairs) {
        int code = -1;
        String msg = "";
        String response = "";
        String error = "";
        Exception e = null;
        try {
            CLog.Companion.i(TAG, "doPost");
            HttpPost request = new HttpPost(url);
            //设置标头
            if (headers != null) {
                Set<String> set = headers.keySet();
                for (String name : set) {
                    request.setHeader(name, headers.get(name));
                }
            }

            //打包post方法的参数
            HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs);
            request.setEntity(entity);

            //连接
            HttpResponse r = MyApplication.INSTANCE.httpClient.execute(request);

            //读取响应
            code = r.getStatusLine().getStatusCode();
            msg = r.getStatusLine().getReasonPhrase();
            StreamUtils su = new StreamUtils();
            InputStream is = r.getEntity().getContent();
            if (is != null) {
                response = su.getString(is);
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
