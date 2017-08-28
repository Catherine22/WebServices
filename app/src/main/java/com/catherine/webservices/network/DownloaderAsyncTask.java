package com.catherine.webservices.network;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.catherine.webservices.Constants;
import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Catherine on 2017/8/28.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class DownloaderAsyncTask extends AsyncTask<String, Void, Void> {
    private final static String TAG = "HttpAsyncTask";
    private final static int THREAD_NUM = 3;
    private String url;
    private Map<String, String> headers;
    private String body;
    private DownloaderListener listener;
    private int total;

    public DownloaderAsyncTask(String url, DownloaderListener listener) {
        this(url, MyHttpURLConnection.getDefaultHeaders(), "", listener);
    }

    public DownloaderAsyncTask(String url, Map<String, String> headers, DownloaderListener listener) {
        this(url, headers, "", listener);
    }

    public DownloaderAsyncTask(String url, String body, DownloaderListener listener) {
        this(url, MyHttpURLConnection.getDefaultHeaders(), body, listener);
    }

    public DownloaderAsyncTask(String url, Map<String, String> headers, String body, DownloaderListener listener) {
        this.url = url;
        this.headers = headers;
        this.body = body;
        this.listener = listener;

        File cacheDir = new File(Constants.CACHE_PATH);
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    @Override
    protected Void doInBackground(String... params) {
        int code = 0;
        int LENGTH;
        String msg = "";
        Exception e = null;
        if (TextUtils.isEmpty(body)) {
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

//            CLog.Companion.i(TAG, "url: " + url);
//            CLog.Companion.i(TAG, "Content Encoding: " + conn.getContentEncoding());
//            CLog.Companion.i(TAG, "Content Length: " + conn.getContentLength());
//            CLog.Companion.i(TAG, "Content Type: " + conn.getContentType());
//            CLog.Companion.i(TAG, "Date: " + conn.getDate());
//            CLog.Companion.i(TAG, "Expiration: " + conn.getExpiration());
//            CLog.Companion.i(TAG, "Last Modified: " + conn.getLastModified());

                code = conn.getResponseCode();
                msg = conn.getResponseMessage();
                LENGTH = conn.getContentLength();
                StreamUtils su = new StreamUtils();

                if (LENGTH == 0) {
                    listener.connectFailure(code, msg, new IOException("Content Length = 0"));
                    return null;
                }

                int start = url.lastIndexOf("/") + 1;
                String fileName = url.substring(start, url.length());

                /*
                 * "r"    以只读方式打开。调用结果对象的任何 write 方法都将导致抛出 IOException。
                 * "rw"   打开以便读取和写入。
                 * "rws"  打开以便读取和写入。相对于 "rw"，"rws" 还要求对“文件的内容”或“meta-data”的每个更新都同步写入到基础存储设备。
                 * "rwd"  打开以便读取和写入，相对于 "rw"，"rwd" 还要求对“文件的内容”的每个更新都同步写入到基础存储设备。
                 */
                RandomAccessFile file = new RandomAccessFile(Constants.CACHE_PATH + fileName, "rws");

                // 1.在本地创建一个文件 文件大小要跟服务器文件的大小一致
                file.setLength(LENGTH);

                /*
                 * 2. 多线程下载，分配每个线程下载如下
                 * 线程1: 0~blockSize <br>
                 * 线程2: 1*blockSize~2*blockSize <br>
                 * 线程3: 2*blockSize~3*blockSize <br>
                 * ...
                 * 线程n: (n-1)*blockSize~len <br>
                 */
                int blockSize = LENGTH / THREAD_NUM;
                for (int i = 0; i < blockSize; i++) {
                    int startPos = i * blockSize;
                    int endPos = (i + 1) * blockSize;

                    //注意最后一个线程的结束位置为文件长度
                    if (i == (THREAD_NUM - 1))
                        endPos = LENGTH;

                    //用一份文件记录下载进度
                    File positionFile = new File(Constants.CACHE_PATH + fileName + i + ".dat");
                    if (positionFile.exists()) {
                        FileInputStream fis = new FileInputStream(positionFile);
                        byte[] result = su.getBytes(fis);

                        String str = new String(result);
                        if (!"".equals(str)) {
                            // byte[]转int，先转string,才能转int
                            int newStartPosition = Integer.parseInt(str);

                            if (newStartPosition > startPos) {
                                startPos = newStartPosition;
                            }
                        }
                    }
                    CLog.Companion.v(TAG, String.format(Locale.ENGLISH, "线程%d正在下载，开始位置%d～结束位置%d", i, startPos, endPos));

                    //设置请求内容字节范围
                    conn.setRequestProperty("Range", String.format(Locale.ENGLISH, "bytes=%d-%d", startPos, endPos));
                    InputStream is = conn.getInputStream();

                    //设置数据从那个位置开始写
                    file.seek(startPos);
                    byte[] buffer = new byte[1024];
                    // 文件长度，当length = -1代表文件读完了
                    int len;
                    //当前进度
                    int currentPos = startPos;
                    while ((len = is.read(buffer)) != -1) {
                        file.write(buffer, 0, len);
                    }

                    // 同步内容
                    synchronized (DownloaderAsyncTask.this) {
                        total += len;
                        listener.update(total, LENGTH);
                    }

                    // 需要把currentPosition信息持久化到存储设备
                    currentPos += len;
                    String position = currentPos + "";
                    FileOutputStream fos = new FileOutputStream(positionFile);
                    fos.write(position.getBytes());
                    fos.flush();
                    fos.close();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                e = ex;
            }

        }
        if (e != null) {
            listener.connectFailure(code, msg, null);
        }
        return null;
    }
}
