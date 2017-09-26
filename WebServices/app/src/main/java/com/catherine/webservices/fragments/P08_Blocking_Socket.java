package com.catherine.webservices.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.interfaces.BackKeyListener;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.network.SocketInputAsyncTask;
import com.catherine.webservices.network.SocketListener;
import com.catherine.webservices.network.SocketOutputAsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P08_Blocking_Socket extends LazyFragment {
    public final static String TAG = "P08_Blocking_Socket";
    private MainInterface mainInterface;
    private TextView tv_history, tv_state;
    private EditText et_input;
    private Button bt_send;
    private FloatingActionButton fab_disconnect, fab_settings;
    private boolean isFABOpen;
    private List<Socket> sockets;
    private NetworkHelper helper;
    private Socket socket;
    private Queue<String> msgQueue;

    public static P08_Blocking_Socket newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P08_Blocking_Socket fragment = new P08_Blocking_Socket();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_08_blocking_socket);
        helper = new NetworkHelper(getActivity());
        sockets = new ArrayList<>();
        msgQueue = new ArrayDeque<>();
        mainInterface = (MainInterface) getActivity();
        init();
    }

    private void init() {
        mainInterface.getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                initComponent();
                initSocket();
            }

            @Override
            public void onDenied(@org.jetbrains.annotations.Nullable List<String> deniedPermissions) {
                StringBuilder context = new StringBuilder();
                if (deniedPermissions != null) {
                    for (String p : deniedPermissions) {
                        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(p)) {
                            context.append("存储、");
                        }
                    }
                }

                context.deleteCharAt(context.length() - 1);

                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
                myAlertDialog.setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .setTitle("注意")
                        .setMessage(String.format("您目前未授权%s存取权限，未授权将造成程式无法执行，是否开启权限？", context.toString()))
                        .setNegativeButton("继续关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        }).setPositiveButton("确定开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getActivity().getPackageName(), null));
                        startActivityForResult(intent, Constants.OPEN_SETTINGS);
                    }
                });
                myAlertDialog.show();
            }

            @Override
            public void onRetry() {
                init();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void initSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //1.创建客户端Socket，指定服务器地址和端口
                    socket = new Socket(Constants.SOCKET_HOST, Constants.SOCKET_PORT);
                    sockets.add(socket);

                    final String state = (socket.isConnected()) ? "Connected" : "Failed to connect";
                    // 获取客户端的IP地址
                    InetAddress address = InetAddress.getLocalHost();
                    final String ip = address.getHostAddress();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_state.setText(String.format("%s (Client): %s", ip, state));
                        }
                    });

                    //2.获取输出流，向服务器端发送信息
                    is = socket.getInputStream(); //获取输入流
                    os = socket.getOutputStream();//字节输出流

                    while (msgQueue.size() > 0) {
                        send(msgQueue.poll());
                    }
                } catch (ConnectException e) {
                    e.printStackTrace();
                    if (!helper.isNetworkHealth()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Offline", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initComponent() {
        fab_disconnect = (FloatingActionButton) findViewById(R.id.fab_disconnect);
        fab_settings = (FloatingActionButton) findViewById(R.id.fab_settings);
        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_history = (TextView) findViewById(R.id.tv_history);
        et_input = (EditText) findViewById(R.id.et_input);
        bt_send = (Button) findViewById(R.id.bt_send);
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(et_input.getText().toString());
            }
        });

        fab_disconnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                send("*#DISCONNECT11223#*");
                tv_state.setText("Stop connecting...");
                release();
            }
        });

        fab_settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isFABOpen) {
                    isFABOpen = true;
                    fab_disconnect.animate().translationY(-getResources().getDimension(R.dimen.fab_01_m_b));
                } else {
                    isFABOpen = false;
                    fab_disconnect.animate().translationY(0);
                }
            }
        });
    }


    private InputStream is;
    private OutputStream os;

    private void send(String content) {
        if (TextUtils.isEmpty(content))
            return;

        if (socket.isClosed()) {
            msgQueue.add(content);
            tv_state.setText("Connecting...");
            initSocket();
        } else {
            new SocketInputAsyncTask(os, content, new SocketListener() {
                @Override
                public void connectSuccess(String message) {
                    et_input.setText("");
                    tv_history.setText(String.format("%s\nYou sent: %s", tv_history.getText(), message));
                }

                @Override
                public void connectFailure(Exception e) {
                    e.printStackTrace();
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


            new SocketOutputAsyncTask(is, new SocketListener() {
                @Override
                public void connectSuccess(String message) {
                    if ("*#DISCONNECT11223#*".equals(message)) {
                        tv_state.setText(socket.getInetAddress() + " disconnected.");
                        release();
                    } else {
                        // 读取socket输入流的内容并打印
                        tv_history.setText(String.format("%s\nYou got: %s", tv_history.getText(), message));
                    }
                }

                @Override
                public void connectFailure(Exception e) {
                    e.printStackTrace();
                    if (e instanceof ConnectException) {
                        if (!helper.isNetworkHealth()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Offline", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void release() {
        for (Socket s : sockets) {
            sockets.remove(s);
            try {
                if (os != null)
                    os.close();
                if (is != null)
                    is.close();// 关闭输入流
                if (s != null)
                    s.close();

                os = null;
                is = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
