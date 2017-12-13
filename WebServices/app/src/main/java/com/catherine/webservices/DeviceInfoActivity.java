package com.catherine.webservices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.format.Formatter;
import android.view.View;

import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.entities.Geolocation;
import com.catherine.webservices.entities.TextCard;
import com.catherine.webservices.interfaces.ADID_Callback;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.network.HttpAsyncTask;
import com.catherine.webservices.network.HttpRequest;
import com.catherine.webservices.network.HttpResponse;
import com.catherine.webservices.network.HttpResponseListener;
import com.catherine.webservices.network.NetworkHealthListener;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.security.ADID_AsyncTask;
import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Catherine on 2017/9/21.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class DeviceInfoActivity extends BaseFragmentActivity {
    public final static String TAG = DeviceInfoActivity.class.getSimpleName();
    private List<TextCard> entities;
    private TextCardRVAdapter adapter;
    private SwipeRefreshLayout srl_container;
    private Handler handler;
    private boolean retry;
    private NetworkHelper helper;

    @Override
    @SuppressLint("MissingSuperCall")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_device_info, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.BLUETOOTH});
    }

    @Override
    protected void onPermissionGranted() {
        handler = new Handler(MyApplication.INSTANCE.calHandlerThread.getLooper());
        helper = new NetworkHelper();
        helper.listenToNetworkState(new NetworkHealthListener() {
            @Override
            public void networkConnected(@NotNull String type) {
                CLog.i(TAG, "network connected:" + type);
                if (retry) {
                    retry = false;
                    getIPGeolocation();
                }
            }

            @Override
            public void networkDisable() {
                CLog.e(TAG, "network disable");
            }
        });
        initComponent();
        fillInData();
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

        entities.add(new TextCard("ip", "", null));
        entities.add(new TextCard("国家代码", "", null));
        entities.add(new TextCard("国家", "", null));
        entities.add(new TextCard("行政区代码", "", null));
        entities.add(new TextCard("行政区", "", null));
        entities.add(new TextCard("城市", "", null));
        entities.add(new TextCard("邮编", "", null));
        entities.add(new TextCard("时区", "", null));
        entities.add(new TextCard("纬度", "", null));
        entities.add(new TextCard("经度", "", null));
        entities.add(new TextCard("地铁代码", "", null));
        getIPGeolocation();

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
                    ip = address.getHostAddress();
                } catch (Exception e) {
                    e.printStackTrace();
                    ip = "Error: " + e.getMessage();
                }
                if (TextUtils.isEmpty(ip))
                    ip = "N/A";

                try {
                    InetAddress address = InetAddress.getLocalHost();
                    hostName = address.getHostName();
                } catch (Exception e) {
                    e.printStackTrace();
                    hostName = "Error: " + e.getMessage();
                }
                if (TextUtils.isEmpty(hostName))
                    hostName = "N/A";

                entities.set(12, new TextCard("Host Name", hostName, null));
                entities.set(13, new TextCard("IP Address", ip, null));
                adapter.setEntities(entities);
                adapter.notifyDataSetChanged();
            }
        });


        WifiManager wm = (WifiManager) MyApplication.INSTANCE.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifi = wm.getConnectionInfo();
        if (wifi == null)
            entities.add(new TextCard("MAC Address", "N/A", null));
        else {
            String macAddress = wifi.getMacAddress();
            if (TextUtils.isEmpty(macAddress))
                macAddress = "N/A";
            entities.add(new TextCard("MAC Address", macAddress, null));
        }

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
        adapter.setEntities(entities);
        adapter.notifyDataSetChanged();
    }

    private void getIPGeolocation() {
        HttpRequest r = new HttpRequest.Builder()
                .url("http://freegeoip.net/json/")
                .listener(new HttpResponseListener() {
                    @Override
                    public void connectSuccess(HttpResponse response) {
                        srl_container.setRefreshing(false);
                        String body = response.getBody();
                        CLog.i(TAG, String.format(Locale.ENGLISH, "connectSuccess code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), body));

                        Gson gson = new GsonBuilder().serializeNulls().create();
                        Geolocation geolocation = gson.fromJson(body, Geolocation.class);

                        entities.set(1, new TextCard("ip", geolocation.getIp(), null));
                        entities.set(2, new TextCard("国家代码", geolocation.getCountry_code(), null));
                        entities.set(3, new TextCard("国家", geolocation.getCountry_name(), null));
                        entities.set(4, new TextCard("行政区代码", geolocation.getRegion_code(), null));
                        entities.set(5, new TextCard("行政区", geolocation.getRegion_name(), null));
                        entities.set(6, new TextCard("城市", geolocation.getCity(), null));
                        entities.set(7, new TextCard("邮编", geolocation.getZip_code(), null));
                        entities.set(8, new TextCard("时区", geolocation.getTime_zone(), null));
                        entities.set(9, new TextCard("纬度", geolocation.getLatitude(), null));
                        entities.set(10, new TextCard("经度", geolocation.getLongitude(), null));
                        entities.set(11, new TextCard("地铁代码", geolocation.getMetro_code(), null));
                        adapter.setEntities(entities);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void connectFailure(HttpResponse response, Exception e) {
                        srl_container.setRefreshing(false);
                        StringBuilder sb = new StringBuilder();
                        if (!helper.isNetworkHealthy()) {
                            DialogManager.showAlertDialog(DeviceInfoActivity.this, "Please turn on Wi-Fi or cellular.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            sb.append("retry...");
                            retry = true;
                        } else {
                            sb.append(String.format(Locale.ENGLISH, "connectFailure code:%s, message:%s, body:%s", response.getCode(), response.getCodeString(), response.getErrorMessage()));
                            String errorString = sb.toString();
                            CLog.e(TAG, errorString);
                            if (e != null) {
                                sb.append("\n");
                                sb.append(e.getMessage());
                                CLog.e(TAG, e.getMessage());

                                if (e instanceof SocketTimeoutException) {
                                    DialogManager.showAlertDialog(DeviceInfoActivity.this, "Connection timeout. Please check your server.", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                }
                            }

                            entities.set(1, new TextCard("ip", errorString, null));
                            entities.set(2, new TextCard("国家代码", errorString, null));
                            entities.set(3, new TextCard("国家", errorString, null));
                            entities.set(4, new TextCard("行政区代码", errorString, null));
                            entities.set(5, new TextCard("行政区", errorString, null));
                            entities.set(6, new TextCard("城市", errorString, null));
                            entities.set(7, new TextCard("邮编", errorString, null));
                            entities.set(8, new TextCard("时区", errorString, null));
                            entities.set(9, new TextCard("纬度", errorString, null));
                            entities.set(10, new TextCard("经度", errorString, null));
                            entities.set(11, new TextCard("地铁代码", errorString, null));
                            adapter.setEntities(entities);
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .build();
        new HttpAsyncTask(r).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Convert raw IP address to string.
     *
     * @param rawBytes raw IP address.
     * @return a string representation of the raw ip address.
     */
    private static String rawToString(byte[] rawBytes) {
        int i = 4;
        String s = "";
        for (byte raw : rawBytes) {
            s += (raw & 0xFF);
            if (--i > 0) {
                s += ".";
            }
        }

        return s;
    }

    private void initComponent() {
        srl_container = findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fillInData();
                srl_container.setRefreshing(false);
            }
        });
        RecyclerView rv_main_list = findViewById(R.id.rv_main_list);
        rv_main_list.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        adapter = new TextCardRVAdapter(this, entities, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                TextCard tc = entities.get(position);
                FileUtils.copyToClipboard(tc.title, tc.contents);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);
    }

}
