package com.catherine.webservices.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.catherine.webservices.Commands;
import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.MultiStyleRVAdapter;
import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.entities.ImageCard;
import com.catherine.webservices.entities.MultiStyleItem;
import com.catherine.webservices.interfaces.BackKeyListener;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.interfaces.OnMultiItemClickListener;
import com.catherine.webservices.interfaces.OnMultiItemSelectListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.network.HttpAsyncTask;
import com.catherine.webservices.network.HttpRequest;
import com.catherine.webservices.network.HttpResponse;
import com.catherine.webservices.network.HttpResponseListener;
import com.catherine.webservices.network.MyHttpURLConnection;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.security.ADID_AsyncTask;
import com.catherine.webservices.toolkits.CLog;
import com.facebook.cache.common.CacheKey;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.DefaultExecutorSupplier;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import catherine.messagecenter.AsyncResponse;
import catherine.messagecenter.Client;
import catherine.messagecenter.CustomReceiver;
import catherine.messagecenter.Result;
import catherine.messagecenter.Server;

/**
 * Created by Catherine on 2017/9/15.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P15_WebView_Settings extends LazyFragment {
    public final static String TAG = "P15_WebView_Settings";
    private List<MultiStyleItem> wvAttr, wvSettings;
    private String[] titles;
    private SwipeRefreshLayout srl_container;
    private MainInterface mainInterface;
    private MultiStyleRVAdapter adapter;
    private SharedPreferences sp;
    private Server sv;

    public static P15_WebView_Settings newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P15_WebView_Settings fragment = new P15_WebView_Settings();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_15_wv_settings);
        init();
    }

    private void init() {
        mainInterface = (MainInterface) getActivity();
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

    //WebView Attribute
    private boolean setVerticalScrollBarEnabled = true;
    private boolean setHorizontalScrollBarEnabled = true;

    //WebSettings
    private boolean setUseWideViewPort = true;
    private boolean setLoadWithOverviewMode = true;
    private boolean setZoom = true;
    private boolean setDisplayZoomControls = false;
    private boolean setAllowFileAccess = true;
    private boolean setNeedInitialFocus = true;
    private boolean setJavaScriptEnabled = false;
    private boolean setJavaScriptCanOpenWindowsAutomatically = false;
    private boolean setLoadsImagesAutomatically = true;

    private void fillInData() {
        titles = new String[]{"WebView Attribute", "WebSettings"};
        sp = getActivity().getSharedPreferences("wv_settings", Context.MODE_PRIVATE);
        setVerticalScrollBarEnabled = sp.getBoolean("setVerticalScrollBarEnabled", true);
        setHorizontalScrollBarEnabled = sp.getBoolean("setHorizontalScrollBarEnabled", true);
        setUseWideViewPort = sp.getBoolean("setUseWideViewPort", true);
        setLoadWithOverviewMode = sp.getBoolean("setLoadWithOverviewMode", true);
        setZoom = sp.getBoolean("setZoom", true);
        setDisplayZoomControls = sp.getBoolean("setDisplayZoomControls", false);
        setAllowFileAccess = sp.getBoolean("setAllowFileAccess", true);
        setNeedInitialFocus = sp.getBoolean("setNeedInitialFocus", true);
        setJavaScriptEnabled = sp.getBoolean("setJavaScriptEnabled", false);
        setJavaScriptCanOpenWindowsAutomatically = sp.getBoolean("setJavaScriptCanOpenWindowsAutomatically", false);
        setLoadsImagesAutomatically = sp.getBoolean("setLoadsImagesAutomatically", true);

        wvAttr = new ArrayList<>();
        wvAttr.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "可垂直滑动", "setVerticalScrollBarEnabled()", setVerticalScrollBarEnabled));
        wvAttr.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "可水平滑动", "setHorizontalScrollBarEnabled()", setHorizontalScrollBarEnabled));

        wvSettings = new ArrayList<>();
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "将图片调整到适合WebView的大小", "setUseWideViewPort()", setUseWideViewPort));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "缩放至屏幕的大小", "setLoadWithOverviewMode()", setLoadWithOverviewMode));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持缩放", "setSupportZoom() + setBuiltInZoomControls()", setZoom));
//        items.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "设置文本的缩放倍数", "setTextZoom()"));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "隐藏原生的缩放控件", "setDisplayZoomControls()", setDisplayZoomControls));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "设置可以访问文件", "setAllowFileAccess()", setAllowFileAccess));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "当WebView调用requestFocus时为WebView设置节点", "setNeedInitialFocus()", setNeedInitialFocus));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持JS", "setJavaScriptEnabled()", setJavaScriptEnabled));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持通过JS打开新窗口", "setJavaScriptCanOpenWindowsAutomatically()", setJavaScriptCanOpenWindowsAutomatically));
        wvSettings.add(new MultiStyleItem(MultiStyleRVAdapter.SWITCH, "支持自动加载图片", "setLoadsImagesAutomatically()", setLoadsImagesAutomatically));

    }

    private void initComponent() {
        sv = new Server(getActivity(), new AsyncResponse() {
            @Override
            public void onFailure(int errorCode) {
                CLog.Companion.e(TAG, "LocalBroadCast error:" + errorCode);
            }
        });
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
        adapter = new MultiStyleRVAdapter(getActivity(), null, null, new OnMultiItemClickListener() {
            @Override
            public void onItemClick(View view, String title, int position) {

            }

            @Override
            public void onItemLongClick(View view, String title, int position) {

            }
        }, new OnMultiItemSelectListener() {
            @Override
            public void onItemSelect(String title, int position, boolean isSelect) {
                CLog.Companion.i(TAG, title + "[" + position + "], isSelect:" + isSelect);
                SharedPreferences.Editor editor = sp.edit();
                if (titles[0].equals(title)) {
                    Bundle b = new Bundle();
                    switch (position) {
                        case 0:
                            editor.putBoolean("setUseWideViewPort", isSelect);
                            editor.commit();
                            b.putBoolean("setUseWideViewPort", isSelect);
                            sv.pushBundle(Commands.WV_SETTINGS, b);
                            break;
                        case 1:
                            editor.putBoolean("setLoadWithOverviewMode", isSelect);
                            editor.commit();
                            b.putBoolean("setLoadWithOverviewMode", isSelect);
                            sv.pushBundle(Commands.WV_SETTINGS, b);
                            break;
                    }
                } else if (titles[1].equals(title)) {
                    Bundle b = new Bundle();
                    switch (position) {
                        case 0:
                            editor.putBoolean("setUseWideViewPort", isSelect);
                            editor.commit();
                            b.putBoolean("setUseWideViewPort", isSelect);
                            sv.pushBundle(Commands.WV_SETTINGS, b);
                            break;
                        case 1:
                            editor.putBoolean("setLoadWithOverviewMode", isSelect);
                            editor.commit();
                            b.putBoolean("setLoadWithOverviewMode", isSelect);
                            sv.pushBundle(Commands.WV_SETTINGS, b);
                            break;
                        case 2:
                            editor.putBoolean("setZoom", isSelect);
                            editor.commit();
                            b.putBoolean("setZoom", isSelect);
                            sv.pushBundle(Commands.WV_SETTINGS, b);
                            break;
                        case 3:
                            editor.putBoolean("setDisplayZoomControls", isSelect);
                            editor.commit();
                            b.putBoolean("setDisplayZoomControls", isSelect);
                            sv.pushBundle(Commands.WV_SETTINGS, b);
                            break;
                        case 4:
                            editor.putBoolean("setAllowFileAccess", isSelect);
                            editor.commit();
                            b.putBoolean("setAllowFileAccess", isSelect);
                            sv.pushBundle(Commands.WV_SETTINGS, b);
                            break;
                        case 5:
                            editor.putBoolean("setNeedInitialFocus", isSelect);
                            editor.commit();
                            b.putBoolean("setNeedInitialFocus", isSelect);
                            sv.pushBundle(Commands.WV_SETTINGS, b);
                            break;
                        case 6:
                            editor.putBoolean("setJavaScriptEnabled", isSelect);
                            editor.commit();
                            b.putBoolean("setJavaScriptEnabled", isSelect);
                            sv.pushBundle(Commands.WV_SETTINGS, b);
                            break;
                        case 7:
                            editor.putBoolean("setJavaScriptCanOpenWindowsAutomatically", isSelect);
                            editor.commit();
                            b.putBoolean("setJavaScriptCanOpenWindowsAutomatically", isSelect);
                            sv.pushBundle(Commands.WV_SETTINGS, b);
                            break;
                        case 8:
                            editor.putBoolean("setLoadsImagesAutomatically", isSelect);
                            editor.commit();
                            b.putBoolean("setLoadsImagesAutomatically", isSelect);
                            sv.pushBundle(Commands.WV_SETTINGS, b);
                            break;
                    }
                }
            }
        });
        adapter.mergeList(titles[0], wvAttr);
        adapter.mergeList(titles[1], wvSettings);
        rv_main_list.setAdapter(adapter);
    }
}
