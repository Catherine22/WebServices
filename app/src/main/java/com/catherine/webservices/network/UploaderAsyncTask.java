package com.catherine.webservices.network;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.StreamUtils;

import org.apache.http.protocol.HTTP;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * Created by Catherine on 2017/9/20.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class UploaderAsyncTask extends AsyncTask<String, Void, Void> {
    public final static String TAG = "MyHttpURLConnection";
    private final static int CONNECT_TIMEOUT = 10000;
    private boolean responseOnUIThread;
    private UploadRequest request;
    private StreamUtils su;

    //response
    private int code;
    private String response;
    private String msg;
    private String error;
    private Exception e;

    public UploaderAsyncTask(UploadRequest request) {
        this(request, true);
    }

    public UploaderAsyncTask(UploadRequest request, boolean responseOnUIThread) {
        this.request = request;
        su = new StreamUtils();
        this.responseOnUIThread = responseOnUIThread;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String boundary = "*****";
        String twoHyphens = "--";
        String end = "\r\n";
        String fileName = "test1";
        String newFileName = "test2";

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(request.getUrl()).openConnection();
            if (TextUtils.isEmpty(request.getBody())) {
                conn.setRequestMethod("GET");
            } else {
                conn.setRequestMethod("POST");
                //获取conn的输出流
                OutputStream os = conn.getOutputStream();
                os.write(request.getBody().getBytes(HTTP.UTF_8));
                os.close();
            }
            //默认可读服务器读结果流，所以可略
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(CONNECT_TIMEOUT);

            //设置标头
            Map<String, String> headers = (request.getHeaders() != null) ? request.getHeaders() : MyHttpURLConnection.getDefaultHeaders();
            Set<String> set = headers.keySet();
            for (String name : set) {
                conn.setRequestProperty(name, headers.get(name));
            }

            //添加额外预设标头
            conn.setRequestProperty("Content-type", "multipart/form-data; boundary=" + boundary);
            conn.setRequestProperty("Charset", "UTF-8");

            //设置DataOutputStream
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(String.format("%s%s%s", twoHyphens, boundary, end));
            dos.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\" %s", fileName, newFileName, end));
            dos.writeBytes(end);

            File f = request.getFile();
            CLog.Companion.i(TAG,"file:"+f.exists());
            CLog.Companion.i(TAG,"file:"+f.getPath());

            //取得文件的FileInputStream
            FileInputStream fis = new FileInputStream(request.getFile());
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = fis.read(buf)) != -1) {
                dos.write(buf, 0, len);
            }
            dos.writeBytes(end);
            dos.writeBytes(String.format("%s%s%s%s", twoHyphens, boundary, twoHyphens, end));
            fis.close();
            dos.flush();

            code = conn.getResponseCode();
            msg = conn.getResponseMessage();
            InputStream is = conn.getInputStream();
            if (is != null) {
                response = su.getString(is);
                is.close();
            }

            is = conn.getErrorStream();
            if (is != null) {
                error = su.getString(is);
                is.close();
            }

            dos.close();

        } catch (Exception ex) {
            e = ex;
            ex.printStackTrace();
        }

        if (!responseOnUIThread) {
            if (e == null && TextUtils.isEmpty(error))
                request.getListener().connectSuccess(new HttpResponse.Builder().code(code).codeString(msg).body(response).build());
            else
                request.getListener().connectFailure(new HttpResponse.Builder().code(code).codeString(msg).errorMessage(response).build(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (responseOnUIThread) {
            if (e == null && TextUtils.isEmpty(error))
                request.getListener().connectSuccess(new HttpResponse.Builder().code(code).codeString(msg).body(response).build());
            else
                request.getListener().connectFailure(new HttpResponse.Builder().code(code).codeString(msg).errorMessage(error).build(), e);
        }
    }
}
