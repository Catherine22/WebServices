package com.catherine.webservices.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.ImageCardRVAdapter;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.network.HttpAsyncTask;
import com.catherine.webservices.network.HttpRequest;
import com.catherine.webservices.network.HttpResponse;
import com.catherine.webservices.network.HttpResponseListener;
import com.catherine.webservices.network.NetworkHealthListener;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.security.ADID_AsyncTask;
import com.catherine.webservices.toolkits.CLog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Catherine on 2017/9/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P05_Gallery extends LazyFragment {
    private final static String TAG = "P05_Gallery";
    private List<String> titles;
    private List<String> attrs;
    private List<String> images;
    private SwipeRefreshLayout srl_container;
    private ImageCardRVAdapter adapter;
    private TextView tv_offline;
    private RecyclerView rv_main_list;
    private NetworkHelper helper;
    private ADID_AsyncTask adid_asyncTask;
    private boolean retry;

    public static P05_Gallery newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P05_Gallery fragment = new P05_Gallery();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_05_gallery);
        rv_main_list = (RecyclerView) findViewById(R.id.rv_main_list);
        tv_offline = (TextView) findViewById(R.id.tv_offline);
        titles = new ArrayList<>();
        attrs = new ArrayList<>();
        images = new ArrayList<>();
        helper = new NetworkHelper(getActivity());
        helper.listenToNetworkState(new NetworkHealthListener() {
            @Override
            public void networkConnected(@NotNull String type) {
                if(retry){
                    fillInData();
                }
            }

            @Override
            public void networkDisable() {

            }
        });
        initComponent();
        fillInData();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void fillInData() {
        retry = false;
        rv_main_list.setVisibility(View.VISIBLE);
        tv_offline.setVisibility(View.GONE);
        adid_asyncTask = new ADID_AsyncTask(new ADID_AsyncTask.ADID_Callback() {
            @Override
            public void onResponse(@NotNull String ADID) {
                HttpRequest r = new HttpRequest(new HttpRequest.Builder()
                        .url(NetworkHelper.Companion.encodeURL(String.format(Locale.ENGLISH, "%sResourceServlet?ADID={%s}&IDFA={}", Constants.HOST, ADID)))
                        .listener(new HttpResponseListener() {
                            @Override
                            public void connectSuccess(HttpResponse response) {
                                CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                                try {
                                    JSONObject jo = new JSONObject(response.getBody());
                                    JSONArray pics = jo.getJSONArray("pics");
                                    for (int i = 0; i < pics.length(); i++) {
                                        images.add(pics.getString(i));
                                        titles.add(NetworkHelper.Companion.getFileNameFromUrl(pics.getString(i)));
                                        attrs.add("fresh");//not cache
                                    }

                                    adapter.setImages(images);
                                    adapter.setTitles(titles);
                                    adapter.setSubtitles(attrs);
                                    adapter.notifyDataSetChanged();

                                } catch (Exception e) {
                                    CLog.Companion.e(TAG, "Json error:" + e.getMessage());
                                }
                            }

                            @Override
                            public void connectFailure(HttpResponse response, Exception e) {
                                CLog.Companion.e(TAG, String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, error:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                                if (e != null)
                                    CLog.Companion.e(TAG, e.getMessage());

                                if (helper.isNetworkHealth()) {
                                    //retry?
                                    tv_offline.setVisibility(View.VISIBLE);
                                    rv_main_list.setVisibility(View.GONE);
                                } else {
                                    retry = true;
                                    tv_offline.setVisibility(View.VISIBLE);
                                    rv_main_list.setVisibility(View.GONE);
                                }
                            }
                        })
                );
                new HttpAsyncTask(r).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onError(@NotNull Exception e) {
                CLog.Companion.e(TAG, "Failed to get ADID: " + e.toString());
                String ADID = "FAKE-ADID";
                HttpRequest request = new HttpRequest(new HttpRequest.Builder()
                        .url(NetworkHelper.Companion.encodeURL(String.format(Locale.ENGLISH, "%sResourceServlet?ADID={%s}&IDFA={}", Constants.HOST, ADID)))
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

                                if (helper.isNetworkHealth()) {
                                    //retry?
                                    tv_offline.setVisibility(View.VISIBLE);
                                    rv_main_list.setVisibility(View.GONE);
                                } else {
                                    retry = true;
                                    tv_offline.setVisibility(View.VISIBLE);
                                    rv_main_list.setVisibility(View.GONE);
                                }
                            }
                        })
                );
                new HttpAsyncTask(request).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });
        adid_asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initComponent() {
        srl_container = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CLog.Companion.d(TAG, "refresh");
                srl_container.setRefreshing(false);
                fillInData();
            }
        });

        RecyclerView rv_main_list = (RecyclerView) findViewById(R.id.rv_main_list);
        rv_main_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ImageCardRVAdapter(getActivity(), images, titles, attrs, new OnItemClickListener() {
            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }

            @Override
            public void onItemClick(@NotNull View view, int position) {
            }
        });
        rv_main_list.setAdapter(adapter);

        FloatingActionButton fab_delete = (FloatingActionButton)  findViewById(R.id.fab_delete);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                adapter.deleteCache();
            }
        });
    }
}
