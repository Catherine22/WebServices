package com.catherine.webservices.network;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;

/**
 * Created by Catherine on 2017/9/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MyUDPSocket {
    private final static int SUCCEED = 0;
    private final static int FAILURE = 1;
    //输入每个数据报最大大小为4KB
    private final static int MAX_LEN = 4096;
    //输出每个数据报最大大小为1KB
    private final static int OUTPUT_BUFF = 1024;
    private SocketListener inputListener, outputListener;
    private Exception e;
    private Handler handler;
    private String host;
    private int port;
    private byte[] buff = new byte[MAX_LEN];
    private DatagramSocket socket;
    //用来发送的DatagramPacket对象
    private DatagramPacket outPacket;
    //用来接收的DatagramPacket对象
    private DatagramPacket inPacket;


    private MyUDPSocket(Builder builder) {
        this.inputListener = builder.inputListener;
        this.outputListener = builder.outputListener;
        this.host = builder.host;
        this.port = builder.port;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(final Message msg) {
                if (msg.what == SUCCEED) {
                    Bundle bundle = msg.getData();
                    String message = bundle.getString("msg");
                    if (inputListener != null)
                        inputListener.connectSuccess(message);

                } else if (msg.what == FAILURE) {
                    if (inputListener != null)
                        inputListener.connectFailure(e);
                }
            }
        };

        initSocket();
    }

    private void initSocket() {
        new InputTask(inputListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private class OutputTask extends AsyncTask<String, Void, Void> {
        private SocketListener listener;
        private String content;

        private OutputTask(String content, SocketListener listener) {
            this.content = content;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                outPacket = new DatagramPacket(new byte[0], 0, InetAddress.getByName(host), port);
                byte[] body = content.getBytes(Charset.forName("UTF-8"));
                if (body.length <= OUTPUT_BUFF) {
                    outPacket.setData(body, 0, body.length);
                    socket.send(outPacket);
                } else {
                    int header = 0;
                    int len = OUTPUT_BUFF;
                    while (len > 0) {
                        outPacket.setData(body, header, len);
                        socket.send(outPacket);
                        header += len;
                        len = (body.length - header > OUTPUT_BUFF) ? OUTPUT_BUFF : body.length - header;
                    }
                }

            } catch (Exception e) {
                MyUDPSocket.this.e = e;
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

    private class InputTask extends AsyncTask<String, Void, Void> {
        private SocketListener listener;
        private String info;

        private InputTask(SocketListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                socket = new DatagramSocket();
                while (socket != null && !socket.isClosed()) {
                    inPacket = new DatagramPacket(buff, buff.length);
                    socket.receive(inPacket);
                    info = new String(buff, 0, inPacket.getLength());

                    Bundle b = new Bundle();
                    b.putString("msg", info);
                    Message msg = new Message();
                    msg.what = SUCCEED;
                    msg.setData(b);
                    handler.sendMessage(msg);

                }
            } catch (Exception e) {
                MyUDPSocket.this.e = e;
                Message msg = new Message();
                msg.what = FAILURE;
                handler.sendMessage(msg);
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


    public static class Builder {
        private SocketListener inputListener, outputListener;
        private String host;
        private int port;

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder inputListener(SocketListener inputListener) {
            this.inputListener = inputListener;
            return this;
        }

        public Builder outputListener(SocketListener outputListener) {
            this.outputListener = outputListener;
            return this;
        }

        public MyUDPSocket build() {
            return new MyUDPSocket(this);
        }
    }

    public void send(String content) {
        if (TextUtils.isEmpty(content))
            return;

        if (socket == null) {
            Message msg = new Message();
            msg.what = FAILURE;
            MyUDPSocket.this.e = new NullPointerException("Server error");
            handler.sendMessage(msg);
            return;
        }

        if (socket.isClosed())
            initSocket();

        new OutputTask(content, outputListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void release() {
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
