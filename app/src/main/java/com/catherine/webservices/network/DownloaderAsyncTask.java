package com.catherine.webservices.network;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.catherine.webservices.MyApplication;
import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Catherine on 2017/8/28.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

/**
 * Response on the non-UI thread
 */
public class DownloaderAsyncTask extends AsyncTask<String, Void, Void> {
    private final static String TAG = "DownloaderAsyncTask";
    private DownloadRequest request;
    private int THREAD_NUM;
    private boolean stop;
    private StreamUtils su = new StreamUtils();
    private boolean[] downloadCompleted;
    private HandlerThread[] threadPool;

    public DownloaderAsyncTask(DownloadRequest request) {
        this.request = request;
        THREAD_NUM = (request.getTHREAD_NUM() > 0) ? request.getTHREAD_NUM() : 1;
        downloadCompleted = new boolean[THREAD_NUM];
        threadPool = new HandlerThread[THREAD_NUM];
    }

    @Override
    protected Void doInBackground(String... params) {
        int code = 0;
        int LENGTH;
        String msg = "";
        Exception e = null;
        if (TextUtils.isEmpty(request.getBody())) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(request.getUrl()).openConnection();
                //默认GET请求，所以可略
                conn.setRequestMethod("GET");
                //默认可读服务器读结果流，所以可略
                conn.setDoInput(true);
                conn.setUseCaches(false);

                //设置标头
                if (request.getHeaders() != null) {
                    Set<String> set = request.getHeaders().keySet();
                    for (String name : set) {
                        conn.setRequestProperty(name, request.getHeaders().get(name));
                    }
                }

                code = conn.getResponseCode();
                msg = conn.getResponseMessage();
                LENGTH = conn.getContentLength();

                if (LENGTH == 0) {
                    request.getListener().connectFailure(new HttpResponse.Builder().code(code).codeString(msg).build(), new IOException("Content Length = 0"));
                    return null;
                }

                int start = request.getUrl().lastIndexOf("/") + 1;
                String fileName = request.getUrl().substring(start, request.getUrl().length());

                /*
                 * "r"    以只读方式打开。调用结果对象的任何 write 方法都将导致抛出 IOException。
                 * "rw"   打开以便读取和写入。
                 * "rws"  打开以便读取和写入。相对于 "rw"，"rws" 还要求对“文件的内容”或“meta-data”的每个更新都同步写入到基础存储设备。
                 * "rwd"  打开以便读取和写入，相对于 "rw"，"rwd" 还要求对“文件的内容”的每个更新都同步写入到基础存储设备。
                 */
                RandomAccessFile file = new RandomAccessFile(MyApplication.INSTANCE.getDiskCacheDir() + fileName, "rwd");

                // 1.在本地创建一个文件 文件大小要跟服务器文件的大小一致
                file.setLength(LENGTH);
                file.close();
                CLog.Companion.i(TAG, "LENGTH:" + LENGTH);

                /*
                 * 2. 多线程下载，分配每个线程下载如下
                 * 线程1: 0~blockSize-1 <br>
                 * 线程2: 1*blockSize~2*blockSize-1 <br>
                 * 线程3: 2*blockSize~3*blockSize-1 <br>
                 * ...
                 * 线程n: (n-1)*blockSize~len <br>
                 */
                int blockSize = LENGTH / THREAD_NUM;
                for (int i = 0; i < THREAD_NUM; i++) {
                    int startPos = i * blockSize;
                    int endPos = (i + 1) * blockSize - 1;

                    //注意最后一个线程的结束位置为文件长度
                    if (i == (THREAD_NUM - 1))
                        endPos = LENGTH;

                    threadPool[i] = new HandlerThread("DownloadLooper" + i);
                    threadPool[i].start();
                    MyRunnable runnable = new MyRunnable(i, startPos, endPos, LENGTH);
                    Handler handler = new Handler(threadPool[i].getLooper());
                    handler.post(runnable);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                e = ex;
            }

        }
        if (e != null) {
            request.getListener().connectFailure(new HttpResponse.Builder().code(code).codeString(msg).build(), null);
        }

        return null;
    }

    /**
     * Stop downloading
     */
    public void stop() {
        stop = true;
    }

    public boolean isStop() {
        return stop;
    }

    /**
     * back to download
     */
    public void resume() {
        stop = false;
    }

    class MyRunnable implements Runnable {
        private int threadId;
        private int startPos;
        private int endPos;
        private int LENGTH;
        private String fileName;


        MyRunnable(int threadId, int startPos, int endPos, int LENGTH) {
            this.threadId = threadId;
            this.startPos = startPos;
            this.endPos = endPos;
            this.LENGTH = LENGTH;
            int start = request.getUrl().lastIndexOf("/") + 1;
            fileName = request.getUrl().substring(start, request.getUrl().length());
        }

        @Override
        public void run() {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(request.getUrl()).openConnection();
                //默认GET请求，所以可略
                conn.setRequestMethod("GET");
                //默认可读服务器读结果流，所以可略
                conn.setDoInput(true);
                //设置逾时
                conn.setConnectTimeout(5000);

                //设置标头
                if (request.getHeaders() != null) {
                    Set<String> set = request.getHeaders().keySet();
                    for (String name : set) {
                        conn.setRequestProperty(name, request.getHeaders().get(name));
                    }
                }

                //用一份文件记录下载进度
                File positionFile = new File(MyApplication.INSTANCE.getDiskCacheDir() + fileName + threadId + ".dat");
                if (positionFile.exists()) {
                    FileInputStream fis = new FileInputStream(positionFile);
                    byte[] result = su.getBytes(fis);

                    String str = new String(result);
                    if (!"".equals(str)) {
                        // byte[]转int，先转string,才能转int
                        int newStartPosition = Integer.parseInt(str);

                        if (newStartPosition > startPos) {
                            request.getListener().update(threadId, newStartPosition - startPos, LENGTH);
                            startPos = newStartPosition;

                        }
                    }
                }
                CLog.Companion.v(TAG, String.format(Locale.ENGLISH, "线程%d正在下载，开始位置%d～结束位置%d", threadId, startPos, endPos));

                //设置请求内容字节范围
                conn.setRequestProperty("Range", String.format(Locale.ENGLISH, "bytes=%d-%d", startPos, endPos));

                //设置数据从那个位置开始写
                RandomAccessFile file = new RandomAccessFile(MyApplication.INSTANCE.getDiskCacheDir() + fileName, "rwd");
                file.seek(startPos);
                byte[] buffer = new byte[1024];
                // 文件长度，当length = -1代表文件读完了
                int len;
                //当前进度
                int currentPos = startPos;

                while ((len = conn.getInputStream().read(buffer)) != -1) {
                    if (!stop) {
                        file.write(buffer, 0, len);
                        request.getListener().update(threadId, len, LENGTH);
                        currentPos += len;
                    } else
                        break;
                }
                CLog.Companion.w(TAG, "currentPos:" + currentPos);

                // 需要把currentPosition信息持久化到存储设备
                String position = currentPos + "";

                FileOutputStream fos = new FileOutputStream(positionFile);
                fos.write(position.getBytes());
                fos.flush();
                fos.close();

                if (currentPos == LENGTH || currentPos == endPos + 1) {
                    CLog.Companion.d(TAG, "threadId_" + threadId + " finished");
                    downloadCompleted[threadId] = true;
                    positionFile.delete();
                    file.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
