package com.catherine.webservices.network;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.catherine.webservices.toolkits.StreamUtils;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
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
    public final static String TAG = "UploaderAsyncTask";
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
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(request.getUrl()).openConnection();
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
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            conn.setRequestProperty("Charset", "UTF-8");


            if (request.getBody() == null)
                conn.setRequestMethod("GET");
            else
                conn.setRequestMethod("POST");


            String file = request.getFile().getName();
//            int start = file.lastIndexOf(".");
//            String fileName = file.substring(0, start);
            String fileName = file;

            //设置DataOutputStream
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(String.format("%s%s%s", twoHyphens, boundary, crlf));
            dos.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\" %s", file, fileName, crlf));
            dos.writeBytes(crlf);

            //取得文件的FileInputStream
            FileInputStream fis = new FileInputStream(request.getFile());
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = fis.read(buf)) != -1) {
                dos.write(buf, 0, len);
            }
            dos.writeBytes(crlf);
            fis.close();

            if (request.getBody() != null) {
                //加入POST方法夹带的本文
                for (String key : request.getBody().keySet()) {
                    String value = request.getBody().get(key);

                    dos.writeBytes(twoHyphens + boundary + crlf);
                    dos.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"%s", key, crlf));
                    dos.writeBytes(String.format("Content-Type: text/plain%s", crlf));
                    dos.writeBytes(crlf);
                    dos.writeBytes(value);
                    dos.writeBytes(crlf);
                }
            }

            dos.writeBytes(String.format("%s%s%s%s", twoHyphens, boundary, twoHyphens, crlf));
            dos.flush();
            dos.close();

            code = conn.getResponseCode();
            msg = conn.getResponseMessage();

            if (code == 200) {
                InputStream is = conn.getInputStream();
                if (is != null) {
                    response = su.getString(is);
                    is.close();
                }
            } else {
                InputStream is = conn.getErrorStream();
                if (is != null) {
                    error = su.getString(is);
                    is.close();
                }
            }


        } catch (Exception ex) {
            e = ex;
            ex.printStackTrace();
        }

        if (!responseOnUIThread) {
            if (e == null && TextUtils.isEmpty(error))
                request.getListener().connectSuccess(new HttpResponse.Builder().code(code).codeString(msg).body(response).build());
            else
                request.getListener().connectFailure(new HttpResponse.Builder().code(code).codeString(msg).errorMessage(error).build(), e);
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
