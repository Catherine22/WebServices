package com.catherine.webservices.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.catherine.webservices.R;
import com.catherine.webservices.adapters.ImageCardRVAdapter;
import com.catherine.webservices.entities.ImageCard;
import com.catherine.webservices.entities.TestData;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.toolkits.CLog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        initComponent();
        fillInData();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void fillInData() {
        tv_offline.setVisibility(View.GONE);
        entities = new ArrayList<>();
        adapter.setImageCards(entities, false);
        adapter.notifyDataSetChanged();
        getPicList();
    }

    private void getPicList() {
        try {
            pb.setVisibility(View.INVISIBLE);
            if (showPicOffline) {
                String pics = sp.getString("pic_list", "");
                if (TextUtils.isEmpty(pics)) {
                    tv_offline.setVisibility(View.VISIBLE);
                    tv_offline.setText(getActivity().getResources().getString(R.string.no_cache));
                } else {
                    loadResponse(new JSONObject(pics), false);
                }
            } else {
                //Let's say those image links are downloaded successfully from an API
                JSONArray ja = new JSONArray();
                for (int i = 0; i < TestData.IMAGES0.length; i++) {
                    ja.put(TestData.IMAGES0[i]);
                }
                JSONObject jo = new JSONObject();
                jo.put("pics", ja);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("pic_list", jo.toString());
                editor.apply();
                loadResponse(jo, false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadResponse(JSONObject jo, boolean shrinkList) throws JSONException {
        CLog.i(TAG, jo.toString());
        tv_offline.setVisibility(View.GONE);
        JSONArray pics = jo.getJSONArray("pics");
        for (int i = 0; i < pics.length(); i++) {
            ImageCard imageCard = new ImageCard();
            imageCard.image = pics.getString(i);
            imageCard.title = NetworkHelper.getFileNameFromUrl(pics.getString(i));
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
                CLog.d(TAG, "refresh");
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
