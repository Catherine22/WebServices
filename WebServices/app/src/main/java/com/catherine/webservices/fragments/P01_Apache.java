package com.catherine.webservices.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.network.HttpResponse;
import com.catherine.webservices.network.HttpResponseListener;
import com.catherine.webservices.network.MyApache;
import com.catherine.webservices.toolkits.CLog;

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
    private List<String> features, contents, desc;
    private SwipeRefreshLayout srl_container;
    private Handler networkTask;
    private MyApache myApache;
    private TextCardRVAdapter adapter;
    private HandlerThread handlerThreadB, handlerThreadC;

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
        contents = new ArrayList<>();
        desc = new ArrayList<>();
        features.add("HttpGet in looper A");
        features.add("HttpPost in looper A");
        features.add("HttpPost in looper A");
        features.add("HttpPost in looper B");
        features.add("HttpGet in looper C");
        desc.add("Connect to the server with user-defined headers");
        desc.add("Connect to the server with correct account");
        desc.add("Connect to the server with false Authorization");
        desc.add("Connect to the server with false account");
        desc.add("Connect to Cambridge dictionary server");
        for (int i = 0; i < features.size(); i++) {
            contents.add("");
        }
    }

    private void initComponent() {
        handlerThreadB = new HandlerThread("Looper B");
        handlerThreadB.start();

        handlerThreadC = new HandlerThread("Looper C");
        handlerThreadC.start();
        networkTask = new Handler(MyApplication.INSTANCE.calHandlerThread.getLooper());
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
        rv_main_list.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        adapter = new TextCardRVAdapter(getActivity(), null, features, desc, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                switch (position) {
                    case 0:
                        networkTask.post(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> headers = MyApache.getDefaultHeaders();
                                headers.put("h1", "Hi there!");
                                headers.put("h2", "I am a mobile phone.");
                                headers.put("Authorization", Constants.AUTHORIZATION);
                                setupApache(position);
                                myApache.doGet(Constants.HOST + "LoginServlet?name=zhangsan&password=123456", headers);
                            }
                        });
                        break;
                    case 1:
                        networkTask.post(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> headers = MyApache.getDefaultHeaders();
                                headers.put("Authorization", Constants.AUTHORIZATION);

                                List<NameValuePair> nameValuePairs = new ArrayList<>();
                                nameValuePairs.add(new BasicNameValuePair("name", "zhangsan"));
                                nameValuePairs.add(new BasicNameValuePair("password", "123456"));
                                setupApache(position);
                                myApache.doPost(Constants.HOST + "LoginServlet", headers, nameValuePairs);
                            }
                        });
                        break;
                    case 2:
                        networkTask.post(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> headers = MyApache.getDefaultHeaders();
                                headers.put("Authorization", "12345");
                                List<NameValuePair> nameValuePairs = new ArrayList<>();
                                nameValuePairs.add(new BasicNameValuePair("name", "zhangsan"));
                                nameValuePairs.add(new BasicNameValuePair("password", "123456"));
                                setupApache(position);
                                myApache.doPost(Constants.HOST + "LoginServlet", headers, nameValuePairs);
                            }
                        });
                        break;
                    case 3:
                        Handler networkTaskB = new Handler(handlerThreadB.getLooper());
                        networkTaskB.post(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> headers = MyApache.getDefaultHeaders();
                                headers.put("Authorization", Constants.AUTHORIZATION);

                                List<NameValuePair> nameValuePairs = new ArrayList<>();
                                nameValuePairs.add(new BasicNameValuePair("name", ""));
                                nameValuePairs.add(new BasicNameValuePair("password", ""));
                                setupApache(position);
                                myApache.doPost(Constants.HOST + "LoginServlet", headers, nameValuePairs);
                            }
                        });
                        break;
                    case 4:
                        Handler networkTaskC = new Handler(handlerThreadC.getLooper());
                        networkTaskC.post(new Runnable() {
                            @Override
                            public void run() {
                                setupApache(position);
                                myApache.doGet("http://dictionary.cambridge.org/dictionary/english-chinese-simplified/philosopher", null);

                            }
                        });
                        break;
                }
            }

            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);
    }

    private void setupApache(final int position) {
        myApache = new MyApache(new HttpResponseListener() {
            @Override
            public void connectSuccess(HttpResponse response) {
                //Running in a non-UI thread right now.
                CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                contents.set(position, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                adapter.setContents(contents);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void connectFailure(HttpResponse response, Exception e) {
                //Running in a non-UI thread right now.
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
        });
    }
}