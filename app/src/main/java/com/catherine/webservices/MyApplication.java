package com.catherine.webservices;

import android.app.Application;
import android.content.res.Configuration;
import android.os.HandlerThread;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
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

/**
 * Created by Catherine on 2017/8/23.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MyApplication extends Application {
    public static MyApplication INSTANCE;
    public HandlerThread calHandlerThread;
    public HttpClient httpClient;

    @Override
    public void onCreate() {
        INSTANCE = this;
        calHandlerThread = new HandlerThread("cal_handler_thread");
        calHandlerThread.start();
        httpClient = getHttpClient();
        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        //退出应用时，关闭HttpClient客户端
        if (httpClient != null && httpClient.getConnectionManager() != null) {
            httpClient.getConnectionManager().shutdown();
        }
    }

    private HttpClient getHttpClient() {
        HttpParams params = new BasicHttpParams();
        //设置协议版本
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        //设置编码
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        //连接超时
        HttpConnectionParams.setConnectionTimeout(params, Constants.TIMEOUT);
        //请求超时
        HttpConnectionParams.setSoTimeout(params, Constants.TIMEOUT);
        //设置异常处理机制
        HttpProtocolParams.setUseExpectContinue(params, true);

        //从连接池中取连接的超时设置
        ConnManagerParams.setTimeout(params, Constants.THREAD_POOL_TIMEOUT);
        //多线程总连接数，整个连接池最大连接数
        ConnManagerParams.setMaxTotalConnections(params, Constants.MAX_CONNECTIONS);
        //多线程最大连接数
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(Constants.MAX_CONNECTIONS));

        //设置http和https两种模式
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        //使用线程安全的连接管理HttpClient
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, registry);
        return new DefaultHttpClient(cm, params);
    }
}
