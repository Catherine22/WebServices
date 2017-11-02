package com.catherine.webservices.network;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebSettings;

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

    public static Map<String, String> getDefaultHeaders() {
        String userAgent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            userAgent = WebSettings.getDefaultUserAgent(MyApplication.INSTANCE);
        } else {
            userAgent = System.getProperty("http.agent");
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.put("Accept-Language", Locale.getDefault().toString());
        headers.put("User-Agent", userAgent);
        headers.put("Accept-Encoding", "gzip");//gzip, deflate, br
        return headers;
    }

    public void doGet(HttpRequest httpRequest) {
        connect(httpRequest, null);
    }

    public void doPost(List<NameValuePair> nameValuePairs, HttpRequest httpRequest) {
        connect(httpRequest, nameValuePairs);
    }

    private void connect(HttpRequest httpRequest, List<NameValuePair> nameValuePairs) {
        int code = -1;
        String msg = "";
        String response = "";
        String error = "";
        Exception e = null;
        try {
            HttpResponse r;
            if (nameValuePairs == null) {
                //GET
                CLog.Companion.i(TAG, "GET");
                HttpGet request = new HttpGet(httpRequest.getUrl());
                //设置标头
                if (httpRequest.getHeaders() != null) {
                    Set<String> set = httpRequest.getHeaders().keySet();
                    for (String name : set) {
                        request.setHeader(name, httpRequest.getHeaders().get(name));
                    }
                }
                //连接
                r = MyApplication.INSTANCE.httpClient.execute(request);
            } else {
                //POST
                CLog.Companion.i(TAG, "POST");
                HttpPost request = new HttpPost(httpRequest.getUrl());

                //设置标头
                if (httpRequest.getHeaders() != null) {
                    Set<String> set = httpRequest.getHeaders().keySet();
                    for (String name : set) {
                        request.setHeader(name, httpRequest.getHeaders().get(name));
                    }
                }

                //打包post方法的参数
                HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs);
                request.setEntity(entity);

                //连接
                r = MyApplication.INSTANCE.httpClient.execute(request);
            }

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
        if (httpRequest.getListener() != null) {
            if (e == null && TextUtils.isEmpty(error))
                httpRequest.getListener().connectSuccess(new com.catherine.webservices.network.HttpResponse.Builder().code(code).codeString(msg).body(response).build());
            else
                httpRequest.getListener().connectFailure(new com.catherine.webservices.network.HttpResponse.Builder().code(code).codeString(msg).errorMessage(error).build(), e);
        }
    }
}
