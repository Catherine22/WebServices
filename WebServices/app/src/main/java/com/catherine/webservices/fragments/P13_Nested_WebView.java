package com.catherine.webservices.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import catherine.messagecenter.Client;
import catherine.messagecenter.CustomReceiver;
import catherine.messagecenter.Result;

import static android.content.Context.DOWNLOAD_SERVICE;

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
        mainInterface.getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                initComponent();
            }

            @Override
            public void onDenied(@org.jetbrains.annotations.Nullable List<String> deniedPermissions) {
                StringBuilder context = new StringBuilder();
                if (deniedPermissions != null) {
                    for (String p : deniedPermissions) {
                        context.append(p);
                        context.append(", ");
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
                Bundle b = result.getMBundle();
                if (b != null) {
                    if (b.getBoolean("clear_history", false)) {
                        wv.clearHistory();
                    }

                    if (b.getBoolean("clear_cache", false)) {
                        wv.clearCache(true);
                    }
                }
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
        wv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                if (null == result)
                    return false;
                int type = result.getType();
                if (type == WebView.HitTestResult.UNKNOWN_TYPE)
                    return false;

                // 这里可以拦截很多类型，我们只处理图片类型就可以了
                switch (type) {
                    case WebView.HitTestResult.PHONE_TYPE: // 处理拨号
                        break;
                    case WebView.HitTestResult.EMAIL_TYPE: // 处理Email
                        break;
                    case WebView.HitTestResult.GEO_TYPE: // 地图类型
                        break;
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                        break;
                    case WebView.HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项
                        // 获取图片的路径
                        final String saveImgUrl = result.getExtra();

                        final Dialog myDialog = new Dialog(getActivity());
                        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        myDialog.setContentView(R.layout.dialog_text);
                        //设置dialog背景透明
                        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        myDialog.show();

                        final TextView tv_title = myDialog.findViewById(R.id.tv_title);
                        tv_title.setText("Alert!");
                        final TextView tv_message = myDialog.findViewById(R.id.tv_message);
                        tv_message.setText("How do you deal with this image?");
                        final Button bt_ok = myDialog.findViewById(R.id.bt_ok);
                        bt_ok.setText("Save");
                        bt_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();

                                try {
                                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";
                                    File dir = new File(path);
                                    if (!dir.exists())
                                        dir.mkdirs();

                                    DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(saveImgUrl));
                                    int start = saveImgUrl.lastIndexOf("/") + 1;
                                    String fileName = saveImgUrl.substring(start, saveImgUrl.length());
                                    request.setDestinationInExternalPublicDir("/Download/", fileName);
                                    request.setTitle("WebServices");
                                    request.setDescription("Downloading...");
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    downloadManager.enqueue(request);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        final Button bt_cancel = myDialog.findViewById(R.id.bt_cancel);
                        bt_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();
                            }
                        });

                        final Button bt_copy = myDialog.findViewById(R.id.bt_copy);
                        bt_copy.setText("Open");
                        bt_copy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                wv.loadUrl(saveImgUrl);
                                myDialog.dismiss();
                            }
                        });
                        return true;
                    default:
                        break;
                }
                return false;
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
        settings.setSupportMultipleWindows(false);
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
        /*
         * 是否允许定位，默认true。注意：为了保证定位可以使用，要保证以下几点：
         * Application 需要有android.Manifest.permission#ACCESS_COARSE_LOCATION的权限
         * Application 需要实现WebChromeClient#onGeolocationPermissionsShowPrompt的回调，
         * 接收Js定位请求访问地理位置的通知
         */
        settings.setGeolocationEnabled(attr.isGeolocationEnabled());
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
        //是否保存表单数据
        settings.setSaveFormData(attr.isSaveFormData());
        //是否存储页面DOM结构
        settings.setDomStorageEnabled(attr.isDomStorageEnabled());
        //是否允许数据库存储
        settings.setDatabaseEnabled(attr.isDatabaseEnabled());
        //是否允许Cache
        settings.setAppCacheEnabled(attr.isAppCacheEnabled());
        //设置存储定位数据库的位置
        settings.setGeolocationDatabasePath(MyApplication.INSTANCE.getDiskCacheDir("webview").getAbsolutePath());
        //设置Cache API缓存路径
        settings.setAppCachePath(MyApplication.INSTANCE.getDiskCacheDir("webview").getAbsolutePath());
         /*
         * 基于WebView导航的类型使用缓存：正常页面加载会加载缓存并按需判断内容是否需要重新验证。
         * 如果是页面返回，页面内容不会重新加载，直接从缓存中恢复。setCacheMode允许客户端根据指定的模式来
         * 使用缓存。
         * LOAD_DEFAULT 默认加载方式
         * LOAD_CACHE_ELSE_NETWORK 按网络情况使用缓存
         * LOAD_NO_CACHE 不使用缓存
         * LOAD_CACHE_ONLY 只使用缓存
         */
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

                CLog.Companion.i(TAG, "baseURL:" + ((baseUrl == null) ? "null" : baseUrl));
                CLog.Companion.i(TAG, "loadData:" + loadData);
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
