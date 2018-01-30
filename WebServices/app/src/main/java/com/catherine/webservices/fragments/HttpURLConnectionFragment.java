package com.catherine.webservices.fragments;

import android.content.DialogInterface;
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
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.entities.TextCard;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.network.HttpAsyncTask;
import com.catherine.webservices.network.HttpRequest;
import com.catherine.webservices.network.HttpResponse;
import com.catherine.webservices.network.HttpResponseListener;
import com.catherine.webservices.network.MyHttpURLConnection;
import com.catherine.webservices.network.NetworkHealthListener;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.security.CertificatesManager;
import com.catherine.webservices.toolkits.CLog;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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

public class HttpURLConnectionFragment extends LazyFragment {
    public final static String TAG = HttpURLConnectionFragment.class.getSimpleName();
    private List<TextCard> entities;
    private SwipeRefreshLayout srl_container;
    private TextCardRVAdapter adapter;
    private MainInterface mainInterface;
    private NetworkHealthListener networkHealthListener;
    private boolean retry;
    private int step;

    public static HttpURLConnectionFragment newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        HttpURLConnectionFragment fragment = new HttpURLConnectionFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_http_url_connection);
        fillInData();
        initComponent();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void fillInData() {
        entities = new ArrayList<>();
        entities.add(new TextCard("GET " + Constants.HOST, "Connect to the server with user-defined headers", null));
        entities.add(new TextCard("POST " + Constants.HOST, "Connect to the server with correct account", null));
        entities.add(new TextCard("POST " + Constants.HOST, "Connect to the server with false Authorization", null));
        entities.add(new TextCard("POST " + Constants.HOST, "Connect to the server with false account", null));
        entities.add(new TextCard("GET http://dictionary.cambridge.org/", "Connect to Cambridge dictionary server", null));
        entities.add(new TextCard("GET " + Constants.GITHUB_API_DOMAIN, "Connect to GitHub api with gzip encoding", null));
        entities.add(new TextCard("https://kyfw.12306.cn/otn/regist/init", "Connect to untrusted url with imported certificate", null));
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
        rv_main_list.setLayoutManager(new LinearLayoutManager(getActivity()));
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

    private void connect(int position) {
        retry = false;
        step = position;
        switch (position) {
            case 0:
                srl_container.setRefreshing(true);
                Map<String, String> h0 = MyHttpURLConnection.getDefaultHeaders();
                h0.put("h1", "Hi there!");
                h0.put("h2", "I am a mobile phone.");
                h0.put("Authorization", Constants.AUTHORIZATION);
                HttpRequest request0 = new HttpRequest.Builder()
                        .url(String.format(Locale.ENGLISH, "%sLoginServlet?name=zhangsan&password=123456", Constants.HOST))
                        .headers(h0)
                        .listener(listener(position))
                        .build();
                new HttpAsyncTask(request0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case 1:
                srl_container.setRefreshing(true);
                Map<String, String> h1 = MyHttpURLConnection.getDefaultHeaders();
                h1.put("Authorization", Constants.AUTHORIZATION);

                Map<String, String> body1 = new HashMap<>();
                body1.put("name", "zhangsan");
                body1.put("password", "123456");
                HttpRequest r1 = new HttpRequest.Builder()
                        .url(String.format(Locale.ENGLISH, "%sLoginServlet", Constants.HOST))
                        .headers(h1)
                        .body(MyHttpURLConnection.getSimpleStringBody(body1))
                        .listener(listener(position))
                        .build();
                new HttpAsyncTask(r1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case 2:
                srl_container.setRefreshing(true);
                Map<String, String> h2 = MyHttpURLConnection.getDefaultHeaders();
                h2.put("Authorization", "12345");
                Map<String, String> body2 = new HashMap<>();
                body2.put("name", "zhangsan");
                body2.put("password", "123456");

                HttpRequest r2 = new HttpRequest.Builder()
                        .url(String.format(Locale.ENGLISH, "%sLoginServlet", Constants.HOST))
                        .headers(h2)
                        .body(MyHttpURLConnection.getSimpleStringBody(body2))
                        .listener(listener(position))
                        .build();
                new HttpAsyncTask(r2, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case 3:
                srl_container.setRefreshing(true);
                Map<String, String> h3 = MyHttpURLConnection.getDefaultHeaders();
                h3.put("Authorization", Constants.AUTHORIZATION);

                Map<String, String> body3 = new HashMap<>();
                body3.put("name", "");
                body3.put("password", "");
                HttpRequest r3 = new HttpRequest.Builder()
                        .url(String.format(Locale.ENGLISH, "%sLoginServlet", Constants.HOST))
                        .headers(h3)
                        .body(MyHttpURLConnection.getSimpleStringBody(body3))
                        .listener(listener(position))
                        .build();
                new HttpAsyncTask(r3).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case 4:
                srl_container.setRefreshing(true);
                HttpRequest r4 = new HttpRequest.Builder()
                        .url("http://dictionary.cambridge.org/dictionary/english-chinese-simplified/philosopher")
                        .listener(listener(position))
                        .build();
                new HttpAsyncTask(r4).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case 5:
                srl_container.setRefreshing(true);
                try {
                    X509Certificate cert = CertificatesManager.pemToX509Certificate(Constants.GITHUB_CERT);
                    CertificatesManager.printCertificatesInfo(cert);
                    HttpRequest r5 = new HttpRequest.Builder()
                            .url(Constants.GITHUB_API_DOMAIN + "users/Catherine22/repos")
                            .certificate(cert)
                            .listener(listener(position))
                            .build();
                    new HttpAsyncTask(r5).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } catch (Exception e) {
                    e.printStackTrace();
                    srl_container.setRefreshing(false);
                    TextCard tc = entities.get(position);
                    tc.contents = String.format(Locale.ENGLISH, "connectFailure Exception:%s", e.toString());
                    entities.set(position, tc);
                    adapter.setEntities(entities);
                    adapter.notifyDataSetChanged();
                }
                break;
            case 6:
                srl_container.setRefreshing(true);
                try {
                    BufferedInputStream bis = new BufferedInputStream(getActivity().getAssets().open("srca.cer"));
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    X509Certificate cert = (X509Certificate) cf.generateCertificate(bis);
                    bis.close();

                    //show certificate info
                    CertificatesManager.printCertificatesInfo(cert);
                    HttpRequest r6 = new HttpRequest.Builder()
                            .url("https://kyfw.12306.cn/otn/regist/init")
                            .certificate(cert)
                            .listener(listener(position))
                            .build();
                    new HttpAsyncTask(r6).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } catch (Exception e) {
                    e.printStackTrace();
                    srl_container.setRefreshing(false);
                    TextCard tc = entities.get(position);
                    tc.contents = String.format(Locale.ENGLISH, "connectFailure Exception:%s", e.toString());
                    entities.set(position, tc);
                    adapter.setEntities(entities);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private HttpResponseListener listener(final int position) {
        return new HttpResponseListener() {
            @Override
            public void connectSuccess(HttpResponse response) {
                srl_container.setRefreshing(false);
                String body = response.getBody();
                CLog.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), body));

                TextCard tc = entities.get(position);
                tc.contents = String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), body);
                entities.set(position, tc);
                adapter.setEntities(entities);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void connectFailure(HttpResponse response, Exception e) {
                srl_container.setRefreshing(false);
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
                adapter.notifyDataSetChanged();
            }
        };
    }


    @Override
    public void onDestroy() {
        mainInterface.stopListeningToNetworkState(networkHealthListener);
        super.onDestroy();
    }
}