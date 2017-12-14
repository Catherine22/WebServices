package com.catherine.webservices.network;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.catherine.webservices.MyApplication;
import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.StreamUtils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    private final static int CONNECT_TIMEOUT = 10000;
    private DownloadRequest request;
    private int THREAD_NUM;
    private boolean stop;
    //    private boolean[] downloadCompleted;
    private HandlerThread[] threadPool;

    public DownloaderAsyncTask(DownloadRequest request) {
        this.request = request;
        THREAD_NUM = (request.getTHREAD_NUM() > 0) ? request.getTHREAD_NUM() : 1;
//        downloadCompleted = new boolean[THREAD_NUM];
        threadPool = new HandlerThread[THREAD_NUM];
    }

    @Override
    protected Void doInBackground(String... params) {
        int code = 0;
        int LENGTH;
        String msg = "";
        String error = "";
        Exception e = null;
        Map<String, String> responseHeaders = new HashMap<>();
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(request.getUrl()).openConnection();
            //默认可读服务器读结果流，所以可略
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setRequestProperty("Connection", "keep-Alive");

            //设置标头
            if (request.getHeaders() != null) {
                Set<String> set = request.getHeaders().keySet();
                for (String name : set) {
                    conn.setRequestProperty(name, request.getHeaders().get(name));
                }
            }

            if (TextUtils.isEmpty(request.getBody())) {
                conn.setRequestMethod("GET");
            } else {
                conn.setRequestMethod("POST");
                //获取conn的输出流
                OutputStream os = conn.getOutputStream();
                os.write(request.getBody().getBytes("utf-8"));
                os.close();
            }
            code = conn.getResponseCode();
            msg = conn.getResponseMessage();
            LENGTH = conn.getContentLength();

            for (Map.Entry<String, List<String>> entries : conn.getHeaderFields().entrySet()) {
                StringBuilder values = new StringBuilder();
                for (String value : entries.getValue()) {
                    values.append(value).append(",");
                }
                values.deleteCharAt(values.length() - 1);
                responseHeaders.put(entries.getKey(), values.toString());
            }

            InputStream is = conn.getErrorStream();
            if (is != null) {
                error = StreamUtils.getString(is);
                is.close();
            }

            if (LENGTH == 0) {
                request.getListener().connectFailure(new HttpResponse.Builder().code(code).headers(responseHeaders).codeString(msg).build(), new IOException("Content Length = 0"));
                return null;
            }

                /*
                 * "r"    以只读方式打开。调用结果对象的任何 write 方法都将导致抛出 IOException。
                 * "rw"   打开以便读取和写入。
                 * "rws"  打开以便读取和写入。相对于 "rw"，"rws" 还要求对“文件的内容”或“meta-data”的每个更新都同步写入到基础存储设备。
                 * "rwd"  打开以便读取和写入，相对于 "rw"，"rwd" 还要求对“文件的内容”的每个更新都同步写入到基础存储设备。
                 */
            RandomAccessFile file = new RandomAccessFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getFileName(), "rwd");            // 1.在本地创建一个文件 文件大小要跟服务器文件的大小一致
            file.setLength(LENGTH);
            file.close();
            CLog.i(TAG, "LENGTH:" + LENGTH);

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
            e = ex;
            ex.printStackTrace();
        }

        if (e != null || !TextUtils.isEmpty(error))
            request.getListener().connectFailure(new HttpResponse.Builder().code(code).codeString(msg).headers(responseHeaders).errorMessage(error).build(), e);

        return null;
    }

    public void stop() {
        CLog.i(TAG, "stop");
        stop = true;
    }

    class MyRunnable implements Runnable {
        private int threadId;
        private int startPos;
        private int endPos;
        private int LENGTH;


        MyRunnable(int threadId, int startPos, int endPos, int LENGTH) {
            this.threadId = threadId;
            this.startPos = startPos;
            this.endPos = endPos;
            this.LENGTH = LENGTH;
        }

        @Override
        public void run() {
            int code = 0;
            String msg = "";
            String error = "";
            Exception e = null;
            Map<String, String> responseHeaders = new HashMap<>();
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(request.getUrl()).openConnection();
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setConnectTimeout(CONNECT_TIMEOUT);


                //设置标头
                if (request.getHeaders() != null) {
                    Set<String> set = request.getHeaders().keySet();
                    for (String name : set) {
                        conn.setRequestProperty(name, request.getHeaders().get(name));
                    }
                }
                if (TextUtils.isEmpty(request.getBody())) {
                    conn.setRequestMethod("GET");
                } else {
                    conn.setRequestMethod("POST");
                    //获取conn的输出流
                    OutputStream os = conn.getOutputStream();
                    os.write(request.getBody().getBytes("utf-8"));
                    os.close();
                }

                //用一份文件记录下载进度
                File positionFile = new File(MyApplication.INSTANCE.getDiskCacheDir() + "/" + getFileName() + threadId + ".dat");
                if (positionFile.exists()) {
                    FileInputStream fis = new FileInputStream(positionFile);
                    byte[] result = StreamUtils.getBytes(fis);

                    String str = new String(result);
                    if (!"".equals(str)) {
                        // byte[]转int，先转string,才能转int
                        int newStartPosition = Integer.parseInt(str);

                        if (newStartPosition > startPos) {
                            //当应用重启或页面重新加载时回调上次下载进度
                            request.getListener().update(threadId, newStartPosition - startPos, LENGTH);

                            startPos = newStartPosition;

                        }
                    }
                }
                CLog.v(TAG, String.format(Locale.ENGLISH, "线程%d正在下载，开始位置%d～结束位置%d", threadId, startPos, endPos));

                //设置请求内容字节范围
                conn.setRequestProperty("Range", String.format(Locale.ENGLISH, "bytes=%d-%d", startPos, endPos));

                code = conn.getResponseCode();
                msg = conn.getResponseMessage();

                for (Map.Entry<String, List<String>> entries : conn.getHeaderFields().entrySet()) {
                    StringBuilder values = new StringBuilder();
                    for (String value : entries.getValue()) {
                        values.append(value).append(",");
                    }
                    values.deleteCharAt(values.length() - 1);
                    responseHeaders.put(entries.getKey(), values.toString());
                }

                InputStream is = conn.getErrorStream();
                if (is != null) {
                    error = StreamUtils.getString(is);
                    is.close();
                }

                //设置数据从那个位置开始写
                RandomAccessFile file = new RandomAccessFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getFileName(), "rwd");
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
                CLog.w(TAG, "currentPos:" + currentPos);

                // 需要把currentPosition信息持久化到存储设备
                String position = currentPos + "";

                FileOutputStream fos = new FileOutputStream(positionFile);
                fos.write(position.getBytes());
                fos.flush();
                fos.close();

                if (currentPos == LENGTH || currentPos == endPos + 1) {
                    CLog.d(TAG, "threadId_" + threadId + " finished");
//                    downloadCompleted[threadId] = true;
                    positionFile.delete();
                    file.close();
                }
            } catch (Exception ex) {
                e = ex;
                ex.printStackTrace();
            }
            if (e != null || !TextUtils.isEmpty(error))
                request.getListener().connectFailure(new HttpResponse.Builder().code(code).codeString(msg).headers(responseHeaders).errorMessage(error).build(), e);

        }
    }

    private String fileName;

    public String getFileName() {
        if (TextUtils.isEmpty(fileName)) {
            int start = request.getUrl().lastIndexOf("/") + 1;
            fileName = request.getUrl().substring(start);

            //Sometimes, url is a redirect address. E.g. https://app.appsflyer.com/com.mygame.softworld-freemycard?pid=freemycard_int&android_id={ANDROID_ID}&af_r=http://mycard.myserver.akamaized.net/freemycard/crazygame.apk&clickid={Recieve_Seq}
            //If you get the file name directly then you'd get "crazygame.apk&clickid={Recieve_Seq}".
            //So that url has to be checked mime type
            int mimeTypeStart = fileName.indexOf(".", 0) + 1;
            if (mimeTypeStart == 0) {
                fileName += ".dat";
            } else {
                String mimeType = fileName.substring(mimeTypeStart);
                String newMimeType = mimeType;
                //The longest mime type has 5 characters and the shortest mime type has 2 characters
                int len = (mimeType.length() >= 5) ? 5 : mimeType.length();
                while (len > 2) {
                    if (MimeTypeList.MIME_TYPE_LIST.containsKey(mimeType.substring(0, len).toUpperCase())) {
                        newMimeType = mimeType.substring(0, len);
                        break;
                    } else
                        newMimeType = null;
                    len--;
                }
                if (TextUtils.isEmpty(newMimeType))
                    newMimeType = "dat";
                fileName = fileName.substring(0, mimeTypeStart) + newMimeType;
            }
        }
        return fileName;
    }

    public File getFile() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getFileName());
    }
}
