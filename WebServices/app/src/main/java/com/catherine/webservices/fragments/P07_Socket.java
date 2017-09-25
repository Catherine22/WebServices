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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.interfaces.BackKeyListener;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.toolkits.CLog;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P07_Socket extends LazyFragment {
    public final static String TAG = "P07_Socket";
    private MainInterface mainInterface;
    private Handler msgHandler, networkHandler;
    private TextView tv_history;
    private EditText et_input;
    private Button bt_send;
    private List<Socket> sockets;
    private NetworkHelper helper;
    private Socket socket;

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
        helper = new NetworkHelper(getActivity());
        mainInterface = (MainInterface) getActivity();
        msgHandler = new Handler(MyApplication.INSTANCE.socketHandlerThread.getLooper());
        networkHandler = new Handler(MyApplication.INSTANCE.socketHandlerThread.getLooper());
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
        sockets = new ArrayList<>();
    }

    private void initComponent() {
        tv_history = (TextView) findViewById(R.id.tv_history);
        et_input = (EditText) findViewById(R.id.et_input);
        bt_send = (Button) findViewById(R.id.bt_send);
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(et_input.getText().toString());
            }
        });
        mainInterface.setBackKeyListener(new BackKeyListener() {
            @Override
            public void OnKeyDown() {
                mainInterface.backToPreviousPage();
            }
        });

    }

    private void send(final String content) {
        if (TextUtils.isEmpty(content))
            return;

        networkHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //1.创建客户端Socket，指定服务器地址和端口
                    socket = new Socket(Constants.SOCKET_HOST, Constants.SOCKET_PORT);
                    final String state = (socket.isConnected()) ? "Connected" : "Failed to connect";
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_history.setText(String.format("%s\n%s", tv_history.getText(), state));
                        }
                    });

                    sockets.add(socket);
                    msgHandler.post(new ClientRunnable(socket));

                    //2.获取输出流，向服务器端发送信息
                    OutputStream os = socket.getOutputStream();//字节输出流
                    PrintWriter pw = new PrintWriter(os);//将输出流包装为打印流
                    // 获取客户端的IP地址
                    InetAddress address = InetAddress.getLocalHost();
                    final String ip = address.getHostAddress();
                    CLog.Companion.d(TAG, "ip:" + ip);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_history.setText(String.format("%s\nip: %s", tv_history.getText(), ip));
                        }
                    });
                    pw.write(content);
                    pw.flush();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_history.setText(String.format("%s\nYou sent: %s", tv_history.getText(), content));
                        }
                    });
                    socket.shutdownOutput();//关闭输出流
                    socket.close();

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
        });
    }

    private class ClientRunnable implements Runnable {
        private BufferedReader br;
        private String info;

        private ClientRunnable(Socket socket) throws IOException {
            // 3.连接后获取输入流，读取客户端信息
            InputStream is = socket.getInputStream(); // 获取输入流
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(isr);
        }

        @Override
        public void run() {
            try {
                while ((info = br.readLine()) != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 读取socket输入流的内容并打印
                            tv_history.setText(String.format("%s\nYou got: %s", tv_history.getText(), info));
                        }
                    });
                }
//				socket.shutdownInput();// 关闭输入流
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
