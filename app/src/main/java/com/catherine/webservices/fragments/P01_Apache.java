package com.catherine.webservices.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.RVAdapter;
import com.catherine.webservices.network.HttpResponseListener;
import com.catherine.webservices.network.MyApache;
import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.views.DividerItemDecoration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Catherine on 2017/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P01_Apache extends LazyFragment {
    public final static String TAG = "P01_Apache";
    private List<String> features;
    private SwipeRefreshLayout srl_container;
    private MyApache myApache;

    public static P01_Apache newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P01_Apache fragment = new P01_Apache();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_01_apache);
        fillInData();
        initComponent();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void fillInData() {
        features = new ArrayList<>();
        features.add("HttpGet");
        features.add("HttpPost");
        for (int i = 2; i < 50; i++)
            features.add("" + i);
    }

    private void initComponent() {
        myApache = new MyApache(new HttpResponseListener() {
            @Override
            public void connectSuccess(int code, @NotNull String message, @NotNull String body) {
                CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", code, message, body));
            }

            @Override
            public void connectFailure(int code, @NotNull String message, @NotNull String errorStream, @org.jetbrains.annotations.Nullable Exception e) {
                CLog.Companion.e(TAG, String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, body:%s", code, message, errorStream));
                if (e != null)
                    CLog.Companion.e(TAG, e.getMessage());
            }
        });
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
        rv_main_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.Companion.getVERTICAL_LIST()));
        rv_main_list.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        rv_main_list.setAdapter(new RVAdapter(getActivity(), features, new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(@NotNull View view, int position) {
                switch (position) {
                    case 0:
                        Handler networkTask = new Handler(MyApplication.INSTANCE.calHandlerThread.getLooper());
                        networkTask.post(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> headers = MyApache.getDefaultHeaders();
                                headers.put("h1", "Hi there!");
                                headers.put("h2", "I am a mobile phone.");
                                myApache.doGet(Constants.HOST + "LoginServlet?name=zhangsan&password=123456", headers);
                                myApache.doGet("http://dictionary.cambridge.org/zhs/%E6%90%9C%E7%B4%A2/%E8%8B%B1%E8%AF%AD-%E6%B1%89%E8%AF%AD-%E7%AE%80%E4%BD%93/direct/?q=philosopher");
                            }
                        });
                        break;
                    case 1:
                        Handler networkTask1 = new Handler(MyApplication.INSTANCE.calHandlerThread.getLooper());
                        networkTask1.post(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> headers = MyApache.getDefaultHeaders();
                                headers.put("Authorization", "12345");
                                List<NameValuePair> nameValuePairs = new ArrayList<>();
                                nameValuePairs.add(new BasicNameValuePair("name", "zhangsan"));
                                nameValuePairs.add(new BasicNameValuePair("password", "123456"));
                                myApache.doPost(Constants.HOST + "LoginServlet", headers, nameValuePairs);
                            }
                        });

                        networkTask1.post(new Runnable() {
                            @Override
                            public void run() {
                                List<NameValuePair> nameValuePairs = new ArrayList<>();
                                nameValuePairs.add(new BasicNameValuePair("name", ""));
                                nameValuePairs.add(new BasicNameValuePair("password", ""));
                                myApache.doPost(Constants.HOST + "LoginServlet", nameValuePairs);
                            }
                        });


                        networkTask1.post(new Runnable() {
                            @Override
                            public void run() {
                                List<NameValuePair> nameValuePairs = new ArrayList<>();
                                nameValuePairs.add(new BasicNameValuePair("name", "zhangsan"));
                                nameValuePairs.add(new BasicNameValuePair("password", "123456"));
                                myApache.doPost(Constants.HOST + "LoginServlet", nameValuePairs);
                            }
                        });
                        break;
                }
            }

            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }
        }));
    }

}