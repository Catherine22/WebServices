package com.catherine.webservices.fragments.cellular_wifi;

import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.catherine.webservices.R;
import com.catherine.webservices.adapters.CardListRVAdapter;
import com.catherine.webservices.components.MyDialogFragment;
import com.catherine.webservices.entities.TextCard;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.toolkits.FileUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Catherine on 2017/11/30.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class ScanResultInfoDialog extends MyDialogFragment {
    private List<TextCard> entities;
    private ScanResult result;
    private CardListRVAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_d_scan_result_info, container, false);
        Bundle b = getArguments();
        result = b.getParcelable("ScanResult");
        ImageButton xButton = view.findViewById(R.id.xButton);
        RecyclerView rv_news = view.findViewById(R.id.rv_news);
        rv_news.setLayoutManager(new LinearLayoutManager(getActivity()));
        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        entities = new ArrayList<>();
        entities.add(new TextCard("网络的名字，唯一区别WIFI网络的名字", "SSID", (TextUtils.isEmpty(result.SSID) ? "" : result.SSID)));
        entities.add(new TextCard("接入点的地址", "BSSID", (TextUtils.isEmpty(result.BSSID) ? "" : result.BSSID)));
        entities.add(new TextCard("网络接入的性能", "capabilities", (TextUtils.isEmpty(result.capabilities) ? "" : result.capabilities)));
        entities.add(new TextCard("当前WIFI设备附近热点的频率(MHz)", "frequency", result.frequency + ""));
        entities.add(new TextCard("所发现的WIFI网络信号强度", "level", result.level + ""));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
            String dateString = sdf.format(new Date(result.timestamp));
            entities.add(new TextCard("timestamp", "timestamp", dateString));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            entities.add(new TextCard("centerFreq0", "centerFreq0", result.centerFreq0 + ""));
            entities.add(new TextCard("centerFreq1", "centerFreq1", result.centerFreq1 + ""));
            entities.add(new TextCard("channelWidth", "channelWidth", result.channelWidth + ""));
            entities.add(new TextCard("is80211mcResponder", "is80211mcResponder", result.is80211mcResponder() + ""));
            entities.add(new TextCard("isPasspointNetwork", "isPasspointNetwork", result.isPasspointNetwork() + ""));
            entities.add(new TextCard("venueName", "venueName", (TextUtils.isEmpty(result.venueName.toString()) ? "" : result.venueName.toString())));
            entities.add(new TextCard("operatorFriendlyName", "operatorFriendlyName", (TextUtils.isEmpty(result.operatorFriendlyName.toString()) ? "" : result.operatorFriendlyName.toString())));
        }


        adapter = new CardListRVAdapter(getActivity(), entities, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextCard tc = entities.get(position);
                FileUtils.copyToClipboard(tc.title, tc.contents);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        rv_news.setAdapter(adapter);
        return view;
    }
}
