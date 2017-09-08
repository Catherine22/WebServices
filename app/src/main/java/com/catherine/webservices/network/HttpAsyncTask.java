package com.catherine.webservices.network;

import android.os.AsyncTask;
import android.text.TextUtils;

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
    private HttpResponseListener listener;
    private MyHttpURLConnection conn;

    public HttpAsyncTask(String url, HttpResponseListener listener) {
        this(url, MyHttpURLConnection.getDefaultHeaders(), "", listener);
    }

    public HttpAsyncTask(String url, Map<String, String> headers, HttpResponseListener listener) {
        this(url, headers, "", listener);
    }

    public HttpAsyncTask(String url, String body, HttpResponseListener listener) {
        this(url, MyHttpURLConnection.getDefaultHeaders(), body, listener);
    }

    public HttpAsyncTask(String url, Map<String, String> headers, String body, HttpResponseListener listener) {
        this.url = url;
        this.headers = headers;
        this.body = body;
        this.listener = listener;
        conn = new MyHttpURLConnection();
    }

    @Override
    protected Void doInBackground(String... params) {
        if (TextUtils.isEmpty(body))
            conn.doGet(url, headers, listener);
        else
            conn.doPost(url, headers, body, listener);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
