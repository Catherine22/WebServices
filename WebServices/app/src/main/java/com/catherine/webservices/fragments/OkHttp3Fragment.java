package com.catherine.webservices.fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Created by Catherine on 2017/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class OkHttp3Fragment extends LazyFragment {
    public final static String TAG = OkHttp3Fragment.class.getSimpleName();
    private List<TextCard> entities;
    private SwipeRefreshLayout srl_container;
    private TextCardRVAdapter adapter;
    private MainInterface mainInterface;
    private boolean retry;
    private int step;
    private NetworkHealthListener networkHealthListener;
    private OkHttpClient client, githubClient, kyfwClient;

    public static OkHttp3Fragment newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        OkHttp3Fragment fragment = new OkHttp3Fragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_okhttp_3);
        client = new OkHttpClient();
        mainInterface = (MainInterface) getActivity();

        try {
            X509Certificate cert = CertificatesManager.pemToX509Certificate(Constants.GITHUB_CERT);
            SSLContext sc = getSslContext(cert);
            githubClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sc.getSocketFactory())
                    .build();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            BufferedInputStream bis = new BufferedInputStream(getActivity().getAssets().open("srca.cer"));
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(bis);
            bis.close();
            SSLContext sc = getSslContext(cert);
            kyfwClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sc.getSocketFactory())
                    .build();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fillInData();
        initComponent();
    }

    @NonNull
    private SSLContext getSslContext(X509Certificate cert) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException {
        //生成包含当前CA证书的keystore
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("user_define_ca", cert);

        //使用包含指定CA证书的keystore生成TrustManager[]数组
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);
        TrustManager[] trustManagers = tmf.getTrustManagers();

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustManagers, new SecureRandom());
        return sc;
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
                Request request0 = new Request.Builder()
                        .url(String.format(Locale.ENGLISH, "%sLoginServlet?name=zhangsan&password=123456", Constants.HOST))
                        .addHeader("h1", "Hi there!")
                        .addHeader("h2", "I am a mobile phone.")
                        .addHeader("Authorization", Constants.AUTHORIZATION)
                        .build();
                getResponse(position, request0);
                break;
            case 1:
                srl_container.setRefreshing(true);
                Map<String, String> body1 = new HashMap<>();
                body1.put("name", "zhangsan");
                body1.put("password", "123456");
                Request request1 = new Request.Builder()
                        .url(String.format(Locale.ENGLISH, "%sLoginServlet", Constants.HOST))
                        .addHeader("Authorization", Constants.AUTHORIZATION)
                        .post(RequestBody.create(null, MyHttpURLConnection.getSimpleStringBody(body1)))
                        .build();
                getResponse(position, request1);
                break;
            case 2:
                srl_container.setRefreshing(true);
                Map<String, String> body2 = new HashMap<>();
                body2.put("name", "zhangsan");
                body2.put("password", "123456");
                Request request2 = new Request.Builder()
                        .url(String.format(Locale.ENGLISH, "%sLoginServlet", Constants.HOST))
                        .addHeader("Authorization", "12345")
                        .post(RequestBody.create(null, MyHttpURLConnection.getSimpleStringBody(body2)))
                        .build();
                getResponse(position, request2);
                break;
            case 3:
                srl_container.setRefreshing(true);
                Map<String, String> body3 = new HashMap<>();
                body3.put("name", "");
                body3.put("password", "");
                Request request3 = new Request.Builder()
                        .url(String.format(Locale.ENGLISH, "%sLoginServlet", Constants.HOST))
                        .addHeader("Authorization", Constants.AUTHORIZATION)
                        .post(RequestBody.create(null, MyHttpURLConnection.getSimpleStringBody(body3)))
                        .build();
                getResponse(position, request3);
                break;
            case 4:
                srl_container.setRefreshing(true);
                Request request4 = new Request.Builder()
                        .url("http://dictionary.cambridge.org/dictionary/english-chinese-simplified/philosopher")
                        .build();
                getResponse(position, request4);
                break;
            case 5:
                srl_container.setRefreshing(true);
                Request request5 = new Request.Builder()
                        .url(Constants.GITHUB_API_DOMAIN + "users/Catherine22/repos")
                        .build();
                getResponse(position, request5, githubClient);
                break;
            case 6:
                srl_container.setRefreshing(true);
                Request request6 = new Request.Builder()
                        .url("https://kyfw.12306.cn/otn/regist/init")
                        .build();
                getResponse(position, request6, kyfwClient);
                break;
        }
    }


    private void getResponse(final int position, Request request) {
        getResponse(position, request, client);
    }

    private void getResponse(final int position, Request request, OkHttpClient client) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
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
                    sb.append("connectFailure\n");
                    sb.append("\n");
                    sb.append(e.getMessage());
                    CLog.e(TAG, e.getMessage());
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

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String returnMsg = String.format(Locale.ENGLISH, "connectSuccess code:%s\nisSuccessful:%b\nisRedirect:%b\ncache control:%s\nbody:%s\n",
                        response.code(), response.isSuccessful(), response.isRedirect(), response.cacheControl().toString(), body.string());
                CLog.i(TAG, returnMsg);

                TextCard tc = entities.get(position);
                tc.contents = returnMsg;
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
        });
    }

    @Override
    public void onDestroy() {
        mainInterface.stopListeningToNetworkState(networkHealthListener);
        super.onDestroy();
    }
}