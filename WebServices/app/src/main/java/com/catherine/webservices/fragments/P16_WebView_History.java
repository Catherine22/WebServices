package com.catherine.webservices.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.catherine.webservices.R;
import com.catherine.webservices.adapters.SimpleStyleRVAdapter;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.entities.ImageCardEx;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnMultiItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.toolkits.CLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P16_WebView_History extends LazyFragment {
    public final static String TAG = "P16_WebView_History";
    private SwipeRefreshLayout srl_container;
    private MainInterface mainInterface;

    public static P16_WebView_History newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P16_WebView_History fragment = new P16_WebView_History();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_16_wv_history);
        mainInterface = (MainInterface) getActivity();
        init();
    }

    private void init() {
        mainInterface.getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                fillInData();
            }

            @Override
            public void onDenied(@Nullable List<String> deniedPermissions) {
                StringBuilder context = new StringBuilder();
                if (deniedPermissions != null) {
                    for (String p : deniedPermissions) {
                        context.append(p);
                        context.append(", ");
                    }
                }

                context.deleteCharAt(context.length() - 1);
                DialogManager.showPermissionDialog(getActivity(), String.format(getActivity().getResources().getString(R.string.permission_request), context), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
            }

            @Override
            public void onRetry() {
                init();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void fillInData() {
        TextView tv_empty = (TextView) findViewById(R.id.tv_empty);
        srl_container = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
                srl_container.setRefreshing(false);
            }
        });

        RecyclerView rv_history = (RecyclerView) findViewById(R.id.rv_history);
        rv_history.setLayoutManager(new LinearLayoutManager(getActivity()));


        SharedPreferences sp = getActivity().getSharedPreferences("wv_history", Context.MODE_PRIVATE);
        String h = sp.getString("data", "");
        CLog.i(TAG, h);
        if (TextUtils.isEmpty(h)) {
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            tv_empty.setVisibility(View.INVISIBLE);
            try {
                JSONArray ja = new JSONArray(h);
                final List<DataObject> itemCollection = new ArrayList<>();
                Map<String, List<ImageCardEx>> data = new LinkedHashMap<>();
                for (int i = 0; i < ja.length(); i++) {
                    DataObject obj = new DataObject();
                    obj.setData(ja.getJSONObject(i));
                    itemCollection.add(obj);
                }
                Collections.sort(itemCollection);


                for (int i = 0; i < itemCollection.size(); i++) {
                    DataObject d = itemCollection.get(i);
                    ImageCardEx ice = new ImageCardEx();
                    String title = d.getRoughDateTime();
                    ice.setTitle(d.getData().optString("shortName", ""));
                    ice.setSubtitle(d.getData().optString("url", ""));
                    ice.setImage(d.getData().optString("icon", ""));
                    ice.setStyle(0);

                    if (data.containsKey(title)) {
                        data.get(title).add(ice);
                    } else {
                        List<ImageCardEx> c = new ArrayList<>();
                        c.add(ice);
                        data.put(title, c);
                    }
                }

                SimpleStyleRVAdapter adapter = new SimpleStyleRVAdapter(getActivity(), null, null, new OnMultiItemClickListener() {
                    @Override
                    public void onItemClick(View view, String title, int position) {
                        CLog.i(TAG, "click:" + position);
                    }

                    @Override
                    public void onItemLongClick(View view, String title, final int position) {
                        DialogManager.showAlertDialog(getActivity(), "Do you want to remove this item from the list?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO remove the item and update recyclerView
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                    }
                });
                for (Map.Entry<String, List<ImageCardEx>> entry : data.entrySet()) {
                    adapter.mergeList(entry.getKey(), entry.getValue());
                }
                rv_history.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
                tv_empty.setVisibility(View.VISIBLE);
            }
        }

    }

    class DataObject implements Comparable<DataObject> {

        private JSONObject data;

        public Date getDateTime() {
            if (data == null) return null;
            try {
                long t = data.getLong("time");
                return new Date(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        public String getRoughDateTime() {
            if (data == null) return null;
            try {
                long t = data.getLong("time");
                Date d = new Date(t);
                DateFormat format = new SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault());
                return format.format(d);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void setData(JSONObject data) {
            this.data = data;
        }


        public JSONObject getData() {
            return data;
        }

        @Override
        public int compareTo(DataObject o) {
            if (getDateTime() == null || o.getDateTime() == null)
                return 0;
            return getDateTime().compareTo(o.getDateTime()) * -1;
        }


    }
}
