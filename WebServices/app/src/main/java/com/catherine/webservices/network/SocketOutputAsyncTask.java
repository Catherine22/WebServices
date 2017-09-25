package com.catherine.webservices.network;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * Created by Catherine on 2017/9/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class SocketOutputAsyncTask extends AsyncTask<String, Void, Void> {
    private SocketListener listener;
    private String info;
    private InputStream socketInputStream;
    private Exception e = null;

    public SocketOutputAsyncTask(InputStream socketInputStream, SocketListener listener) {
        this.socketInputStream = socketInputStream;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            //2. 获取输入流，读取服务器端发送端信息
            InputStreamReader isr = new InputStreamReader(socketInputStream, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            while ((info = br.readLine()) != null) {
                return null;
            }
        } catch (Exception e) {
            this.e = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (e == null)
            listener.connectSuccess(info);
        else
            listener.connectFailure(e);
    }

}
