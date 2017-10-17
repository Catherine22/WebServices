package com.catherine.webservices.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.ImageCardRVAdapter;
import com.catherine.webservices.entities.ImageCard;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.network.HttpAsyncTask;
import com.catherine.webservices.network.HttpRequest;
import com.catherine.webservices.network.HttpResponse;
import com.catherine.webservices.network.HttpResponseListener;
import com.catherine.webservices.network.MyHttpURLConnection;
import com.catherine.webservices.network.NetworkHealthListener;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.security.ADID_AsyncTask;
import com.catherine.webservices.toolkits.CLog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Catherine on 2017/9/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P05_Gallery extends LazyFragment {
    private final static String TAG = "P05_Gallery";
    private List<ImageCard> entities;
    private SwipeRefreshLayout srl_container;
    private ImageCardRVAdapter adapter;
    private ProgressBar pb;
    private TextView tv_offline;
    private NetworkHelper helper;
    private boolean retry;
    private boolean showPicOffline;
    private SharedPreferences sp;

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

        if (getArguments() != null) {
            showPicOffline = getArguments().getBoolean("show_pic_offline", false);
        }

        sp = getActivity().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        helper = new NetworkHelper(getActivity());
        helper.listenToNetworkState(new NetworkHealthListener() {
            @Override
            public void networkConnected(@NotNull String type) {
                if (retry) {
                    fillInData();
                }
            }

            @Override
            public void networkDisable() {
                if (!showPicOffline) {
                    tv_offline.setText(getString(R.string.offline));
                    tv_offline.setVisibility(View.VISIBLE);
                }
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
        tv_offline.setVisibility(View.GONE);
        entities = new ArrayList<>();
        adapter.setImageCards(entities, false);
        adapter.notifyDataSetChanged();
        ADID_AsyncTask adid_asyncTask = new ADID_AsyncTask(
                new ADID_AsyncTask.ADID_Callback() {
                    @Override
                    public void onResponse(@NonNull String ADID) {
                        getPicList(ADID);
                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        CLog.Companion.e(TAG, "Failed to get ADID: " + e.toString());
                        getPicList("FAKE-ADID");

                    }
                });
        adid_asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getPicList(String ADID) {
        Map<String, String> body = new HashMap<>();
        body.put("from", "30");
        body.put("to", "70");
        body.put("ADID", ADID);
        HttpRequest r = new HttpRequest.Builder()
                .body(MyHttpURLConnection.getSimpleStringBody(body))
                .url(NetworkHelper.Companion.encodeURL(String.format(Locale.ENGLISH, "%sResourceServlet", Constants.HOST)))
                .listener(new HttpResponseListener() {
                    @Override
                    public void connectSuccess(@NonNull HttpResponse response) {
                        pb.setVisibility(View.INVISIBLE);
                        CLog.Companion.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getBody()));
                        try {
                            JSONObject jo = new JSONObject(response.getBody());
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("pic_list", jo.toString());
                            editor.apply();
                            loadResponse(jo, false);
                        } catch (Exception e) {
                            CLog.Companion.e(TAG, "Json error:" + e.getMessage());
                        }
                    }

                    @Override
                    public void connectFailure(@NonNull HttpResponse response, Exception e) {
                        pb.setVisibility(View.INVISIBLE);
                        StringBuilder sb = new StringBuilder();
                        sb.append(String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, error:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                        CLog.Companion.e(TAG, sb.toString());
                        if (e != null) {
                            sb.append("\n");
                            sb.append(e.getMessage());
                            CLog.Companion.e(TAG, e.getMessage());
                        }

                        if (helper.isNetworkHealth()) {
                            //retry?
                            tv_offline.setText(sb.toString());
                            tv_offline.setVisibility(View.VISIBLE);
                        } else {
                            retry = true;
                            if (showPicOffline) {
                                String s = sp.getString("pic_list", "");
                                if (TextUtils.isEmpty(s)) {
                                    tv_offline.setText(getString(R.string.no_cache));
                                    tv_offline.setVisibility(View.VISIBLE);
                                } else {
                                    try {
                                        JSONObject jo = new JSONObject(s);
                                        loadResponse(jo, true);
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                        CLog.Companion.e(TAG, "Json error:" + e1.getMessage());
                                    }
                                }
                            } else {
                                tv_offline.setText(getString(R.string.offline));
                                tv_offline.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                })
                .build();
        new HttpAsyncTask(r).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void loadResponse(JSONObject jo, boolean shrinkList) throws JSONException {
        JSONArray pics = jo.getJSONArray("pics");
        ImageCard imageCard;
        for (int i = 0; i < pics.length(); i++) {
            imageCard = new ImageCard();
            imageCard.image = pics.getString(i);
            imageCard.title = NetworkHelper.Companion.getFileNameFromUrl(pics.getString(i));
            imageCard.subtitle = "fresh";//not cache
            entities.add(imageCard);
        }
        adapter.setImageCards(entities, shrinkList);
        adapter.notifyDataSetChanged();
    }


    private void initComponent() {
        srl_container = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pb.setVisibility(View.VISIBLE);
                CLog.Companion.d(TAG, "refresh");
                fillInData();
                srl_container.setRefreshing(false);
            }
        });

        RecyclerView rv_main_list = (RecyclerView) findViewById(R.id.rv_main_list);
        rv_main_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ImageCardRVAdapter(getActivity(), entities, showPicOffline, new OnItemClickListener() {
            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }

            @Override
            public void onItemClick(@NotNull View view, int position) {
            }
        });
        rv_main_list.setAdapter(adapter);

        pb = (ProgressBar) findViewById(R.id.pb);
        tv_offline = (TextView) findViewById(R.id.tv_offline);

        FloatingActionButton fab_delete = (FloatingActionButton) findViewById(R.id.fab_delete);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                adapter.deleteCache();
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("pic_list");
                editor.apply();
            }
        });
    }
}
