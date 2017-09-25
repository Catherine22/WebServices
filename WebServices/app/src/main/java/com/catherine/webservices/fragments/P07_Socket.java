package com.catherine.webservices.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.interfaces.BackKeyListener;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.toolkits.CLog;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P07_Socket extends LazyFragment {
    public final static String TAG = "P07_Socket";
    private List<String> features, descriptions, contents;
    private MainInterface mainInterface;
    private SwipeRefreshLayout srl_container;
    private TextCardRVAdapter adapter;
    private Handler handler;

    private List<Socket> sockets;
    private ServerSocket server;
    private Socket socket = null;

    public static P07_Socket newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P07_Socket fragment = new P07_Socket();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_07_socket);
        mainInterface = (MainInterface) getActivity();
        handler = new Handler(MyApplication.INSTANCE.calHandlerThread.getLooper());
        init();
    }

    private void init() {
        mainInterface.getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                fillInData();
                initComponent();
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


    private void fillInData() {
        features = new ArrayList<>();
        features.add("Send messages to a specific ip address");
        features.add("UDP");

        descriptions = new ArrayList<>();
        descriptions.add("Socket connection based on TCP/IP.");
        descriptions.add("Socket connection based on UDP.");


        contents = new ArrayList<>();
        for (int i = 0; i < features.size(); i++) {
            contents.add("");
        }

        sockets = new ArrayList<>();
    }

    private void initComponent() {
        srl_container = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (server != null) {
                    try {
                        server.close();
                        Toast.makeText(getApplicationContext(), "SocketServer closed", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                for (Socket sk : sockets) {
                    if (sk != null) {
                        try {
                            sk.close();
                            Toast.makeText(getApplicationContext(), "SocketServer closed", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    sockets.clear();
                    socket.close();
                    CLog.Companion.i(TAG, "Socket server is closed.");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                init();
                srl_container.setRefreshing(false);
            }
        });

        mainInterface.setBackKeyListener(new BackKeyListener() {
            @Override
            public void OnKeyDown() {

                if (server != null || sockets != null) {
                    try {
                        server.close();
                        Toast.makeText(getApplicationContext(), "SocketServer closed", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    for (Socket sk : sockets) {
                        if (sk != null) {
                            try {
                                sk.close();
                                Toast.makeText(getApplicationContext(), "SocketServer closed", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    try {
                        sockets.clear();
                        socket.close();
                        CLog.Companion.i(TAG, "Socket server is closed.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mainInterface.backToPreviousPage();
                } else
                    mainInterface.backToPreviousPage();
            }
        });

        RecyclerView rv_main_list = (RecyclerView) findViewById(R.id.rv_main_list);
//        rv_main_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.Companion.getVERTICAL_LIST()));
        rv_main_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TextCardRVAdapter(getActivity(), null, features, descriptions, new OnItemClickListener() {
            @Override
            public void onItemClick(@NotNull View view, final int position) {
                switch (position) {
                    case 0:
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                String s;
                                try {
                                    //1.创建客户端Socket，指定服务器地址和端口
                                    Socket socket = new Socket(Constants.SOCKET_HOST, Constants.SOCKET_PORT);
                                    //2.获取输出流，向服务器端发送信息
                                    OutputStream os = socket.getOutputStream();//字节输出流
                                    PrintWriter pw = new PrintWriter(os);//将输出流包装为打印流
                                    //获取客户端的IP地址
                                    InetAddress address = InetAddress.getLocalHost();
                                    String ip = address.getHostAddress();
                                    pw.write("Hi there, I am " + ip);
                                    pw.flush();
                                    socket.shutdownOutput();//关闭输出流
                                    socket.close();

                                    CLog.Companion.i(TAG, "Send the message");
                                    s = contents.get(position);
                                    s += "\n" + "Send the message";
                                    contents.set(position, s);
                                    adapter.setContents(contents);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    s = contents.get(position);
                                    s += "\n" + "Error: " + e.getMessage();
                                    contents.set(position, s);
                                    adapter.setContents(contents);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        });
                        break;
                    case 1:
                        break;
                }
            }

            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);
    }
}
