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
import com.catherine.webservices.adapters.ShortCardRVAdapter;
import com.catherine.webservices.network.HttpAsyncTask;
import com.catherine.webservices.network.HttpRequest;
import com.catherine.webservices.network.HttpResponse;
import com.catherine.webservices.network.HttpResponseListener;
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

public class P04_Gallery extends LazyFragment {
    private final static String TAG = "P04_Gallery";
    private List<String> titles;
    private List<String> attrs;
    private List<String> images;
    private SwipeRefreshLayout srl_container;
    private ShortCardRVAdapter adapter;

    public static P04_Gallery newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P04_Gallery fragment = new P04_Gallery();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_04_gallery);
        titles = new ArrayList<>();
        attrs = new ArrayList<>();
        images = new ArrayList<>();
        initComponent();
        fillInData();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void fillInData() {
        new ADID_AsyncTask(new ADID_AsyncTask.ADID_Callback() {
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
                            }
                        })
                );
                new HttpAsyncTask(r).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onError(@NotNull Exception e) {
                CLog.Companion.e(TAG, "Failed to get ADID: " + e.toString());
                String ADID = "FAKE-ADID";
                HttpRequest request5 = new HttpRequest(new HttpRequest.Builder()
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
                            }
                        })
                );
                new HttpAsyncTask(request5).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        rv_main_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ShortCardRVAdapter(getActivity(), images, titles, attrs, new ShortCardRVAdapter.OnItemClickListener() {
            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }

            @Override
            public void onItemClick(@NotNull View view, int position) {
            }
        });
        rv_main_list.setAdapter(adapter);
    }
}
