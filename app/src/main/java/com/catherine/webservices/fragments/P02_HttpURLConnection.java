package com.catherine.webservices.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.CardRVAdapter;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.network.HttpAsyncTask;
import com.catherine.webservices.network.HttpRequest;
import com.catherine.webservices.network.HttpResponse;
import com.catherine.webservices.network.HttpResponseListener;
import com.catherine.webservices.network.MyHttpURLConnection;
import com.catherine.webservices.toolkits.CLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by Catherine on 2017/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P02_HttpURLConnection extends LazyFragment {
    public final static String TAG = "P02_HttpURLConnection";
    private List<String> features;
    private List<String> descriptions;
    private SwipeRefreshLayout srl_container;
    private MainInterface mainInterface;
    private CardRVAdapter adapter;

    public static P02_HttpURLConnection newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P02_HttpURLConnection fragment = new P02_HttpURLConnection();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_02_http_url_connection);
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
        features.add("HttpGet");
        features.add("HttpPost");


        descriptions = new ArrayList<>();
        descriptions.add("Set the method for the URL request.");
        descriptions.add("Set the method for the URL request.");
    }

    private void initComponent() {
        srl_container = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CLog.Companion.d(TAG, "refresh");
                srl_container.setRefreshing(false);
            }
        });

        RecyclerView rv_main_list = (RecyclerView) findViewById(R.id.rv_main_list);
//        rv_main_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.Companion.getVERTICAL_LIST()));
        rv_main_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CardRVAdapter(getActivity(), null, features, descriptions, new OnItemClickListener() {
            @Override
            public void onItemClick(@NotNull View view, int position) {
                switch (position) {
                    case 0:
                        Map<String, String> h1 = MyHttpURLConnection.getDefaultHeaders();
                        h1.put("h1", "Hi there!");
                        h1.put("h2", "I am a mobile phone.");
                        HttpRequest request0 = new HttpRequest(new HttpRequest.Builder()
                                .url(String.format(Locale.ENGLISH, "%sLoginServlet?name=zhangsan&password=123456", Constants.HOST))
                                .headers(h1)
                                .listener(new HttpResponseListener() {
                                    @Override
                                    public void connectSuccess(HttpResponse response) {
                                        CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                    }

                                    @Override
                                    public void connectFailure(HttpResponse response, Exception e) {
                                        CLog.Companion.e(TAG, String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, error:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                                        if (e != null)
                                            CLog.Companion.e(TAG, e.getMessage());
                                    }
                                }));
                        new HttpAsyncTask(request0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        HttpRequest request1 = new HttpRequest(new HttpRequest.Builder()
                                .url("http://dictionary.cambridge.org/zhs/%E6%90%9C%E7%B4%A2/%E8%8B%B1%E8%AF%AD-%E6%B1%89%E8%AF%AD-%E7%AE%80%E4%BD%93/direct/?q=philosopher")
                                .listener(new HttpResponseListener() {
                                    @Override
                                    public void connectSuccess(HttpResponse response) {
                                        CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                    }

                                    @Override
                                    public void connectFailure(HttpResponse response, Exception e) {
                                        CLog.Companion.e(TAG, String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, error:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                                        if (e != null)
                                            CLog.Companion.e(TAG, e.getMessage());
                                    }
                                })
                        );
                        new HttpAsyncTask(request1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                    case 1:
                        Map<String, String> h2 = MyHttpURLConnection.getDefaultHeaders();
                        h2.put("Authorization", "12345");
                        Map<String, String> body = new HashMap<>();
                        body.put("name", "zhangsan");
                        body.put("password", "123456");

                        HttpRequest request2 = new HttpRequest(new HttpRequest.Builder()
                                .url(String.format(Locale.ENGLISH, "%sLoginServlet", Constants.HOST))
                                .headers(h2)
                                .body(MyHttpURLConnection.getSimpleStringBody(body))
                                .listener(new HttpResponseListener() {
                                    @Override
                                    public void connectSuccess(HttpResponse response) {
                                        CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                    }

                                    @Override
                                    public void connectFailure(HttpResponse response, Exception e) {
                                        CLog.Companion.e(TAG, String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, error:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                                        if (e != null)
                                            CLog.Companion.e(TAG, e.getMessage());
                                    }
                                })
                        );
                        new HttpAsyncTask(request2, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                        body.put("name", "");
                        body.put("password", "");
                        HttpRequest request3 = new HttpRequest(new HttpRequest.Builder()
                                .url(String.format(Locale.ENGLISH, "%sLoginServlet", Constants.HOST))
                                .body(MyHttpURLConnection.getSimpleStringBody(body))
                                .listener(new HttpResponseListener() {
                                    @Override
                                    public void connectSuccess(HttpResponse response) {
                                        CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                    }

                                    @Override
                                    public void connectFailure(HttpResponse response, Exception e) {
                                        CLog.Companion.e(TAG, String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, error:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                                        if (e != null)
                                            CLog.Companion.e(TAG, e.getMessage());
                                    }
                                })
                        );
                        new HttpAsyncTask(request3).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                        body.put("name", "zhangsan");
                        body.put("password", "123456");
                        HttpRequest request4 = new HttpRequest(new HttpRequest.Builder()
                                .url(String.format(Locale.ENGLISH, "%sLoginServlet", Constants.HOST))
                                .body(MyHttpURLConnection.getSimpleStringBody(body))
                                .listener(new HttpResponseListener() {
                                    @Override
                                    public void connectSuccess(HttpResponse response) {
                                        CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                    }

                                    @Override
                                    public void connectFailure(HttpResponse response, Exception e) {
                                        CLog.Companion.e(TAG, String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, error:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                                        if (e != null)
                                            CLog.Companion.e(TAG, e.getMessage());
                                    }
                                })
                        );
                        new HttpAsyncTask(request4).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                }
            }

            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);
    }
}