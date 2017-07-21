package com.catherine.webservices.tasks;

import android.os.AsyncTask;

import com.catherine.webservices.toolkits.CLog;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Catherine on 2017/7/17.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class SampleAsyncTask extends AsyncTask<String, Void, Void> {
    private final static String TAG = "SampleAsyncTask";
    private OkHttpClient client;

    @Override
    protected Void doInBackground(String... params) {
        client = new OkHttpClient();
        try {
            CLog.w(TAG, "run:" + run("http://test.active.mygame.com.tw/OfficialWebsite/SetPictureResources/index.htm"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
