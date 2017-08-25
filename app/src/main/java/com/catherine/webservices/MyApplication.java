package com.catherine.webservices;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
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
    private int runningActivities;

    @Override
    public void onCreate() {
        INSTANCE = this;
        httpClient = getHttpClient();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                /*
                 * 应用中止时关闭HandlerThread，
                 * 假如service存活，MyApplication不会呼叫onCreate()，
                 * 所以让HandlerThread在确定在foreground执行或是有Activity时创建HandlerThread
                 */
                if (runningActivities == 0) {
                    calHandlerThread = new HandlerThread("cal_handler_thread");
                    calHandlerThread.start();
                }
                runningActivities++;
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                runningActivities--;
                //当应用已无运行画面时释放HandlerThread
                if (runningActivities == 0)
                    stopLooper(calHandlerThread);
            }
        });
        super.onCreate();
    }

    public void stopLooper(HandlerThread handlerThread) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (handlerThread != null && handlerThread.isAlive())
                handlerThread.quitSafely();
            else
                handlerThread.quit();
        }
    }

    /**
     * 内存不足
     */
    @Override
    public void onLowMemory() {
        shutdownHttpClient(httpClient);
        super.onLowMemory();
    }

    /**
     * 只要创建一个HttpClient供整个应用使用，通过ThreadSafeClientConnManager管理
     *
     * @return
     */

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

    /**
     * 关闭HttpClient释放资源
     *
     * @param httpClient
     */
    private void shutdownHttpClient(HttpClient httpClient) {
        if (httpClient != null && httpClient.getConnectionManager() != null)
            httpClient.getConnectionManager().shutdown();
    }
}