package com.catherine.webservices;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Catherine on 2017/11/2.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public abstract class BaseFragmentActivity extends AppCompatActivity {
    private final int GRANTED_SAW = 0x0001;
    private final int GRANTED_WS = 0x0010;
    private int requestSpec = 0x0000;
    private int grantedSpec = 0x0000;
    private int confirmedSpec = 0x0000;
    private List<String> deniedPermissionsList;
    private OnRequestPermissionsListener listener;


    protected void onCreate(Bundle savedInstanceState, int layoutResID) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);
        onPermissionGranted();
    }

    protected void onCreate(Bundle savedInstanceState, int layoutResID, String[] permissions) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);
        init(permissions);
    }


    private void init(final String[] permissions) {
        getPermissions(permissions, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                onPermissionGranted();
            }

            @Override
            public void onDenied(@Nullable List<String> deniedPermissions) {
                onPermissionDenied(deniedPermissions);
            }

            @Override
            public void onRetry() {
                init(permissions);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissions(String[] permissions, OnRequestPermissionsListener listener) {
        if (permissions == null || permissions.length == 0 || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            listener.onGranted();
            return;
        }
        this.listener = listener;
        deniedPermissionsList = new LinkedList<>();
        for (String p : permissions) {
            if (p.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                requestSpec |= GRANTED_SAW;
                if (android.provider.Settings.canDrawOverlays(BaseFragmentActivity.this))
                    grantedSpec |= GRANTED_SAW;
            } else if (p.equals(Manifest.permission.WRITE_SETTINGS)) {
                requestSpec |= GRANTED_WS;
                if (android.provider.Settings.System.canWrite(BaseFragmentActivity.this))
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
                ActivityCompat.requestPermissions(this, deniedPermissions, Constants.ACCESS_PERMISSION);
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
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + BaseFragmentActivity.this.getPackageName()));
            startActivityForResult(intent, Constants.PERMISSION_OVERLAY);
        }

        if ((permissions & GRANTED_WS) == GRANTED_WS && (permissions & grantedSpec) != GRANTED_WS) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + BaseFragmentActivity.this.getPackageName()));
            startActivityForResult(intent, Constants.PERMISSION_WRITE_SETTINGS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
            deniedResults.add("Manifest.permission.WRITE_SETTINGS");

        if ((requestSpec & GRANTED_SAW) == GRANTED_SAW && (grantedSpec & GRANTED_SAW) != GRANTED_SAW)
            deniedResults.add("Manifest.permission.SYSTEM_ALERT_WINDOW");


        if (deniedResults.size() != 0)
            listener.onDenied(deniedResults);
        else
            listener.onGranted();


        requestSpec = 0x0000;
        grantedSpec = 0x0000;
        confirmedSpec = 0x0000;
        deniedPermissionsList = null;
    }

    protected abstract void onPermissionGranted();

    protected void onPermissionDenied(@Nullable List<String> deniedPermissions) {
        StringBuilder context = new StringBuilder();
        if (deniedPermissions != null) {
            for (String p : deniedPermissions) {
                if (Manifest.permission.READ_PHONE_STATE.equals(p)) {
                    context.append(getResources().getString(R.string.permission_phone));
                    context.append("、");
                } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(p)) {
                    context.append(getResources().getString(R.string.permission_storage));
                    context.append("、");
                } else if (Manifest.permission.RECEIVE_SMS.equals(p)) {
                    context.append(getResources().getString(R.string.permission_sms));
                    context.append("、");
                }
            }
        }

        if (context.toString().length() != 0)
            context.replace(context.toString().length() - 1, context.toString().length(), "");

        DialogManager.showPermissionDialog(BaseFragmentActivity.this, String.format(getResources().getString(R.string.permission_request), context.toString()), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.PERMISSION_OVERLAY:
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
                            ActivityCompat.requestPermissions(this, deniedPermissions, Constants.ACCESS_PERMISSION);
                        } else {
                            List<String> deniedResults = new ArrayList<>();
                            if ((requestSpec & GRANTED_WS) == GRANTED_WS && (grantedSpec & GRANTED_WS) != GRANTED_WS)
                                deniedResults.add("Manifest.permission.WRITE_SETTINGS");

                            if ((requestSpec & GRANTED_SAW) == GRANTED_SAW && (grantedSpec & GRANTED_SAW) != GRANTED_SAW)
                                deniedResults.add("Manifest.permission.SYSTEM_ALERT_WINDOW");

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
            case Constants.PERMISSION_WRITE_SETTINGS:
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
                            ActivityCompat.requestPermissions(this, deniedPermissions, Constants.ACCESS_PERMISSION);
                        } else {
                            List<String> deniedResults = new ArrayList<>();
                            if ((requestSpec & GRANTED_WS) == GRANTED_WS && (grantedSpec & GRANTED_WS) != GRANTED_WS)
                                deniedResults.add("Manifest.permission.WRITE_SETTINGS");

                            if ((requestSpec & GRANTED_SAW) == GRANTED_SAW && (grantedSpec & GRANTED_SAW) != GRANTED_SAW)
                                deniedResults.add("Manifest.permission.SYSTEM_ALERT_WINDOW");

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
            case Constants.OPEN_SETTINGS:
                requestSpec = 0x0000;
                grantedSpec = 0x0000;
                confirmedSpec = 0x0000;
                deniedPermissionsList = null;
                listener.onRetry();
                break;
        }
    }
}
