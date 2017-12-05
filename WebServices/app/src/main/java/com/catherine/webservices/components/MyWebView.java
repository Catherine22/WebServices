package com.catherine.webservices.components;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
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
import android.os.Message;
import android.text.TextUtils;
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

import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.interfaces.ActivityResultListener;
import com.catherine.webservices.interfaces.WebViewProgressListener;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.toolkits.ApplicationConfig;
import com.catherine.webservices.toolkits.CLog;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Locale;

import static com.catherine.webservices.Constants.FILECHOOSER_RESULTCODE;


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
    private WebViewProgressListener progressListener;
    private ActivityResultListener activityResultListener;
    //save console logs in disk
    private final boolean keepLog = false;
    private boolean jsTimeout;
    private ApplicationConfig config;
    private Context ctx;

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
        initSettings(context, false, true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initSettings(Context context, boolean safer, boolean enableCache) {
        ctx = context;
        config = new ApplicationConfig();
        NetworkHelper networkHelper = new NetworkHelper();
        activityResultListener = (ActivityResultListener) ctx;
        WebSettings settings = getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setDefaultTextEncodingName("utf-8");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setUserAgentString(WebSettings.getDefaultUserAgent(ctx));
        } else {
            settings.setUserAgentString(System.getProperty("http.agent"));
        }
        String ua = settings.getUserAgentString();
        CLog.i(TAG, "my user agent:" + ua);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(true);
        }

        if (safer) {
            settings.setJavaScriptEnabled(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            settings.setAllowContentAccess(true);
            settings.setAllowFileAccess(true);
            settings.setGeolocationEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.setSafeBrowsingEnabled(true);
            }
        } else {
            settings.setJavaScriptEnabled(false);
            settings.setJavaScriptCanOpenWindowsAutomatically(false);
            settings.setAllowContentAccess(false);
            settings.setAllowFileAccess(false);
            settings.setGeolocationEnabled(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.setSafeBrowsingEnabled(false);
            }
        }

        //cache
        if (enableCache) {
            settings.setSaveFormData(true);
            settings.setDomStorageEnabled(true);
            settings.setDatabaseEnabled(true);
            settings.setAppCacheEnabled(true);
            settings.setDatabasePath(MyApplication.INSTANCE.getDiskCacheDir("webview").getAbsolutePath());
            settings.setGeolocationDatabasePath(MyApplication.INSTANCE.getDiskCacheDir("webview").getAbsolutePath());
            settings.setAppCachePath(MyApplication.INSTANCE.getDiskCacheDir("webview").getAbsolutePath());
            if (networkHelper.isNetworkHealthy()) {
                settings.setCacheMode(WebSettings.LOAD_DEFAULT);
            } else {
                settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
            settings.setAppCacheMaxSize(5 * 1024 * 1024);//5M
        } else {
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }

        settings.setDefaultTextEncodingName("utf-8");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // In this mode, the WebView will allow a secure origin to load content from any other origin, even if that origin is insecure.
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                CLog.i(TAG, "onReceivedIcon");
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
                CLog.i(TAG, "onReceivedTouchIconUrl:" + url);
                super.onReceivedTouchIconUrl(view, url, precomposed);
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                CLog.i(TAG, "onShowCustomView1");
                super.onShowCustomView(view, callback);
            }

            @Override
            public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                CLog.i(TAG, "onShowCustomView2");
                super.onShowCustomView(view, requestedOrientation, callback);
            }

            @Override
            public void onHideCustomView() {
                CLog.i(TAG, "onHideCustomView");
                super.onHideCustomView();
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                CLog.i(TAG, "onCreateWindow");
                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
            }

            @Override
            public void onRequestFocus(WebView view) {
                CLog.i(TAG, "onRequestFocus");
                super.onRequestFocus(view);
            }

            @Override
            public void onCloseWindow(WebView window) {
                CLog.i(TAG, "onCloseWindow");
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
                CLog.i(TAG, "onJsAlert:" + message);
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
                CLog.i(TAG, "onJsConfirm:" + message);
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
                CLog.i(TAG, "onJsBeforeUnload:" + message);
                return super.onJsBeforeUnload(view, url, message, result);
            }

            @Override
            public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
                CLog.i(TAG, "onExceededDatabaseQuota:" + databaseIdentifier);
                super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
            }

            //扩充缓存的容量
            @Override
            public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
                CLog.i(TAG, "onReachedMaxAppCacheSize:" + requiredStorage);
                super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
            }

            /**
             * Note that for applications targeting Android N and later SDKs (API level > M)
             * this method is only called for requests originating from secure origins such as https.
             * On non-secure origins geolocation requests are automatically denied.
             * @param origin The origin of the web content attempting to use the Geolocation API.
             * @param callback The callback to use to set the permission state for the origin.
             */
            @Override
            public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
                CLog.i(TAG, "onGeolocationPermissionsShowPrompt:" + origin);
                //User have to grant ACCESS_FINE_LOCATION permission first.
                final Dialog myDialog = new Dialog(ctx);
                myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

            /**
             * 通知程序有定位权限请求。如果onGeolocationPermissionsShowPrompt权限申请操作被取消，则隐藏相关的UI界面。
             */
            @Override
            public void onGeolocationPermissionsHidePrompt() {
                CLog.i(TAG, "onGeolocationPermissionsHidePrompt");
                super.onGeolocationPermissionsHidePrompt();
            }

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                CLog.i(TAG, "onPermissionRequest");
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

            /**
             * 通知主程序相关权限被取消。任何相关UI都应该隐藏掉。
             */
            @Override
            public void onPermissionRequestCanceled(PermissionRequest request) {
                CLog.i(TAG, "onPermissionRequestCanceled");
                super.onPermissionRequestCanceled(request);
            }

            /**
             * 通知主程序 执行的Js操作超时。客户端决定是否中断JavaScript继续执行。如果客户端返回true，JavaScript中断执行。如果客户端返回false，则执行继续。注意：如果继续执行，重置JavaScript超时计时器。如果Js下一次检查点仍没有结束，则再次提示。
             */
            @Override
            public boolean onJsTimeout() {
                CLog.i(TAG, "onJsTimeout");
                DialogManager.showErrorDialog(ctx, "JavaScript timeout. Do you want to retry?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        jsTimeout = true;
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        jsTimeout = false;
                    }
                });
                return jsTimeout;
