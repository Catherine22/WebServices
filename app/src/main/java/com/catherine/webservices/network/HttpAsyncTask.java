package com.catherine.webservices.network;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Created by Catherine on 2017/8/28.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class HttpAsyncTask extends AsyncTask<String, Void, Void> {
    private MyHttpURLConnection conn;
    private HttpRequest request;
    private boolean responseOnUIThread;

    //response
    private boolean connectSuccess;
    private int code;
    private String body;
    private String message;
    private String errorStream;
    private Exception e;

    public HttpAsyncTask(HttpRequest request) {
        this(request, true);
    }

    public HttpAsyncTask(HttpRequest request, boolean responseOnUIThread) {
        this.request = request;
        this.responseOnUIThread = responseOnUIThread;
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
            listener = request.getListener();

        if (TextUtils.isEmpty(request.getBody()))
            conn.doGet(request.getUrl(), request.getHeaders(), listener);
        else
            conn.doPost(request.getUrl(), request.getHeaders(), request.getBody(), listener);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (responseOnUIThread) {
            if (connectSuccess)
                request.getListener().connectSuccess(code, message, body);
            else
                request.getListener().connectFailure(code, message, errorStream, e);
        }
    }
}
