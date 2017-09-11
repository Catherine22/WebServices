package com.catherine.webservices.network;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created by Catherine on 2017/8/28.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class HttpAsyncTask extends AsyncTask<String, Void, Void> {
    private String url;
    private Map<String, String> headers;
    private String body;
    private HttpResponseListener listenerOnUIThread;
    private MyHttpURLConnection conn;

    private boolean responseOnUIThread = true;
    private boolean connectSuccess;
    private int code;
    private String message;
    private String errorStream;
    private Exception e;

    public HttpAsyncTask(String url, HttpResponseListener listener) {
        this(url, MyHttpURLConnection.getDefaultHeaders(), "", listener, true);
    }

    public HttpAsyncTask(String url, Map<String, String> headers, HttpResponseListener listener) {
        this(url, headers, "", listener, true);
    }

    public HttpAsyncTask(String url, String body, HttpResponseListener listener) {
        this(url, MyHttpURLConnection.getDefaultHeaders(), body, listener, true);
    }

    public HttpAsyncTask(String url, Map<String, String> headers, String body, HttpResponseListener listener) {
        this(url, headers, body, listener, true);
    }

    public HttpAsyncTask(String url, Map<String, String> headers, String body, HttpResponseListener listener, boolean responseOnUIThread) {
        this.url = url;
        this.headers = headers;
        this.body = body;
        this.responseOnUIThread = responseOnUIThread;
        this.listenerOnUIThread = listener;
        conn = new MyHttpURLConnection();
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpResponseListener listener;
        if (responseOnUIThread) {
            listener = new HttpResponseListener() {
                @Override
                public void connectSuccess(int code, @NotNull String message, @NotNull String body) {
                    connectSuccess = true;
                    HttpAsyncTask.this.code = code;
                    HttpAsyncTask.this.message = message;
                    HttpAsyncTask.this.body = body;
                }

                @Override
                public void connectFailure(int code, @NotNull String message, @NotNull String errorStream, @Nullable Exception e) {
                    connectSuccess = false;
                    HttpAsyncTask.this.code = code;
                    HttpAsyncTask.this.message = message;
                    HttpAsyncTask.this.errorStream = errorStream;
                    HttpAsyncTask.this.e = e;
                }
            };
        } else
            listener = listenerOnUIThread;

        if (TextUtils.isEmpty(body))
            conn.doGet(url, headers, listener);
        else
            conn.doPost(url, headers, body, listener);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (responseOnUIThread) {
            if (connectSuccess)
                listenerOnUIThread.connectSuccess(code, message, body);
            else
                listenerOnUIThread.connectFailure(code, message, errorStream, e);
        }
    }
}
