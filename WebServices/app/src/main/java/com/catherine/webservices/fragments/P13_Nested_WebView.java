package com.catherine.webservices.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.TextView;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.views.NestedWebView;

import java.util.List;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P13_Nested_WebView extends LazyFragment {
    public final static String TAG = "P13_Nested_WebView";
    private MainInterface mainInterface;
    private NestedWebView wv;
    private EditText et_url;

    public static P13_Nested_WebView newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P13_Nested_WebView fragment = new P13_Nested_WebView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_13_nested_webview);
        mainInterface = (MainInterface) getActivity();
        init();
    }

    private void init() {
        mainInterface.getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new OnRequestPermissionsListener() {
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
                        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(p)) {
                            context.append("存储、");
                        }
                    }
                }

                context.deleteCharAt(context.length() - 1);

                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
                myAlertDialog.setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .setTitle("注意")
                        .setMessage(String.format("您目前未授权%s存取权限，未授权将造成程式无法执行，是否开启权限？", context.toString()))
                        .setNegativeButton("继续关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        }).setPositiveButton("确定开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getActivity().getPackageName(), null));
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void fillInData() {

    }

    private void initComponent() {
        et_url = (EditText) findViewById(R.id.et_url);
        et_url.setText(Constants.MY_GITHUB);
        //handle "enter" event
        et_url.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    wv.loadUrl(formattedUrl(et_url.getText().toString()));
                    return true;
                } else
                    return false;
            }
        });
        wv = (NestedWebView) findViewById(R.id.wv);
        //可滑动
        wv.setVerticalScrollBarEnabled(true);
        //可滑动
        wv.setHorizontalScrollBarEnabled(true);

        WebSettings settings = wv.getSettings();


        wv.loadUrl(formattedUrl(Constants.MY_GITHUB));
    }

    private String formattedUrl(String url) {
        String tmp = url;
        if (!url.contains("http://") && !url.contains("https://")) {
            tmp = "http://" + url;
        }
        return tmp;
    }
}
