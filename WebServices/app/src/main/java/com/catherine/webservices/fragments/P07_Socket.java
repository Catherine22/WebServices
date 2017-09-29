package com.catherine.webservices.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.interfaces.BackKeyListener;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.toolkits.CLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P07_Socket extends LazyFragment {
    public final static String TAG = "P07_Socket";
    private List<String> features;
    private List<String> descriptions;
    private SwipeRefreshLayout srl_container;
    private MainInterface mainInterface;
    private TextCardRVAdapter adapter;

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
        features.add("TCP Socket");
        features.add("NIO Socket");
        features.add("UDP Socket");

        descriptions = new ArrayList<>();
        descriptions.add("TCP socket transmission on blocking thread.");
        descriptions.add("TCP socket transmission on non-blocking thread (channel).");
        descriptions.add("UDP socket transmission on non-blocking thread.");
    }

    private void initComponent() {
        srl_container = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
                srl_container.setRefreshing(false);
            }
        });

        RecyclerView rv_main_list = (RecyclerView) findViewById(R.id.rv_main_list);
//        rv_main_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.Companion.getVERTICAL_LIST()));
        rv_main_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TextCardRVAdapter(getActivity(), null, features, descriptions, new OnItemClickListener() {
            @Override
            public void onItemClick(@NotNull View view, int position) {
                switch (position) {
                    case 0:
                        callFragment(Constants.P08_BLOCKING_SOCKET);
                        break;
                    case 1:
                        callFragment(Constants.P09_NIO_SOCKET);
                        break;
                    case 2:
                        callFragment(Constants.P10_UDP_SOCKET);
                        break;
                }
            }

            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);

        mainInterface.setBackKeyListener(new BackKeyListener() {
            @Override
            public void OnKeyDown() {
                if (getChildFragmentManager().getBackStackEntryCount() > 0) {
                    getChildFragmentManager().popBackStack();
                    mainInterface.restoreBottomLayout();
                } else
                    mainInterface.backToPreviousPage();
            }
        });
    }

    private void callFragment(int id) {
        CLog.Companion.d(TAG, "call " + id);
        Fragment fragment = null;
        String tag = "";
        String title = "";
        switch (id) {
            case Constants.P08_BLOCKING_SOCKET:
                title = "P08_BLOCKING_SOCKET";
                fragment = P08_Blocking_Socket.newInstance(true);
                tag = "P08";
                break;
            case Constants.P09_NIO_SOCKET:
                title = "P09_NIO_SOCKET";
                fragment = P09_NIO_Socket.newInstance(true);
                tag = "P09";
                break;
            case Constants.P10_UDP_SOCKET:
                title = "P10_UDP_SOCKET";
                fragment = P10_UDP_Socket.newInstance(true);
                tag = "P10";
                break;
        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fl_container, fragment, tag);
        transaction.addToBackStack(title);
        transaction.commitAllowingStateLoss();
    }

}
