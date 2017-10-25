package com.catherine.webservices.network;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.IgnoreProguard;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Catherine on 2017/10/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 * <p>
 * <p>
 * Call Java function from JavaScript with WebView
 */
public class MyJavaScriptInterface implements IgnoreProguard {
    private final static String TAG = "MyJavaScriptInterface";
    private Context ctx;
    private MainInterface mainInterface;

    public MyJavaScriptInterface(Context mainActivity) {
        ctx = mainActivity;
        //this ctx should be MainActivity or you can't do any functions of MainInterface
        mainInterface = (MainInterface) ctx;
    }

    @JavascriptInterface
    public void getPermission(final String[] permissions) {
        CLog.Companion.i(TAG, "getPermission()");
        mainInterface.getPermissions(permissions, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(ctx);
                myAlertDialog.setIcon(R.drawable.ic_warning_black_24dp)
                        .setCancelable(false)
                        .setTitle("Result")
                        .setMessage("Succeed!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                myAlertDialog.show();
            }

            @Override
            public void onDenied(@Nullable List<String> deniedPermissions) {
                StringBuilder context = new StringBuilder();
                if (deniedPermissions != null) {
                    for (String p : deniedPermissions) {
                        if (Manifest.permission.READ_PHONE_STATE.equals(p)) {
                            context.append("联络人信息、");
                        } else if (Manifest.permission.READ_SMS.equals(p)) {
                            context.append("短信、");
                        }
                    }
                }

                context.deleteCharAt(context.length() - 1);

                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(ctx);
                myAlertDialog.setIcon(R.drawable.ic_warning_black_24dp)
                        .setCancelable(false)
                        .setTitle("注意")
                        .setMessage(String.format("您目前未授权%s存取权限，未授权将造成程式无法执行，是否开启权限？", context.toString()))
                        .setNegativeButton("继续关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                ((Activity) ctx).finish();
                            }
                        }).setPositiveButton("确定开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", ctx.getPackageName(), null));
                        ((Activity) ctx).startActivityForResult(intent, Constants.OPEN_SETTINGS);
                    }
                });
                myAlertDialog.show();
            }

            @Override
            public void onRetry() {
                getPermission(permissions);
            }
        });
    }

    @JavascriptInterface
    public void showDialog(String message) {
        CLog.Companion.i(TAG, "showDialog()");
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(ctx);
        myAlertDialog.setIcon(R.drawable.ic_warning_black_24dp)
                .setCancelable(false)
                .setTitle("JS Message")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        myAlertDialog.show();
    }
}
