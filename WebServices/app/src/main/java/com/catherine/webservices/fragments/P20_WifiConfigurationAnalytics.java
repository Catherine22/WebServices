package com.catherine.webservices.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.net.NetworkInfo;
import android.net.ProxyInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.catherine.webservices.R;
import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.entities.TextCard;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P20_WifiConfigurationAnalytics extends LazyFragment {
    public final static String TAG = P20_WifiConfigurationAnalytics.class.getSimpleName();
    private List<TextCard> entities;
    private SwipeRefreshLayout srl_container;
    private MainInterface mainInterface;

    public static P20_WifiConfigurationAnalytics newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P20_WifiConfigurationAnalytics fragment = new P20_WifiConfigurationAnalytics();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_20_wifi_configuration_analytics);
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
        Bundle b = getArguments();
        StringBuilder errorSb = new StringBuilder();
        WifiConfiguration wifiConfiguration = b.getParcelable("WifiConfiguration");
        entities.add(new TextCard("热点名称", "SSID", (TextUtils.isEmpty(wifiConfiguration.SSID) ? "" : wifiConfiguration.SSID)));
        entities.add(new TextCard("BSSID", "BSSID", (TextUtils.isEmpty(wifiConfiguration.BSSID) ? "" : wifiConfiguration.BSSID)));
        entities.add(new TextCard("WPA-PSK使用的预共享密钥", "preSharedKey", (TextUtils.isEmpty(wifiConfiguration.preSharedKey) ? "" : wifiConfiguration.preSharedKey)));
        entities.add(new TextCard("网络配置的ID", "networkId", wifiConfiguration.networkId + ""));
        entities.add(new TextCard("访问的优先级", "priority", wifiConfiguration.priority + ""));
        entities.add(new TextCard("隐藏SSID（只能扫描）", "hiddenSSID", wifiConfiguration.hiddenSSID + ""));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            entities.add(new TextCard("Fully qualified domain name", "FQDN", (TextUtils.isEmpty(wifiConfiguration.FQDN) ? "" : wifiConfiguration.FQDN)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            entities.add(new TextCard("是否为Passpoint", "isPasspoint", wifiConfiguration.isPasspoint() + ""));
            entities.add(new TextCard("Name of Passpoint credential provider", "providerFriendlyName", wifiConfiguration.providerFriendlyName));

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            entities.add(new TextCard("是否为家庭Passpoint", "isHomeProviderNetwork", wifiConfiguration.isHomeProviderNetwork + ""));
            ProxyInfo proxyInfo = wifiConfiguration.getHttpProxy();
            if (proxyInfo == null) {
                errorSb.append("ProxyInfo is null!\n");
            } else {
                entities.add(new TextCard("查看当前代理主机", "proxyInfo.getHost()", proxyInfo.getHost()));
                entities.add(new TextCard("查看当前代理端口", "proxyInfo.getPort()", proxyInfo.getPort() + ""));
            }
        }
        entities.add(new TextCard("状态", "status", WifiConfiguration.Status.strings[wifiConfiguration.status]));
        String[] wepKeys = wifiConfiguration.wepKeys;
        for (int i = 0; i < wepKeys.length; i++)
            entities.add(new TextCard("WEP key(最多有四组)", "wepKeys", wepKeys[i]));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            WifiEnterpriseConfig enterpriseConfig = wifiConfiguration.enterpriseConfig;
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
        TextCardRVAdapter adapter = new TextCardRVAdapter(getActivity(), entities, new OnItemClickListener() {
            @Override
            public void onItemClick(@NotNull View view, int position) {
                try {
                    TextCard tc = entities.get(position);
                    int sdk = Build.VERSION.SDK_INT;
                    if (sdk < Build.VERSION_CODES.HONEYCOMB) {
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
