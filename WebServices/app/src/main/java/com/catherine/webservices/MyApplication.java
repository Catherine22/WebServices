package com.catherine.webservices;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.FileUtils;
import com.facebook.cache.common.CacheErrorLogger;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import catherine.messagecenter.Client;

/**
 * Created by Catherine on 2017/8/23.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MyApplication extends Application {
    private final static String TAG = "MyApplication";
    public static MyApplication INSTANCE;
    public HandlerThread calHandlerThread, socketHandlerThread;
    public HttpClient httpClient;
    private List<String> runningActivities;

    @Override
    public void onCreate() {
        INSTANCE = this;
        httpClient = getHttpClient();
        runningActivities = new ArrayList<>();

        //Invoke the largest storage to save data.
        Map<String, File> externalLocations = FileUtils.getAllStorageLocations();
        File sdCard = externalLocations.get(FileUtils.SD_CARD);
        File externalSdCard = externalLocations.get(FileUtils.EXTERNAL_SD_CARD);
        long sdCardSize = 0;
        long extSdCardSize = 0;
        if (sdCard != null) {
            sdCardSize = sdCard.getFreeSpace();
            CLog.Companion.i(TAG, "free storage: (sd card) " + sdCard.getAbsolutePath());
            CLog.Companion.i(TAG, "free storage: (sd card) " + sdCard.getFreeSpace() / ByteConstants.MB + " MB");
        }
        if (externalSdCard != null) {
            extSdCardSize = externalSdCard.getFreeSpace();
            CLog.Companion.i(TAG, "free storage: (external sd card) " + externalSdCard.getAbsolutePath());
            CLog.Companion.i(TAG, "free storage: (external sd card) " + externalSdCard.getFreeSpace() / ByteConstants.MB + " MB");
        }

        try {
            if (sdCardSize > extSdCardSize) {
                Constants.ROOT_PATH = sdCard.getAbsolutePath() + "/WebServices/";
                CLog.Companion.i(TAG, "Your data will save in " + Constants.ROOT_PATH);
            } else {
                Constants.ROOT_PATH = externalSdCard.getAbsolutePath() + "/WebServices/";
                CLog.Companion.i(TAG, "Your data will save in " + Constants.ROOT_PATH);
            }
        } catch (NullPointerException e) {
            Constants.ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WebServices/";
            CLog.Companion.i(TAG, "Your data will save in " + Constants.ROOT_PATH);
        }

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                /*
                 * 应用中止时关闭HandlerThread，
                 * 假如service存活，MyApplication不会呼叫onCreate()，
                 * 所以让HandlerThread在确定在foreground执行或是有Activity时创建HandlerThread
                 */
                if (runningActivities.size() == 0) {
                    calHandlerThread = new HandlerThread("cal_handler_thread");
                    calHandlerThread.start();
                    socketHandlerThread = new HandlerThread("socket_handler_thread");
                    socketHandlerThread.start();
                }
                runningActivities.add(activity.getLocalClassName());
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
                //startActivity()后才能执行finish()，否则会计算错误
                runningActivities.remove(activity.getLocalClassName());
                //当应用已无运行画面时释放HandlerThread
                if (runningActivities.size() == 0) {
                    stopLooper(calHandlerThread);
                    stopLooper(socketHandlerThread);
                }
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
     * 获取权限后重新初始化
     */
    public void init() {
        Handler h = new Handler(calHandlerThread.getLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                File rootDir = new File(Constants.ROOT_PATH);
                if (!rootDir.exists())
                    rootDir.mkdirs();
                FileUtils.copyAssets();


                //fresco
                //check free memory (MB)
                File externalStorageDir = Environment.getExternalStorageDirectory();
                long temp = externalStorageDir.getFreeSpace();
                long free = (temp > 10 * ByteConstants.MB) ? 20 * ByteConstants.MB : temp;
                CLog.Companion.i(TAG, "free memory = " + temp / ByteConstants.MB + " MB, use " + free / (2 * ByteConstants.MB) + " MB to cache images.");
                DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(MyApplication.this)
                        .setCacheErrorLogger(new CacheErrorLogger() {
                            @Override
                            public void logError(CacheErrorCategory category, Class<?> clazz, String message, Throwable throwable) {
                                CLog.Companion.e("Fresco CacheError", category + ":" + message);
                            }
                        })
                        .setVersion(1)
                        .setMaxCacheSize(free / 2)
                        .setBaseDirectoryName(Constants.FRESCO_DIR)
                        .setBaseDirectoryPath(rootDir)
                        .build();
                ImagePipelineConfig config = ImagePipelineConfig.newBuilder(MyApplication.this)
                        .setResizeAndRotateEnabledForNetwork(true)
                        .setMainDiskCacheConfig(diskCacheConfig)
                        .build();
                Fresco.initialize(INSTANCE, config);
            }
        });
    }

    public File getDiskCacheDir() throws NullPointerException {
        return getDiskCacheDir(null);
    }

    public File getDiskCacheDir(String dirName) throws NullPointerException {
        String cachePath = (FileUtils.isExternalStorageWritable()) ? Constants.CACHE_PATH : getCacheDir().getAbsolutePath();
        File dir = new File(cachePath);
        boolean b = true;
        if (!dir.exists())
            b = dir.mkdirs();

        if (!TextUtils.isEmpty(dirName)) {
            dir = new File(cachePath + dirName + "/");
            if (!dir.exists())
                b = dir.mkdirs();
        }

        if (!b)
            throw new NullPointerException(String.format("Failed to access external storage, isExternalStorageWritable:%s", FileUtils.isExternalStorageWritable()));
        else
            return dir;
    }

    public File getDataCacheDir() throws NullPointerException {
        return getDataCacheDir(null);
    }

    public File getDataCacheDir(String dirName) throws NullPointerException {
        String dataPath = (FileUtils.isExternalStorageWritable()) ? Constants.EXT_PATH : getCacheDir().getAbsolutePath();
        File dir = new File(dataPath);
        boolean b = true;
        if (!dir.exists())
            b = dir.mkdirs();

        if (!TextUtils.isEmpty(dirName)) {
            dir = new File(dataPath + dirName + "/");
            if (!dir.exists())
                b = dir.mkdirs();
        }

        if (!b)
            throw new NullPointerException(String.format("Failed to access external storage, isExternalStorageWritable:%s", FileUtils.isExternalStorageWritable()));
        else
            return dir;
    }

    public boolean isActivityAvaliable(Activity activity) {
        return runningActivities.contains(activity.getLocalClassName());
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
