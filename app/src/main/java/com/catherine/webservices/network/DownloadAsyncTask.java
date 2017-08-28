package com.catherine.webservices.network;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.util.Map;

/**
 * Created by Catherine on 2017/8/28.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class DownloadAsyncTask extends AsyncTask<String, Void, Void> {
    private final static String TAG = "DownloadAsyncTask";
    private String url;
    private Map<String, String> headers;
    private String body;
    private HttpResponseListener listener;
    private boolean showProgress;

    public DownloadAsyncTask(String url, String body, HttpResponseListener listener, boolean showProgress) {
        this(url, MyHttpURLConnection.getDefaultHeaders(), body, listener, showProgress);
    }

    public DownloadAsyncTask(String url, Map<String, String> headers, String body, HttpResponseListener listener, boolean showProgress) {
        this.url = url;
        this.headers = headers;
        this.body = body;
        this.listener = listener;
        this.showProgress = showProgress;
    }

    @Override
    protected Void doInBackground(String... params) {
        String t = "";
        if (TextUtils.isEmpty(params[0]))
            t = params[0];
        Thread.currentThread().setName(String.format("%s_%s", TAG, t));


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
