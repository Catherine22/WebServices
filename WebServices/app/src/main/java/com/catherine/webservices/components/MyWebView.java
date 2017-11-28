package com.catherine.webservices.components;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.catherine.webservices.R;
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

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSettings(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
            public void onReceivedIcon(WebView view, Bitmap icon) {
                CLog.Companion.i(TAG, "onReceivedIcon");
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
                CLog.Companion.i(TAG, "onReceivedTouchIconUrl:" + url);
                super.onReceivedTouchIconUrl(view, url, precomposed);
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                CLog.Companion.i(TAG, "onShowCustomView1");
                super.onShowCustomView(view, callback);
            }

            @Override
            public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                CLog.Companion.i(TAG, "onShowCustomView2");
                super.onShowCustomView(view, requestedOrientation, callback);
            }

            @Override
            public void onHideCustomView() {
                CLog.Companion.i(TAG, "onHideCustomView");
                super.onHideCustomView();
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                CLog.Companion.i(TAG, "onCreateWindow");
                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
            }

            @Override
            public void onRequestFocus(WebView view) {
                CLog.Companion.i(TAG, "onRequestFocus");
                super.onRequestFocus(view);
            }

            @Override
            public void onCloseWindow(WebView window) {
                CLog.Companion.i(TAG, "onCloseWindow");
                super.onCloseWindow(window);
            }

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

            @Override
            public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
                CLog.Companion.i(TAG, "onJsBeforeUnload:" + message);
                return super.onJsBeforeUnload(view, url, message, result);
            }

            @Override
            public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
                CLog.Companion.i(TAG, "onExceededDatabaseQuota:" + databaseIdentifier);
                super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
            }

            @Override
            public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
                CLog.Companion.i(TAG, "onReachedMaxAppCacheSize:" + requiredStorage);
                super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
                CLog.Companion.i(TAG, "onGeolocationPermissionsShowPrompt:" + origin);
                //User have to grant ACCESS_FINE_LOCATION permission first.
                final Dialog myDialog = new Dialog(ctx);
                myDialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
                myDialog.setContentView(R.layout.dialog_checkbox);
                //设置dialog背景透明
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();

                final TextView tv_title = myDialog.findViewById(R.id.tv_title);
                tv_title.setText(ctx.getString(R.string.dialog_title_hint));
                final TextView tv_message = myDialog.findViewById(R.id.tv_message);
                tv_message.setText(String.format("%s%s", origin, ctx.getString(R.string.would_like_to_use_your_current_location)));
                final CheckBox cb = myDialog.findViewById(R.id.cb);
                final Button bt_ok = myDialog.findViewById(R.id.bt_ok);
                bt_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.invoke(origin, true, cb.isChecked());
                        myDialog.dismiss();
                    }
                });
                final Button bt_cancel = myDialog.findViewById(R.id.bt_cancel);
                bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.invoke(origin, false, cb.isChecked());
                        myDialog.dismiss();
                    }
                });
//                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public void onGeolocationPermissionsHidePrompt() {
                CLog.Companion.i(TAG, "onGeolocationPermissionsHidePrompt");
                super.onGeolocationPermissionsHidePrompt();
            }

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                CLog.Companion.i(TAG, "onPermissionRequest");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    final String[] res = request.getResources();
                    StringBuilder context = new StringBuilder();
                    for (String s : res) {
                        context.append(s);
                        context.append(", ");
                    }
                    context.deleteCharAt(context.length() - 1);
                    DialogManager.showAlertDialog(ctx, String.format("%s\n\n%s", ctx.getResources().getString(R.string.This_page_wants_to_use_following_resources), context), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                request.grant(res);
                            }
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                request.deny();
                            }
                        }
                    });
                }
