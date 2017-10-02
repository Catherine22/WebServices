package com.catherine.webservices.network;

import android.text.TextUtils;

import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.StreamUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.io.InputStream;
import java.util.HashMap;
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
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.put("Accept-Language", Locale.getDefault().toString());
        headers.put("User-Agent", System.getProperty("http.agent"));
        headers.put("Accept-Encoding", "gzip, deflate, br");
        return headers;
    }

    public void doGet(String url) {
        doGet(url, getDefaultHeaders());
    }

    public void doPost(String url, List<NameValuePair> nameValuePairs) {
        doPost(url, getDefaultHeaders(), nameValuePairs);
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
                for (String name : set) {
                    request.setHeader(name, headers.get(name));
                }
            }
            //连接
            HttpResponse r = MyApplication.INSTANCE.httpClient.execute(request);

            //读取响应
            code = r.getStatusLine().getStatusCode();
            msg = r.getStatusLine().getReasonPhrase();

            String contentEncoding = "";
            CLog.Companion.i(TAG, "------Header------");
            for (Header header : r.getAllHeaders()) {
                StringBuilder sb = new StringBuilder();
                sb.append(header.getName());
                sb.append(": ");
                sb.append(header.getValue());
                sb.append(",");
                CLog.Companion.i(TAG, sb.toString());

                if ("Content-Encoding".equals(header.getName())) {
                    contentEncoding = header.getValue();

                }
            }
            CLog.Companion.i(TAG, "------Header------");

            InputStream is = r.getEntity().getContent();
            if (is != null) {
                if (contentEncoding.toUpperCase().contains("GZIP"))
                    response = StreamUtils.decompressGZIP(is);
                else
                    response = StreamUtils.getString(is);
                is.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            e = ex;
        }
        if (e == null && TextUtils.isEmpty(error))
            listener.connectSuccess(new com.catherine.webservices.network.HttpResponse.Builder().code(code).codeString(msg).body(response).build());
        else
            listener.connectFailure(new com.catherine.webservices.network.HttpResponse.Builder().code(code).codeString(msg).errorMessage(error).build(), e);
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


            String contentEncoding = "";
            CLog.Companion.i(TAG, "------Header------");
            for (Header header : r.getAllHeaders()) {
                StringBuilder sb = new StringBuilder();
                sb.append(header.getName());
                sb.append(": ");
                sb.append(header.getValue());
                sb.append(",");
                CLog.Companion.i(TAG, sb.toString());

                if ("Content-Encoding".equals(header.getName())) {
                    contentEncoding = header.getValue();

                }
            }
            CLog.Companion.i(TAG, "------Header------");

            InputStream is = r.getEntity().getContent();
            if (is != null) {
                if (contentEncoding.toUpperCase().contains("GZIP"))
                    response = StreamUtils.decompressGZIP(is);
                else
                    response = StreamUtils.getString(is);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            e = ex;
        }
        if (e == null && TextUtils.isEmpty(error))
            listener.connectSuccess(new com.catherine.webservices.network.HttpResponse.Builder().code(code).codeString(msg).body(response).build());
        else
            listener.connectFailure(new com.catherine.webservices.network.HttpResponse.Builder().code(code).codeString(msg).errorMessage(error).build(), e);
    }
}
