package com.catherine.webservices.fragments;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.components.MyDialogFragment;
import com.catherine.webservices.entities.TextCard;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.network.NetworkHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2017/11/30.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P22_D_WifiConfigurations extends MyDialogFragment {
    private MainInterface mainInterface;
    private  List<TextCard> entities;
    private TextView tv_error;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_22_d_wifi_configurations, container, false);
        mainInterface = (MainInterface) getActivity();
        tv_error = view.findViewById(R.id.tv_error);
        ImageButton xButton = view.findViewById(R.id.xButton);
        RecyclerView rv_news = view.findViewById(R.id.rv_news);
        rv_news.setLayoutManager(new LinearLayoutManager(getActivity()));
        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        WifiManager wm = (WifiManager) MyApplication.INSTANCE.getSystemService(Context.WIFI_SERVICE);
        final List<WifiConfiguration> wifiConfigurations = wm.getConfiguredNetworks();
        if (wifiConfigurations == null) {
            tv_error.setText(getString(R.string.error));
        } else if (wifiConfigurations.size() == 0) {
            tv_error.setText(getString(R.string.no_networks));
        } else {
            tv_error.setText("");
            entities = new ArrayList<>();
            for (WifiConfiguration wc : wifiConfigurations) {
                entities.add(new TextCard(wc.SSID, null, null));
            }
        }

        rv_news.setAdapter(new TextCardRVAdapter(getActivity(), entities, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dismiss();
                Bundle b = new Bundle();
                b.putParcelable("WifiConfiguration", wifiConfigurations.get(position));
                mainInterface.callFragment(Constants.P20_WIFI_CONFIGURATION_ANALYTICS, b);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        return view;
    }
}
