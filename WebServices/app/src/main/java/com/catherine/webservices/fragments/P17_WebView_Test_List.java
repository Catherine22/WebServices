package com.catherine.webservices.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.SimpleStyleRVAdapter;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.entities.ImageCardEx;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnMultiItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.toolkits.CLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P17_WebView_Test_List extends LazyFragment {
    public final static String TAG = "P17_WebView_Test_List";
    private SwipeRefreshLayout srl_container;
    private MainInterface mainInterface;
    private SimpleStyleRVAdapter adapter;

    private List<ImageCardEx> jsList, intentList, userDefinedSchemeList, sslList, h5List, others;
    private final String titleIntent = "Intent links";
    private final String titleScheme = "User-defined scheme";
    private final String titleJs = "JavaScript";
    private final String titleSSL = "SSL";
    private final String titleH5 = "HTML5";
    private final String titleOthers = "Others";

    public static P17_WebView_Test_List newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P17_WebView_Test_List fragment = new P17_WebView_Test_List();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_17_webview_test_list);
        mainInterface = (MainInterface) getActivity();
        init();
    }


    private void init() {
        mainInterface.getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                fillInData();
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
        srl_container = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
                srl_container.setRefreshing(false);
            }
        });

        RecyclerView rv_url_list = (RecyclerView) findViewById(R.id.rv_url_list);
//        rv_main_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.Companion.getVERTICAL_LIST()));
        rv_url_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SimpleStyleRVAdapter(getActivity(), null, null, new OnMultiItemClickListener() {
            @Override
            public void onItemClick(View view, String title, int position) {
                CLog.Companion.i(TAG, "title:" + title + ",click:" + position);
                Bundle b = new Bundle();
                if (titleIntent.equals(title)) {
                    b.putString("url", intentList.get(position).getSubtitle());
                } else if (titleH5.equals(title)) {
                    b.putString("url", h5List.get(position).getSubtitle());
                } else if (titleJs.equals(title)) {
                    b.putString("url", jsList.get(position).getSubtitle());
                } else if (titleScheme.equals(title)) {
                    b.putString("url", userDefinedSchemeList.get(position).getSubtitle());
                } else if (titleSSL.equals(title)) {
                    b.putString("url", sslList.get(position).getSubtitle());
                } else if (titleOthers.equals(title)) {
                    b.putString("url", others.get(position).getSubtitle());
                }

                mainInterface.callFragment(Constants.P14_FULL_WEBVIEW, b);
            }

            @Override
            public void onItemLongClick(View view, String title, final int position) {
            }
        });
        setList();
        rv_url_list.setAdapter(adapter);
    }

    private void setList() {
        jsList = new ArrayList<>();
        intentList = new ArrayList<>();
        userDefinedSchemeList = new ArrayList<>();
        sslList = new ArrayList<>();
        h5List = new ArrayList<>();
        others = new ArrayList<>();

        ImageCardEx imageCardEx2 = new ImageCardEx();
        imageCardEx2.setTitle("play.google.com");
        imageCardEx2.setSubtitle("https://play.google.com/store/apps/details?id=com.google.android.apps.maps&amp;hl=en");
        imageCardEx2.setStyle(0);
        intentList.add(imageCardEx2);
        adapter.mergeList(titleIntent, intentList);

        ImageCardEx imageCardEx3 = new ImageCardEx();
        imageCardEx3.setTitle("market://");
        imageCardEx3.setSubtitle("market://details?id=com.google.android.apps.maps");
        imageCardEx3.setStyle(0);
        userDefinedSchemeList.add(imageCardEx3);
        adapter.mergeList(titleScheme, userDefinedSchemeList);

        ImageCardEx imageCardEx4 = new ImageCardEx();
        imageCardEx4.setTitle("javascript.com");
        imageCardEx4.setSubtitle("https://www.javascript.com/");
        imageCardEx4.setStyle(0);
        jsList.add(imageCardEx4);
        ImageCardEx imageCardEx5 = new ImageCardEx();
        imageCardEx5.setTitle("js alert");
        imageCardEx5.setSubtitle("file:///android_asset/js_alert.html");
        imageCardEx5.setStyle(0);
        jsList.add(imageCardEx5);
        ImageCardEx imageCardEx6 = new ImageCardEx();
        imageCardEx6.setTitle("js confirm");
        imageCardEx6.setSubtitle("file:///android_asset/js_confirm.html");
        imageCardEx6.setStyle(0);
        jsList.add(imageCardEx6);
        ImageCardEx imageCardEx7 = new ImageCardEx();
        imageCardEx7.setTitle("js prompt");
        imageCardEx7.setSubtitle("file:///android_asset/js_prompt.html");
        imageCardEx7.setStyle(0);
        jsList.add(imageCardEx7);
        ImageCardEx imageCardEx8 = new ImageCardEx();
        imageCardEx8.setTitle("JavaScriptInterface");
        imageCardEx8.setSubtitle("file:///android_asset/js_command.html");
        imageCardEx8.setStyle(0);
        jsList.add(imageCardEx8);
        adapter.mergeList(titleJs, jsList);

        ImageCardEx imageCardEx9 = new ImageCardEx();
        imageCardEx9.setTitle("12306.cn");
        imageCardEx9.setSubtitle("https://kyfw.12306.cn/otn/regist/init");
        imageCardEx9.setStyle(0);
        sslList.add(imageCardEx9);
        adapter.mergeList(titleSSL, sslList);

        ImageCardEx imageCardEx10 = new ImageCardEx();
        imageCardEx10.setTitle("html5test.com");
        imageCardEx10.setSubtitle("http://html5test.com/");
        imageCardEx10.setStyle(0);
        h5List.add(imageCardEx10);
        adapter.mergeList(titleH5, h5List);

        ImageCardEx imageCardEx0 = new ImageCardEx();
        imageCardEx0.setTitle("github.com");
        imageCardEx0.setSubtitle("https://github.com/Catherine22");
        imageCardEx0.setStyle(0);
        others.add(imageCardEx0);
        ImageCardEx imageCardEx1 = new ImageCardEx();
        imageCardEx1.setTitle("m.facebook.com");
        imageCardEx1.setSubtitle("https://m.facebook.com/100014541792899");
        imageCardEx1.setStyle(0);
        others.add(imageCardEx1);
        adapter.mergeList(titleOthers, others);
    }
}
