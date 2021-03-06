package com.catherine.webservices.fragments.socket;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.fragments.LazyFragment;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.network.MyUDPSocket;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.network.SocketListener;

import java.net.ConnectException;
import java.net.SocketException;
import java.util.List;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class UDP_SocketFragment extends LazyFragment {
    public final static String TAG = UDP_SocketFragment.class.getSimpleName();
    private MainInterface mainInterface;
    private TextView tv_history, tv_state;
    private EditText et_input;
    private Button bt_send;
    private FloatingActionButton fab_disconnect, fab_settings;
    private boolean isFABOpen;
    private NetworkHelper helper;
    private MyUDPSocket myUDPSocket;

    public static UDP_SocketFragment newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        UDP_SocketFragment fragment = new UDP_SocketFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_common_socket);
        helper = new NetworkHelper();
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
            public void onDenied(@Nullable List<String> deniedPermissions) {
                StringBuilder context = new StringBuilder();
                if (deniedPermissions != null) {
                    for (String p : deniedPermissions) {
                        context.append(p);
                        context.append(", ");
                    }
                }

                context.deleteCharAt(context.length() - 1);
                DialogManager.showPermissionDialog(getActivity(), String.format(getActivity().getResources().getString(R.string.permission_request), context), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
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
        myUDPSocket = new MyUDPSocket.Builder()
                .host(Constants.SOCKET_HOST)
                .port(Constants.UDP_SOCKET_PORT)
                .inputListener(new InputListener())
                .outputListener(new OutputListener())
                .build();
    }

    private void initComponent() {
        fab_disconnect = (FloatingActionButton) findViewById(R.id.fab_disconnect);
        fab_settings = (FloatingActionButton) findViewById(R.id.fab_settings);
        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_history = (TextView) findViewById(R.id.tv_history);

        mainInterface.restoreBottomLayout();
        mainInterface.addBottomLayout(R.layout.bottom_socket);
        View bottom = mainInterface.getBottomLayout();
        et_input = bottom.findViewById(R.id.et_input);
        bt_send = bottom.findViewById(R.id.bt_send);
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myUDPSocket.send(et_input.getText().toString());
            }
        });

        fab_disconnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myUDPSocket.send("*#DISCONNECT12435#*");
                tv_state.setText(Constants.SOCKET_HOST + " disconnected.");
                myUDPSocket.release();
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

    private class InputListener implements SocketListener {

        @Override
        public void connectSuccess(String message) {
            if ("*#DISCONNECT12435#*".equals(message)) {
                tv_state.setText(Constants.SOCKET_HOST + " disconnected.");
                myUDPSocket.release();
            } else {
                // 读取socket输入流的内容并打印
                tv_history.setText(String.format("%s\nYou got: %s", tv_history.getText(), message));
            }
        }

        @Override
        public void connectFailure(Exception e) {
            e.printStackTrace();
            if (e instanceof ConnectException) {
                if (!helper.isNetworkHealthy()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_state.setText(getResources().getString(R.string.offline));
                        }
                    });
                }
            } else if (e instanceof SocketException) {
                tv_state.setText("Server error");
            }
        }
    }

    private class OutputListener implements SocketListener {


        @Override
        public void connectSuccess(String message) {
            et_input.setText("");
            tv_history.setText(String.format("%s\nYou sent: %s", tv_history.getText(), message));
        }

        @Override
        public void connectFailure(Exception e) {
            e.printStackTrace();
        }
    }


}