//                super.onPermissionRequest(request);
            }

            @Override
            public void onPermissionRequestCanceled(PermissionRequest request) {
                CLog.Companion.i(TAG, "onPermissionRequestCanceled");
                super.onPermissionRequestCanceled(request);
            }

            @Override
            public boolean onJsTimeout() {
                CLog.Companion.i(TAG, "onJsTimeout");
                return super.onJsTimeout();
            }

            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                CLog.Companion.i(TAG, "onConsoleMessage:" + message);
                super.onConsoleMessage(message, lineNumber, sourceID);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                CLog.Companion.i(TAG, "onConsoleMessage");
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public Bitmap getDefaultVideoPoster() {
                CLog.Companion.i(TAG, "getDefaultVideoPoster");
                return super.getDefaultVideoPoster();
            }

            @Override
            public View getVideoLoadingProgressView() {
                CLog.Companion.i(TAG, "getVideoLoadingProgressView");
                return super.getVideoLoadingProgressView();
            }

            @Override
            public void getVisitedHistory(ValueCallback<String[]> callback) {
                CLog.Companion.i(TAG, "getVisitedHistory");
                super.getVisitedHistory(callback);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                CLog.Companion.i(TAG, "onShowFileChooser");
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }
        });

        setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                CLog.Companion.e(TAG, "onReceivedSslError");
                String message = "SSL Certificate error.";
                switch (error.getPrimaryError()) {
                    case SslError.SSL_UNTRUSTED:
                        message = "The certificate authority is not trusted.";
                        break;
                    case SslError.SSL_EXPIRED:
                        message = "The certificate has expired.";
                        break;
                    case SslError.SSL_IDMISMATCH:
                        message = "The certificate Hostname mismatch.";
                        break;
                    case SslError.SSL_NOTYETVALID:
                        message = "The certificate is not yet valid.";
                        break;
                }
                message += " Do you want to continue anyway?";
                DialogManager.showErrorDialog(ctx, "SSL Error:\n" + message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
//                        super.onReceivedSslError(view, handler, error);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                CLog.Companion.i(TAG, "shouldOverrideUrlLoading:" + url);
                loadUrl(url);
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                CLog.Companion.i(TAG, "shouldOverrideUrlLoading2");
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                CLog.Companion.i(TAG, "onPageStarted:" + url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//              CLog.Companion.i(TAG, "onPageFinished:" + url);
                super.onPageFinished(view, url);
            }


            //在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
            @Override
            public void onLoadResource(WebView view, String url) {
                CLog.Companion.i(TAG, "onLoadResource:" + url);
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                CLog.Companion.i(TAG, "onPageCommitVisible:" + url);
                super.onPageCommitVisible(view, url);
            }

            //拦截替换网络请求数据,  API 11开始引入，API 21弃用
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                CLog.Companion.i(TAG, "shouldInterceptRequest:" + url);
                return super.shouldInterceptRequest(view, url);
            }

            //拦截替换网络请求数据,  从API 21开始引入
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                CLog.Companion.i(TAG, "shouldInterceptRequest2");
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
                CLog.Companion.w(TAG, "onTooManyRedirects:" + continueMsg);
                super.onTooManyRedirects(view, cancelMsg, continueMsg);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                CLog.Companion.e(TAG, "onReceivedError:" + description);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                CLog.Companion.e(TAG, "onReceivedError2");
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                CLog.Companion.e(TAG, "onReceivedHttpError");
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                CLog.Companion.i(TAG, "onFormResubmission:" + dontResend);
                super.onFormResubmission(view, dontResend, resend);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                CLog.Companion.i(TAG, "doUpdateVisitedHistory:" + url);
                super.doUpdateVisitedHistory(view, url, isReload);
            }

            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                CLog.Companion.w(TAG, "onReceivedClientCertRequest");
                super.onReceivedClientCertRequest(view, request);
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                CLog.Companion.w(TAG, "onReceivedHttpAuthRequest");
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                CLog.Companion.i(TAG, "shouldOverrideKeyEvent");
                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                CLog.Companion.w(TAG, "onUnhandledKeyEvent");
                super.onUnhandledKeyEvent(view, event);
            }

//                    @Override
//                    public void onScaleChanged(WebView view, float oldScale, float newScale) {
//                        CLog.Companion.i(TAG, "onScaleChanged");
//                        super.onScaleChanged(view, oldScale, newScale);
//                    }

            @Override
            public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
                CLog.Companion.w(TAG, "onReceivedLoginRequest");
                super.onReceivedLoginRequest(view, realm, account, args);
            }

            @Override
            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                CLog.Companion.i(TAG, "onRenderProcessGone");
                return super.onRenderProcessGone(view, detail);
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
