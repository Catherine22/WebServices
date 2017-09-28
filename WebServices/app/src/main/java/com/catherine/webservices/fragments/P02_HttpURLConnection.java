package com.catherine.webservices.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.interfaces.OnItemClickListener;
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
    private List<String> features, descriptions, contents;
    private SwipeRefreshLayout srl_container;
    private TextCardRVAdapter adapter;

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
        fillInData();
        initComponent();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void fillInData() {
        features = new ArrayList<>();
        contents = new ArrayList<>();
        descriptions = new ArrayList<>();
        features.add("HttpGet in AsyncTask");
        features.add("HttpPost in AsyncTask");
        features.add("HttpPost in AsyncTask");
        features.add("HttpPost in AsyncTask");
        features.add("HttpGet in AsyncTask");
        descriptions.add("Connect to the server with user-defined headers");
        descriptions.add("Connect to the server with correct account");
        descriptions.add("Connect to the server with false Authorization");
        descriptions.add("Connect to the server with false account");
        descriptions.add("Connect to Cambridge dictionary server");
        for (int i = 0; i < features.size(); i++) {
            contents.add("");
        }
    }

    private void initComponent() {
        srl_container = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fillInData();
                initComponent();
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
                        Map<String, String> h0 = MyHttpURLConnection.getDefaultHeaders();
                        h0.put("h1", "Hi there!");
                        h0.put("h2", "I am a mobile phone.");
                        HttpRequest request0 = new HttpRequest.Builder()
                                .url(String.format(Locale.ENGLISH, "%sLoginServlet?name=zhangsan&password=123456", Constants.HOST))
                                .headers(h0)
                                .listener(buildListener(position))
                                .build();
                        new HttpAsyncTask(request0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                    case 1:
                        Map<String, String> body1 = new HashMap<>();
                        body1.put("name", "zhangsan");
                        body1.put("password", "123456");
                        HttpRequest r1 = new HttpRequest.Builder()
                                .url(String.format(Locale.ENGLISH, "%sLoginServlet", Constants.HOST))
                                .body(MyHttpURLConnection.getSimpleStringBody(body1))
                                .listener(buildListener(position))
                                .build();
                        new HttpAsyncTask(r1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                    case 2:
                        Map<String, String> h2 = MyHttpURLConnection.getDefaultHeaders();
                        h2.put("Authorization", "12345");
                        Map<String, String> body2 = new HashMap<>();
                        body2.put("name", "zhangsan");
                        body2.put("password", "123456");

                        HttpRequest r2 = new HttpRequest.Builder()
                                .url(String.format(Locale.ENGLISH, "%sLoginServlet", Constants.HOST))
                                .headers(h2)
                                .body(MyHttpURLConnection.getSimpleStringBody(body2))
                                .listener(buildListener(position))
                                .build();
                        new HttpAsyncTask(r2, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        break;
                    case 3:
                        Map<String, String> body3 = new HashMap<>();
                        body3.put("name", "");
                        body3.put("password", "");
                        HttpRequest r3 = new HttpRequest.Builder()
                                .url(String.format(Locale.ENGLISH, "%sLoginServlet", Constants.HOST))
                                .body(MyHttpURLConnection.getSimpleStringBody(body3))
                                .listener(buildListener(position))
                                .build();
                        new HttpAsyncTask(r3).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                    case 4:
                        HttpRequest r4 = new HttpRequest.Builder()
                                .url("http://dictionary.cambridge.org/zhs/%E6%90%9C%E7%B4%A2/%E8%8B%B1%E8%AF%AD-%E6%B1%89%E8%AF%AD-%E7%AE%80%E4%BD%93/direct/?q=philosopher")
                                .listener(buildListener(position))
                                .build();
                        new HttpAsyncTask(r4).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                }
            }

            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);
    }

    private HttpResponseListener buildListener(final int position) {
        return new HttpResponseListener() {
            @Override
            public void connectSuccess(HttpResponse response) {
                CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                contents.set(position, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                adapter.setContents(contents);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void connectFailure(HttpResponse response, Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                CLog.Companion.e(TAG, sb.toString());
                if (e != null) {
                    sb.append("\n");
                    sb.append(e.getMessage());
                    CLog.Companion.e(TAG, e.getMessage());
                }
                contents.set(position, sb.toString());
                adapter.setContents(contents);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        };
    }
}