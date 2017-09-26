package com.catherine.webservices.network;

import android.os.AsyncTask;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by Catherine on 2017/9/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class NIOSocketInputAsyncTask extends AsyncTask<String, Void, Void> {
    private SocketListener listener;
    private SocketChannel socketChannel;
    private String content;
    private Exception e = null;

    public NIOSocketInputAsyncTask(SocketChannel socketChannel, String content, SocketListener listener) {
        this.content = content;
        this.listener = listener;
        this.socketChannel = socketChannel;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            //给服务端发送信息
            socketChannel.write(ByteBuffer.wrap(content.getBytes(Charset.forName("UTF-8"))));
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
