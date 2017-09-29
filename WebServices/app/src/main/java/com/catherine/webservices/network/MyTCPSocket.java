package com.catherine.webservices.network;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by Catherine on 2017/9/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MyTCPSocket {
    private final static int SUCCEED = 0;
    private final static int FAILURE = 1;
    private SocketListener initListener, inputListener, outputListener;
    private Exception e;
    private Handler handler;
    private String host;
    private int port;
    private Socket socket;
    private Queue<String> msgQueue;
    private InputStream is;
    private OutputStream os;

    private MyTCPSocket(Builder builder) {
        this.inputListener = builder.inputListener;
        this.outputListener = builder.outputListener;
        this.initListener = builder.initListener;
        this.host = builder.host;
        this.port = builder.port;
        msgQueue = new ArrayDeque<>();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(final Message msg) {
                if (msg.what == SUCCEED) {
                    Bundle bundle = msg.getData();
                    String message = bundle.getString("msg");
                    if (initListener != null)
                        initListener.connectSuccess(message);

                } else if (msg.what == FAILURE) {
                    if (initListener != null)
                        initListener.connectFailure(e);
                }
            }

        };
        //init
        new InitAsyncTask(handler).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class InitAsyncTask extends AsyncTask<String, Void, Void> {
        private Handler handler;

        InitAsyncTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                //1.创建客户端Socket，指定服务器地址和端口
                socket = new Socket(host, port);
                final String state = (socket.isConnected()) ? "Connected" : "Failed to connect";
                // 获取客户端的IP地址
                InetAddress address = InetAddress.getLocalHost();
                final String ip = address.getHostAddress();
                Bundle bundle = new Bundle();
                bundle.putString("msg", String.format("%s (Client): %s", ip, state));
                Message msg = new Message();
                msg.what = SUCCEED;
                msg.setData(bundle);
                handler.sendMessage(msg);

                //2.获取输出流，向服务器端发送信息
                os = socket.getOutputStream();//字节输出流

                //注册输入流，等待服务器发送的信息
                Handler h = new Handler(Looper.getMainLooper()) {
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
                new InputTask(h).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                while (msgQueue.size() > 0) {
                    send(msgQueue.poll());
                }

            } catch (Exception e) {
                e.printStackTrace();
                MyTCPSocket.this.e = e;
                Message msg = new Message();
                msg.what = FAILURE;
                handler.sendMessage(msg);
            }
            return null;
        }
    }

    private class OutputTask extends AsyncTask<String, Void, Void> {
        private SocketListener listener;
        private String content;
        private OutputStream socketOutputStream;

        private OutputTask(OutputStream socketOutputStream, String content, SocketListener listener) {
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
                MyTCPSocket.this.e = e;
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
        private String info;
        private Handler handler;

        private InputTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                while (true) {
                    is = socket.getInputStream(); //获取输入流
                    //2. 获取输入流，读取服务器端发送的信息
                    InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                    BufferedReader br = new BufferedReader(isr);
                    while ((info = br.readLine()) != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("msg", info);
                        Message msg = new Message();
                        msg.what = SUCCEED;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }
            } catch (Exception e) {
                MyTCPSocket.this.e = e;
                Message msg = new Message();
                msg.what = FAILURE;
                handler.sendMessage(msg);
            }
            return null;
        }

    }


    public static class Builder {
        private SocketListener initListener, inputListener, outputListener;
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

        public Builder initListener(SocketListener initListener) {
            this.initListener = initListener;
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

        public MyTCPSocket build() {
            return new MyTCPSocket(this);
        }
    }

    public void send(String content) {
        if (TextUtils.isEmpty(content))
            return;

        if (socket == null) {
            Message msg = new Message();
            msg.what = FAILURE;
            MyTCPSocket.this.e = new NullPointerException("Server error");
            handler.sendMessage(msg);
            return;
        }

        if (socket.isClosed()) {
            msgQueue.add(content);
            //Connecting...
            new InitAsyncTask(handler).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new OutputTask(os, content, outputListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void release() {
        try {
            if (os != null)
                os.close();
            if (is != null)
                is.close();// 关闭输入流
            if (socket != null)
                socket.close();

            os = null;
            is = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
