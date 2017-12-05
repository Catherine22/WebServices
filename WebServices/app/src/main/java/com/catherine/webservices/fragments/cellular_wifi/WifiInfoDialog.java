package com.catherine.webservices.fragments.cellular_wifi;

import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
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
import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.components.MyDialogFragment;
import com.catherine.webservices.entities.TextCard;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.toolkits.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2017/11/30.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class WifiInfoDialog extends MyDialogFragment {
    private List<TextCard> entities;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_d_wifi_info, container, false);
        Bundle b = getArguments();
        WifiInfo wifiInfo = b.getParcelable("WifiInfo");

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

        entities.add(new TextCard("网络的名字，唯一区别WIFI网络的名字", "SSID", (TextUtils.isEmpty(wifiInfo.getSSID()) ? "" : wifiInfo.getSSID())));
        entities.add(new TextCard("接入点的地址", "BSSID", (TextUtils.isEmpty(wifiInfo.getBSSID()) ? "" : wifiInfo.getBSSID())));
        entities.add(new TextCard("Mac Address", "MacAddress", (TextUtils.isEmpty(wifiInfo.getMacAddress()) ? "" : wifiInfo.getMacAddress())));
        entities.add(new TextCard("是否隱藏SSID", "hiddenSSID", wifiInfo.getHiddenSSID() + ""));
        entities.add(new TextCard("IP地址", "IpAddress", wifiInfo.getIpAddress() + ""));
        entities.add(new TextCard("连接的速度", "LinkSpeed", wifiInfo.getLinkSpeed() + ""));
        entities.add(new TextCard("NetworkId", "NetworkId", wifiInfo.getNetworkId() + ""));
        entities.add(new TextCard("获取802.11n网络的信号", "Rssi", wifiInfo.getRssi() + ""));
        SupplicantState state = wifiInfo.getSupplicantState();
        if (state != null) {
            entities.add(new TextCard("获取具体客户端状态的信息", "SupplicantState.name", (TextUtils.isEmpty(state.name()) ? "" : state.name())));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            entities.add(new TextCard("当前WIFI设备附近热点的频率(MHz)", "Frequency", wifiInfo.getFrequency() + ""));
        }


        TextCardRVAdapter adapter = new TextCardRVAdapter(getActivity(), entities, new OnItemClickListener() {
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