//                super.onJsTimeout();
            }

            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                CLog.i(TAG, "onConsoleMessage:" + message);
                if (keepLog) {
                    config.writeWebViewLog(String.format(Locale.ENGLISH, "lineNumber(%d), sourceID(%s), message:%s", lineNumber, sourceID, message));
                }
                super.onConsoleMessage(message, lineNumber, sourceID);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                CLog.i(TAG, "onConsoleMessage");
                if (keepLog) {
                    config.writeWebViewLog(String.format(Locale.ENGLISH, "lineNumber(%d), sourceID(%s), message:%s", consoleMessage.lineNumber(), consoleMessage.sourceId(), consoleMessage.message()));
                }
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public Bitmap getDefaultVideoPoster() {
                CLog.i(TAG, "getDefaultVideoPoster");
                return super.getDefaultVideoPoster();
            }

            @Override
            public View getVideoLoadingProgressView() {
                CLog.i(TAG, "getVideoLoadingProgressView");
                return super.getVideoLoadingProgressView();
            }

            @Override
            public void getVisitedHistory(ValueCallback<String[]> callback) {
                CLog.i(TAG, "getVisitedHistory");
                super.getVisitedHistory(callback);
            }

            //Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                CLog.i(TAG, "onShowFileChooser：" + acceptType + ", capture:" + capture);
                activityResultListener.addValueCallback(uploadMsg);
                Intent i;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                else
                    i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                String type = TextUtils.isEmpty(acceptType) ? "*/*" : acceptType;
                i.setType(type);
                ((Activity) ctx).startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            //Android 5.0+
            @Override
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                CLog.i(TAG, "onShowFileChooser：" + fileChooserParams.getAcceptTypes()[0] + ", capture:" + fileChooserParams.isCaptureEnabled());
                activityResultListener.addValueCallbackL(filePathCallback);
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);

                String type = "*/*";
                if (fileChooserParams != null && fileChooserParams.getAcceptTypes() != null
                        && fileChooserParams.getAcceptTypes().length > 0) {
                    type = fileChooserParams.getAcceptTypes()[0];
                }
                i.setType(type);
                ((Activity) ctx).startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
                return true;
