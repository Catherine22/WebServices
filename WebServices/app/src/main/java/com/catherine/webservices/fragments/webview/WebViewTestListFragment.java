package com.catherine.webservices.fragments.webview;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.catherine.webservices.Commands;
import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.SimpleStyleRVAdapter;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.entities.ImageCardEx;
import com.catherine.webservices.fragments.LazyFragment;
import com.catherine.webservices.interfaces.BackKeyListener;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnMultiItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.toolkits.CLog;

import java.util.ArrayList;
import java.util.List;

import catherine.messagecenter.AsyncResponse;
import catherine.messagecenter.Server;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class WebViewTestListFragment extends LazyFragment {
    public final static String TAG = WebViewTestListFragment.class.getSimpleName();
    private SwipeRefreshLayout srl_container;
    private MainInterface mainInterface;
    private SimpleStyleRVAdapter adapter;

    private List<ImageCardEx> jsList, intentList, userDefinedSchemeList, sslList, h5List, loadDataList, others;
    private final String titleIntent = "Intent links";
    private final String titleScheme = "User-defined scheme";
    private final String titleJs = "JavaScript";
    private final String titleSSL = "SSL";
    private final String titleH5 = "HTML5";
    private final String titleLoadData = "Load Data With Base URL";
    private final String titleOthers = "Others";

    public static WebViewTestListFragment newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        WebViewTestListFragment fragment = new WebViewTestListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_webview_test_list);
        mainInterface = (MainInterface) getActivity();
        init();
    }


    private void init() {
        mainInterface.getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                mainInterface.setBackKeyListener(new BackKeyListener() {
                    @Override
                    public void OnKeyDown() {
                        if (getChildFragmentManager().getBackStackEntryCount() == 0) {
                            Server sv = new Server(getActivity(), new AsyncResponse() {
                                @Override
                                public void onFailure(int errorCode) {
                                    CLog.e(TAG, "onFailure:" + errorCode);
                                }
                            });
                            mainInterface.removeBackKeyListener();
                            sv.pushBoolean(Commands.BACK_TO_PREV, true);
                        } else {
                            getChildFragmentManager().popBackStack();
                        }
                    }
                });
                fillInData();
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
        rv_url_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SimpleStyleRVAdapter(getActivity(), null, null, new OnMultiItemClickListener() {
            @Override
            public void onItemClick(View view, String title, int position) {
                CLog.i(TAG, "title:" + title + ",click:" + position);
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
                } else if (titleLoadData.equals(title)) {
                    b.putString("loadData", loadDataList.get(position).getSubtitle());
                }

                callFragment(Constants.Fragments.F_NESTED_WEBVIEW, b);
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
        loadDataList = new ArrayList<>();
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

        ImageCardEx imageCardEx11 = new ImageCardEx();
        imageCardEx11.setTitle("Data includes html and js");
        imageCardEx11.setSubtitle("<html>\n" +
                "<body>\n" +
                "  <p>Before the script...</p>\n" +
                "  <script>\n" +
                "    alert( 'Hello, world!' );\n" +
                "  </script>\n" +
                "  <p>...After the script.</p>\n" +
                "</body>\n" +
                "</html>");
        imageCardEx11.setStyle(0);
        loadDataList.add(imageCardEx11);
        adapter.mergeList(titleLoadData, loadDataList);

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
        ImageCardEx imageCardEx12 = new ImageCardEx();
        imageCardEx12.setTitle("Geo location");
        imageCardEx12.setSubtitle("file:///android_asset/geo_location.html");
        imageCardEx12.setStyle(0);
        others.add(imageCardEx12);
        ImageCardEx imageCardEx13 = new ImageCardEx();
        imageCardEx13.setTitle("Get media permission");
        imageCardEx13.setSubtitle("file:///android_asset/media_permission.html");
        imageCardEx13.setStyle(0);
        others.add(imageCardEx13);
        ImageCardEx imageCardEx14 = new ImageCardEx();
        imageCardEx14.setTitle("Session storage");
        imageCardEx14.setSubtitle("file:///android_asset/session_storage.html");
        imageCardEx14.setStyle(0);
        others.add(imageCardEx14);
        ImageCardEx imageCardEx15 = new ImageCardEx();
        imageCardEx15.setTitle("Counter");
        imageCardEx15.setSubtitle("file:///android_asset/session_storage_counter.html");
        imageCardEx15.setStyle(0);
        others.add(imageCardEx15);
        ImageCardEx imageCardEx16 = new ImageCardEx();
        imageCardEx16.setTitle("Web SQL database");
        imageCardEx16.setSubtitle("file:///android_asset/web_sql_db.html");
        imageCardEx16.setStyle(0);
        others.add(imageCardEx16);
        ImageCardEx imageCardEx21 = new ImageCardEx();
        imageCardEx21.setTitle("Indexed database");
        imageCardEx21.setSubtitle("file:///android_asset/indexed_db.html");
        imageCardEx21.setStyle(0);
        others.add(imageCardEx21);
        ImageCardEx imageCardEx17 = new ImageCardEx();
        imageCardEx17.setTitle("File chooser");
        imageCardEx17.setSubtitle("file:///android_asset/file_chooser.html");
        imageCardEx17.setStyle(0);
        others.add(imageCardEx17);
        ImageCardEx imageCardEx18 = new ImageCardEx();
        imageCardEx18.setTitle("Non-cached Resources");
        imageCardEx18.setSubtitle("http://appcache-demo.s3-website-us-east-1.amazonaws.com/without-network/");
        imageCardEx18.setStyle(0);
        others.add(imageCardEx18);
        adapter.mergeList(titleOthers, others);
    }

    private void callFragment(int id, Bundle bundle) {
        CLog.d(TAG, "call " + id);
        Fragment fragment = null;
        String tag = "";
        String title = "";
        switch (id) {
            case Constants.Fragments.F_NESTED_WEBVIEW:
                title = "P13_Nested_WebView";
                fragment = NestedWebViewFragment.newInstance(true);
                fragment.setArguments(bundle);
                tag = "P13";
                break;
        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fl_container, fragment, tag);
        transaction.addToBackStack(title);
        transaction.commitAllowingStateLoss();
    }
}
