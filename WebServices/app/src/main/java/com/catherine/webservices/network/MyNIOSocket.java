package com.catherine.webservices.network;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by Catherine on 2017/9/28.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MyNIOSocket {
    private final static int SENT_SUCCESSFULLY = 0;
    private final static int FAILED_TO_SEND = 1;
    private SocketListener inputListener, outputListener;
    private SocketChannel socketChannel;
    private ByteBuffer readBuffer;
    private int readBytes;
    private Exception e;
    private Handler handler;
    private String host;
    private int port;

    private MyNIOSocket(Builder builder) {
        this.inputListener = builder.inputListener;
        this.outputListener = builder.outputListener;
        this.host = builder.host;
        this.port = builder.port;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(final Message msg) {
                if (msg.what == SENT_SUCCESSFULLY) {
                    Bundle bundle = msg.getData();
                    String message = bundle.getString("msg");
                    if (inputListener != null)
                        inputListener.connectSuccess(message);

                } else if (msg.what == FAILED_TO_SEND) {
                    if (inputListener != null)
                        inputListener.connectFailure(e);
                }
            }
        };

        new InputTask(handler).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void write(String content) {
        new OutputTask(socketChannel, content, outputListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void release() {
        try {
            socketChannel.finishConnect();
            socketChannel.close();
        } catch (Exception e1) {
            e1.printStackTrace();
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

        public MyNIOSocket build() {
            return new MyNIOSocket(this);
        }
    }

    private class InputTask extends AsyncTask<String, Void, Void> {
        private Handler handler;

        InputTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                //打开SocketChannel
                socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
                //设置为非阻塞
                socketChannel.configureBlocking(false);

                readBuffer = ByteBuffer.allocate(1024);
                Message msg;
                Bundle bundle;

                while (true) {
                    readBuffer.clear();
                    readBytes = socketChannel.read(readBuffer);
                    if (readBytes > 0) {
                        readBuffer.flip();
                        msg = new Message();
                        bundle = new Bundle();
                        bundle.putString("msg", new String(readBuffer.array(), 0, readBytes));
                        msg.setData(bundle);
                        msg.what = SENT_SUCCESSFULLY;
                        handler.sendMessage(msg);
                    } else if (readBytes == -1) {
                        //如果read（）接收到-1，表明服务端关闭，抛出异常
                        e = new SocketException("Connection closed prematurely");
                        MyNIOSocket.this.e = e;
                        msg = new Message();
                        msg.what = FAILED_TO_SEND;
                        handler.sendMessage(msg);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                MyNIOSocket.this.e = e;
                Message msg = new Message();
                msg.what = FAILED_TO_SEND;
                handler.sendMessage(msg);
            }
            return null;
        }
    }

    private class OutputTask extends AsyncTask<String, Void, Void> {
        private SocketListener listener;
        private SocketChannel socketChannel;
        private String content;

        OutputTask(SocketChannel socketChannel, String content, SocketListener listener) {
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
                MyNIOSocket.this.e = e;
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
}
