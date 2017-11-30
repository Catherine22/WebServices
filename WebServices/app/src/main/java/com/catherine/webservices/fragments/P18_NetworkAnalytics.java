package com.catherine.webservices.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ProxyInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.entities.TextCard;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.toolkits.CLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P18_NetworkAnalytics extends LazyFragment {
    public final static String TAG = P18_NetworkAnalytics.class.getSimpleName();
    private List<TextCard> entities;
    private TextCardRVAdapter adapter;
    private SwipeRefreshLayout srl_container;
    private MainInterface mainInterface;
    private ConnectivityManager cm;
    private NetworkInfo networkInfo;
    private WifiManager wm;
    private WifiManager.WifiLock wl;
    private NetworkHelper helper;

    public static P18_NetworkAnalytics newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P18_NetworkAnalytics fragment = new P18_NetworkAnalytics();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_19_network_info_analytics);
        mainInterface = (MainInterface) getActivity();
        helper = new NetworkHelper();
        init();
    }

    private void init() {
        mainInterface.getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WAKE_LOCK}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                fillInData();
                initComponent();
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


    private void fillInData() {
        entities = new ArrayList<>();
        cm = (ConnectivityManager) MyApplication.INSTANCE.getSystemService(Context.CONNECTIVITY_SERVICE);
        wm = (WifiManager) MyApplication.INSTANCE.getSystemService(Context.WIFI_SERVICE);
        networkInfo = cm.getActiveNetworkInfo();
        StringBuilder errorSb = new StringBuilder();
        if (cm == null) {
            errorSb.append("ConnectivityManager is null!\n");
        } else if (networkInfo == null) {
            errorSb.append("NetworkInfo is null!\n");
        } else {
            entities.add(new TextCard("查看当前网络", "ConnectivityManager.getActiveNetworkInfo()", null));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ProxyInfo proxyInfo = cm.getDefaultProxy();
                if (proxyInfo == null) {
                    errorSb.append("ProxyInfo is null!\n");
                } else {
                    entities.add(new TextCard("查看当前代理主机", "proxyInfo.getHost()", proxyInfo.getHost()));
                    entities.add(new TextCard("查看当前代理端口", "proxyInfo.getPort()", proxyInfo.getPort() + ""));
                }
            }
        }

        if (wm == null)
            errorSb.append("WifiManager is null!\n");
        else {
            if (wm.isWifiEnabled()) {
                entities.add(new TextCard("关闭Wi-Fi", "Close Wi-Fi", null));
            } else {
                entities.add(new TextCard("开启Wi-Fi", "Open Wi-Fi", null));
            }

            wl = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, "myWifiLock");
            if (wl == null)
                errorSb.append("WifiManager.WifiLock is null!\n");
            else {
                if (wl.isHeld()) {
                    entities.add(new TextCard("解除Wi-Fi锁定", "Release Wi-Fi lock", null));
                } else {
                    entities.add(new TextCard("锁定Wi-Fi", "Held Wi-Fi lock", null));
                }
            }

            entities.add(new TextCard("扫描Wi-Fi", "Scan Wi-Fi", null));
            List<WifiConfiguration> wifiConfigurations = wm.getConfiguredNetworks();
            if (wifiConfigurations == null) {
                errorSb.append("WifiManager.wifiConfigurations is null!\n");
            } else {
                entities.add(new TextCard("查看网络配置", "wifiConfigurations", null));
            }
        }


        String message = errorSb.toString();
        if (!TextUtils.isEmpty(message)) {
            DialogManager.showErrorDialog(getActivity(), message, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
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
        rv_main_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TextCardRVAdapter(getActivity(), entities, new OnItemClickListener() {
            @Override
            public void onItemClick(@NotNull View view, int position) {
                TextCard tc = entities.get(position);
                CLog.i(TAG, tc.title);
                if ("查看当前网络".equals(tc.title)) {
                    Bundle b = new Bundle();
                    b.putParcelable("NetworkInfo", networkInfo);
                    mainInterface.callFragment(Constants.P19_NETWORK_INFO_ANALYTICS, b);
                } else if ("开启Wi-Fi".equals(tc.title)) {
                    helper.openWifi();
                    entities.set(position, new TextCard("关闭Wi-Fi", "Close Wi-Fi", null));
                    adapter.notifyDataSetChanged();
                } else if ("关闭Wi-Fi".equals(tc.title)) {
                    helper.closeWifi();
                    entities.set(position, new TextCard("开启Wi-Fi", "Open Wi-Fi", null));
                    adapter.notifyDataSetChanged();
                } else if ("解除Wi-Fi锁定".equals(tc.title)) {
                    if (wl.isHeld())
                        wl.release();
                    entities.set(position, new TextCard("锁定Wi-Fi", "Held Wi-Fi lock", null));
                    adapter.notifyDataSetChanged();
                } else if ("锁定Wi-Fi".equals(tc.title)) {
                    if (!wl.isHeld())
                        wl.acquire();
                    entities.set(position, new TextCard("解除Wi-Fi锁定", "Release Wi-Fi lock", null));
                    adapter.notifyDataSetChanged();
                } else if ("扫描Wi-Fi".equals(tc.title)) {
                    mainInterface.callFragmentDialog(Constants.P21_D_SCAN_RESULT);
                } else if ("查看网络配置".equals(tc.title)) {
                    mainInterface.callFragmentDialog(Constants.P22_D_WIFI_CONFIGURATIONS);
                } else {
                    try {
                        int sdk = android.os.Build.VERSION.SDK_INT;
                        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                                    getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setText(tc.contents);
                        } else {
                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                                    getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            android.content.ClipData clip = android.content.ClipData
                                    .newPlainText(tc.title, tc.contents);
                            clipboard.setPrimaryClip(clip);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
