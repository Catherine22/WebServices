package com.catherine.webservices.components;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.UnsupportedSchemeException;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.catherine.webservices.interfaces.WebViewProgressListener;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.toolkits.CLog;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Created by Catherine on 2017/11/3.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 * <p>
 * <p>
 * This WebView is suppose to deal with url starts with "http://", "https://", "file:///", "content://" and "intent://"
 */

public class MyWebView extends WebView {
    private final static String TAG = "MyWebView";
    private Context ctx;
    private WebViewProgressListener progressListener;

    public MyWebView(Context context) {
        super(context);
        initSettings(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSettings(context);
    }

    public MyWebView(Context context, AttributeSet attrs, Context ctx) {
        super(context, attrs);
        initSettings(context);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, Context ctx) {
        super(context, attrs, defStyleAttr);
        initSettings(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, Context ctx) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSettings(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initSettings(Context context) {
        ctx = context;
        WebSettings settings = getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setAllowFileAccess(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setDefaultTextEncodingName("utf-8");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // In this mode, the WebView will allow a secure origin to load content from any other origin, even if that origin is insecure.
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (progressListener != null)
                    progressListener.onProgressChanged(view, newProgress);
                super.onProgressChanged(view, newProgress);
            }


            @Override
            public boolean onJsAlert(WebView view, final String url, final String message, final JsResult result) {
                CLog.Companion.i(TAG, "onJsAlert:" + message);
                DialogManager.showAlertDialog(ctx, message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });

//                return super.onJsAlert(view, url, message, result);
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, final String url, final String message, final JsResult result) {
                CLog.Companion.i(TAG, "onJsConfirm:" + message);
                DialogManager.showAlertDialog(ctx, message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
                return true;
//                return super.onJsConfirm(view, url, message, result);
            }
        });

        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                CLog.Companion.i(TAG, "shouldOverrideUrlLoading:" + url);
                loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                CLog.Companion.i(TAG, "onPageStarted:" + url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                                 CLog.Companion.i(TAG, "onPageFinished:" + url);
                super.onPageFinished(view, url);
            }

            //skip SSL validation
            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
//                                 CLog.Companion.e(TAG, "onReceivedSslError");
                handler.proceed();
            }

        });
    }

    /**
     * Loading progress from 0 to 100
     *
     * @param progressListener
     */
    public void addWebViewProgressListener(WebViewProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /**
     * 不是用来加载整个页面文件的，而是用来加载一段代码片，不能加载图片。
     *
     * @param data 代码片段内容
     */
    public void loadData(String data) throws UnsupportedEncodingException {
        //解决字符冲突，注意做内容encode和后面的编码方式要一样。
        loadData(URLEncoder.encode(data, "utf-8"), "text/html", "utf-8");
    }

    /**
     * @param data     代码片段内容，不能包括'#', '%', '\' , '?'
     * @param mimeType 代码片段所对应的MIME类型，如果传null，则默认为text/html
     * @param encoding 代码片段的编码方式
     */
    @Override
    public void loadData(String data, String mimeType, String encoding) {
        super.loadData(data, mimeType, encoding);
    }

    /**
     * 所有{@link #loadData(String, String, String)}做的到的事都做得到，而且不会有字符冲突，可开启图片。
     *
     * @param data 代码片段内容。
     */
    public void loadDataWithBaseURL(String data) {
        loadDataWithBaseURL(null, data);
    }

    /**
     * 所有{@link #loadData(String, String, String)}做的到的事都做得到，而且不会有字符冲突，可开启图片。
     *
     * @param baseUrl 如果data中的url是相对地址，则就会加上基准url来拼接出完整的地址。
     * @param data    代码片段内容。
     */
    public void loadDataWithBaseURL(String baseUrl, String data) {
        loadDataWithBaseURL(baseUrl, data, "text/html", "utf-8", null);
    }

    /**
     * 所有{@link #loadData(String, String, String)}做的到的事都做得到，而且不会有字符冲突，可开启图片。
     *
     * @param baseUrl    如果data中的url是相对地址，则就会加上基准url来拼接出完整的地址。
     * @param data       代码片段内容。
     * @param mimeType   代码片段所对应的MIME类型，如果传null，则默认为text/html
     * @param encoding   代码片段的编码方式
     * @param historyUrl 当前的历史记录所要存储的值。
     */
    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    @Override
    public void loadUrl(String urlString) {
        String url = NetworkHelper.Companion.formattedUrl(urlString);
        CLog.Companion.i(TAG, "Load " + url);
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file:///") || url.startsWith("content://")) {
            super.loadUrl(url);
        } else if (url.startsWith("intent://")) {
            try {
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                if (intent != null) {
                    stopLoading();
                    PackageManager packageManager = ctx.getPackageManager();
                    ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (info != null) {
                        ctx.startActivity(intent);
                    } else {
                        String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                        super.loadUrl(fallbackUrl);

                        // or call external browser
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl));
//                    context.startActivity(browserIntent);
                    }
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
                DialogManager.showErrorDialog(ctx, "Can't resolve intent://", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                ctx.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
//                DialogManager.showErrorDialog(getActivity(), "Failed to load URL, try other URL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });

                //try to google
                super.loadUrl("https://www.google.com/search?q=" + urlString);
            }
        }
    }
}
