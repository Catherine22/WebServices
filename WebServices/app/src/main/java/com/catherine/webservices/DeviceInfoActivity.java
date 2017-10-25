package com.catherine.webservices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.security.ADID_AsyncTask;
import com.catherine.webservices.toolkits.CLog;

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Catherine on 2017/9/21.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class DeviceInfoActivity extends FragmentActivity {
    public final static String TAG = "P00_DeviceInfo";
    private List<String> features, contents, desc;
    private TextCardRVAdapter adapter;
    private SwipeRefreshLayout srl_container;
    private ADID_AsyncTask adid_asyncTask;
    private Handler handler;
    private OnRequestPermissionsListener listener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f_00_device_info);
        handler = new Handler(MyApplication.INSTANCE.calHandlerThread.getLooper());
        init();
    }

    private void init() {
        getPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.BLUETOOTH}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                fillInData();
                initComponent();
            }

            @Override
            public void onDenied(@org.jetbrains.annotations.Nullable List<String> deniedPermissions) {
                StringBuilder context = new StringBuilder();
                if (deniedPermissions != null) {
                    for (String p : deniedPermissions) {
                        if (Manifest.permission.READ_PHONE_STATE.equals(p)) {
                            context.append("存取电话、");
                        }
                        if (Manifest.permission.BLUETOOTH.equals(p)) {
                            context.append("蓝牙、");
                        }
                    }
                }

                context.deleteCharAt(context.length() - 1);

                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(DeviceInfoActivity.this);
                myAlertDialog.setIcon(R.drawable.ic_warning_black_24dp)
                        .setCancelable(false)
                        .setTitle("注意")
                        .setMessage(String.format("您目前未授权%s存取权限，未授权将造成程式无法执行，是否开启权限？", context.toString()))
                        .setNegativeButton("继续关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setPositiveButton("确定开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        startActivityForResult(intent, Constants.OPEN_SETTINGS);
                    }
                });
                myAlertDialog.show();
            }

            @Override
            public void onRetry() {
                init();
            }
        });
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
        desc.add(networkHelper.isNetworkHealth() + "");
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
        srl_container = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fillInData();
                initComponent();
                srl_container.setRefreshing(false);
            }
        });
        RecyclerView rv_main_list = (RecyclerView) findViewById(R.id.rv_main_list);
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
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);
    }

    //constants
    private final static int OPEN_SETTINGS = 1;
    private final static int ACCESS_PERMISSION = 2;
    private final static int PERMISSION_OVERLAY = 3;
    private final static int PERMISSION_WRITE_SETTINGS = 4;

    private final int GRANTED_SAW = 0x0001;     //同意特殊权限(SYSTEM_ALERT_WINDOW)
    private final int GRANTED_WS = 0x0010;      //同意特殊权限(WRITE_SETTINGS)
    private int requestSpec = 0x0000;           //需要的特殊权限
    private int grantedSpec = 0x0000;           //已取得的特殊权限
    private int confirmedSpec = 0x0000;         //已询问的特殊权限
    private List<String> deniedPermissionsList; //被拒绝的权限


    /**
     * 要求用户打开权限,仅限android 6.0 以上
     * <p/>
     * SYSTEM_ALERT_WINDOW 和 WRITE_SETTINGS, 这两个权限比较特殊，
     * 不能通过代码申请方式获取，必须得用户打开软件设置页手动打开，才能授权。
     *
     * @param permissions 手机权限 e.g. Manifest.permission.ACCESS_FINE_LOCATION
     * @param listener    此变量implements事件的接口,负责传递信息
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissions(String[] permissions, OnRequestPermissionsListener listener) {
        if (permissions == null || permissions.length == 0 || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            listener.onGranted();
            return;
        }
        this.listener = listener;
        deniedPermissionsList = new LinkedList<>();
        for (String p : permissions) {
            if (p.equals(android.Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                requestSpec |= GRANTED_SAW;
                if (android.provider.Settings.canDrawOverlays(this))
                    grantedSpec |= GRANTED_SAW;
            } else if (p.equals(android.Manifest.permission.WRITE_SETTINGS)) {
                requestSpec |= GRANTED_WS;
                if (android.provider.Settings.System.canWrite(this))
                    grantedSpec |= GRANTED_WS;
            } else if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissionsList.add(p);
            }

        }

        if (requestSpec != grantedSpec) {
            getASpecPermission(requestSpec);
        } else {// Granted all of the special permissions
            if (deniedPermissionsList.size() != 0) {
                //Ask for the permissions
                String[] deniedPermissions = new String[deniedPermissionsList.size()];
                for (int i = 0; i < deniedPermissionsList.size(); i++) {
                    deniedPermissions[i] = deniedPermissionsList.get(i);
                }
                ActivityCompat.requestPermissions(this, deniedPermissions, ACCESS_PERMISSION);
            } else {
                listener.onGranted();

                requestSpec = 0x0000;
                grantedSpec = 0x0000;
                confirmedSpec = 0x0000;
                deniedPermissionsList = null;
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void getASpecPermission(int permissions) {
        if ((permissions & GRANTED_SAW) == GRANTED_SAW && (permissions & grantedSpec) != GRANTED_SAW) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, Constants.PERMISSION_OVERLAY);
        }

        if ((permissions & GRANTED_WS) == GRANTED_WS && (permissions & grantedSpec) != GRANTED_WS) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, Constants.PERMISSION_WRITE_SETTINGS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Press home key then click icon to launch while checking permission
        if (permissions.length == 0) {
            requestSpec = 0x0000;
            grantedSpec = 0x0000;
            confirmedSpec = 0x0000;
            deniedPermissionsList = null;
            listener.onRetry();
            return;
        }

        List<String> deniedResults = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedResults.add(permissions[i]);
            }
        }

        if ((requestSpec & GRANTED_WS) == GRANTED_WS && (grantedSpec & GRANTED_WS) != GRANTED_WS)
            deniedResults.add(Manifest.permission.WRITE_SETTINGS);

        if ((requestSpec & GRANTED_SAW) == GRANTED_SAW && (grantedSpec & GRANTED_SAW) != GRANTED_SAW)
            deniedResults.add(Manifest.permission.SYSTEM_ALERT_WINDOW);


        if (deniedResults.size() != 0)
            listener.onDenied(deniedResults);
        else
            listener.onGranted();


        requestSpec = 0x0000;
        grantedSpec = 0x0000;
        confirmedSpec = 0x0000;
        deniedPermissionsList = null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CLog.Companion.i(TAG, "request:" + requestCode + "/resultCode" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSION_OVERLAY:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    confirmedSpec |= GRANTED_SAW;
                    confirmedSpec |= grantedSpec;
                    if (android.provider.Settings.canDrawOverlays(this))
                        grantedSpec |= GRANTED_SAW;
                    if (confirmedSpec == requestSpec) {
                        if (deniedPermissionsList.size() != 0) {
                            //Ask for the permissions
                            String[] deniedPermissions = new String[deniedPermissionsList.size()];
                            for (int i = 0; i < deniedPermissionsList.size(); i++) {
                                deniedPermissions[i] = deniedPermissionsList.get(i);
                            }
                            ActivityCompat.requestPermissions(this, deniedPermissions, ACCESS_PERMISSION);
                        } else {
                            List<String> deniedResults = new ArrayList<>();
                            if ((requestSpec & GRANTED_WS) == GRANTED_WS && (grantedSpec & GRANTED_WS) != GRANTED_WS)
                                deniedResults.add(Manifest.permission.WRITE_SETTINGS);

                            if ((requestSpec & GRANTED_SAW) == GRANTED_SAW && (grantedSpec & GRANTED_SAW) != GRANTED_SAW)
                                deniedResults.add(Manifest.permission.SYSTEM_ALERT_WINDOW);

                            if (deniedResults.size() > 0)
                                listener.onDenied(deniedResults);
                            else
                                listener.onGranted();

                            requestSpec = 0x0000;
                            grantedSpec = 0x0000;
                            confirmedSpec = 0x0000;
                            deniedPermissionsList = null;
                        }
                    }
                }
                break;
            case PERMISSION_WRITE_SETTINGS:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    confirmedSpec |= GRANTED_WS;
                    confirmedSpec |= grantedSpec;
                    if (android.provider.Settings.System.canWrite(this))
                        grantedSpec |= GRANTED_WS;
                    if (confirmedSpec == requestSpec) {
                        if (deniedPermissionsList.size() != 0) {
                            //Ask for the permissions
                            String[] deniedPermissions = new String[deniedPermissionsList.size()];
                            for (int i = 0; i < deniedPermissionsList.size(); i++) {
                                deniedPermissions[i] = deniedPermissionsList.get(i);
                            }
                            ActivityCompat.requestPermissions(this, deniedPermissions, ACCESS_PERMISSION);
                        } else {
                            List<String> deniedResults = new ArrayList<>();
                            if ((requestSpec & GRANTED_WS) == GRANTED_WS && (grantedSpec & GRANTED_WS) != GRANTED_WS)
                                deniedResults.add(Manifest.permission.WRITE_SETTINGS);

                            if ((requestSpec & GRANTED_SAW) == GRANTED_SAW && (grantedSpec & GRANTED_SAW) != GRANTED_SAW)
                                deniedResults.add(Manifest.permission.SYSTEM_ALERT_WINDOW);

                            if (deniedResults.size() > 0)
                                listener.onDenied(deniedResults);
                            else
                                listener.onGranted();

                            requestSpec = 0x0000;
                            grantedSpec = 0x0000;
                            confirmedSpec = 0x0000;
                            deniedPermissionsList = null;
                        }
                    }
                }
                break;
            case OPEN_SETTINGS:
                requestSpec = 0x0000;
                grantedSpec = 0x0000;
                confirmedSpec = 0x0000;
                deniedPermissionsList = null;
                listener.onRetry();
                break;
        }
    }

}
