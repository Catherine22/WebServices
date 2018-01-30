package com.catherine.webservices.fragments;

import android.content.DialogInterface;
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
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.entities.TextCard;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.network.HttpRequest;
import com.catherine.webservices.network.HttpResponse;
import com.catherine.webservices.network.HttpResponseListener;
import com.catherine.webservices.network.MyApache;
import com.catherine.webservices.network.NetworkHealthListener;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.toolkits.CLog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Catherine on 2017/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class ApacheFragment extends LazyFragment {
    public final static String TAG = ApacheFragment.class.getSimpleName();
    private List<TextCard> entities;
    private SwipeRefreshLayout srl_container;
    private Handler networkTask;
    private MyApache myApache;
    private Callback callback;
    private TextCardRVAdapter adapter;
    private HandlerThread handlerThreadB, handlerThreadC;
    private MainInterface mainInterface;
    private NetworkHealthListener networkHealthListener;
    private boolean retry;
    private int step;

    public static ApacheFragment newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        ApacheFragment fragment = new ApacheFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_apache);
        fillInData();
        initComponent();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void fillInData() {
        entities = new ArrayList<>();
        entities.add(new TextCard("GET " + Constants.HOST + " in looper A", "Connect to the server with user-defined headers", null));
        entities.add(new TextCard("POST " + Constants.HOST + " in looper A", "Connect to the server with correct account", null));
        entities.add(new TextCard("POST " + Constants.HOST + " in looper A", "Connect to the server with false Authorization", null));
        entities.add(new TextCard("POST " + Constants.HOST + " in looper B", "Connect to the server with false account", null));
        entities.add(new TextCard("GET http://dictionary.cambridge.org/ in looper C", "Connect to Cambridge dictionary server", null));
        entities.add(new TextCard("GET " + Constants.GITHUB_API_DOMAIN + " in looper C", "Connect to GitHub api with gzip encoding", null));
    }

    private void initComponent() {
        mainInterface = (MainInterface) getActivity();
        networkHealthListener = new NetworkHealthListener() {
            @Override
            public void networkConnected(@NotNull String type) {
                CLog.i(TAG, "network connected:" + type);
                if (retry) {
                    retry = false;
                    connect(step);
                }
            }

            @Override
            public void networkDisable() {
                CLog.e(TAG, "network disable");
            }
        };
        mainInterface.listenToNetworkState(networkHealthListener);
        myApache = new MyApache();
        callback = new Callback();
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
        rv_main_list.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        adapter = new TextCardRVAdapter(getActivity(), entities, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                connect(position);
            }

            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);
        adapter.setFromHtml(true);
    }

    private void connect(final int position) {
        step = position;
        retry = false;
        switch (position) {
            case 0:
                srl_container.setRefreshing(true);
                networkTask.post(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, String> headers = MyApache.getDefaultHeaders();
                        headers.put("h1", "Hi there!");
                        headers.put("h2", "I am a mobile phone.");
                        headers.put("Authorization", Constants.AUTHORIZATION);
                        callback.setPosition(position);
                        myApache.doGet(new HttpRequest.Builder()
                                .url(Constants.HOST + "LoginServlet?name=zhangsan&password=123456")
                                .headers(headers)
                                .listener(callback)
                                .build());
                    }
                });
                break;
            case 1:
                srl_container.setRefreshing(true);
                networkTask.post(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, String> headers = MyApache.getDefaultHeaders();
                        headers.put("Authorization", Constants.AUTHORIZATION);

                        List<NameValuePair> nameValuePairs = new ArrayList<>();
                        nameValuePairs.add(new BasicNameValuePair("name", "zhangsan"));
                        nameValuePairs.add(new BasicNameValuePair("password", "123456"));
                        callback.setPosition(position);
                        myApache.doPost(nameValuePairs, new HttpRequest.Builder()
                                .url(Constants.HOST + "LoginServlet")
                                .headers(headers)
                                .listener(callback)
                                .build());
                    }
                });
                break;
            case 2:
                srl_container.setRefreshing(true);
                networkTask.post(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, String> headers = MyApache.getDefaultHeaders();
                        headers.put("Authorization", "12345");
                        List<NameValuePair> nameValuePairs = new ArrayList<>();
                        nameValuePairs.add(new BasicNameValuePair("name", "zhangsan"));
                        nameValuePairs.add(new BasicNameValuePair("password", "123456"));
                        callback.setPosition(position);
                        myApache.doPost(nameValuePairs, new HttpRequest.Builder()
                                .url(Constants.HOST + "LoginServlet")
                                .headers(headers)
                                .listener(callback)
                                .build());
                    }
                });
                break;
            case 3:
                srl_container.setRefreshing(true);
                Handler networkTaskB = new Handler(handlerThreadB.getLooper());
                networkTaskB.post(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, String> headers = MyApache.getDefaultHeaders();
                        headers.put("Authorization", Constants.AUTHORIZATION);

                        List<NameValuePair> nameValuePairs = new ArrayList<>();
                        nameValuePairs.add(new BasicNameValuePair("name", ""));
                        nameValuePairs.add(new BasicNameValuePair("password", ""));
                        callback.setPosition(position);
                        myApache.doPost(nameValuePairs, new HttpRequest.Builder()
                                .url(Constants.HOST + "LoginServlet")
                                .headers(headers)
                                .listener(callback)
                                .build());
                    }
                });
                break;
            case 4:
                srl_container.setRefreshing(true);
                Handler networkTaskC = new Handler(handlerThreadC.getLooper());
                networkTaskC.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.setPosition(position);
                        myApache.doGet(new HttpRequest.Builder()
                                .url("http://dictionary.cambridge.org/dictionary/english-chinese-simplified/philosopher")
                                .listener(callback)
                                .build());

                    }
                });
                break;
            case 5:
                srl_container.setRefreshing(true);
                Handler networkTaskC2 = new Handler(handlerThreadC.getLooper());
                networkTaskC2.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.setPosition(position);
                        myApache.doGet(new HttpRequest.Builder()
                                .url(Constants.GITHUB_API_DOMAIN + "users/Catherine22/repos")
                                .listener(callback)
                                .build());

                    }
                });
                break;
        }
    }


    private class Callback implements HttpResponseListener {
        private int position;

        void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void connectSuccess(HttpResponse response) {
            //Running in a non-UI thread right now.
            CLog.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
            TextCard tc = entities.get(position);
            tc.contents = String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody());
            entities.set(position, tc);
            adapter.setEntities(entities);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    srl_container.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void connectFailure(HttpResponse response, Exception e) {
            //Running in a non-UI thread right now.
            StringBuilder sb = new StringBuilder();
            if (!NetworkHelper.isNetworkHealthy()) {
                DialogManager.showAlertDialog(getActivity(), "Please turn on Wi-Fi or cellular.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                sb.append("retry...");
                retry = true;
            } else {
                sb.append(String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                CLog.e(TAG, sb.toString());
                if (e != null) {
                    sb.append("\n");
                    sb.append(e.getMessage());
                    CLog.e(TAG, e.getMessage());

                    if (e instanceof SocketTimeoutException) {
                        DialogManager.showAlertDialog(getActivity(), "Connection timeout. Please check your server.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                    }
                }
            }
            TextCard tc = entities.get(position);
            tc.contents = sb.toString();
            entities.set(position, tc);
            adapter.setEntities(entities);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    srl_container.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }


    @Override
    public void onDestroy() {
        if (mainInterface != null)
            mainInterface.stopListeningToNetworkState(networkHealthListener);
        super.onDestroy();
    }
}