package com.catherine.webservices.network;

import android.os.AsyncTask;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by Catherine on 2017/9/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class SocketInputAsyncTask extends AsyncTask<String, Void, Void> {
    private SocketListener listener;
    private String content;
    private OutputStream socketOutputStream;
    private Exception e = null;

    public SocketInputAsyncTask(OutputStream socketOutputStream, String content, SocketListener listener) {
        this.socketOutputStream = socketOutputStream;
        this.content = content;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            PrintWriter pw = new PrintWriter(socketOutputStream);//将输出流包装为打印流
            pw.write(content + "\n");
            pw.flush();
        } catch (Exception e) {
            this.e = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (e == null)
            listener.connectSuccess(content);
        else
            listener.connectFailure(e);
    }

}
