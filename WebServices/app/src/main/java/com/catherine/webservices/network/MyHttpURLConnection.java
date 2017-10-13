package com.catherine.webservices.network;


import android.net.http.HttpResponseCache;
import android.os.Build;
import android.text.TextUtils;

import com.catherine.webservices.MyApplication;
import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.StreamUtils;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

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
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.put("Accept-Language", Locale.getDefault().toString());
        headers.put("User-Agent", System.getProperty("http.agent"));
        headers.put("Accept-Encoding", "gzip");//gzip, deflate, br
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
        connect(request, listener, false);
    }

    public void doPost(HttpRequest request, HttpResponseListener listener) {
        connect(request, listener, true);
    }

    private void connect(HttpRequest request, HttpResponseListener listener, boolean isPost) {
        int code = -1;
        String msg = "";
        String response = "";
        String error = "";
        Exception e = null;
        Map<String, String> responseHeaders = new HashMap<>();
        try {
            boolean cacheable = isCacheable(request);
            if (cacheable) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                    Class.forName("android.net.http.HttpResponseCache").getMethod("install", File.class, long.class).invoke(null, MyApplication.INSTANCE.getDiskCacheDir("entity"), MyHttpURLConnection.MAX_CACHE_SIZE);
                else
                    HttpResponseCache.install(MyApplication.INSTANCE.getDiskCacheDir("entity"), MyHttpURLConnection.MAX_CACHE_SIZE);
            } else {

            }

            HttpURLConnection conn;
            //处理 "https://"
            //设置证书 - 信任指定的CA证书
            if (request.getCertificate() != null && request.getUrl().contains("https://")) {
                //生成包含当前CA证书的keystore
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);
                keyStore.setCertificateEntry("user_define_ca", request.getCertificate());

                //使用包含指定CA证书的keystore生成TrustManager[]数组
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);
                TrustManager[] trustManagers = tmf.getTrustManagers();

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustManagers, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                conn = (HttpsURLConnection) new URL(request.getUrl()).openConnection();
            } else
                conn = (HttpURLConnection) new URL(request.getUrl()).openConnection();

            if (isPost) {
                conn.setRequestMethod("POST");
            } else {
                //默认GET请求，所以可略
                conn.setRequestMethod("GET");
            }
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

            if (isPost) {
                //获取conn的输出流
                OutputStream os = conn.getOutputStream();
                os.write(request.getBody().getBytes(HTTP.UTF_8));
                os.close();
            }

            conn.connect();
            code = conn.getResponseCode();
            msg = conn.getResponseMessage();

            String contentEncoding = "";
            CLog.Companion.i(TAG, "------Header------");
            for (Map.Entry<String, List<String>> entries : conn.getHeaderFields().entrySet()) {
                StringBuilder values = new StringBuilder();
                for (String value : entries.getValue()) {
                    values.append(value).append(",");
                }

                if ("Content-Encoding".equals(entries.getKey())) {
                    contentEncoding = values.toString();
                }

                values.deleteCharAt(values.length() - 1);
                responseHeaders.put(entries.getKey(), values.toString());
                CLog.Companion.i(TAG, entries.getKey() + ": " + values);
            }
            CLog.Companion.i(TAG, "------Header------");

            InputStream is = conn.getInputStream();
            if (is != null) {
                if (contentEncoding.toUpperCase().contains("GZIP"))
                    response = StreamUtils.decompressGZIP(is);
                else
                    response = StreamUtils.getString(is);
                is.close();
            }

            is = conn.getErrorStream();
            if (is != null) {
                error = StreamUtils.getString(is);
                is.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            e = ex;
        }

        if (e == null && TextUtils.isEmpty(error))
            listener.connectSuccess(new HttpResponse.Builder()
                    .code(code)
                    .codeString(msg)
                    .headers(responseHeaders)
                    .body(response)
                    .build());
        else
            listener.connectFailure(new HttpResponse.Builder()
                    .code(code)
                    .codeString(msg)
                    .headers(responseHeaders)
                    .errorMessage(response)
                    .build(), e);

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
