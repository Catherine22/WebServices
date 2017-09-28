package com.catherine.webservices.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.network.MyNIOSocket;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.network.SocketListener;

import java.net.ConnectException;
import java.util.List;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P09_NIO_Socket extends LazyFragment {
    public final static String TAG = "P09_NIO_Socket";
    private MainInterface mainInterface;
    private TextView tv_history, tv_state;
    private EditText et_input;
    private Button bt_send;
    private FloatingActionButton fab_disconnect, fab_settings;
    private boolean isFABOpen;
    private NetworkHelper helper;
    private MyNIOSocket nioSocket;

    public static P09_NIO_Socket newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P09_NIO_Socket fragment = new P09_NIO_Socket();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_socket);
        helper = new NetworkHelper(getActivity());
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
        nioSocket = new MyNIOSocket.Builder()
                .inputListener(new Input())
                .outputListener(new Output())
                .build();
    }

    private void initComponent() {
        fab_disconnect = (FloatingActionButton) findViewById(R.id.fab_disconnect);
        fab_settings = (FloatingActionButton) findViewById(R.id.fab_settings);
        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_history = (TextView) findViewById(R.id.tv_history);

        mainInterface.addBottomLayout(R.layout.bottom_socket);
        View bottom = mainInterface.getBottomLayout();
        et_input = bottom.findViewById(R.id.et_input);
        bt_send = bottom.findViewById(R.id.bt_send);
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(et_input.getText().toString());
            }
        });

        fab_disconnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tv_state.setText("Stop");
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

    class Output implements SocketListener {

        @Override
        public void connectSuccess(String message) {
            tv_history.setText(String.format("%s\nYou got: %s", tv_history.getText(), message));

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
    }

    class Input implements SocketListener {

        @Override
        public void connectSuccess(String message) {
            et_input.setText("");
            tv_history.setText(String.format("%s\nYou sent: %s", tv_history.getText(), message));

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
    }


    private void send(final String message) {
        if (TextUtils.isEmpty(message))
            return;
        nioSocket.write(message);
    }

    private void release() {
        nioSocket.release();
    }
}
