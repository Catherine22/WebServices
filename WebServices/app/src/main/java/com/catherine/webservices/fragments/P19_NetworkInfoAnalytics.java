package com.catherine.webservices.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class P19_NetworkInfoAnalytics extends LazyFragment {
    public final static String TAG = P19_NetworkInfoAnalytics.class.getSimpleName();
    private List<TextCard> entities;
    private SwipeRefreshLayout srl_container;
    private MainInterface mainInterface;

    public static P19_NetworkInfoAnalytics newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P19_NetworkInfoAnalytics fragment = new P19_NetworkInfoAnalytics();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_19_network_info_analytics);
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
        NetworkInfo networkInfo = b.getParcelable("NetworkInfo");
        entities.add(new TextCard("网络是否有效可用", "networkInfo.isAvailable()", null));
        entities.add(new TextCard("是否已连线", "networkInfo.isConnected()", networkInfo.isConnected() + ""));
        entities.add(new TextCard("正在连线或已连线", "networkInfo.isConnectedOrConnecting()", networkInfo.isConnectedOrConnecting() + ""));


        entities.add(new TextCard("返回当前粗略的网络状态", "networkInfo.getState()", getStateName(networkInfo.getState())));
        entities.add(new TextCard("返回当前详细的网络状态", "networkInfo.getDetailedState()", getDetailStateName(networkInfo.getDetailedState())));
        entities.add(new TextCard("网络类型", "networkInfo.getType()", String.format(Locale.ENGLISH, "(%d) %s", networkInfo.getType(), networkInfo.getTypeName())));
        entities.add(new TextCard("特定网络类型", "networkInfo.getSubtype()", String.format(Locale.ENGLISH, "(%d) %s", networkInfo.getSubtype(), networkInfo.getSubtypeName())));
        entities.add(new TextCard("如果有，尝试连线失败的原因", "networkInfo.getReason()", networkInfo.getReason()));
        entities.add(new TextCard("网络是否有问题", "networkInfo.isFailover()", networkInfo.isFailover() + ""));
        entities.add(new TextCard("是否漫游", "networkInfo.isRoaming()", networkInfo.isRoaming() + ""));
        entities.add(new TextCard("其他网络信息", "nnetworkInfo.getExtraInfo()", networkInfo.getExtraInfo()));
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

            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);
    }

    private String getStateName(NetworkInfo.State state) {
        if (state == NetworkInfo.State.CONNECTING)
            return "CONNECTING";
        else if (state == NetworkInfo.State.CONNECTED)
            return "CONNECTED";
        else if (state == NetworkInfo.State.SUSPENDED)
            return "SUSPENDED";
        else if (state == NetworkInfo.State.DISCONNECTING)
            return "DISCONNECTING";
        else if (state == NetworkInfo.State.DISCONNECTED)
            return "DISCONNECTED";
        else if (state == NetworkInfo.State.UNKNOWN)
            return "UNKNOWN";
        else
            return "???";
    }

    private String getDetailStateName(NetworkInfo.DetailedState state) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (state == NetworkInfo.DetailedState.VERIFYING_POOR_LINK)
                return "VERIFYING_POOR_LINK";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (state == NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK)
                return "CAPTIVE_PORTAL_CHECK";
        }
        if (state == NetworkInfo.DetailedState.IDLE)
            return "IDLE";
        else if (state == NetworkInfo.DetailedState.SCANNING)
            return "SCANNING";
        else if (state == NetworkInfo.DetailedState.CONNECTING)
            return "CONNECTING";
        else if (state == NetworkInfo.DetailedState.AUTHENTICATING)
            return "AUTHENTICATING";
        else if (state == NetworkInfo.DetailedState.OBTAINING_IPADDR)
            return "OBTAINING_IPADDR";
        else if (state == NetworkInfo.DetailedState.CONNECTED)
            return "CONNECTED";
        else if (state == NetworkInfo.DetailedState.SUSPENDED)
            return "SUSPENDED";
        else if (state == NetworkInfo.DetailedState.DISCONNECTING)
            return "DISCONNECTING";
        else if (state == NetworkInfo.DetailedState.DISCONNECTED)
            return "DISCONNECTED";
        else if (state == NetworkInfo.DetailedState.FAILED)
            return "FAILED";
        else if (state == NetworkInfo.DetailedState.BLOCKED)
            return "BLOCKED";
        else
            return "???";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