//                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }
        });

        setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                CLog.e(TAG, "onReceivedSslError");
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
                        if (canGoBack()) {
                            goBack();
                        }
                    }
                });
//                        super.onReceivedSslError(view, handler, error);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                CLog.i(TAG, "shouldOverrideUrlLoading:" + url);
                loadUrl(url);
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                CLog.i(TAG, "shouldOverrideUrlLoading2");
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                CLog.i(TAG, "onPageStarted:" + url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//              CLog.i(TAG, "onPageFinished:" + url);
                super.onPageFinished(view, url);
            }


            //在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
            @Override
            public void onLoadResource(WebView view, String url) {
                CLog.i(TAG, "onLoadResource:" + url);
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                CLog.i(TAG, "onPageCommitVisible:" + url);
                super.onPageCommitVisible(view, url);
            }

            //拦截替换网络请求数据,  API 11开始引入，API 21弃用
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                CLog.i(TAG, "shouldInterceptRequest:" + url);
                return super.shouldInterceptRequest(view, url);
            }

            //拦截替换网络请求数据,  从API 21开始引入
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                CLog.i(TAG, "shouldInterceptRequest2");
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
                CLog.w(TAG, "onTooManyRedirects:" + continueMsg);
                super.onTooManyRedirects(view, cancelMsg, continueMsg);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                CLog.e(TAG, "onReceivedError:" + description);
                String message = String.format(Locale.ENGLISH, "%s\nError code:%d\n%s", failingUrl, errorCode, description);
                DialogManager.showErrorDialog(ctx, message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (canGoBack())
                            goBack();
                    }
                });
//                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                CLog.e(TAG, "onReceivedError2");
                String message = String.format(Locale.ENGLISH, "%s\nError code:%d\n%s", request.getUrl(), error.getErrorCode(), error.getDescription());
                DialogManager.showErrorDialog(ctx, message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (canGoBack())
                            goBack();
                    }
                });
//                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                CLog.e(TAG, "onReceivedHttpError");
                super.onReceivedHttpError(view, request, errorResponse);
            }

            /**
             * 是否重发POST请求数据，默认不重发。
             */
            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                CLog.i(TAG, "onFormResubmission:" + dontResend);
                super.onFormResubmission(view, dontResend, resend);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                CLog.i(TAG, "doUpdateVisitedHistory:" + url);
                super.doUpdateVisitedHistory(view, url, isReload);
            }

            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                CLog.w(TAG, "onReceivedClientCertRequest");
                super.onReceivedClientCertRequest(view, request);
            }

            /**
             * 通知主程序：WebView接收HTTP认证请求，主程序可以使用HttpAuthHandler为请求设置WebView响应。默认取消请求。
             */
            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                CLog.w(TAG, "onReceivedHttpAuthRequest");
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                CLog.i(TAG, "shouldOverrideKeyEvent");
                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                CLog.w(TAG, "onUnhandledKeyEvent");
                super.onUnhandledKeyEvent(view, event);
            }

//                    @Override
//                    public void onScaleChanged(WebView view, float oldScale, float newScale) {
//                        CLog.i(TAG, "onScaleChanged");
//                        super.onScaleChanged(view, oldScale, newScale);
//                    }

            /**
             * 通知主程序执行了自动登录请求。
             */
            @Override
            public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
                CLog.w(TAG, "onReceivedLoginRequest");
                super.onReceivedLoginRequest(view, realm, account, args);
            }

            @Override
            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                CLog.i(TAG, "onRenderProcessGone");
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
        String url = NetworkHelper.formattedUrl(urlString);
        CLog.i(TAG, "Load " + url);
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file:///") || url.startsWith("content://") || url.startsWith("javascript:")) {
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
