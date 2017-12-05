package com.catherine.webservices.fragments.cellular_wifi;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
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
import com.catherine.webservices.adapters.CardListRVAdapter;
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

public class ScanResultDialog extends MyDialogFragment {
    private TextView tv_error;
    private NetworkHelper helper;
    private List<TextCard> entities;
    private List<ScanResult> results;
    private CardListRVAdapter adapter;
    private MainInterface mainInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_d_scan_result, container, false);
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
        helper = new NetworkHelper();
        entities = new ArrayList<>();
        adapter = new CardListRVAdapter(getActivity(), entities, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dismiss();
                Bundle b = new Bundle();
                b.putParcelable("ScanResult", results.get(position));
                mainInterface.callFragment(Constants.Fragments.F_SCAN_RESULT_INFO, b);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        rv_news.setAdapter(adapter);
        Handler handler = new Handler(MyApplication.INSTANCE.calHandlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                results = helper.scanWifi();
                if (results == null) {
                    tv_error.setText(getString(R.string.error));
                } else if (results.size() == 0) {
                    tv_error.setText(getString(R.string.no_hotspot));
                } else {
                    tv_error.setText("");
                    for (ScanResult sr : results) {
                        entities.add(new TextCard(sr.SSID, null, null));
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        return view;
    }
}
