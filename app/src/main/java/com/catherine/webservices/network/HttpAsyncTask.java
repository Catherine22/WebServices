package com.catherine.webservices.network;

import android.os.AsyncTask;
import android.text.TextUtils;


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
                public void connectSuccess(HttpResponse response) {
                    connectSuccess = true;
                    HttpAsyncTask.this.code = response.getCode();
                    HttpAsyncTask.this.message = response.getCodeString();
                    HttpAsyncTask.this.body = response.getBody();
                }

                @Override
                public void connectFailure(HttpResponse response, Exception e) {
                    connectSuccess = false;
                    HttpAsyncTask.this.code = response.getCode();
                    HttpAsyncTask.this.message = response.getCodeString();
                    HttpAsyncTask.this.errorStream = response.getErrorMessage();
                    HttpAsyncTask.this.e = e;
                }
            };
        } else
            listener = request.getListener();

        if (TextUtils.isEmpty(request.getBody()))
            conn.doGet(request, listener);
        else
            conn.doPost(request, listener);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (responseOnUIThread) {
            if (connectSuccess)
                request.getListener().connectSuccess(new HttpResponse.Builder().code(code).codeString(message).body(body).build());
            else
                request.getListener().connectFailure(new HttpResponse.Builder().code(code).codeString(message).errorMessage(errorStream).build(), e);
        }
    }
}
