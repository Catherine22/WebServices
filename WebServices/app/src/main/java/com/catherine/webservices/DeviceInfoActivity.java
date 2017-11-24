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
    private List<String> features, contents, desc;
    private TextCardRVAdapter adapter;
    private SwipeRefreshLayout srl_container;
    private ADID_AsyncTask adid_asyncTask;
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
        features = new ArrayList<>();
        contents = new ArrayList<>();
        desc = new ArrayList<>();


        desc.add("");//ADID
        features.add("ADID");
        adid_asyncTask = new ADID_AsyncTask(new ADID_AsyncTask.ADID_Callback() {
            @Override
            public void onResponse(@NotNull String ADID) {
                desc.set(0, ADID);
                adapter.setSubtitles(desc);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(@NotNull Exception e) {
                desc.set(0, "Error:" + e.getMessage());
                adapter.setSubtitles(desc);
                adapter.notifyDataSetChanged();
            }
        });
        adid_asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        desc.add("");//Host Name
        features.add("Host Name");

        desc.add("");//IP Address
        features.add("IP Address");
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

                desc.set(1, hostName);

                desc.set(2, ip);
            }
        });


        WifiManager wm = (WifiManager) MyApplication.INSTANCE.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifi = wm.getConnectionInfo();
        String macAddress = wifi.getMacAddress();
        if (TextUtils.isEmpty(macAddress))
            macAddress = "N/A";
        desc.add(macAddress);
        features.add("MAC Address");


        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(androidId))
            androidId = "N/A";
        desc.add(androidId);
        features.add("Android ID");


        String uuid = UUID.randomUUID().toString();
        if (TextUtils.isEmpty(uuid))
            uuid = "N/A";
        desc.add(uuid);
        features.add("UUID, 每次都不一样");


        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String IMEI = tm.getDeviceId();
            if (TextUtils.isEmpty(IMEI))
                IMEI = "N/A";
            desc.add(IMEI);
        } catch (SecurityException e) {
            e.printStackTrace();
            desc.add("Error: " + e.getMessage());
        }
        features.add("IMEI/MEID/ESN");


        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        String btAddress = (btAdapter != null) ? btAdapter.getAddress() : "N/A";
        if (TextUtils.isEmpty(btAddress))
            btAddress = "N/A";
        desc.add(btAddress);
        features.add("Bluetooth Address");


        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String imsi = tm.getSubscriberId();
            if (TextUtils.isEmpty(imsi))
                imsi = "N/A";
            desc.add(imsi);
        } catch (SecurityException e) {
            e.printStackTrace();
            desc.add("Error: " + e.getMessage());
        }
        features.add("IMSI (GSM)");

        NetworkHelper networkHelper = new NetworkHelper(this);
        desc.add(networkHelper.isNetworkHealthy() + "");
        features.add("Network Health");

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        String networkType = networkInfo.getTypeName();
        if (TextUtils.isEmpty(networkType))
            networkType = "N/A";
        desc.add(networkType);
        features.add("Network Type");


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
        desc.add(ns);
        features.add("Network state");

        try {
            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();
            if (TextUtils.isEmpty(mPhoneNumber))
                mPhoneNumber = "N/A";
            desc.add(mPhoneNumber);
        } catch (SecurityException e) {
            e.printStackTrace();
            desc.add("Error: " + e.getMessage());
        }
        features.add("Phone number");


        String p10 = Build.BRAND;
        if (TextUtils.isEmpty(p10))
            p10 = "N/A";
        desc.add(p10);
        features.add("Brand, 系统定制商");


        String p11 = Build.BOOTLOADER;
        if (TextUtils.isEmpty(p11))
            p11 = "N/A";
        desc.add(p11);
        features.add("The system bootloader version number, 系统启动程序版本号");


        //board
        String p12 = Build.BOARD;
        if (TextUtils.isEmpty(p12))
            p12 = "N/A";
        desc.add(p12);
        features.add("Board, 主板");


        //CPU_ABI
        String p13 = Build.CPU_ABI;
        if (TextUtils.isEmpty(p13))
            p13 = "N/A";
        desc.add(p13);
        features.add("CPU_ABI, cpu指令集");


        //CPU_ABI2
        String p14 = Build.CPU_ABI;
        if (TextUtils.isEmpty(p14))
            p14 = "N/A";
        desc.add(p14);
        features.add("CPU_ABI2, cpu指令集2");


        //DEVICE
        String p15 = Build.DEVICE;
        if (TextUtils.isEmpty(p15))
            p15 = "N/A";
        desc.add(p15);
        features.add("DEVICE, 设置参数");


        //DISPLAY
        String p16 = Build.DISPLAY;
        if (TextUtils.isEmpty(p16))
            p16 = "N/A";
        desc.add(p16);
        features.add("DISPLAY, 显示屏参数");


        //Radio Version
        String p17 = Build.RADIO;
        if (TextUtils.isEmpty(p17))
            p17 = "N/A";
        desc.add(p17);
        features.add("Radio Version, 无线电固件版本");


        //FINGERPRINT
        String p18 = Build.FINGERPRINT;
        if (TextUtils.isEmpty(p18))
            p18 = "N/A";
        desc.add(p18);
        features.add("FINGERPRINT, 硬件识别码");


        //HARDWARE
        String p19 = Build.HARDWARE;
        if (TextUtils.isEmpty(p19))
            p19 = "N/A";
        desc.add(p19);
        features.add("HARDWARE, 硬件名");


        //Build.HOST
        String p20 = Build.HOST;
        if (TextUtils.isEmpty(p20))
            p20 = "N/A";
        desc.add(p20);
        features.add("HOST, 主机名");


        //Build.ID
        String p21 = Build.ID;
        if (TextUtils.isEmpty(p21))
            p21 = "N/A";
        desc.add(p21);
        features.add("Build.ID, 修订版本列表");


        //MANUFACTURER
        String p22 = Build.MANUFACTURER;
        if (TextUtils.isEmpty(p22))
            p22 = "N/A";
        desc.add(p22);
        features.add("MANUFACTURER, 硬件制造商");


        //Build.MODEL
        String p23 = Build.MODEL;
        if (TextUtils.isEmpty(p23))
            p23 = "N/A";
        desc.add(p23);
        features.add("Build.MODEL, 版本");


        //Build.SERIAL
        String p24 = Build.SERIAL;
        if (TextUtils.isEmpty(p24))
            p24 = "N/A";
        desc.add(p24);
        features.add("Build.SERIAL, 硬件序列号");


        //Build.PRODUCT
        String p25 = Build.PRODUCT;
        if (TextUtils.isEmpty(p25))
            p25 = "N/A";
        desc.add(p25);
        features.add("PRODUCT, 手机制造商");


        //Build.TAGS
        String p26 = Build.TAGS;
        if (TextUtils.isEmpty(p26))
            p26 = "N/A";
        desc.add(p26);
        features.add("Build.TAGS, Build的标签");


        //Build.TIME
        String p27 = Build.TIME + "";
        if (TextUtils.isEmpty(p27))
            p27 = "N/A";
        desc.add(p27);
        features.add("Build.TIME, 时间？");


        //Build.TYPE
        String p28 = Build.TYPE;
        if (TextUtils.isEmpty(p28))
            p28 = "N/A";
        desc.add(p28);
        features.add("Build.TYPE, 类型");


        //Build.PRODUCT
        String p29 = Build.USER;
        if (TextUtils.isEmpty(p29))
            p29 = "N/A";
        desc.add(p29);
        features.add("Build.USER, 用户");


        for (int i = 0; i < features.size(); i++) {
            contents.add("");
        }
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
//        rv_main_list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.Companion.getVERTICAL_LIST()));
        rv_main_list.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        adapter = new TextCardRVAdapter(this, null, features, desc, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                try {
                    int sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                                getSystemService(CLIPBOARD_SERVICE);
                        clipboard.setText(desc.get(position));
                    } else {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                                getSystemService(CLIPBOARD_SERVICE);
                        android.content.ClipData clip = android.content.ClipData
                                .newPlainText(features.get(position), desc.get(position));
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
