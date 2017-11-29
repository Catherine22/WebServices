package com.catherine.webservices.network;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.webkit.JavascriptInterface;

import com.catherine.webservices.Commands;
import com.catherine.webservices.R;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.toolkits.IgnoreProguard;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import catherine.messagecenter.AsyncResponse;
import catherine.messagecenter.Server;

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
    private MainInterface mainInterface;
    private Context ctx;
    private Server sv;

    public MyJavaScriptInterface(Context mainActivity) {
        ctx = mainActivity;
        //this ctx should be MainActivity or you can't do any functions of MainInterface
        mainInterface = (MainInterface) ctx;
        sv = new Server(ctx, new AsyncResponse() {
            @Override
            public void onFailure(int errorCode) {
                CLog.Companion.e(TAG, "onFailure:" + errorCode);
            }
        });
    }

    @JavascriptInterface
    public void getPermission(final String[] permissions) {
        CLog.Companion.i(TAG, "getPermission()");
        mainInterface.getPermissions(permissions, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                //Show contacts
                DialogManager.showAlertDialog(ctx, "Succeed!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }

            @Override
            public void onDenied(@Nullable List<String> deniedPermissions) {
                StringBuilder context = new StringBuilder();
                if (deniedPermissions != null) {
                    for (String p : deniedPermissions) {
                        context.append(p);
                        context.append(", ");
                    }
                }

                context.deleteCharAt(context.length() - 1);
                DialogManager.showPermissionDialog(ctx, String.format(ctx.getResources().getString(R.string.permission_request), context), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        ((Activity) ctx).finish();
                    }
                });
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
        DialogManager.showAlertDialog(ctx, "You received a message from JS:\n" + message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    @JavascriptInterface
    public void vibrate(long milliseconds) {
        Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(milliseconds);
        }
    }

    @JavascriptInterface
    public void savePreferences(String key, String value) {
        SharedPreferences sp = ctx.getSharedPreferences("wv_js", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
        showDialog("Succeed!");
    }

    //callback to JS
    @JavascriptInterface
    public String loadPreferences(String key) {
        SharedPreferences sp = ctx.getSharedPreferences("wv_js", Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    @JavascriptInterface
    public void dial(String message) {
        sv.pushString(Commands.JS_CALLBACK, message);
    }
}
