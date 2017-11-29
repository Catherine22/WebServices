package com.catherine.webservices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.entities.TextCard;
import com.catherine.webservices.interfaces.ADID_Callback;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.security.ADID_AsyncTask;

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Catherine on 2017/9/21.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class DeviceInfoActivity extends BaseFragmentActivity {
    public final static String TAG = "P00_DeviceInfo";
    private List<TextCard> entities;
    private TextCardRVAdapter adapter;
    private SwipeRefreshLayout srl_container;
    private Handler handler;

    @Override
    @SuppressLint("MissingSuperCall")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.f_00_device_info, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.BLUETOOTH});
    }

    @Override
    protected void onPermissionGranted() {
        handler = new Handler(MyApplication.INSTANCE.calHandlerThread.getLooper());
        fillInData();
        initComponent();
    }

    @SuppressLint("HardwareIds")
    private void fillInData() {
        entities = new ArrayList<>();

        entities.add(new TextCard("ADID", "", null));
        ADID_AsyncTask adid_asyncTask = new ADID_AsyncTask(new ADID_Callback() {
            @Override
            public void onResponse(@NotNull String ADID) {
                entities.set(0, new TextCard("ADID", ADID, null));
                adapter.setEntities(entities);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(@NotNull Exception e) {
                entities.set(0, new TextCard("ADID", "Error:" + e.getMessage(), null));
                adapter.setEntities(entities);
                adapter.notifyDataSetChanged();
            }
        });
        adid_asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //Host Name
        entities.add(new TextCard("Host Name", "", null));

        //IP Address
        entities.add(new TextCard("IP Address", "", null));
        handler.post(new Runnable() {
            @Override
            public void run() {
                String hostName, ip;
                try {
                    InetAddress address = InetAddress.getLocalHost();
                    hostName = address.getHostName();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    hostName = "Error: " + e.getMessage();
                }
                if (TextUtils.isEmpty(hostName))
                    hostName = "N/A";

                try {
                    InetAddress address = InetAddress.getLocalHost();
                    ip = address.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    ip = "Error: " + e.getMessage();
                }
                if (TextUtils.isEmpty(ip))
                    ip = "N/A";

                entities.set(1, new TextCard("Host Name", hostName, null));
                entities.set(2, new TextCard("IP Address", ip, null));
            }
        });


        WifiManager wm = (WifiManager) MyApplication.INSTANCE.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifi = wm.getConnectionInfo();
        String macAddress = wifi.getMacAddress();
        if (TextUtils.isEmpty(macAddress))
            macAddress = "N/A";
        entities.add(new TextCard("MAC Address", macAddress, null));


        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(androidId))
            androidId = "N/A";
        entities.add(new TextCard("Android ID", androidId, null));


        String uuid = UUID.randomUUID().toString();
        if (TextUtils.isEmpty(uuid))
            uuid = "N/A";
        entities.add(new TextCard("UUID, 每次都不一样", uuid, null));


        String IMEI;
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = tm.getDeviceId();
            if (TextUtils.isEmpty(IMEI))
                IMEI = "N/A";
        } catch (SecurityException e) {
            e.printStackTrace();
            IMEI = "Error: " + e.getMessage();
        }
        entities.add(new TextCard("IMEI/MEID/ESN", IMEI, null));


        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        String btAddress = (btAdapter != null) ? btAdapter.getAddress() : "N/A";
        if (TextUtils.isEmpty(btAddress))
            btAddress = "N/A";
        entities.add(new TextCard("Bluetooth Address", btAddress, null));

        String imsi;
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imsi = tm.getSubscriberId();
            if (TextUtils.isEmpty(imsi))
                imsi = "N/A";
        } catch (SecurityException e) {
            e.printStackTrace();
            imsi = "Error: " + e.getMessage();
        }
        entities.add(new TextCard("IMSI (GSM)", imsi, null));

        NetworkHelper networkHelper = new NetworkHelper();
        entities.add(new TextCard("Network Health", networkHelper.isNetworkHealthy() + "", null));


        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        String networkType = networkInfo.getTypeName();
        if (TextUtils.isEmpty(networkType))
            networkType = "N/A";
        entities.add(new TextCard("Network Type", networkType, null));


        NetworkInfo.State networkState = networkInfo.getState();
        String ns = "";
        if (networkState == NetworkInfo.State.CONNECTED)
            ns = "CONNECTED";
        else if (networkState == NetworkInfo.State.CONNECTING)
            ns = "CONNECTING";
        else if (networkState == NetworkInfo.State.DISCONNECTED)
            ns = "DISCONNECTED";
        else if (networkState == NetworkInfo.State.DISCONNECTING)
            ns = "DISCONNECTING";
        else if (networkState == NetworkInfo.State.SUSPENDED)
            ns = "SUSPENDED";
        else if (networkState == NetworkInfo.State.UNKNOWN)
            ns = "UNKNOWN";

        if (TextUtils.isEmpty(ns))
            ns = "N/A";
        entities.add(new TextCard("Network state", ns, null));

        String mPhoneNumber;
        try {
            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            mPhoneNumber = tMgr.getLine1Number();
            if (TextUtils.isEmpty(mPhoneNumber))
                mPhoneNumber = "N/A";
        } catch (SecurityException e) {
            e.printStackTrace();
            mPhoneNumber = "Error: " + e.getMessage();
        }
        entities.add(new TextCard("Phone number", mPhoneNumber, null));
        entities.add(new TextCard("Brand, 系统定制商", (TextUtils.isEmpty(Build.BRAND) ? "N/A" : Build.BRAND), null));
        entities.add(new TextCard("The system bootloader version number, 系统启动程序版本号", (TextUtils.isEmpty(Build.BOOTLOADER) ? "N/A" : Build.BOOTLOADER), null));
        entities.add(new TextCard("Board, 主板", (TextUtils.isEmpty(Build.BOARD) ? "N/A" : Build.BOARD), null));
        entities.add(new TextCard("CPU_ABI, cpu指令集", (TextUtils.isEmpty(Build.CPU_ABI) ? "N/A" : Build.CPU_ABI), null));
        entities.add(new TextCard("CPU_ABI2, cpu指令集2", (TextUtils.isEmpty(Build.CPU_ABI) ? "N/A" : Build.CPU_ABI), null));
        entities.add(new TextCard("DEVICE, 设置参数", (TextUtils.isEmpty(Build.DEVICE) ? "N/A" : Build.DEVICE), null));
        entities.add(new TextCard("DISPLAY, 显示屏参数", (TextUtils.isEmpty(Build.DISPLAY) ? "N/A" : Build.DISPLAY), null));
        entities.add(new TextCard("Radio Version, 无线电固件版本", (TextUtils.isEmpty(Build.RADIO) ? "N/A" : Build.RADIO), null));
        entities.add(new TextCard("FINGERPRINT, 硬件识别码", (TextUtils.isEmpty(Build.FINGERPRINT) ? "N/A" : Build.FINGERPRINT), null));
        entities.add(new TextCard("HARDWARE, 硬件名", (TextUtils.isEmpty(Build.HARDWARE) ? "N/A" : Build.HARDWARE), null));
        entities.add(new TextCard("HOST, 主机名", (TextUtils.isEmpty(Build.HOST) ? "N/A" : Build.HOST), null));
        entities.add(new TextCard("Build.ID, 修订版本列表", (TextUtils.isEmpty(Build.ID) ? "N/A" : Build.ID), null));
        entities.add(new TextCard("MANUFACTURER, 硬件制造商", (TextUtils.isEmpty(Build.MANUFACTURER) ? "N/A" : Build.MANUFACTURER), null));
        entities.add(new TextCard("Build.MODEL, 版本", (TextUtils.isEmpty(Build.MODEL) ? "N/A" : Build.MODEL), null));
        entities.add(new TextCard("Build.SERIAL, 硬件序列号", (TextUtils.isEmpty(Build.SERIAL) ? "N/A" : Build.SERIAL), null));
        entities.add(new TextCard("PRODUCT, 手机制造商", (TextUtils.isEmpty(Build.PRODUCT) ? "N/A" : Build.PRODUCT), null));
        entities.add(new TextCard("Build.TAGS, Build的标签", (TextUtils.isEmpty(Build.TAGS) ? "N/A" : Build.TAGS), null));
        entities.add(new TextCard("Build.TIME, 时间？", (TextUtils.isEmpty(Build.TIME + "") ? "N/A" : Build.TIME + ""), null));
        entities.add(new TextCard("Build.TYPE, 类型", (TextUtils.isEmpty(Build.TYPE) ? "N/A" : Build.TYPE), null));
        entities.add(new TextCard("Build.USER, 用户", (TextUtils.isEmpty(Build.USER) ? "N/A" : Build.USER), null));
    }

    private void initComponent() {
        srl_container = findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fillInData();
                initComponent();
                srl_container.setRefreshing(false);
            }
        });
        RecyclerView rv_main_list = findViewById(R.id.rv_main_list);
        rv_main_list.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        adapter = new TextCardRVAdapter(this, entities, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                TextCard tc = entities.get(position);
                try {
                    int sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                                getSystemService(CLIPBOARD_SERVICE);
                        clipboard.setText(tc.subtitle);
                    } else {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                                getSystemService(CLIPBOARD_SERVICE);
                        android.content.ClipData clip = android.content.ClipData
                                .newPlainText(tc.title, tc.subtitle);
                        clipboard.setPrimaryClip(clip);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);
    }

}
