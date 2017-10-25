package com.catherine.webservices.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.catherine.webservices.Commands;
import com.catherine.webservices.Constants;
import com.catherine.webservices.MyApplication;
import com.catherine.webservices.R;
import com.catherine.webservices.entities.WebViewAttr;
import com.catherine.webservices.interfaces.BackKeyListener;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.toolkits.CLog;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import catherine.messagecenter.Client;
import catherine.messagecenter.CustomReceiver;
import catherine.messagecenter.Result;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P14_Full_WebView extends LazyFragment {
    public final static String TAG = "P14_Full_WebView";
    private MainInterface mainInterface;
    private WebView wv;
    private ImageView iv_icon;
    private AutoCompleteTextView actv_url;
    private ProgressBar pb;
    private String currentUrl = Constants.MY_GITHUB;
    private Client client;
    private WebViewAttr attr;
    private Dialog jsDialog;
    //test js -> https://www.javascript.com/

    public static P14_Full_WebView newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P14_Full_WebView fragment = new P14_Full_WebView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_14_full_webview);
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
        mainInterface.setBackKeyListener(new BackKeyListener() {
            @Override
            public void OnKeyDown() {
                CLog.Companion.i(TAG, "back");
//                //history
//                WebBackForwardList history = wv.copyBackForwardList();
//                for (int i = 0; i < history.getSize(); i++) {
//                    CLog.Companion.d(TAG, history.getItemAtIndex(i).getOriginalUrl());
//                }
                if (wv.canGoBack())
                    wv.goBack();
                else {
                    mainInterface.backToPreviousPage();
                }
            }
        });
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        iv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainInterface.openSlideMenu();
            }
        });
        actv_url = (AutoCompleteTextView) findViewById(R.id.actv_url);
        actv_url.setFocusableInTouchMode(true);
        actv_url.requestFocus();
        actv_url.setText(formattedUrl(currentUrl));
        //handle "enter" event
        actv_url.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //hide soft keyboard
                    mainInterface.hideKeyboard();

                    // Perform action on key press
                    wv.loadUrl(formattedUrl(actv_url.getText().toString()));
                    return true;
                }
                return false;
            }
        });
        String[] urls = getResources().getStringArray(R.array.url_suggestions);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, Arrays.asList(urls));
        actv_url.setAdapter(adapter);

        pb = (ProgressBar) findViewById(R.id.pb);
        pb.setMax(100);
        wv = (WebView) findViewById(R.id.wv);
        refresh();
    }

    private void refresh() {
        attr = new WebViewAttr(getActivity());
        //可滑动，默认为true
        wv.setVerticalScrollBarEnabled(attr.isVerticalScrollBarEnabled());
        //可滑动，默认为true
        wv.setHorizontalScrollBarEnabled(attr.isHorizontalScrollBarEnabled());
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pb.setProgress(newProgress);
                if (pb.getProgress() == 100)
                    pb.setVisibility(View.GONE);
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                CLog.Companion.i(TAG, "onReceivedTitle:" + title);
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                CLog.Companion.i(TAG, "onReceivedIcon");
                iv_icon.setVisibility(View.VISIBLE);
                iv_icon.setImageBitmap(icon);
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

            //处理alert弹出框，html弹框的一种方式
            @Override
            public boolean onJsAlert(WebView view, final String url, final String message, final JsResult result) {
                CLog.Companion.i(TAG, "onJsAlert:" + message);

                //处理JS的弹窗，改成以自定义style实现
                if (jsDialog != null && jsDialog.isShowing())
                    result.cancel();
                else {
                    jsDialog = new Dialog(getActivity());
                    jsDialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
                    jsDialog.setContentView(R.layout.dialog_text);
                    //设置dialog背景透明
                    jsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    jsDialog.show();

                    final TextView tv_title = jsDialog.findViewById(R.id.tv_title);
                    tv_title.setText("JS alert from " + url);
                    final TextView tv_message = jsDialog.findViewById(R.id.tv_message);
                    if (!TextUtils.isEmpty(message))
                        tv_message.setText(message);
                    final Button bt_ok = jsDialog.findViewById(R.id.bt_ok);
                    bt_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            jsDialog.dismiss();
                            result.confirm();
                        }
                    });
                    final Button bt_cancel = jsDialog.findViewById(R.id.bt_cancel);
                    bt_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            jsDialog.dismiss();
                            result.cancel();
                        }
                    });

                    final Button bt_copy = jsDialog.findViewById(R.id.bt_copy);
                    bt_copy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(url, message);
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                            } else {
                                Toast.makeText(getActivity(), "Clipboard not works", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

//                return super.onJsAlert(view, url, message, result);
                return true;
            }

            //处理confirm弹出框
            @Override
            public boolean onJsConfirm(WebView view, final String url, final String message, final JsResult result) {
                CLog.Companion.i(TAG, "onJsConfirm:" + message);

                //处理JS的弹窗，改成以自定义style实现
                if (jsDialog != null && jsDialog.isShowing())
                    result.cancel();
                else {
                    jsDialog = new Dialog(getActivity());
                    jsDialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
                    jsDialog.setContentView(R.layout.dialog_text);
                    //设置dialog背景透明
                    jsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    jsDialog.show();

                    final TextView tv_title = jsDialog.findViewById(R.id.tv_title);
                    tv_title.setText("JS confirm from " + url);
                    final TextView tv_message = jsDialog.findViewById(R.id.tv_message);
                    if (!TextUtils.isEmpty(message))
                        tv_message.setText(message);
                    final Button bt_ok = jsDialog.findViewById(R.id.bt_ok);
                    bt_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            jsDialog.dismiss();
                            result.confirm();
                        }
                    });
                    final Button bt_cancel = jsDialog.findViewById(R.id.bt_cancel);
                    bt_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            jsDialog.dismiss();
                            result.cancel();
                        }
                    });

                    final Button bt_copy = jsDialog.findViewById(R.id.bt_copy);
                    bt_copy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(url, message);
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                            } else {
                                Toast.makeText(getActivity(), "Clipboard not works", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                return true;
//                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, final String url, final String message, final String defaultValue, final JsPromptResult result) {
                CLog.Companion.i(TAG, "onJsPrompt:" + message);

                //处理JS的弹窗，改成以自定义style实现
                if (jsDialog != null && jsDialog.isShowing())
                    result.cancel();
                else {
                    jsDialog = new Dialog(getActivity());
                    jsDialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
                    jsDialog.setContentView(R.layout.dialog_edittext);
                    //设置dialog背景透明
                    jsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    jsDialog.show();

                    final TextView tv_title = jsDialog.findViewById(R.id.tv_title);
                    tv_title.setText("JS prompt from " + url);
                    final TextView tv_message = jsDialog.findViewById(R.id.tv_message);
                    if (!TextUtils.isEmpty(message))
                        tv_message.setText(message);
                    final EditText et_input = jsDialog.findViewById(R.id.et_input);
                    if (!TextUtils.isEmpty(defaultValue))
                        et_input.setText(defaultValue);
                    final Button bt_ok = jsDialog.findViewById(R.id.bt_ok);
                    bt_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(et_input.getText())) {
                                et_input.setHint(getActivity().getString(R.string.et_hint));
                            } else {
                                jsDialog.dismiss();
                                result.confirm(et_input.getText().toString());
                            }
                        }
                    });
                    final Button bt_cancel = jsDialog.findViewById(R.id.bt_cancel);
                    bt_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            jsDialog.dismiss();
                            result.cancel();
                        }
                    });

                    final Button bt_copy = jsDialog.findViewById(R.id.bt_copy);
                    bt_copy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(url, message);
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                            } else {
                                Toast.makeText(getActivity(), "Clipboard not works", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                return true;
//                return super.onJsPrompt(view, url, message, defaultValue, result);
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
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                CLog.Companion.i(TAG, "onGeolocationPermissionsShowPrompt:" + origin);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public void onGeolocationPermissionsHidePrompt() {
                CLog.Companion.i(TAG, "onGeolocationPermissionsHidePrompt");
                super.onGeolocationPermissionsHidePrompt();
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                CLog.Companion.i(TAG, "onPermissionRequest");
                super.onPermissionRequest(request);
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
        wv.setWebViewClient(
                new WebViewClient() {
                    //打开网页时不调用系统浏览器， 而是在本WebView中显示
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        CLog.Companion.i(TAG, "shouldOverrideUrlLoading:" + url);
//                        iv_icon.setVisibility(View.GONE);
//                        iv_icon.setImageResource(R.mipmap.ic_launcher_round);
                        actv_url.setText(url);
                        currentUrl = url;
                        view.loadUrl(formattedUrl(currentUrl));
                        return true;
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        CLog.Companion.i(TAG, "shouldOverrideUrlLoading2");
                        return super.shouldOverrideUrlLoading(view, request);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        CLog.Companion.i(TAG, "onPageStarted:" + url);
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        CLog.Companion.i(TAG, "onPageFinished:" + url);
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
                        CLog.Companion.i(TAG, "onTooManyRedirects:" + continueMsg);
                        super.onTooManyRedirects(view, cancelMsg, continueMsg);
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        CLog.Companion.i(TAG, "onReceivedError:" + description);
                        super.onReceivedError(view, errorCode, description, failingUrl);
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        CLog.Companion.i(TAG, "onReceivedError2");
                        super.onReceivedError(view, request, error);
                    }

                    @Override
                    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                        CLog.Companion.i(TAG, "onReceivedHttpError");
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
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        CLog.Companion.i(TAG, "onReceivedSslError");
                        super.onReceivedSslError(view, handler, error);
                    }

                    @Override
                    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                        CLog.Companion.i(TAG, "onReceivedClientCertRequest");
                        super.onReceivedClientCertRequest(view, request);
                    }

                    @Override
                    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                        CLog.Companion.i(TAG, "onReceivedHttpAuthRequest");
                        super.onReceivedHttpAuthRequest(view, handler, host, realm);
                    }

                    @Override
                    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                        CLog.Companion.i(TAG, "shouldOverrideKeyEvent");
                        return super.shouldOverrideKeyEvent(view, event);
                    }

                    @Override
                    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                        CLog.Companion.i(TAG, "onUnhandledKeyEvent");
                        super.onUnhandledKeyEvent(view, event);
                    }

                    @Override
                    public void onScaleChanged(WebView view, float oldScale, float newScale) {
                        CLog.Companion.i(TAG, "onScaleChanged");
                        super.onScaleChanged(view, oldScale, newScale);
                    }

                    @Override
                    public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
                        CLog.Companion.i(TAG, "onReceivedLoginRequest");
                        super.onReceivedLoginRequest(view, realm, account, args);
                    }

                    @Override
                    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                        CLog.Companion.i(TAG, "onRenderProcessGone");
                        return super.onRenderProcessGone(view, detail);
                    }
                }
        );


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

        //cache
        settings.setAppCachePath(MyApplication.INSTANCE.getDiskCacheDir("webview").getAbsolutePath());
        //设置WebView中的缓存模式
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        wv.loadUrl(formattedUrl(currentUrl));
    }

    private String formattedUrl(String url) {
        String tmp = url;
        if (url.contains("file:///") || url.contains("content://"))
            return tmp;

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
