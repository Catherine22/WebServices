package com.catherine.webservices.network;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;

import java.net.InetSocketAddress;
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

    private MyNIOSocket(Builder builder) {
        this.inputListener = builder.inputListener;
        this.outputListener = builder.outputListener;
        handler = new Handler(MyApplication.INSTANCE.socketHandlerThread.getLooper()) {
            @Override
            public void handleMessage(final Message msg) {
                if (msg.what == SENT_SUCCESSFULLY) {
                    Bundle bundle = msg.getData();
                    String message = bundle.getString("msg");
                    if (outputListener != null)
                        outputListener.connectSuccess(message);

                } else if (msg.what == FAILED_TO_SEND) {
                    if (outputListener != null)
                        outputListener.connectFailure(e);
                }
            }
        };

        new NIOSocketOutputAsyncTask(handler).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void execute(String content) {
        new NIOSocketInputAsyncTask(socketChannel, content, inputListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static class Builder {
        private SocketListener inputListener, outputListener;

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

    class NIOSocketOutputAsyncTask extends AsyncTask<String, Void, Void> {
        private Handler handler;

        NIOSocketOutputAsyncTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                //打开SocketChannel
                socketChannel = SocketChannel.open(new InetSocketAddress(Constants.SOCKET_HOST, Constants.NIO_SOCKET_PORT));
                //设置为非阻塞
                socketChannel.configureBlocking(false);

                readBuffer = ByteBuffer.allocate(1024);
                Message msg = new Message();
                Bundle bundle = new Bundle();
                while (true) {
                    readBuffer.clear();
                    readBytes = socketChannel.read(readBuffer);
                    if (readBytes > 0) {
                        readBuffer.flip();
                        bundle.putString("msg", new String(readBuffer.array(), 0, readBytes));
                        msg.setData(bundle);
                        msg.what = SENT_SUCCESSFULLY;
                        handler.sendMessage(msg);
                        socketChannel.close();
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

    class NIOSocketInputAsyncTask extends AsyncTask<String, Void, Void> {
        private SocketListener listener;
        private SocketChannel socketChannel;
        private String content;
        private Exception e = null;

        NIOSocketInputAsyncTask(SocketChannel socketChannel, String content, SocketListener listener) {
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
}
