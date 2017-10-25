package com.catherine.webservices.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.catherine.webservices.Commands;
import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.entities.WebViewAttr;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.toolkits.CLog;
import com.catherine.webservices.views.NestedWebView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import catherine.messagecenter.Client;
import catherine.messagecenter.CustomReceiver;
import catherine.messagecenter.Result;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 * <p>
 * Go to P14_Full_WebView to see more
 */

public class P13_Nested_WebView extends LazyFragment {
    public final static String TAG = "P13_Nested_WebView";
    private MainInterface mainInterface;
    private NestedWebView wv;
    private ProgressBar pb;
    private Client client;
    private WebViewAttr attr;

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

    private void initComponent() {
        client = new Client(getActivity(), new CustomReceiver() {
            @Override
            public void onBroadcastReceive(@NotNull Result result) {
                refresh();
            }
        });
        MyApplication.INSTANCE.registerLocalBroadCastReceiver(client);
        client.gotMessages(Commands.WV_SETTINGS);
        wv = (NestedWebView) findViewById(R.id.wv);
        pb = (ProgressBar) findViewById(R.id.pb);
        refresh();
    }

    private void refresh() {
        attr = new WebViewAttr(getActivity());
        //可滑动，默认为true
        wv.setVerticalScrollBarEnabled(attr.isVerticalScrollBarEnabled());
        //可滑动，默认为true
        wv.setHorizontalScrollBarEnabled(attr.isHorizontalScrollBarEnabled());
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                pb.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pb.setVisibility(View.INVISIBLE);
                super.onPageFinished(view, url);
            }
        });

        WebSettings settings = wv.getSettings();
        //将图片调整到适合WebView的大小
        settings.setUseWideViewPort(attr.isUseWideViewPort());
        //缩放至屏幕的大小
        settings.setLoadWithOverviewMode(attr.isLoadWithOverviewMode());
        //支持缩放，默认为true。是下面那个的前提。
        settings.setSupportZoom(attr.isSupportZoom());
        //设置内置的缩放控件。
        settings.setBuiltInZoomControls(attr.isBuiltInZoomControls());
        //设置文本的缩放倍数，默认为 100，若上面是false，则该WebView不可缩放，这个不管设置什么都不能缩放。
        settings.setTextZoom(attr.getTextZoom());
        //隐藏原生的缩放控件
        settings.setDisplayZoomControls(attr.isDisplayZoomControls());

        //支持内容重新布局
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //多窗口
        settings.supportMultipleWindows();
        //关闭WebView中缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //设置可以访问文件
        settings.setAllowFileAccess(attr.isAllowFileAccess());
        //当WebView调用requestFocus时为WebView设置节点
        settings.setNeedInitialFocus(attr.isNeedInitialFocus());
        //支持JS
        settings.setJavaScriptEnabled(attr.isJavaScriptEnabled());
        //支持通过JS打开新窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(attr.isJavaScriptCanOpenWindowsAutomatically());
        //支持自动加载图片
        settings.setLoadsImagesAutomatically(attr.isLoadsImagesAutomatically());
        //设置编码格式
        settings.setDefaultTextEncodingName(attr.getDefaultTextEncodingName());
        //设置WebView的字体，默认字体为 "sans-serif"
        settings.setStandardFontFamily(attr.getStandardFontFamily());
        //设置WebView字体的大小，默认大小为 16
        settings.setDefaultFontSize(attr.getDefaultFontSize());
        //设置WebView支持的最小字体大小，默认为 8
        settings.setMinimumFontSize(attr.getMinimumFontSize());
        String ua = settings.getUserAgentString();
        CLog.Companion.i(TAG, "user agent:" + ua);

        wv.loadUrl(formattedUrl(Constants.MY_GITHUB));
    }

    private String formattedUrl(String url) {
        String tmp = url;
        if (!url.contains("http://") && !url.contains("https://")) {
            tmp = "http://" + url;
        }
        return tmp;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
