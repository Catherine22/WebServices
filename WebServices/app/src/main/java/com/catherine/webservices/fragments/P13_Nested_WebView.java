package com.catherine.webservices.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.catherine.webservices.Commands;
import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.components.MyNestedWebView;
import com.catherine.webservices.entities.WebViewAttr;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.interfaces.WebViewProgressListener;
import com.catherine.webservices.network.MyJavaScriptInterface;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.toolkits.CLog;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
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
    private MyNestedWebView wv;
    private ProgressBar pb;
    private Client client;

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
                DialogManager.showPermissionDialog(getActivity(), String.format(getActivity().getResources().getString(R.string.permission_request), context), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
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
        client.gotMessages(Commands.WV_SETTINGS);
        wv = (MyNestedWebView) findViewById(R.id.wv);
        pb = (ProgressBar) findViewById(R.id.pb);
        refresh();
    }

    private void refresh() {
        WebViewAttr attr = new WebViewAttr(getActivity());
        //可滑动，默认为true
        wv.setVerticalScrollBarEnabled(attr.isVerticalScrollBarEnabled());
        //可滑动，默认为true
        wv.setHorizontalScrollBarEnabled(attr.isHorizontalScrollBarEnabled());
        wv.addWebViewProgressListener(new WebViewProgressListener() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100)
                    pb.setVisibility(View.INVISIBLE);
                else
                    pb.setVisibility(View.VISIBLE);
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
        //当WebView调用requestFocus时为WebView设置节点
        settings.setNeedInitialFocus(attr.isNeedInitialFocus());
        //支持JS
        settings.setJavaScriptEnabled(attr.isJavaScriptEnabled());
        //支持JS呼叫MyJavaScriptInterface提供的方法
        MyJavaScriptInterface javaScriptInterface = new MyJavaScriptInterface(getActivity());
        //In JS, your code would be name.function_of_your_javaScriptInterface(). For example, AndroidFunction.vibrate(500)
        wv.addJavascriptInterface(javaScriptInterface, "AndroidFunction");
        //支持通过JS打开新窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(attr.isJavaScriptCanOpenWindowsAutomatically());
        //支持自动加载图片
        settings.setLoadsImagesAutomatically(attr.isLoadsImagesAutomatically());
        //是否允许获取WebView的内容URL ，可以让WebView访问ContentProvider存储的内容。
        settings.setAllowContentAccess(attr.isAllowContentAccess());
        //设置可以访问文件
        settings.setAllowFileAccess(attr.isAllowFileAccess());
        //设置编码格式
        settings.setDefaultTextEncodingName(attr.getDefaultTextEncodingName());
        //设置WebView的字体，默认字体为 "sans-serif"
        settings.setStandardFontFamily(attr.getStandardFontFamily());
        //设置WebView字体的大小，默认大小为 16
        settings.setDefaultFontSize(attr.getDefaultFontSize());
        //设置WebView支持的最小字体大小，默认为 8
        settings.setMinimumFontSize(attr.getMinimumFontSize());
        //设置User Agent（手机版或桌面版）
        settings.setUserAgentString(attr.getUserAgentString(attr.getUserAgent()));
        String ua = settings.getUserAgentString();
        CLog.Companion.i(TAG, "my user agent:" + ua);
        //是否保存表单数据
        settings.setSaveFormData(attr.isSaveFormData());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            /*
             * 是否允许Js访问任何来源的内容。包括访问file scheme的URLs。考虑到安全性，
             * 限制Js访问范围默认禁用。注意：该方法只影响file scheme类型的资源，其他类型资源如图片类型的，
             * 不会受到影响。ICE_CREAM_SANDWICH_MR1版本以及以下默认为true，JELLY_BEAN版本
             * 以上默认为false
             */
            settings.setAllowUniversalAccessFromFileURLs(attr.isAllowUniversalAccessFromFileURLs());
            /*
             * 是否允许Js访问其他file scheme的URLs。包括访问file scheme的资源。考虑到安全性，
             * 限制Js访问范围默认禁用。注意：该方法只影响file scheme类型的资源，其他类型资源如图片类型的，
             * 不会受到影响。如果getAllowUniversalAccessFromFileURLs为true，则该方法被忽略。
             * ICE_CREAM_SANDWICH_MR1版本以及以下默认为true，JELLY_BEAN版本以上默认为false
             */
            settings.setAllowFileAccessFromFileURLs(attr.isAllowFileAccessFromFileURLs());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //是否需要用户手势来播放Media，默认true
            settings.setMediaPlaybackRequiresUserGesture(attr.isMediaPlaybackRequiresUserGesture());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //在Android 5.0上 WebView 默认不允许加载 Http 与 Https 混合内容
            settings.setMixedContentMode(attr.getMixedContentMode());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //支持安全浏览
            settings.setSafeBrowsingEnabled(attr.isSafeBrowsingEnabled());
        }

        //cache
        settings.setAppCachePath(MyApplication.INSTANCE.getDiskCacheDir("webview").getAbsolutePath());
        //设置WebView中的缓存模式
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        Bundle b = getArguments();
        if (b != null) {
            String loadData = b.getString("loadData", null);
            if (TextUtils.isEmpty(loadData))
                wv.loadUrl(NetworkHelper.Companion.formattedUrl(b.getString("url", Constants.MY_GITHUB)));
            else {
                String baseUrl = b.getString("baseURL", null);
                String mimeType = b.getString("mimeType", null);
                String encoding = b.getString("encoding", null);
                String historyUrl = b.getString("historyUrl", null);
                wv.loadDataWithBaseURL(baseUrl, loadData, mimeType, encoding, historyUrl);
            }
        } else {
            wv.loadUrl(NetworkHelper.Companion.formattedUrl(Constants.MY_GITHUB));
        }
    }


    @Override
    public void onDestroy() {
        client.release();
        super.onDestroy();
    }
}
